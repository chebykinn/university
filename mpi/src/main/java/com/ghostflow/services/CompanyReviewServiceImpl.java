package com.ghostflow.services;

import com.ghostflow.GhostFlowException;
import com.ghostflow.database.BidRepository;
import com.ghostflow.database.CompanyReviewRepository;
import com.ghostflow.database.CompanyReviews;
import com.ghostflow.database.postgres.entities.CompanyReviewEntity;
import com.ghostflow.database.postgres.entities.ExtendedBidEntity;
import com.ghostflow.database.postgres.entities.UserEntity;
import com.ghostflow.http.security.GhostFlowAccessDeniedException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static com.google.common.base.Preconditions.checkArgument;

@Slf4j
@Service("companyReviewService")
public class CompanyReviewServiceImpl implements CompanyReviewService {
    private final BidRepository bidRepository;
    private final UserService userService;
    private final CompanyReviewRepository companyReviewRepository;

    @Autowired
    public CompanyReviewServiceImpl(BidRepository bidRepository, UserService userService, CompanyReviewRepository companyReviewRepository) {
        this.bidRepository = bidRepository;
        this.userService = userService;
        this.companyReviewRepository = companyReviewRepository;
    }

    public CompanyReviewEntity upsert(String email, long bidId, int rating, String review, boolean isNew) {
        checkArgument(rating >= 1 && rating <= 10, "Rating must be between 1 and 10");
        checkArgument(review != null && !review.trim().isEmpty(), "Review field must be not empty");
        ExtendedBidEntity<?> bid = getBidChecked(email, bidId);
        Optional<CompanyReviewEntity> reviewEntity = companyReviewRepository.findByBidId(bid.getBidId());
        if (isNew) {
            checkArgument(!reviewEntity.isPresent(), new IllegalArgumentException("Review already exists"));
            return companyReviewRepository.create(new CompanyReviewEntity(userService.get(email).getUserId(), rating, review));
        } else {
            checkArgument(reviewEntity.isPresent(), new IllegalArgumentException("Unable to find company review by bid id"));
            return companyReviewRepository.update(reviewEntity.get().getCompanyReviewId(), rating, review)
                .orElseThrow(() -> new GhostFlowException(""));
        }
    }

    @Override
    public CompanyReviewEntity create(String email, long bidId, int rating, String review) {
        return upsert(email, bidId, rating, review, true);
    }

    @Override
    public CompanyReviewEntity update(String email, long bidId, int rating, String review) {
        return upsert(email, bidId, rating, review, false);
    }

    @Override
    public CompanyReviewEntity get(String email, long bidId) {
        ExtendedBidEntity bid = getBidChecked(email, bidId);
        return companyReviewRepository.findByBidId(bid.getBidId())
            .orElseThrow(() -> new IllegalArgumentException("Can't find review by user id"));
    }


    @Override
    public CompanyReviewEntity get(long companyReviewId) {
        return companyReviewRepository.find(companyReviewId)
            .orElseThrow(() -> new IllegalArgumentException("Can't find review by id"));
    }

    @Override
    public CompanyReviews all(int limit, int offset) {
        return companyReviewRepository.extended().all(limit, offset);
    }

    @Override
    public void delete(String email, long bidId) {
        CompanyReviewEntity review = get(email, bidId);
        companyReviewRepository.delete(review.getCompanyReviewId());
    }

    private ExtendedBidEntity getBidChecked(String email, long bidId) {
        UserEntity user = userService.get(email);
        ExtendedBidEntity bid = bidRepository.extended().findExtended(bidId)
            .orElseThrow(() -> new IllegalArgumentException("Unknown bid id"));
        checkArgument(user.getUserId().equals(bid.getCustomerId()), new GhostFlowAccessDeniedException());
        return bid;
    }
}
