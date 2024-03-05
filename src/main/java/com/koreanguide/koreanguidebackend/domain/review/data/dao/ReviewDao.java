package com.koreanguide.koreanguidebackend.domain.review.data.dao;

import com.koreanguide.koreanguidebackend.domain.review.data.entity.Review;
import com.koreanguide.koreanguidebackend.domain.track.data.entity.Track;

import java.util.List;

public interface ReviewDao {
    List<Review> getTrackAllReview(Track track);

    Review getReviewEntity(Long reviewId);

    void saveReviewEntity(Review review);
}
