package com.koreanguide.koreanguidebackend.domain.track.service.Impl;

import com.koreanguide.koreanguidebackend.domain.auth.data.dto.response.BaseResponseDto;
import com.koreanguide.koreanguidebackend.domain.auth.data.entity.User;
import com.koreanguide.koreanguidebackend.domain.auth.data.repository.UserRepository;
import com.koreanguide.koreanguidebackend.domain.track.data.dto.request.TrackApplyRequestDto;
import com.koreanguide.koreanguidebackend.domain.track.data.dto.request.TrackImageApplyRequestDto;
import com.koreanguide.koreanguidebackend.domain.track.data.dto.request.TrackTagApplyRequestDto;
import com.koreanguide.koreanguidebackend.domain.track.data.dto.response.TrackResponseDto;
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
    public ResponseEntity<TrackResponseDto> getTrackById(Long userId, Long trackId) {
        Track track = trackRepository.getById(trackId);
        Optional<User> user = userRepository.findById(userId);

        if(user.isEmpty()) {
            return ResponseEntity.status(HttpStatus.OK).body(TrackResponseDto.builder()
                            .baseResponseDto(BaseResponseDto.builder()
                                    .success(false)
                                    .msg("사용자 정보를 확인할 수 없습니다.")
                                    .build())
                    .build());
        }

        if(!track.isVisible() && !track.getUser().equals(user.get())) {
            return ResponseEntity.status(HttpStatus.OK).body(TrackResponseDto.builder()
                            .baseResponseDto(BaseResponseDto.builder()
                                    .success(false)
                                    .msg("공개되지 않은 트랙입니다. 트랙 게시자만 열람할 수 있습니다.")
                                    .build())
                    .build());
        }

        if(track.isBlocked() && !track.getUser().equals(user.get())) {
            return ResponseEntity.status(HttpStatus.OK).body(TrackResponseDto.builder()
                            .baseResponseDto(BaseResponseDto.builder()
                                    .success(false)
                                    .msg("'" + track.getBlockedReason() + "'" + " 사유로 인해 더 이상 사용 및 열람할 수 없는 트랙입니다.")
                                    .build())
                    .build());
        }

        TrackResponseDto trackResponseDto = new TrackResponseDto();

        trackResponseDto.setBaseResponseDto(BaseResponseDto.builder()
                        .success(true)
                        .msg("정상 처리되었습니다.")
                .build());
        trackResponseDto.setTrackTitle(track.getTrackTitle());
        trackResponseDto.setTrackPreview(track.getTrackPreview());
        trackResponseDto.setPrimaryImageUrl(track.getPrimaryImageUrl());
        trackResponseDto.setImages(track.getTrackImages());
        trackResponseDto.setTags(track.getTrackTags());
        trackResponseDto.setName(track.getUser().getNickname());
        trackResponseDto.setEmail(track.getUser().getEmail());
        trackResponseDto.setVisible(track.isVisible());
        trackResponseDto.setBlocked(track.isBlocked());
        trackResponseDto.setBlockedReason(track.getBlockedReason());
        trackResponseDto.setStar(track.isStar());

        return ResponseEntity.status(HttpStatus.OK).body(trackResponseDto);
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
        track.setStar(false);

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

    @Override
    public ResponseEntity<BaseResponseDto> changeTracksVisible(Long userId, Long trackId) {
        Track track = trackRepository.getById(trackId);
        Optional<User> user = userRepository.findById(userId);

        if(user.isEmpty()) {
            throw new RuntimeException("사용자를 찾을 수 없음");
        }

        track.setVisible(!track.isVisible());
        trackRepository.save(track);

        return ResponseEntity.status(HttpStatus.OK).body(BaseResponseDto.builder()
                        .success(true)
                        .msg(track.isVisible() ? "공개" : "비공개" + " 상태로 변경되었습니다.")
                .build());
    }

    @Override
    public ResponseEntity<?> changeTrackStar(Long userId, Long trackId) {
        Optional<User> user = userRepository.findById(userId);

        if(user.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("사용자를 찾을 수 없음");
        }

        Optional<Track> selectedTrack = trackRepository.findById(trackId);

        if(selectedTrack.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("트랙을 찾을 수 없음");
        }

        if(!selectedTrack.get().getUser().equals(user.get())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("대표 사진 권한 부족");
        }

        List<Track> trackList = trackRepository.getAllByUser(user.get());

        for(Track track : trackList) {
            if(track.isStar()) {
                track.setStar(false);
            }

            trackRepository.save(track);
        }

        Track newTrack = selectedTrack.get();
        newTrack.setStar(true);
        trackRepository.save(newTrack);

        return ResponseEntity.status(HttpStatus.OK).body("정상 처리");
    }
}
