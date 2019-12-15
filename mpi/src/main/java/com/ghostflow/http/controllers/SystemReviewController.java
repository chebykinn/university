package com.ghostflow.http.controllers;

import com.ghostflow.database.postgres.entities.SystemReviewEntity;
import com.ghostflow.http.beans.Review;
import com.ghostflow.services.SystemReviewService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.security.Principal;

@Slf4j
@Controller
@RequestMapping("/api/reviews/system")
public class SystemReviewController {

    private final SystemReviewService systemReviewService;

    @Autowired
    public SystemReviewController(SystemReviewService systemReviewService) {
        this.systemReviewService = systemReviewService;
    }

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<?> get(Principal principal) {
        return ResponseEntity.ok(systemReviewService.get(principal.getName()));
    }


    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<SystemReviewEntity> create(Principal principal, @RequestBody Review review) {
        return ResponseEntity.ok(systemReviewService.create(principal.getName(), review.getRating(), review.getReview()));
    }

    @RequestMapping(method = RequestMethod.PUT)
    public ResponseEntity<SystemReviewEntity> update(Principal principal, @RequestBody Review review) {
        return ResponseEntity.ok(systemReviewService.update(principal.getName(), review.getRating(), review.getReview()));
    }

    @RequestMapping(method = RequestMethod.DELETE)
    public ResponseEntity<?> delete(Principal principal) {
        systemReviewService.delete(principal.getName());
        return ResponseEntity.ok().build();
    }
}
