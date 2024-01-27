package com.koreanguide.koreanguidebackend.domain.review.service.Impl;

import com.koreanguide.koreanguidebackend.domain.auth.data.entity.User;
import com.koreanguide.koreanguidebackend.domain.auth.data.repository.UserRepository;
import com.koreanguide.koreanguidebackend.domain.review.data.dto.request.ReviewCommentRequestDto;
import com.koreanguide.koreanguidebackend.domain.review.data.dto.request.ReviewRequestDto;
import com.koreanguide.koreanguidebackend.domain.review.data.dto.response.RecentReviewResponseDto;
import com.koreanguide.koreanguidebackend.domain.review.data.dto.response.ReviewResponseDto;
import com.koreanguide.koreanguidebackend.domain.review.data.entity.Review;
import com.koreanguide.koreanguidebackend.domain.review.data.repository.ReviewRepository;
import com.koreanguide.koreanguidebackend.domain.review.service.ReviewService;
import com.koreanguide.koreanguidebackend.domain.track.data.entity.Track;
import com.koreanguide.koreanguidebackend.domain.track.data.repository.TrackRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ReviewServiceImpl implements ReviewService {
    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final TrackRepository trackRepository;
    private final String USER_NOT_FOUND = "미등록 사용자";
    private final String TRACK_NOT_FOUND = "미등록 트랙";
    private final String REVIEW_NOT_FOUND = "미등록 리뷰";
    private final String UNAUTHORIZED_USER = "권한 부족";
    private final String SUCCESS_MSG = "처리 성공";

    @Autowired
    public ReviewServiceImpl(ReviewRepository reviewRepository, UserRepository userRepository,
                             TrackRepository trackRepository) {
        this.reviewRepository = reviewRepository;
        this.userRepository = userRepository;
        this.trackRepository = trackRepository;
    }

    @Override
    public ResponseEntity<?> getRecentReview(Long userId) {
        Optional<User> user = userRepository.findById(userId);

        if(user.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(USER_NOT_FOUND);
        }

        List<Track> track = trackRepository.getAllByUser(user.get());

        if(track.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        List<Review> reviewList = new ArrayList<>();

        for(Track temp : track) {
            List<Review> foundReviewViaTrack = reviewRepository.getAllByTrack(temp);
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
        Optional<User> user = userRepository.findById(userId);

        if(user.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(USER_NOT_FOUND);
        }

        List<Track> trackList = trackRepository.getAllByUser(user.get());
        List<ReviewResponseDto> reviewResponseDtoList = new ArrayList<>();

        for(Track track : trackList) {
            List<Review> reviewList = reviewRepository.getAllByTrack(track);

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
        }

        return ResponseEntity.status(HttpStatus.OK).body(reviewResponseDtoList);
    }

    @Override
    public ResponseEntity<?> getReview(Long trackId) {
        Optional<Track> track = trackRepository.findById(trackId);

        if(track.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(TRACK_NOT_FOUND);
        }

        List<Review> reviewList = reviewRepository.getAllByTrack(track.get());
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
        Optional<User> user = userRepository.findById(userId);

        if(user.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(USER_NOT_FOUND);
        }

        Optional<Track> track = trackRepository.findById(reviewRequestDto.getTargetTrackId());

        if(track.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(TRACK_NOT_FOUND);
        }

        reviewRepository.save(Review.builder()
                        .content(reviewRequestDto.getContent())
                        .user(user.get())
                        .useAble(true)
                        .track(track.get())
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                .build());

        return ResponseEntity.status(HttpStatus.OK).body(SUCCESS_MSG);
    }

    @Override
    public ResponseEntity<?> uploadReviewComment(Long userId, ReviewCommentRequestDto reviewCommentRequestDto) {
        Optional<User> user = userRepository.findById(userId);

        if(user.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(USER_NOT_FOUND);
        }

        Optional<Track> track = trackRepository.findById(reviewCommentRequestDto.getTargetTrackId());

        if(track.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(TRACK_NOT_FOUND);
        }

        if(!track.get().getUser().equals(user.get())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(UNAUTHORIZED_USER);
        }

        Optional<Review> review = reviewRepository.findById(reviewCommentRequestDto.getTargetReviewId());

        if(review.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(REVIEW_NOT_FOUND);
        }

        Review newReview = review.get();

        newReview.setComment(reviewCommentRequestDto.getContent());
        newReview.setUpdatedAt(LocalDateTime.now());

        reviewRepository.save(newReview);

        return ResponseEntity.status(HttpStatus.OK).body(SUCCESS_MSG);
    }

    @Override
    public ResponseEntity<?> deleteReview(Long userId, Long reviewId) {
        Optional<User> user = userRepository.findById(userId);

        if(user.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(USER_NOT_FOUND);
        }

        Optional<Review> review = reviewRepository.findById(reviewId);

        if(review.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(REVIEW_NOT_FOUND);
        }

        if(!review.get().getUser().equals(user.get())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(UNAUTHORIZED_USER);
        }

        Review updatedReview = review.get();

        updatedReview.setUseAble(false);

        reviewRepository.save(updatedReview);

        return ResponseEntity.status(HttpStatus.OK).body(SUCCESS_MSG);
    }
}
