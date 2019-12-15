package com.ghostflow.database;

import com.ghostflow.database.postgres.entities.ExtendedCompanyReviewEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor(force = true)
@AllArgsConstructor
@Getter
public class CompanyReviews {
    private final List<ExtendedCompanyReviewEntity> reviews;
    private final long count;
}
