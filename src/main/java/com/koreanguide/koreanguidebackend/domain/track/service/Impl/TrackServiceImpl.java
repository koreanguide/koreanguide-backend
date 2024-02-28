package com.koreanguide.koreanguidebackend.domain.track.service.Impl;

import com.koreanguide.koreanguidebackend.domain.auth.data.entity.User;
import com.koreanguide.koreanguidebackend.domain.auth.data.repository.UserRepository;
import com.koreanguide.koreanguidebackend.domain.review.data.entity.Review;
import com.koreanguide.koreanguidebackend.domain.review.data.repository.ReviewRepository;
import com.koreanguide.koreanguidebackend.domain.track.data.dto.request.*;
import com.koreanguide.koreanguidebackend.domain.track.data.dto.response.*;
import com.koreanguide.koreanguidebackend.domain.track.data.entity.Track;
import com.koreanguide.koreanguidebackend.domain.track.data.entity.TrackImage;
import com.koreanguide.koreanguidebackend.domain.track.data.entity.TrackLike;
import com.koreanguide.koreanguidebackend.domain.track.data.entity.TrackTag;
import com.koreanguide.koreanguidebackend.domain.track.data.repository.TrackImageRepository;
import com.koreanguide.koreanguidebackend.domain.track.data.repository.TrackLikeRepository;
import com.koreanguide.koreanguidebackend.domain.track.data.repository.TrackRepository;
import com.koreanguide.koreanguidebackend.domain.track.data.repository.TrackTagRepository;
import com.koreanguide.koreanguidebackend.domain.track.service.TrackService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class TrackServiceImpl implements TrackService {
    private final TrackRepository trackRepository;
    private final UserRepository userRepository;
    private final TrackImageRepository trackImageRepository;
    private final TrackTagRepository trackTagRepository;
    private final TrackLikeRepository trackLikeRepository;
    private final PasswordEncoder passwordEncoder;
    private final ReviewRepository reviewRepository;

    @Autowired
    public TrackServiceImpl(TrackRepository trackRepository, UserRepository userRepository,
                            TrackImageRepository trackImageRepository, TrackTagRepository trackTagRepository,
                            TrackLikeRepository trackLikeRepository, PasswordEncoder passwordEncoder,
                            ReviewRepository reviewRepository) {
        this.trackRepository = trackRepository;
        this.userRepository = userRepository;
        this.trackImageRepository = trackImageRepository;
        this.trackTagRepository = trackTagRepository;
        this.trackLikeRepository = trackLikeRepository;
        this.passwordEncoder = passwordEncoder;
        this.reviewRepository = reviewRepository;
    }

    public TrackInfoResponseDto GET_TRACK(User user, Long trackId) {
        Optional<Track> track = trackRepository.findById(trackId);
        TrackInfoResponseDto trackInfoResponseDto = new TrackInfoResponseDto();

        if(track.isEmpty()) {
            throw new RuntimeException("트랙을 찾을 수 없음");
        }

        if(track.get().isBlocked()) {
            trackInfoResponseDto.setUseAble(false);
        } else {
            trackInfoResponseDto.setVisible(true);
        }

        trackInfoResponseDto.setAdmin(track.get().getUser().equals(user));
        trackInfoResponseDto.setVisible(track.get().isVisible());
        trackInfoResponseDto.setTrack(track.get());

        return trackInfoResponseDto;
    }

    public User GET_VALID_USER(Long userId) {
        Optional<User> user = userRepository.findById(userId);

        if(user.isEmpty()) {
            throw new RuntimeException("사용자를 찾을 수 없음");
        } else if(!user.get().isEnabled()) {
            throw new RuntimeException("활성 사용자가 아님");
        }

        return user.get();
    }

    public boolean VALIDATE_USER_OWN_TRACK(User user, Track track) {
        return track.getUser().equals(user);
    }

    @Override
    public ResponseEntity<?> getAllTrack(Long userId) {
        User user = GET_VALID_USER(userId);
        List<TrackMainResponseDto> trackMainResponseDtoList = new ArrayList<>();
        List<Track> trackList = trackRepository.getAllByUser(user);

        for(Track track : trackList) {
            List<String> tagList = new ArrayList<>();
            List<TrackTag> trackTagList = trackTagRepository.findAllByTrack(track);
            for(TrackTag trackTag : trackTagList) {
                tagList.add(trackTag.getTagName());
            }

            List<TrackLike> trackLikeList = trackLikeRepository.findAllByTrack(track);


            trackMainResponseDtoList.add(TrackMainResponseDto.builder()
                            .trackId(track.getId())
                            .trackTitle(track.getTrackTitle())
                            .trackPreview(track.getTrackPreview())
                            .primaryImageUrl(track.getPrimaryImageUrl())
                            .tags(tagList)
                            .view(track.getViewCount())
                            .like((long) trackLikeList.size())
                            .star(track.isStar())
                    .build());
        }

        return ResponseEntity.status(HttpStatus.OK).body(trackMainResponseDtoList);
    }

    @Override
    public ResponseEntity<?> applyTrack(Long userId, TrackApplyRequestDto trackApplyRequestDto) {
        User user = GET_VALID_USER(userId);
        LocalDateTime CURRENT_TIME = LocalDateTime.now();

        if(!trackApplyRequestDto.isAgreeTerms() && !trackApplyRequestDto.isAgreePublicTerms()
                && !trackApplyRequestDto.isAgreePrivacyPolicy()) {
            throw new RuntimeException("미동의 항목 존재");
        }

        Track track = Track.builder()
                .agreePublicTerms(trackApplyRequestDto.isAgreePublicTerms())
                .agreePublicTermsDt(CURRENT_TIME)
                .agreeTerms(trackApplyRequestDto.isAgreeTerms())
                .agreeTermsDt(CURRENT_TIME)
                .agreePrivacyPolicy(trackApplyRequestDto.isAgreePrivacyPolicy())
                .agreePrivacyPolicyDt(CURRENT_TIME)
                .trackTitle(trackApplyRequestDto.getTrackTitle())
                .trackContent(trackApplyRequestDto.getTrackContent())
                .trackPreview(trackApplyRequestDto.getTrackPreview())
                .primaryImageUrl(trackApplyRequestDto.getPrimaryImageUrl())
                .star(false)
                .user(user)
                .viewCount(0L)
                .autoTranslate(trackApplyRequestDto.isUseAutoTranslate())
                .visible(true)
                .useAble(true)
                .blocked(false)
                .createdAt(CURRENT_TIME)
                .updatedAt(CURRENT_TIME)
                .build();

        trackRepository.save(track);

        for(TrackTagApplyRequestDto trackTagApplyRequestDto : trackApplyRequestDto.getTags()) {
            TrackTag trackTag = TrackTag.builder()
                    .track(track)
                    .tagName(trackTagApplyRequestDto.getTagName())
                    .uploadedDt(CURRENT_TIME)
                    .build();

            trackTagRepository.save(trackTag);
        }

        for(TrackImageApplyRequestDto trackImageApplyRequestDto : trackApplyRequestDto.getImages()) {
            TrackImage trackImage = TrackImage.builder()
                    .imageUrl(trackImageApplyRequestDto.getImageUrl())
                    .useAble(true)
                    .uploadedDt(CURRENT_TIME)
                    .track(track)
                    .build();

            trackImageRepository.save(trackImage);
        }

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @Override
    public ResponseEntity<?> updateTrack(Long userId, TrackUpdateRequestDto trackUpdateRequestDto) {
        LocalDateTime CURRENT_TIME = LocalDateTime.now();
        TrackInfoResponseDto trackInfoResponseDto = GET_TRACK(GET_VALID_USER(userId),
                trackUpdateRequestDto.getTrackId());

        if(!trackInfoResponseDto.isAdmin()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Track track = trackInfoResponseDto.getTrack();

        List<TrackTag> trackTagList = trackTagRepository.findAllByTrack(track);
        trackTagRepository.deleteAll(trackTagList);

        List<TrackImage> trackImageList = trackImageRepository.findAllByTrack(track);
        trackImageRepository.deleteAll(trackImageList);

        track.setTrackTitle(trackUpdateRequestDto.getTrackTitle());
        track.setTrackContent(trackUpdateRequestDto.getTrackContent());
        track.setTrackPreview(trackUpdateRequestDto.getTrackPreview());
        track.setPrimaryImageUrl(trackUpdateRequestDto.getPrimaryImageUrl());
        track.setAutoTranslate(trackUpdateRequestDto.isUseAutoTranslate());
        track.setUpdatedAt(CURRENT_TIME);

        trackRepository.save(track);

        for(TrackTagApplyRequestDto trackTagApplyRequestDto : trackUpdateRequestDto.getTags()) {
            TrackTag trackTag = TrackTag.builder()
                    .track(track)
                    .tagName(trackTagApplyRequestDto.getTagName())
                    .uploadedDt(CURRENT_TIME)
                    .build();

            trackTagRepository.save(trackTag);
        }

        for(TrackImageApplyRequestDto trackImageApplyRequestDto : trackUpdateRequestDto.getImages()) {
            TrackImage trackImage = TrackImage.builder()
                    .imageUrl(trackImageApplyRequestDto.getImageUrl())
                    .useAble(true)
                    .uploadedDt(CURRENT_TIME)
                    .track(track)
                    .build();

            trackImageRepository.save(trackImage);
        }

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @Override
    public ResponseEntity<?> removeTrack(Long userId, TrackRemoveRequestDto trackRemoveRequestDto) {
        User user = GET_VALID_USER(userId);

        Optional<Track> track = trackRepository.findById(trackRemoveRequestDto.getTrackId());

        if(track.isEmpty()) {
            log.error("요호 트랙이 발견되지 않음");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        if(!track.get().getUser().equals(user)) {
            log.error("대상 트랙과 요청 사용자 미일치");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        if(!passwordEncoder.matches(trackRemoveRequestDto.getPassword(), user.getPassword())) {
            log.error("사용자 비밀번호 미일치");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        List<TrackLike> trackLikeList = trackLikeRepository.findAllByTrack(track.get());
        trackLikeRepository.deleteAll(trackLikeList);
        log.info("해당 트랙의 모든 관심 지수 삭제");

        List<TrackTag> trackTagList = trackTagRepository.findAllByTrack(track.get());
        trackTagRepository.deleteAll(trackTagList);
        log.info("해당 트랙의 모든 태그 삭제");

        List<TrackImage> trackImageList = trackImageRepository.findAllByTrack(track.get());
        trackImageRepository.deleteAll(trackImageList);
        log.info("해당 트랙의 모든 이미지 삭제");

        List<Review> reviewList = reviewRepository.getAllByTrack(track.get());
        reviewRepository.deleteAll(reviewList);
        log.info("해당 트랙의 모든 리뷰 삭제");

        trackRepository.delete(track.get());
        log.info("해당 트랙 삭제");

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @Override
    public ResponseEntity<?> setPrimaryTrack(Long userId, Long trackId) {
        User user = GET_VALID_USER(userId);
        List<Track> trackList = trackRepository.getAllByUser(user);

        for(Track track : trackList) {
            if(track.isStar()) {
                track.setStar(false);
                trackRepository.save(track);
            }
        }

        Track track = trackRepository.getById(trackId);
        track.setStar(true);

        trackRepository.save(track);

        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
