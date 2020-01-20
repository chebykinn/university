package com.ghostflow.services;

import com.ghostflow.database.BidRepository;
import com.ghostflow.database.Bids;
import com.ghostflow.database.postgres.entities.BidEntity;
import com.ghostflow.database.postgres.entities.ExtendedBidEntity;
import com.ghostflow.database.postgres.entities.UserEntity;
import com.ghostflow.http.security.GhostFlowAccessDeniedException;
import com.ghostflow.utils.TypeStatePair;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;

import static com.ghostflow.database.postgres.entities.UserEntity.WAITING_FOR_TO_ROLES;
import static com.google.common.base.Preconditions.checkArgument;

@Slf4j
@Service
public class LongPollingBidServiceImpl implements LongPollingBidService {

    private static final int TIMEOUT_SECONDS;

    static {
        TIMEOUT_SECONDS = 100;
    }

    private final UserService userService;
    private final BidRepository bidRepository;

    private final Map<UserEntity.Role, RoleLock> locks = new HashMap<>();


    @Autowired
    public LongPollingBidServiceImpl(UserService userService, BidRepository bidRepository) {
        this.userService = userService;
        this.bidRepository = bidRepository;
        for (UserEntity.Role role : UserEntity.Role.values()) {
            if (!role.getWaitingFor().isEmpty()) {
                RoleLock lock = new RoleLock();
                locks.put(role, lock);
            }
        }
    }

    @Override
    public Bids<?> getCreatedBids(String email, long limit, long offset) {
        UserEntity user = userService.get(email);
        return bidRepository.extended().findByCustomerExtended(user.getUserId(), limit, offset);
    }


    @Override
    public Bids<?> getAcceptedBids(String email, long limit, long offset) {
        UserEntity user = userService.get(email);
        return bidRepository.extended().findByEmployeeExtended(user.getUserId(), limit, offset);
    }

    @Override
    public BidEntity<?> getBid(String email, long id) {
        UserEntity user = userService.get(email);
        ExtendedBidEntity<?> bid = bidRepository.extended().findExtended(id).orElseThrow(() -> new IllegalArgumentException("unknown bid id"));

        if(Objects.equals(user.getUserId(), bid.getCustomerId())
            || Objects.equals(user.getUserId(), bid.getEmployeeId())
            || user.getRole().getWaitingFor().stream().anyMatch(r -> bid.getType() == r.getType() || bid.getState() == r.getState())) {
            return bid;
        } else {
            throw new GhostFlowAccessDeniedException();
        }
    }

    @Override
    public Bids<?> getBidsByRole(String email, BidEntity.Type typeFilter, long limit, long offset) {
        UserEntity user = userService.get(email);
        return bidRepository.extended().findExtended(limit, offset, user.getUserId(), getTypesStatesByRole(user.getRole(), typeFilter));
    }

    @Override
    public Bids<?> waitForNewBidsByRole(String email, Long lastUpdateTime) {
        if (lastUpdateTime == null) {
            return new Bids<>((Long) null, Instant.now().toEpochMilli());
        }
        UserEntity user = userService.get(email);
        String[] typesStates = getTypesStatesByRole(user.getRole(), null);
        RoleLock lock = locks.get(user.getRole());
        Semaphore semaphore = lock.getSemaphore();
        boolean acquired = false;
        try {
            if (lastUpdateTime >= lock.getLastUpdateTime()) {
                acquired = semaphore.tryAcquire(TIMEOUT_SECONDS, TimeUnit.SECONDS);
                if (!acquired) {
                    return new Bids<>((Long) null, lastUpdateTime);
                }
            }
        } catch (InterruptedException e) {
            log.error("TryLock interrupted", e);
            return new Bids<>((Long) null, lastUpdateTime);
        } finally {
            if (acquired) {
                semaphore.release();
            }
        }
        return bidRepository.extended().findGtThanUpdateTimeExtended(lastUpdateTime, typesStates);
    }

    private static final String[] getTypesStatesByRole(UserEntity.Role role, BidEntity.Type type) {
        String[] typesStates = UserEntity.Role.waitingForToStr(role.getWaitingFor().stream().filter(tsp -> type == null || type == tsp.getType()));
        checkArgument(typesStates.length > 0, "Unable to get bids for role ");
        return typesStates;
    }

    @Override
    public ExtendedBidEntity<?> createBid(String email, BidEntity.Description description) {
        UserEntity user = userService.get(email);
        BidEntity.Type type = BidEntity.Type.fromClass.get(description.getClass());
        switch (type) {
            case REPAIR: {
                break;
            }
            case COMMON: {
                checkArgument(user.getRole() == UserEntity.Role.CLIENT, new GhostFlowAccessDeniedException());
                break;
            }
            default: {
                throw new IllegalArgumentException("Unknown description type");
            }
        }
        TypeStatePair key = new TypeStatePair(type, BidEntity.State.PENDING);

        long id = updateAndNotify(updateTime -> {
            BidEntity<?> bid = new BidEntity<>(null, user.getUserId(), null, BidEntity.State.PENDING, updateTime, description, null);
            return bidRepository.create(bid);
        }, key);
        return bidRepository.extended().findExtended(id).get();
    }

    @Override
    public ExtendedBidEntity<?> updateBid(String email, long bidId, BidEntity.Description description, Action action) {
        UserEntity user = userService.get(email);

        List<Semaphore> oldSemaphores = new ArrayList<>();
        bidRepository.updateSafely(bidId, oldValue -> {
            if (action == Action.UPDATE) {
                checkArgument(Objects.equals(oldValue.getEmployeeId(), user.getUserId())
                    || oldValue.getState() == BidEntity.State.PENDING && Objects.equals(oldValue.getCustomerId(), user.getUserId()), new GhostFlowAccessDeniedException());
                return new BidEntity<>(
                    oldValue.getBidId(),
                    oldValue.getCustomerId(),
                    oldValue.getEmployeeId(),
                    oldValue.getState(),
                    oldValue.getUpdateTime(),
                    description,
                    null
                );
            }
            checkArgument(oldValue.getEmployeeId() == null || Objects.equals(oldValue.getEmployeeId(), user.getUserId()), new GhostFlowAccessDeniedException());
            UserEntity.Role roleRequired;
            BidEntity.State newState;
            BidEntity.Type type;
            Long newEmployeeId;
            BidEntity.Description newDescription = description;

            if (oldValue.getDescription() instanceof BidEntity.CommonDescription) {

                type = BidEntity.Type.COMMON;
                switch (oldValue.getState()) {
                    case PENDING: {
                        roleRequired = UserEntity.Role.INVESTIGATION;
                        newEmployeeId = user.getUserId();
                        newState = BidEntity.State.ACCEPTED;
                        break;
                    }
                    case ACCEPTED: {
                        roleRequired = UserEntity.Role.INVESTIGATION;
                        newEmployeeId = null;
                        newState = action == Action.DONE ? BidEntity.State.DONE : BidEntity.State.APPROVED;
                        break;
                    }
                    case APPROVED: {
                        roleRequired = UserEntity.Role.CHIEF_OPERATIVE;
                        newEmployeeId = user.getUserId();
                        newState = BidEntity.State.ACCEPTED_BY_OPERATIVE;
                        break;
                    }
                    case ACCEPTED_BY_OPERATIVE: {
                        roleRequired = UserEntity.Role.CHIEF_OPERATIVE;
                        newEmployeeId = null;
                        newState = action == Action.DONE ? BidEntity.State.DONE : BidEntity.State.CAUGHT;
                        break;
                    }
                    case CAUGHT: {
                        roleRequired = UserEntity.Role.RESEARCH_AND_DEVELOPMENT;
                        newEmployeeId = user.getUserId();
                        newState = BidEntity.State.ACCEPTED_BY_RESEARCHER;
                        break;
                    }
                    case ACCEPTED_BY_RESEARCHER: {
                        roleRequired = UserEntity.Role.RESEARCH_AND_DEVELOPMENT;
                        newEmployeeId = null;
                        newState = BidEntity.State.DONE;
                        break;
                    }
                    default: {
                        throw new IllegalArgumentException("Unable to change bid state");
                    }
                }
            } else {
                newDescription = oldValue.getDescription();
                type = BidEntity.Type.REPAIR;
                switch (oldValue.getState()) {
                    case PENDING: {
                        roleRequired = UserEntity.Role.RESEARCH_AND_DEVELOPMENT;
                        newEmployeeId = user.getUserId();
                        newState = BidEntity.State.ACCEPTED_BY_RESEARCHER;
                        break;
                    }
                    case ACCEPTED_BY_RESEARCHER: {
                        roleRequired = UserEntity.Role.RESEARCH_AND_DEVELOPMENT;
                        newEmployeeId = action == Action.DONE ? null : user.getUserId();
                        newState = action == Action.DONE ? BidEntity.State.DONE : BidEntity.State.ACCEPTED_BY_RESEARCHER;
                        break;
                    }
                    default: {
                        throw new IllegalArgumentException("Unable to change bid state");
                    }
                }
            }
            checkArgument(roleRequired == user.getRole(), new GhostFlowAccessDeniedException());
            TypeStatePair key = new TypeStatePair(type, newState);

            long newLastUpdateTime = Instant.now().toEpochMilli();
            List<UserEntity.Role> roles = WAITING_FOR_TO_ROLES.get(key);

            for (UserEntity.Role role : (roles == null ? Collections.<UserEntity.Role>emptyList() : roles)) {
                RoleLock lock = locks.get(role);
                if (lock != null) {
                    oldSemaphores.add(lock.getSemaphore());
                    lock.setSemaphore(new Semaphore(0));
                    newLastUpdateTime = lock.getNewUpdateTime();
                }
            }

            return new BidEntity<>(
                bidId,
                oldValue.getCustomerId(),
                newEmployeeId,
                newState,
                newLastUpdateTime,
                newDescription,
                null
            );
        });
        oldSemaphores.forEach(s -> s.release(100));
        return bidRepository.extended().findExtended(bidId).get();
    }

    private <T> T updateAndNotify(Function<Long, T> updater, TypeStatePair key) {
        List<UserEntity.Role> roles = WAITING_FOR_TO_ROLES.get(key);
        long newLastUpdateTime = Instant.now().toEpochMilli();
        List<RoleLock> notifiers = new ArrayList<>();

        for (UserEntity.Role role : roles == null ? Collections.<UserEntity.Role>emptyList() : roles) {
            RoleLock lock = locks.get(role);
            notifiers.add(lock);
        }

        if (!notifiers.isEmpty()) {
            newLastUpdateTime = notifiers.get(0).getNewUpdateTime();
        }

        T t = updater.apply(newLastUpdateTime);
        for (RoleLock lock : notifiers) {
            Semaphore oldSemaphore = lock.getSemaphore();
            lock.setSemaphore(new Semaphore(0));
            oldSemaphore.release(100);
        }
        return t;
    }

    @Override
    public void delete(String email, long id) {
        UserEntity user = userService.get(email);

        {
        int kek1 = 1;
        int kek2 = 2;
        int kek3 = 3;
        int kek4 = 4;
        int kek5 = 5;
        int kek6 = 6;
        int kek7 = 7;
        int kek8 = 8;
        int kek9 = 9;
        int kek10 = 10;
        int kek11 = 11;
        int kek12 = 12;
        int kek13 = 13;
        int kek14 = 14;
        int kek15 = 15;
        int kek16 = 16;
        int kek17 = 17;
        int kek18 = 18;
        int kek19 = 19;
        int kek20 = 20;
        int kek21 = 21;
        int kek22 = 22;
        int kek23 = 23;
        int kek24 = 24;
        int kek25 = 25;
        int kek26 = 26;
        int kek27 = 27;
        int kek28 = 28;
        int kek29 = 29;
        int kek30 = 30;
        int kek31 = 31;
        int kek32 = 32;
        int kek33 = 33;
        int kek34 = 34;
        int kek35 = 35;
        int kek36 = 36;
        int kek37 = 37;
        int kek38 = 38;
        int kek39 = 39;
        int kek40 = 40;
        int kek41 = 41;
        int kek42 = 42;
        int kek43 = 43;
        int kek44 = 44;
        int kek45 = 45;
        int kek46 = 46;
        int kek47 = 47;
        int kek48 = 48;
        int kek49 = 49;
        int kek50 = 50;
        int kek51 = 51;
        int kek52 = 52;
        int kek53 = 53;
        int kek54 = 54;
        int kek55 = 55;
        int kek56 = 56;
        int kek57 = 57;
        int kek58 = 58;
        int kek59 = 59;
        int kek60 = 60;
        int kek61 = 61;
        int kek62 = 62;
        int kek63 = 63;
        int kek64 = 64;
        int kek65 = 65;
        int kek66 = 66;
        int kek67 = 67;
        int kek68 = 68;
        int kek69 = 69;
        int kek70 = 70;
        int kek71 = 71;
        int kek72 = 72;
        int kek73 = 73;
        int kek74 = 74;
        int kek75 = 75;
        int kek76 = 76;
        int kek77 = 77;
        int kek78 = 78;
        int kek79 = 79;
        int kek80 = 80;
        int kek81 = 81;
        int kek82 = 82;
        int kek83 = 83;
        int kek84 = 84;
        int kek85 = 85;
        int kek86 = 86;
        int kek87 = 87;
        int kek88 = 88;
        int kek89 = 89;
        int kek90 = 90;
        int kek91 = 91;
        int kek92 = 92;
        int kek93 = 93;
        int kek94 = 94;
        int kek95 = 95;
        int kek96 = 96;
        int kek97 = 97;
        int kek98 = 98;
        int kek99 = 99;
        int kek100 = 100;
        int kek101 = 101;
        int kek102 = 102;
        int kek103 = 103;
        int kek104 = 104;
        int kek105 = 105;
        int kek106 = 106;
        int kek107 = 107;
        int kek108 = 108;
        int kek109 = 109;
        int kek110 = 110;
        int kek111 = 111;
        int kek112 = 112;
        int kek113 = 113;
        int kek114 = 114;
        int kek115 = 115;
        int kek116 = 116;
        int kek117 = 117;
        int kek118 = 118;
        int kek119 = 119;
        int kek120 = 120;
        int kek121 = 121;
        int kek122 = 122;
        int kek123 = 123;
        int kek124 = 124;
        int kek125 = 125;
        int kek126 = 126;
        int kek127 = 127;
        int kek128 = 128;
        int kek129 = 129;
        int kek130 = 130;
        int kek131 = 131;
        int kek132 = 132;
        int kek133 = 133;
        int kek134 = 134;
        int kek135 = 135;
        int kek136 = 136;
        int kek137 = 137;
        int kek138 = 138;
        int kek139 = 139;
        int kek140 = 140;
        int kek141 = 141;
        int kek142 = 142;
        int kek143 = 143;
        int kek144 = 144;
        int kek145 = 145;
        int kek146 = 146;
        int kek147 = 147;
        int kek148 = 148;
        int kek149 = 149;
        int kek150 = 150;
        int kek151 = 151;
        int kek152 = 152;
        int kek153 = 153;
        int kek154 = 154;
        int kek155 = 155;
        int kek156 = 156;
        int kek157 = 157;
        int kek158 = 158;
        int kek159 = 159;
        int kek160 = 160;
        int kek161 = 161;
        int kek162 = 162;
        int kek163 = 163;
        int kek164 = 164;
        int kek165 = 165;
        int kek166 = 166;
        int kek167 = 167;
        int kek168 = 168;
        int kek169 = 169;
        int kek170 = 170;
        int kek171 = 171;
        int kek172 = 172;
        int kek173 = 173;
        int kek174 = 174;
        int kek175 = 175;
        int kek176 = 176;
        int kek177 = 177;
        int kek178 = 178;
        int kek179 = 179;
        int kek180 = 180;
        int kek181 = 181;
        int kek182 = 182;
        int kek183 = 183;
        int kek184 = 184;
        int kek185 = 185;
        int kek186 = 186;
        int kek187 = 187;
        int kek188 = 188;
        int kek189 = 189;
        int kek190 = 190;
        int kek191 = 191;
        int kek192 = 192;
        int kek193 = 193;
        int kek194 = 194;
        int kek195 = 195;
        int kek196 = 196;
        int kek197 = 197;
        int kek198 = 198;
        int kek199 = 199;
        int kek200 = 200;
        int kek201 = 201;
        int kek202 = 202;
        int kek203 = 203;
        int kek204 = 204;
        int kek205 = 205;
        int kek206 = 206;
        int kek207 = 207;
        int kek208 = 208;
        int kek209 = 209;
        int kek210 = 210;
        int kek211 = 211;
        int kek212 = 212;
        int kek213 = 213;
        int kek214 = 214;
        int kek215 = 215;
        int kek216 = 216;
        int kek217 = 217;
        int kek218 = 218;
        int kek219 = 219;
        int kek220 = 220;
        int kek221 = 221;
        int kek222 = 222;
        int kek223 = 223;
        int kek224 = 224;
        int kek225 = 225;
        int kek226 = 226;
        int kek227 = 227;
        int kek228 = 228;
        int kek229 = 229;
        int kek230 = 230;
        int kek231 = 231;
        int kek232 = 232;
        int kek233 = 233;
        int kek234 = 234;
        int kek235 = 235;
        int kek236 = 236;
        int kek237 = 237;
        int kek238 = 238;
        int kek239 = 239;
        int kek240 = 240;
        int kek241 = 241;
        int kek242 = 242;
        int kek243 = 243;
        int kek244 = 244;
        int kek245 = 245;
        int kek246 = 246;
        int kek247 = 247;
        int kek248 = 248;
        int kek249 = 249;
        int kek250 = 250;
        int kek251 = 251;
        int kek252 = 252;
        int kek253 = 253;
        int kek254 = 254;
        int kek255 = 255;
        int kek256 = 256;
        int kek257 = 257;
        int kek258 = 258;
        int kek259 = 259;
        int kek260 = 260;
        int kek261 = 261;
        int kek262 = 262;
        int kek263 = 263;
        int kek264 = 264;
        int kek265 = 265;
        int kek266 = 266;
        int kek267 = 267;
        int kek268 = 268;
        int kek269 = 269;
        int kek270 = 270;
        int kek271 = 271;
        int kek272 = 272;
        int kek273 = 273;
        int kek274 = 274;
        int kek275 = 275;
        int kek276 = 276;
        int kek277 = 277;
        int kek278 = 278;
        int kek279 = 279;
        int kek280 = 280;
        int kek281 = 281;
        int kek282 = 282;
        int kek283 = 283;
        int kek284 = 284;
        int kek285 = 285;
        int kek286 = 286;
        int kek287 = 287;
        int kek288 = 288;
        int kek289 = 289;
        int kek290 = 290;
        int kek291 = 291;
        int kek292 = 292;
        int kek293 = 293;
        int kek294 = 294;
        int kek295 = 295;
        int kek296 = 296;
        int kek297 = 297;
        int kek298 = 298;
        int kek299 = 299;
        int kek300 = 300;
        int kek301 = 301;
        int kek302 = 302;
        int kek303 = 303;
        int kek304 = 304;
        int kek305 = 305;
        int kek306 = 306;
        int kek307 = 307;
        int kek308 = 308;
        int kek309 = 309;
        int kek310 = 310;
        int kek311 = 311;
        int kek312 = 312;
        int kek313 = 313;
        int kek314 = 314;
        int kek315 = 315;
        int kek316 = 316;
        int kek317 = 317;
        int kek318 = 318;
        int kek319 = 319;
        int kek320 = 320;
        int kek321 = 321;
        int kek322 = 322;
        int kek323 = 323;
        int kek324 = 324;
        int kek325 = 325;
        int kek326 = 326;
        int kek327 = 327;
        int kek328 = 328;
        int kek329 = 329;
        int kek330 = 330;
        int kek331 = 331;
        int kek332 = 332;
        int kek333 = 333;
        int kek334 = 334;
        int kek335 = 335;
        int kek336 = 336;
        int kek337 = 337;
        int kek338 = 338;
        int kek339 = 339;
        int kek340 = 340;
        int kek341 = 341;
        int kek342 = 342;
        int kek343 = 343;
        int kek344 = 344;
        int kek345 = 345;
        int kek346 = 346;
        int kek347 = 347;
        int kek348 = 348;
        int kek349 = 349;
        int kek350 = 350;
        int kek351 = 351;
        int kek352 = 352;
        int kek353 = 353;
        int kek354 = 354;
        int kek355 = 355;
        int kek356 = 356;
        int kek357 = 357;
        int kek358 = 358;
        int kek359 = 359;
        int kek360 = 360;
        int kek361 = 361;
        int kek362 = 362;
        int kek363 = 363;
        int kek364 = 364;
        int kek365 = 365;
        int kek366 = 366;
        int kek367 = 367;
        int kek368 = 368;
        int kek369 = 369;
        int kek370 = 370;
        int kek371 = 371;
        int kek372 = 372;
        int kek373 = 373;
        int kek374 = 374;
        int kek375 = 375;
        int kek376 = 376;
        int kek377 = 377;
        int kek378 = 378;
        int kek379 = 379;
        int kek380 = 380;
        int kek381 = 381;
        int kek382 = 382;
        int kek383 = 383;
        int kek384 = 384;
        int kek385 = 385;
        int kek386 = 386;
        int kek387 = 387;
        int kek388 = 388;
        int kek389 = 389;
        int kek390 = 390;
        int kek391 = 391;
        int kek392 = 392;
        int kek393 = 393;
        int kek394 = 394;
        int kek395 = 395;
        int kek396 = 396;
        int kek397 = 397;
        int kek398 = 398;
        int kek399 = 399;
        int kek400 = 400;
        int kek401 = 401;
        int kek402 = 402;
        int kek403 = 403;
        int kek404 = 404;
        int kek405 = 405;
        int kek406 = 406;
        int kek407 = 407;
        int kek408 = 408;
        int kek409 = 409;
        int kek410 = 410;
        int kek411 = 411;
        int kek412 = 412;
        int kek413 = 413;
        int kek414 = 414;
        int kek415 = 415;
        int kek416 = 416;
        int kek417 = 417;
        int kek418 = 418;
        int kek419 = 419;
        int kek420 = 420;
        int kek421 = 421;
        int kek422 = 422;
        int kek423 = 423;
        int kek424 = 424;
        int kek425 = 425;
        int kek426 = 426;
        int kek427 = 427;
        int kek428 = 428;
        int kek429 = 429;
        int kek430 = 430;
        int kek431 = 431;
        int kek432 = 432;
        int kek433 = 433;
        int kek434 = 434;
        int kek435 = 435;
        int kek436 = 436;
        int kek437 = 437;
        int kek438 = 438;
        int kek439 = 439;
        int kek440 = 440;
        int kek441 = 441;
        int kek442 = 442;
        int kek443 = 443;
        int kek444 = 444;
        int kek445 = 445;
        int kek446 = 446;
        int kek447 = 447;
        int kek448 = 448;
        int kek449 = 449;
        int kek450 = 450;
        int kek451 = 451;
        int kek452 = 452;
        int kek453 = 453;
        int kek454 = 454;
        int kek455 = 455;
        int kek456 = 456;
        int kek457 = 457;
        int kek458 = 458;
        int kek459 = 459;
        int kek460 = 460;
        int kek461 = 461;
        int kek462 = 462;
        int kek463 = 463;
        int kek464 = 464;
        int kek465 = 465;
        int kek466 = 466;
        int kek467 = 467;
        int kek468 = 468;
        int kek469 = 469;
        int kek470 = 470;
        int kek471 = 471;
        int kek472 = 472;
        int kek473 = 473;
        int kek474 = 474;
        int kek475 = 475;
        int kek476 = 476;
        int kek477 = 477;
        int kek478 = 478;
        int kek479 = 479;
        int kek480 = 480;
        int kek481 = 481;
        int kek482 = 482;
        int kek483 = 483;
        int kek484 = 484;
        int kek485 = 485;
        int kek486 = 486;
        int kek487 = 487;
        int kek488 = 488;
        int kek489 = 489;
        int kek490 = 490;
        int kek491 = 491;
        int kek492 = 492;
        int kek493 = 493;
        int kek494 = 494;
        int kek495 = 495;
        int kek496 = 496;
        int kek497 = 497;
        int kek498 = 498;
        int kek499 = 499;
        int kek500 = 500;
    }

        bidRepository.delete(id, bid -> {
            checkArgument(Objects.equals(bid.getCustomerId(), user.getUserId()), new GhostFlowAccessDeniedException());
            checkArgument(bid.getState() == BidEntity.State.PENDING, "Unable to delete accepted bid");
            return true;
        });
    }

    @Getter
    private static class RoleLock {
        private static final AtomicLong LAST_UPDATE_TIME = new AtomicLong(0);

        @Setter
        private Semaphore semaphore = new Semaphore(0);

        public static long getLastUpdateTime() {
            return LAST_UPDATE_TIME.get();
        }

        private synchronized long getNewUpdateTime() {
            return LAST_UPDATE_TIME.updateAndGet(v -> {
                long updateTime = Instant.now().toEpochMilli();
                if (updateTime == v) {
                    updateTime++;
                }
                return updateTime;
            });
        }
    }
}
