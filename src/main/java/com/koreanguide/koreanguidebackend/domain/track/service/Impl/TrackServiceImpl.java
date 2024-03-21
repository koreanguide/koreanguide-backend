package com.koreanguide.koreanguidebackend.domain.track.service.Impl;

import com.koreanguide.koreanguidebackend.domain.auth.data.dao.UserDao;
import com.koreanguide.koreanguidebackend.domain.auth.data.entity.User;
import com.koreanguide.koreanguidebackend.domain.review.data.dao.ReviewDao;
import com.koreanguide.koreanguidebackend.domain.review.data.entity.Review;
import com.koreanguide.koreanguidebackend.domain.track.data.dao.TrackDao;
import com.koreanguide.koreanguidebackend.domain.track.data.dto.request.*;
import com.koreanguide.koreanguidebackend.domain.track.data.dto.response.*;
import com.koreanguide.koreanguidebackend.domain.track.data.entity.Track;
import com.koreanguide.koreanguidebackend.domain.track.data.entity.TrackImage;
import com.koreanguide.koreanguidebackend.domain.track.data.entity.TrackTag;
import com.koreanguide.koreanguidebackend.domain.track.service.TrackService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class TrackServiceImpl implements TrackService {
    private final TrackDao trackDao;
    private final ReviewDao reviewDao;
    private final UserDao userDao;

    @Autowired
    public TrackServiceImpl(ReviewDao reviewDao, UserDao userDao, TrackDao trackDao) {
        this.reviewDao = reviewDao;
        this.userDao = userDao;
        this.trackDao = trackDao;
    }

    @Override
    public ResponseEntity<?> getAllTrack(Long userId) {
        User user = userDao.getUserEntity(userId);

        List<TrackMainResponseDto> trackMainResponseDtoList = new ArrayList<>();
        List<Track> trackList = trackDao.getUserAllTrack(user);

        for(Track track : trackList) {
            List<String> tagList = new ArrayList<>();

            List<TrackTag> trackTagList = trackDao.getTrackTagEntityViaEntity(track);

            for(TrackTag trackTag : trackTagList) {
                tagList.add(trackTag.getTagName());
            }

            trackMainResponseDtoList.add(TrackMainResponseDto.builder()
                            .trackId(track.getId())
                            .trackTitle(track.getTrackTitle())
                            .trackPreview(track.getTrackPreview())
                            .primaryImageUrl(track.getPrimaryImageUrl())
                            .tags(tagList)
                            .view(track.getViewCount())
                            .like(trackDao.trackLikeCount(track))
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

        trackDao.saveTrack(track);
        trackDao.updateTrackTag(track, trackApplyRequestDto.getTags());
        trackDao.updateTrackImage(track, trackApplyRequestDto.getImages());

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @Override
    public ResponseEntity<?> updateTrack(Long userId, TrackUpdateRequestDto trackUpdateRequestDto) {
        LocalDateTime CURRENT_TIME = LocalDateTime.now();

        Track track = trackDao.getTrackEntity(trackUpdateRequestDto.getTrackId());
        track.setTrackTitle(trackUpdateRequestDto.getTrackTitle());
        track.setTrackContent(trackUpdateRequestDto.getTrackContent());
        track.setTrackPreview(trackUpdateRequestDto.getTrackPreview());
        track.setPrimaryImageUrl(trackUpdateRequestDto.getPrimaryImageUrl());
        track.setAutoTranslate(trackUpdateRequestDto.isUseAutoTranslate());
        track.setUpdatedAt(CURRENT_TIME);

        trackDao.saveTrack(track);
        trackDao.updateTrackTag(track, trackUpdateRequestDto.getTags());
        trackDao.updateTrackImage(track, trackUpdateRequestDto.getImages());

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @Override
    public ResponseEntity<?> removeTrack(Long userId, TrackRemoveRequestDto trackRemoveRequestDto) {
        User user = userDao.getUserEntity(userId);

        if(!userDao.checkPassword(user, trackRemoveRequestDto.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        trackDao.deleteTrack(trackRemoveRequestDto.getTrackId(), userId);

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @Override
    public ResponseEntity<?> setPrimaryTrack(Long userId, Long trackId) {
        User user = userDao.getUserEntity(userId);
        List<Track> trackList = trackDao.getUserAllTrack(user);

        for(Track track : trackList) {
            if(track.isStar()) {
                track.setStar(false);
                trackDao.saveTrack(track);
            }
        }

        Track track = trackDao.getTrackEntity(trackId);
        track.setStar(true);

        trackDao.saveTrack(track);

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @Override
    public ResponseEntity<?> getTrackDeleteInfo(Long userId, Long trackId) {
        Track track = trackDao.getTrackEntity(trackId);
        List<Review> reviewList = reviewDao.getTrackAllReview(track);

        return ResponseEntity.status(HttpStatus.OK).body(TrackDeleteInfoResponseDto.builder()
                        .like(trackDao.trackLikeCount(track))
                        .view(track.getViewCount())
                        .review((long) reviewList.size())
                .build());
    }

    @Override
    public ResponseEntity<?> getTrackEditInfo(Long userId, Long trackId) {
        Track track = trackDao.getTrackEntity(trackId);

        TrackEditInfoResponseDto trackEditInfoResponseDto = new TrackEditInfoResponseDto();

        trackEditInfoResponseDto.setTrackId(track.getId());
        trackEditInfoResponseDto.setTitle(track.getTrackTitle());
        trackEditInfoResponseDto.setPrimaryImage(track.getPrimaryImageUrl());

        List<String> ADDITIONAL_IMAGE_LIST = new ArrayList<>();
        List<TrackImage> trackImageList = trackDao.getTrackImageEntityViaEntity(track);

        for(TrackImage trackImage : trackImageList) {
            ADDITIONAL_IMAGE_LIST.add(trackImage.getImageUrl());
        }

        trackEditInfoResponseDto.setAdditionalImage(ADDITIONAL_IMAGE_LIST);

        trackEditInfoResponseDto.setPreview(track.getTrackPreview());

        List<String> TAGS_LIST = new ArrayList<>();
        List<TrackTag> trackTagList = trackDao.getTrackTagEntityViaEntity(track);

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
        Track track = trackDao.getTrackEntity(trackId);
        List<TrackTag> trackTagList = trackDao.getTrackTagEntityViaEntity(track);
        List<TrackImage> trackImageList = trackDao.getTrackImageEntityViaEntity(track);

        TrackResponseDto trackResponseDto = new TrackResponseDto();
        trackResponseDto.setTrackId(track.getId());
        trackResponseDto.setTitle(track.getTrackTitle());
        trackResponseDto.setPreview(track.getTrackPreview());
        trackResponseDto.setPrimaryImage(track.getPrimaryImageUrl());

        List<String> TAGS_LIST = new ArrayList<>();
        for(TrackTag trackTag : trackTagList) {
            TAGS_LIST.add(trackTag.getTagName());
        }

        List<String> ADDITIONAL_IMAGE_LIST = new ArrayList<>();
        for(TrackImage trackImage : trackImageList) {
            ADDITIONAL_IMAGE_LIST.add(trackImage.getImageUrl());
        }

        trackResponseDto.setTags(TAGS_LIST);
        trackResponseDto.setAdditionalImage(ADDITIONAL_IMAGE_LIST);
        trackResponseDto.setContent(track.getTrackContent());
        trackResponseDto.setLike(trackDao.trackLikeCount(track));
        trackResponseDto.setView(track.getViewCount());

        return ResponseEntity.status(HttpStatus.OK).body(trackResponseDto);
    }

    @Override
    public ResponseEntity<?> getTopTrackUsedByMainPage() {
        List<Track> topTracks = trackDao.getTopTrackList();
        List<TopTrackResponseDto> topTrackResponseDtoList = new ArrayList<>();

        for(Track track : topTracks) {
            List<TrackTag> trackTagList = trackDao.getTrackTagEntityViaEntity(track);
            List<String> TAGS_LIST = new ArrayList<>();

            for(TrackTag trackTag : trackTagList) {
                TAGS_LIST.add(trackTag.getTagName());
            }

            topTrackResponseDtoList.add(TopTrackResponseDto.builder()
                            .trackId(track.getId())
                            .title(track.getTrackTitle())
                            .preview(track.getTrackPreview())
                            .profileUrl(track.getUser().getProfileUrl())
                            .nickname(track.getUser().getNickname())
                            .view(track.getViewCount())
                            .like(trackDao.trackLikeCount(track))
                            .tags(TAGS_LIST)
                    .build());
        }

        return ResponseEntity.status(HttpStatus.OK).body(topTrackResponseDtoList);
    }
}
