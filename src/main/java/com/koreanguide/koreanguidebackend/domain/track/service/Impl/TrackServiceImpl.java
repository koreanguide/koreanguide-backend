package com.koreanguide.koreanguidebackend.domain.track.service.Impl;

import com.koreanguide.koreanguidebackend.domain.auth.data.dto.response.BaseResponseDto;
import com.koreanguide.koreanguidebackend.domain.auth.data.entity.User;
import com.koreanguide.koreanguidebackend.domain.auth.data.repository.UserRepository;
import com.koreanguide.koreanguidebackend.domain.track.data.dto.request.TrackApplyRequestDto;
import com.koreanguide.koreanguidebackend.domain.track.data.dto.request.TrackImageApplyRequestDto;
import com.koreanguide.koreanguidebackend.domain.track.data.dto.request.TrackTagApplyRequestDto;
import com.koreanguide.koreanguidebackend.domain.track.data.entity.Track;
import com.koreanguide.koreanguidebackend.domain.track.data.entity.TrackImage;
import com.koreanguide.koreanguidebackend.domain.track.data.entity.TrackTag;
import com.koreanguide.koreanguidebackend.domain.track.data.repository.TrackRepository;
import com.koreanguide.koreanguidebackend.domain.track.service.TrackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class TrackServiceImpl implements TrackService {
    private final TrackRepository trackRepository;
    private final UserRepository userRepository;

    @Autowired
    public TrackServiceImpl(TrackRepository trackRepository, UserRepository userRepository) {
        this.trackRepository = trackRepository;
        this.userRepository = userRepository;
    }

    @Override
    public ResponseEntity<BaseResponseDto> applyTrack(Long userId, TrackApplyRequestDto trackApplyRequestDto) {
        Optional<User> user = userRepository.findById(userId);
        LocalDateTime CURRENT_TIME = LocalDateTime.now();

        if(user.isEmpty()) {
            throw new RuntimeException("사용자를 찾을 수 없음");
        }

        Track track = new Track();

        track.setAgreePublicTerms(trackApplyRequestDto.isAgreePublicTerms());
        track.setAgreePublicTermsDt(CURRENT_TIME);
        track.setAgreeTerms(trackApplyRequestDto.isAgreeTerms());
        track.setAgreeTermsDt(CURRENT_TIME);
        track.setAgreePrivacyPolicy(trackApplyRequestDto.isAgreePrivacyPolicy());
        track.setAgreePrivacyPolicyDt(CURRENT_TIME);
        track.setTrackTitle(trackApplyRequestDto.getTrackTitle());
        track.setTrackContent(trackApplyRequestDto.getTrackPreview());
        track.setTrackPreview(trackApplyRequestDto.getTrackPreview());
        track.setPrimaryImageUrl(trackApplyRequestDto.getPrimaryImageUrl());

        List<TrackTag> trackTagList = new ArrayList<>();

        for(TrackTagApplyRequestDto trackTagApplyRequestDto : trackApplyRequestDto.getTrackTagApplyRequestDtoList()) {
            trackTagList.add(TrackTag.builder()
                    .track(track)
                    .tagName(trackTagApplyRequestDto.getTagName())
                    .uploadedDt(CURRENT_TIME)
                    .build());
        }

        track.setTrackTags(trackTagList);

        List<TrackImage> trackImageList = new ArrayList<>();

        for(TrackImageApplyRequestDto trackImageApplyRequestDto : trackApplyRequestDto.getTrackImageApplyRequestDtoList()) {
            trackImageList.add(TrackImage.builder()
                            .imageUrl(trackImageApplyRequestDto.getImageUrl())
                            .useAble(true)
                            .uploadedDt(CURRENT_TIME)
                            .track(track)
                    .build());
        }

        track.setTrackImages(trackImageList);
        track.setUser(user.get());
        track.setVisible(trackApplyRequestDto.isVisible());
        track.setUseAble(true);
        track.setCreatedAt(CURRENT_TIME);
        track.setUpdatedAt(CURRENT_TIME);
        track.setBlocked(false);

        trackRepository.save(track);

        return ResponseEntity.status(HttpStatus.OK).body(BaseResponseDto.builder()
                        .success(true)
                        .msg("트랙 생성이 완료되었습니다.")
                .build());
    }
}
