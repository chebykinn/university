package com.ghostflow.services;

import com.ghostflow.GhostFlowException;
import com.ghostflow.database.SystemReviewRepository;
import com.ghostflow.database.postgres.entities.SystemReviewEntity;
import com.ghostflow.database.postgres.entities.UserEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static com.google.common.base.Preconditions.checkArgument;

@Slf4j
@Service("systemReviewService")
public class SystemReviewServiceImpl implements SystemReviewService {
    private final UserService userService;
    private final SystemReviewRepository systemReviewRepository;

    @Autowired
    public SystemReviewServiceImpl(UserService userService, SystemReviewRepository systemReviewRepository) {
        this.userService = userService;
        this.systemReviewRepository = systemReviewRepository;
    }

    public SystemReviewEntity upsert(String email, int rating, String review, boolean isNew) {
        checkArgument(rating >= 1 && rating <= 10, "Rating must be between 1 and 10");
        checkArgument(review != null && !review.trim().isEmpty(), "Review field must be not empty");
        UserEntity userEntity = userService.get(email);
        Optional<SystemReviewEntity> reviewEntity = systemReviewRepository.findByUserId(userEntity.getUserId());
        if (isNew) {
            checkArgument(!reviewEntity.isPresent(), new IllegalArgumentException("Review already exists"));
            return systemReviewRepository.create(new SystemReviewEntity(userEntity.getUserId(), rating, review));
        } else {
            checkArgument(reviewEntity.isPresent(), new IllegalArgumentException("Unable to find system review by user"));
            return systemReviewRepository.update(reviewEntity.get().getSystemReviewId(), rating, review)
                .orElseThrow(() -> new GhostFlowException(""));
        }
    }

    @Override
    public SystemReviewEntity create(String email, int rating, String review) {
        return upsert(email, rating, review, true);
    }

    @Override
    public SystemReviewEntity update(String email, int rating, String review) {
        return upsert(email, rating, review, false);
    }

    @Override
    public SystemReviewEntity get(String email) {
        UserEntity userEntity = userService.get(email);
        return systemReviewRepository.findByUserId(userEntity.getUserId())
            .orElseThrow(() -> new IllegalArgumentException("Can't find review by user id"));
    }

    @Override
    public void delete(String email) {
        SystemReviewEntity review = get(email);
        systemReviewRepository.delete(review.getSystemReviewId());
    }
}
