package com.ghostflow.http.controllers;

import com.ghostflow.database.CompanyReviews;
import com.ghostflow.database.postgres.entities.CompanyReviewEntity;
import com.ghostflow.http.beans.Review;
import com.ghostflow.services.CompanyReviewService;
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
@RequestMapping("/api")
public class CompanyReviewController {

    private final CompanyReviewService companyReviewService;

    @Autowired
    public CompanyReviewController(CompanyReviewService companyReviewService) {
        this.companyReviewService = companyReviewService;
    }

    @RequestMapping(path = "/bids/{bidId}/review", method = RequestMethod.GET)
    public ResponseEntity<?> get(Principal principal, @PathVariable("bidId") long bidId) {
        return ResponseEntity.ok(companyReviewService.get(principal.getName(), bidId));
    }

    @RequestMapping(path = "/bids/{bidId}/review", method = RequestMethod.POST)
    public ResponseEntity<CompanyReviewEntity> create(Principal principal, @PathVariable("bidId") long bidId, @RequestBody Review review) {
        return ResponseEntity.ok(companyReviewService.create(principal.getName(), bidId, review.getRating(), review.getReview()));
    }

    @RequestMapping(path = "/bids/{bidId}/review", method = RequestMethod.PUT)
    public ResponseEntity<CompanyReviewEntity> update(Principal principal, @PathVariable("bidId") long bidId, @RequestBody Review review) {
        return ResponseEntity.ok(companyReviewService.update(principal.getName(), bidId, review.getRating(), review.getReview()));
    }

    @RequestMapping(path = "/bids/{bidId}/review", method = RequestMethod.DELETE)
    public ResponseEntity<CompanyReviewEntity> delete(Principal principal, @PathVariable("bidId") long bidId) {
        companyReviewService.delete(principal.getName(), bidId);
        return ResponseEntity.ok().build();
    }


    @RequestMapping(path = "/reviews/{companyReviewId}", method = RequestMethod.GET)
    public ResponseEntity<CompanyReviewEntity> get(@PathVariable("companyReviewId") long companyReviewId) {
        return ResponseEntity.ok(companyReviewService.get(companyReviewId));
    }

    @RequestMapping(path = "/reviews", method = RequestMethod.GET)
    public ResponseEntity<CompanyReviews> get(@RequestParam(value = "limit", defaultValue = DEFAULT_LIMIT) int limit,
                                              @RequestParam(value = "offset", defaultValue = DEFAULT_OFFSET) int offset) {
        return ResponseEntity.ok(companyReviewService.all(offset, limit));
    }
}
