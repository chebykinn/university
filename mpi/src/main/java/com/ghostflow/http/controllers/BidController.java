package com.ghostflow.http.controllers;

import com.ghostflow.database.Bids;
import com.ghostflow.database.postgres.entities.BidEntity;
import com.ghostflow.services.Action;
import com.ghostflow.services.LongPollingBidService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;

import static com.ghostflow.http.beans.PaginationParameters.DEFAULT_LIMIT;
import static com.ghostflow.http.beans.PaginationParameters.DEFAULT_OFFSET;

@Slf4j
@Controller
@RequestMapping("/api/bids")
public class BidController {

    private final LongPollingBidService longPollingBidService;

    @Autowired
    public BidController(LongPollingBidService longPollingBidService) {
        this.longPollingBidService = longPollingBidService;
    }

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<Bids> get(Principal principal,
                                    @RequestParam(value = "limit", defaultValue = DEFAULT_LIMIT) long limit,
                                    @RequestParam(value = "offset", defaultValue = DEFAULT_OFFSET) long offset) {
        return ResponseEntity.ok(longPollingBidService.getBidsByRole(principal.getName(), limit, offset));
    }

    @RequestMapping(value = "/created", method = RequestMethod.GET)
    public ResponseEntity<Bids> getCreated(Principal principal,
                                           @RequestParam(value = "limit", defaultValue = DEFAULT_LIMIT) long limit,
                                           @RequestParam(value = "offset", defaultValue = DEFAULT_OFFSET) long offset) {
        return ResponseEntity.ok(longPollingBidService.getCreatedBids(principal.getName(), limit, offset));
    }

    @RequestMapping(value = "/wait", method = RequestMethod.GET)
    public ResponseEntity<Bids> getCreated(Principal principal, @RequestParam(value = "last_update_time") long lastUpdateTime) {
        return ResponseEntity.ok(longPollingBidService.waitForNewBidsByRole(principal.getName(), lastUpdateTime));
    }

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<BidEntity> create(Principal principal, @RequestBody BidEntity.Description description) {
        return ResponseEntity.ok(longPollingBidService.createBid(principal.getName(), description));
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public ResponseEntity<BidEntity> update(Principal principal, @PathVariable("id") long id, UpdateRequest updateRequest) {
        return ResponseEntity.ok(longPollingBidService.updateBid(principal.getName(), id, updateRequest.getDescription(), updateRequest.getAction()));
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<BidEntity> delete(Principal principal, @PathVariable("id") long id) {
        longPollingBidService.delete(principal.getName(), id);
        return ResponseEntity.ok().build();
    }

    @NoArgsConstructor(force = true)
    @AllArgsConstructor
    @Getter
    @Setter
    private static class UpdateRequest {
        private final BidEntity.Description description;
        private final Action action;
    }
}
