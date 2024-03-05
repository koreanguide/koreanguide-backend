package com.koreanguide.koreanguidebackend.domain.review.data.dao.Impl;

import com.koreanguide.koreanguidebackend.domain.review.data.dao.ReviewDao;
import com.koreanguide.koreanguidebackend.domain.review.data.entity.Review;
import com.koreanguide.koreanguidebackend.domain.review.data.repository.ReviewRepository;
import com.koreanguide.koreanguidebackend.domain.review.exception.ReviewNotFoundException;
import com.koreanguide.koreanguidebackend.domain.track.data.entity.Track;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class ReviewDaoImpl implements ReviewDao {
    private final ReviewRepository reviewRepository;

    public ReviewDaoImpl(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    @Override
    public List<Review> getTrackAllReview(Track track) {
        return reviewRepository.getAllByTrack(track);
    }

    @Override
    public Review getReviewEntity(Long reviewId) {
        Optional<Review> review = reviewRepository.findById(reviewId);

        if(review.isEmpty()) {
            throw new ReviewNotFoundException();
        }

        return review.get();
    }

    @Override
    public void saveReviewEntity(Review review) {
        reviewRepository.save(review);
    }
}
