package com.koreanguide.koreanguidebackend.domain.track.service.Impl;

import com.koreanguide.koreanguidebackend.domain.auth.data.dao.UserDao;
import com.koreanguide.koreanguidebackend.domain.auth.data.entity.User;
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
import org.springframework.data.domain.PageRequest;
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
    private final TrackImageRepository trackImageRepository;
    private final TrackTagRepository trackTagRepository;
    private final TrackLikeRepository trackLikeRepository;
    private final PasswordEncoder passwordEncoder;
    private final ReviewRepository reviewRepository;
    private final UserDao userDao;

    @Autowired
    public TrackServiceImpl(TrackRepository trackRepository, TrackImageRepository trackImageRepository,
                            TrackTagRepository trackTagRepository, TrackLikeRepository trackLikeRepository,
                            PasswordEncoder passwordEncoder, ReviewRepository reviewRepository,
                            UserDao userDao) {
        this.trackRepository = trackRepository;
        this.trackImageRepository = trackImageRepository;
        this.trackTagRepository = trackTagRepository;
        this.trackLikeRepository = trackLikeRepository;
        this.passwordEncoder = passwordEncoder;
        this.reviewRepository = reviewRepository;
        this.userDao = userDao;
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

    @Override
    public ResponseEntity<?> getAllTrack(Long userId) {
        User user = userDao.getUserEntity(userId);

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
        User user = userDao.getUserEntity(userId);
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
        TrackInfoResponseDto trackInfoResponseDto = GET_TRACK(userDao.getUserEntity(userId),
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
        User user = userDao.getUserEntity(userId);

        Optional<Track> track = trackRepository.findById(trackRemoveRequestDto.getTrackId());

        if(track.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        if(!track.get().getUser().equals(user)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        if(!passwordEncoder.matches(trackRemoveRequestDto.getPassword(), user.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        List<TrackLike> trackLikeList = trackLikeRepository.findAllByTrack(track.get());
        trackLikeRepository.deleteAll(trackLikeList);

        List<TrackTag> trackTagList = trackTagRepository.findAllByTrack(track.get());
        trackTagRepository.deleteAll(trackTagList);

        List<TrackImage> trackImageList = trackImageRepository.findAllByTrack(track.get());
        trackImageRepository.deleteAll(trackImageList);

        List<Review> reviewList = reviewRepository.getAllByTrack(track.get());
        reviewRepository.deleteAll(reviewList);

        trackRepository.delete(track.get());

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @Override
    public ResponseEntity<?> setPrimaryTrack(Long userId, Long trackId) {
        User user = userDao.getUserEntity(userId);
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

    @Override
    public ResponseEntity<?> getTrackDeleteInfo(Long userId, Long trackId) {
        Track track = trackRepository.getById(trackId);
        List<Review> reviewList = reviewRepository.getAllByTrack(track);
        List<TrackLike> trackLikeList = trackLikeRepository.findAllByTrack(track);

        return ResponseEntity.status(HttpStatus.OK).body(TrackDeleteInfoResponseDto.builder()
                        .like((long) trackLikeList.size())
                        .view(track.getViewCount())
                        .review((long) reviewList.size())
                .build());
    }

    @Override
    public ResponseEntity<?> getTrackEditInfo(Long userId, Long trackId) {
        Track track = trackRepository.getById(trackId);

        TrackEditInfoResponseDto trackEditInfoResponseDto = new TrackEditInfoResponseDto();

        trackEditInfoResponseDto.setTrackId(track.getId());
        trackEditInfoResponseDto.setTitle(track.getTrackTitle());
        trackEditInfoResponseDto.setPrimaryImage(track.getPrimaryImageUrl());

        List<String> ADDITIONAL_IMAGE_LIST = new ArrayList<>();
        List<TrackImage> trackImageList = trackImageRepository.findAllByTrack(track);

        for(TrackImage trackImage : trackImageList) {
            ADDITIONAL_IMAGE_LIST.add(trackImage.getImageUrl());
        }

        trackEditInfoResponseDto.setAdditionalImage(ADDITIONAL_IMAGE_LIST);

        trackEditInfoResponseDto.setPreview(track.getTrackPreview());

        List<String> TAGS_LIST = new ArrayList<>();
        List<TrackTag> trackTagList = trackTagRepository.findAllByTrack(track);

        for(TrackTag trackTag : trackTagList) {
            TAGS_LIST.add(trackTag.getTagName());
        }

        trackEditInfoResponseDto.setTags(TAGS_LIST);
        trackEditInfoResponseDto.setContent(track.getTrackContent());
        trackEditInfoResponseDto.setVisible(track.isVisible());
        trackEditInfoResponseDto.setUseAutoTranslate(trackEditInfoResponseDto.isUseAutoTranslate());

        return ResponseEntity.status(HttpStatus.OK).body(trackEditInfoResponseDto);
    }

    @Override
    public ResponseEntity<?> getTrackInfo(Long userId, Long trackId) {
        Track track = trackRepository.getById(trackId);

        TrackResponseDto trackResponseDto = new TrackResponseDto();
        trackResponseDto.setTrackId(track.getId());
        trackResponseDto.setTitle(track.getTrackTitle());
        trackResponseDto.setPreview(track.getTrackPreview());

        List<String> TAGS_LIST = new ArrayList<>();
        List<TrackTag> trackTagList = trackTagRepository.findAllByTrack(track);

        for(TrackTag trackTag : trackTagList) {
            TAGS_LIST.add(trackTag.getTagName());
        }

        trackResponseDto.setTags(TAGS_LIST);

        List<String> ADDITIONAL_IMAGE_LIST = new ArrayList<>();
        List<TrackImage> trackImageList = trackImageRepository.findAllByTrack(track);

        for(TrackImage trackImage : trackImageList) {
            ADDITIONAL_IMAGE_LIST.add(trackImage.getImageUrl());
        }

        trackResponseDto.setAdditionalImage(ADDITIONAL_IMAGE_LIST);

        trackResponseDto.setContent(track.getTrackContent());

        List<TrackLike> trackLikeList = trackLikeRepository.findAllByTrack(track);
        trackResponseDto.setLike((long) trackLikeList.size());
        trackResponseDto.setView(track.getViewCount());

        return ResponseEntity.status(HttpStatus.OK).body(trackResponseDto);
    }

    @Override
    public ResponseEntity<?> getTopTrackUsedByMainPage() {
        PageRequest pageRequest = PageRequest.of(0, 3);
        List<Track> topTracks = trackRepository.findTop3ByOrderByLikesAndViewCountAndCreatedAtDesc(pageRequest);

        List<TopTrackResponseDto> topTrackResponseDtoList = new ArrayList<>();

        for(Track track : topTracks) {
            List<TrackTag> trackTagList = trackTagRepository.findAllByTrack(track);
            List<String> TAGS_LIST = new ArrayList<>();

            for(TrackTag trackTag : trackTagList) {
                TAGS_LIST.add(trackTag.getTagName());
            }

            List<TrackLike> trackLikeList = trackLikeRepository.findAllByTrack(track);

            topTrackResponseDtoList.add(TopTrackResponseDto.builder()
                            .trackId(track.getId())
                            .title(track.getTrackTitle())
                            .preview(track.getTrackPreview())
                            .profileUrl(track.getUser().getProfileUrl())
                            .nickname(track.getUser().getNickname())
                            .view(track.getViewCount())
                            .like((long) trackLikeList.size())
                            .tags(TAGS_LIST)
                    .build());
        }

        return ResponseEntity.status(HttpStatus.OK).body(topTrackResponseDtoList);
    }
}
