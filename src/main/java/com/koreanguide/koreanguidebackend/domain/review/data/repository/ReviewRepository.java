package com.koreanguide.koreanguidebackend.domain.review.data.repository;

import com.koreanguide.koreanguidebackend.domain.review.data.entity.Review;
import com.koreanguide.koreanguidebackend.domain.track.data.entity.Track;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> getAllByTrack(Track track);
}
