package com.koreanguide.koreanguidebackend.domain.review.service.Impl;

import com.koreanguide.koreanguidebackend.domain.auth.data.dao.UserDao;
import com.koreanguide.koreanguidebackend.domain.auth.data.entity.User;
import com.koreanguide.koreanguidebackend.domain.review.data.dao.ReviewDao;
import com.koreanguide.koreanguidebackend.domain.review.data.dto.request.ReviewCommentRequestDto;
import com.koreanguide.koreanguidebackend.domain.review.data.dto.request.ReviewRequestDto;
import com.koreanguide.koreanguidebackend.domain.review.data.dto.response.RecentReviewResponseDto;
import com.koreanguide.koreanguidebackend.domain.review.data.dto.response.ReviewResponseDto;
import com.koreanguide.koreanguidebackend.domain.review.data.entity.Review;
import com.koreanguide.koreanguidebackend.domain.review.service.ReviewService;
import com.koreanguide.koreanguidebackend.domain.track.data.dao.TrackDao;
import com.koreanguide.koreanguidebackend.domain.track.data.entity.Track;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReviewServiceImpl implements ReviewService {
    private final ReviewDao reviewDao;
    private final UserDao userDao;
    private final TrackDao trackDao;

    @Autowired
    public ReviewServiceImpl(ReviewDao reviewDao, UserDao userDao, TrackDao trackDao) {
        this.reviewDao = reviewDao;
        this.userDao = userDao;
        this.trackDao = trackDao;
    }

    @Override
    public ResponseEntity<?> getRecentReview(Long userId) {
        User user = userDao.getUserEntity(userId);
        List<Track> track = trackDao.getUserAllTrack(user);

        List<Review> reviewList = new ArrayList<>();

        for(Track temp : track) {
            List<Review> foundReviewViaTrack = reviewDao.getTrackAllReview(temp);
            reviewList.addAll(foundReviewViaTrack);
        }

        if(reviewList.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        List<Review> sortedReviewList = reviewList.stream()
                .sorted(Comparator.comparing(Review::getCreatedAt).reversed())
                .limit(2)
                .collect(Collectors.toList());

        List<RecentReviewResponseDto> recentReviewResponseDtoList = new ArrayList<>();

        for (Review review : sortedReviewList) {
            recentReviewResponseDtoList.add(RecentReviewResponseDto.builder()
                            .reviewContent(review.getContent())
                            .reviewUserName(review.getUser().getNickname())
                            .star(review.getStar())
                    .build());
        }

        return ResponseEntity.status(HttpStatus.OK).body(recentReviewResponseDtoList);
    }

    @Override
    public ResponseEntity<?> getAllReview(Long userId) {
        User user = userDao.getUserEntity(userId);
        List<Track> trackList = trackDao.getUserAllTrack(user);
        List<ReviewResponseDto> reviewResponseDtoList = new ArrayList<>();

        for(Track track : trackList) {
            List<Review> reviewList = reviewDao.getTrackAllReview(track);

            for(Review review : reviewList) {
                reviewResponseDtoList.add(ReviewResponseDto.builder()
                        .trackName(review.getTrack().getTrackTitle())
                        .trackDescription(review.getTrack().getTrackPreview())
                        .reviewUserProfileUrl(null)
                        .reviewUserName(review.getUser().getNickname())
                        .reviewUserRegion(review.getUser().getState().toString())
                        .reviewContent(review.getContent())
                        .comment(review.getComment().isEmpty())
                        .reviewCommentContent(review.getComment().isEmpty() ? null : review.getComment())
                        .build());
            }
        }

        return ResponseEntity.status(HttpStatus.OK).body(reviewResponseDtoList);
    }

    @Override
    public ResponseEntity<?> getReview(Long trackId) {
        Track track = trackDao.getTrackEntity(trackId);

        List<Review> reviewList = reviewDao.getTrackAllReview(track);
        List<ReviewResponseDto> reviewResponseDtoList = new ArrayList<>();

        for(Review review : reviewList) {
            reviewResponseDtoList.add(ReviewResponseDto.builder()
                            .trackName(review.getTrack().getTrackTitle())
                            .trackDescription(review.getTrack().getTrackPreview())
                            .reviewUserProfileUrl(null)
                            .reviewUserName(review.getUser().getNickname())
                            .reviewUserRegion(review.getUser().getState().toString())
                            .reviewContent(review.getContent())
                            .comment(!review.getComment().isEmpty())
                            .reviewCommentContent(review.getComment())
                    .build());
        }

        return ResponseEntity.status(HttpStatus.OK).body(reviewResponseDtoList);
    }

    @Override
    public ResponseEntity<?> uploadReview(Long userId, ReviewRequestDto reviewRequestDto) {
        User user = userDao.getUserEntity(userId);
        Track track = trackDao.getTrackEntity(reviewRequestDto.getTargetTrackId());
        LocalDateTime CURRENT_TIME = LocalDateTime.now();

        reviewDao.saveReviewEntity(Review.builder()
                        .content(reviewRequestDto.getContent())
                        .user(user)
                        .useAble(true)
                        .track(track)
                        .createdAt(CURRENT_TIME)
                        .updatedAt(CURRENT_TIME)
                .build());

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @Override
    public ResponseEntity<?> uploadReviewComment(Long userId, ReviewCommentRequestDto reviewCommentRequestDto) {
        User user = userDao.getUserEntity(userId);
        Track track = trackDao.getTrackEntity(reviewCommentRequestDto.getTargetTrackId());

        if(!track.getUser().equals(user)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Review review = reviewDao.getReviewEntity(reviewCommentRequestDto.getTargetReviewId());

        review.setComment(reviewCommentRequestDto.getContent());
        review.setUpdatedAt(LocalDateTime.now());

        reviewDao.saveReviewEntity(review);

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @Override
    public ResponseEntity<?> deleteReview(Long userId, Long reviewId) {
        User user = userDao.getUserEntity(userId);
        Review review = reviewDao.getReviewEntity(reviewId);

        if(!review.getUser().equals(user)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        review.setUseAble(false);

        reviewDao.saveReviewEntity(review);

        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
