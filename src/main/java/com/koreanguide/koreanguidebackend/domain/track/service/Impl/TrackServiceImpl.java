package com.koreanguide.koreanguidebackend.domain.track.service.Impl;

import com.koreanguide.koreanguidebackend.domain.auth.data.dto.response.BaseResponseDto;
import com.koreanguide.koreanguidebackend.domain.auth.data.entity.User;
import com.koreanguide.koreanguidebackend.domain.auth.data.repository.UserRepository;
import com.koreanguide.koreanguidebackend.domain.track.data.dto.request.*;
import com.koreanguide.koreanguidebackend.domain.track.data.dto.response.*;
import com.koreanguide.koreanguidebackend.domain.track.data.entity.Track;
import com.koreanguide.koreanguidebackend.domain.track.data.entity.TrackImage;
import com.koreanguide.koreanguidebackend.domain.track.data.entity.TrackTag;
import com.koreanguide.koreanguidebackend.domain.track.data.repository.TrackImageRepository;
import com.koreanguide.koreanguidebackend.domain.track.data.repository.TrackRepository;
import com.koreanguide.koreanguidebackend.domain.track.data.repository.TrackTagRepository;
import com.koreanguide.koreanguidebackend.domain.track.service.TrackService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
@Slf4j
public class TrackServiceImpl implements TrackService {
    private final TrackRepository trackRepository;
    private final UserRepository userRepository;
    private final TrackImageRepository trackImageRepository;
    private final TrackTagRepository trackTagRepository;

    @Autowired
    public TrackServiceImpl(TrackRepository trackRepository, UserRepository userRepository,
                            TrackImageRepository trackImageRepository, TrackTagRepository trackTagRepository) {
        this.trackRepository = trackRepository;
        this.userRepository = userRepository;
        this.trackImageRepository = trackImageRepository;
        this.trackTagRepository = trackTagRepository;
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
    public ResponseEntity<?> getRandomTrack() {
        log.info("TrackServiceImpl - getRandomTrack: 모든 트랙 조회 시도");
        List<Track> trackList = trackRepository.findAll();
        log.info("TrackServiceImpl - getRandomTrack: 모든 트랙 조회 완료");
        List<Track> enableTrackList = new ArrayList<>();

        log.info("TrackServiceImpl - getRandomTrack: 유효 트랙 구분 프로세스 시작");
        for(Track track : trackList) {
//            Public 상태와 동시에 차단 상태가 아닌 트랙
            if(track.isVisible() && !track.isBlocked()) {
                enableTrackList.add(track);
            }
        }
        log.info("TrackServiceImpl - getRandomTrack: 유효 트랙 구분 프로세스 완료");

        int randomIndex = new Random().nextInt(enableTrackList.size());
        log.info("TrackServiceImpl - getRandomTrack: 랜덤 트랙 선정 및 조회 시도");
        Track track = enableTrackList.get(randomIndex);
        log.info("TrackServiceImpl - getRandomTrack: 랜덤 트랙 선정 및 조회 완료");

        log.info("TrackServiceImpl - getRandomTrack: 해당 트랙의 전체 이미지 조회 시도");
        List<TrackImage> trackImageList = trackImageRepository.findAllByTrack(track);
        List<TrackImageResponseDto> trackImageResponseDtoList = new ArrayList<>();
        log.info("TrackServiceImpl - getRandomTrack: 해당 트랙의 전체 이미지 조회 완료");

        for(TrackImage trackImage : trackImageList) {
            trackImageResponseDtoList.add(TrackImageResponseDto.builder()
                            .url(trackImage.getImageUrl())
                    .build());
        }

        log.info("TrackServiceImpl - getRandomTrack: 해당 트랙의 전체 태그 조회 시도");
        List<TrackTag> trackTagList = trackTagRepository.findAllByTrack(track);
        List<TrackTagResponseDto> trackTagResponseDtoList = new ArrayList<>();
        log.info("TrackServiceImpl - getRandomTrack: 해당 트랙의 전체 태그 조회 완료");

        for(TrackTag trackTag : trackTagList) {
            trackTagResponseDtoList.add(TrackTagResponseDto.builder()
                            .tag(trackTag.getTagName())
                    .build());
        }

        TrackResponseDto trackResponseDto = TrackResponseDto.builder()
                .baseResponseDto(BaseResponseDto.builder()
                        .success(true)
                        .msg("처리 완료")
                        .build())
                .trackTitle(track.getTrackTitle())
                .trackPreview(track.getTrackPreview())
                .primaryImageUrl(track.getPrimaryImageUrl())
                .images(trackImageResponseDtoList)
                .tags(trackTagResponseDtoList)
                .name(track.getUser().getNickname())
                .email(track.getUser().getEmail())
                .visible(track.isVisible())
                .blocked(track.isBlocked())
                .blockedReason(track.getBlockedReason())
                .star(track.isStar())
                .build();

        return ResponseEntity.status(HttpStatus.OK).body(trackResponseDto);
    }

    @Override
    public ResponseEntity<?> getAllTrackInMainPages(Long userId) {
        User user = GET_VALID_USER(userId);
        List<TrackMainResponseDto> trackMainResponseDtoList = new ArrayList<>();
        List<Track> trackList = trackRepository.getAllByUser(user);

        for(Track track : trackList) {
//            태그 가져오기
            List<String> tagList = new ArrayList<>();
            List<TrackTag> trackTagList = trackTagRepository.findAllByTrack(track);
            for(TrackTag trackTag : trackTagList) {
                tagList.add(trackTag.getTagName());
            }


            trackMainResponseDtoList.add(TrackMainResponseDto.builder()
                            .trackTitle(track.getTrackTitle())
                            .trackPreview(track.getTrackPreview())
                            .primaryImageUrl(track.getPrimaryImageUrl())
                            .tags(tagList)
                            .star(track.isStar())
                    .build());
        }

        return ResponseEntity.status(HttpStatus.OK).body(trackMainResponseDtoList);
    }

    @Override
    public ResponseEntity<?> getAllTrackByUser(Long userId) {
        User user = GET_VALID_USER(userId);

        List<TrackResponseDto> trackResponseDtoList = new ArrayList<>();
        List<Track> trackList = trackRepository.getAllByUser(user);

        for(Track track : trackList) {
            List<TrackImage> trackImageList = trackImageRepository.findAllByTrack(track);
            List<TrackImageResponseDto> trackImageResponseDtoList = new ArrayList<>();

            for(TrackImage trackImage : trackImageList) {
                trackImageResponseDtoList.add(TrackImageResponseDto.builder()
                        .url(trackImage.getImageUrl())
                        .build());
            }

            List<TrackTag> trackTagList = trackTagRepository.findAllByTrack(track);
            List<TrackTagResponseDto> trackTagResponseDtoList = new ArrayList<>();

            for(TrackTag trackTag : trackTagList) {
                trackTagResponseDtoList.add(TrackTagResponseDto.builder()
                                .tag(trackTag.getTagName())
                        .build());
            }

            trackResponseDtoList.add(TrackResponseDto.builder()
                            .baseResponseDto(BaseResponseDto.builder()
                                    .success(true)
                                    .msg("처리 완료")
                                    .build())
                            .trackTitle(track.getTrackTitle())
                            .trackPreview(track.getTrackPreview())
                            .primaryImageUrl(track.getPrimaryImageUrl())
                            .images(trackImageResponseDtoList)
                            .tags(trackTagResponseDtoList)
                            .name(track.getUser().getNickname())
                            .email(track.getUser().getEmail())
                            .visible(track.isVisible())
                            .blocked(track.isBlocked())
                            .blockedReason(track.getBlockedReason())
                            .star(track.isStar())
                    .build());
        }

        return ResponseEntity.status(HttpStatus.OK).body(trackResponseDtoList);
    }

    @Override
    public ResponseEntity<TrackResponseDto> getTrackById(Long userId, Long trackId) {
        Track track = trackRepository.getById(trackId);
        User user = GET_VALID_USER(userId);

        if(!track.isVisible() && !VALIDATE_USER_OWN_TRACK(user, track)) {
            return ResponseEntity.status(HttpStatus.OK).body(TrackResponseDto.builder()
                            .baseResponseDto(BaseResponseDto.builder()
                                    .success(false)
                                    .msg("공개되지 않은 트랙입니다. 트랙 게시자만 열람할 수 있습니다.")
                                    .build())
                    .build());
        }

        if(track.isBlocked() && !VALIDATE_USER_OWN_TRACK(user, track)) {
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
//        trackResponseDto.setImages(track.getTrackImages());
//        trackResponseDto.setTags(track.getTrackTags());
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
        log.info("TrackServiceImpl - applyTrack: 트랙 저장 프로세스 시작 및 시도");

        User user = GET_VALID_USER(userId);
        LocalDateTime CURRENT_TIME = LocalDateTime.now();

        if(!trackApplyRequestDto.isAgreeTerms() && !trackApplyRequestDto.isAgreePublicTerms()
                && !trackApplyRequestDto.isAgreePrivacyPolicy()) {
            throw new RuntimeException("미동의 항목 존재");
        }

        log.info("TrackServiceImpl - applyTrack: 사용자 조회 성공");

        log.info("TrackServiceImpl - applyTrack: 트랙 객체 생성");
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
                .visible(true)
                .useAble(true)
                .blocked(false)
                .createdAt(CURRENT_TIME)
                .updatedAt(CURRENT_TIME)
                .build();

        log.info("TrackServiceImpl - applyTrack: 트랙 저장 시도");
        trackRepository.save(track);
        log.info("TrackServiceImpl - applyTrack: 트랙 저장 성공");

        log.info("TrackServiceImpl - applyTrack: 태그 저장 시작");
        for(TrackTagApplyRequestDto trackTagApplyRequestDto : trackApplyRequestDto.getTags()) {
            log.info("TrackServiceImpl - applyTrack: 태그 객체 생성");
            TrackTag trackTag = TrackTag.builder()
                    .track(track)
                    .tagName(trackTagApplyRequestDto.getTagName())
                    .uploadedDt(CURRENT_TIME)
                    .build();
            log.info("TrackServiceImpl - applyTrack: 태그 저장 시도");
            trackTagRepository.save(trackTag);
            log.info("TrackServiceImpl - applyTrack: 태그 저장 완료");
        }

        log.info("TrackServiceImpl - applyTrack: 이미지 저장 시작");
        for(TrackImageApplyRequestDto trackImageApplyRequestDto : trackApplyRequestDto.getImages()) {
            log.info("TrackServiceImpl - applyTrack: 이미지 객체 생성");
            TrackImage trackImage = TrackImage.builder()
                    .imageUrl(trackImageApplyRequestDto.getImageUrl())
                    .useAble(true)
                    .uploadedDt(CURRENT_TIME)
                    .track(track)
                    .build();
            log.info("TrackServiceImpl - applyTrack: 이미지 저장 시도");
            trackImageRepository.save(trackImage);
            log.info("TrackServiceImpl - applyTrack: 이미지 저장 완료");
        }

        log.info("TrackServiceImpl - applyTrack: 트랙 저장 프로세스 완료");
        return ResponseEntity.status(HttpStatus.OK).body(BaseResponseDto.builder()
                        .success(true)
                        .msg("트랙 생성이 완료되었습니다.")
                .build());
    }

    @Override
    public ResponseEntity<?> changeTrackName(Long userId, ChangeTrackValueRequestDto changeTrackValueRequestDto) {
        User user = GET_VALID_USER(userId);
        TrackInfoResponseDto trackInfoResponseDto = GET_TRACK(user, changeTrackValueRequestDto.getTrackId());

        if(!trackInfoResponseDto.isAdmin()) {
            throw new RuntimeException("트랙 생성자 미일치");
        }

        Track updatedTrack = trackInfoResponseDto.getTrack();
        updatedTrack.setTrackTitle(changeTrackValueRequestDto.getChangeValue());

        trackRepository.save(updatedTrack);

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @Override
    public ResponseEntity<?> changeTrackPreview(Long userId, ChangeTrackValueRequestDto changeTrackValueRequestDto) {
        User user = GET_VALID_USER(userId);
        TrackInfoResponseDto trackInfoResponseDto = GET_TRACK(user, changeTrackValueRequestDto.getTrackId());

        if(!trackInfoResponseDto.isAdmin()) {
            throw new RuntimeException("트랙 생성자 미일치");
        }

        Track updatedTrack = trackInfoResponseDto.getTrack();
        updatedTrack.setTrackPreview(changeTrackValueRequestDto.getChangeValue());
        trackRepository.save(updatedTrack);

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @Override
    public ResponseEntity<?> changeTrackContent(Long userId, ChangeTrackValueRequestDto changeTrackValueRequestDto) {
        User user = GET_VALID_USER(userId);
        TrackInfoResponseDto trackInfoResponseDto = GET_TRACK(user, changeTrackValueRequestDto.getTrackId());

        if(!trackInfoResponseDto.isAdmin()) {
            throw new RuntimeException("트랙 생성자 미일치");
        }

        Track updatedTrack = trackInfoResponseDto.getTrack();
        updatedTrack.setTrackContent(changeTrackValueRequestDto.getChangeValue());
        trackRepository.save(updatedTrack);

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @Override
    public ResponseEntity<?> changeTrackTag(Long userId, ChangeTrackTagRequestDto changeTrackTagRequestDto) {
        User user = GET_VALID_USER(userId);
        TrackInfoResponseDto trackInfoResponseDto = GET_TRACK(user, changeTrackTagRequestDto.getTrackId());

        if(!trackInfoResponseDto.isAdmin()) {
            throw new RuntimeException("트랙 생성자 미일치");
        }

        if(changeTrackTagRequestDto.getTrackTagApplyRequestDtoList().size() < 3) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(TrackAlertResponseDto.builder()
                            .ko("태그는 최소 3개 이상 추가해야 합니다.")
                            .en("You must add at least three tags.")
                    .build());
        }

        List<TrackTag> trackTagList = trackTagRepository.findAllByTrack(trackInfoResponseDto.getTrack());
        trackTagRepository.deleteAll(trackTagList);

        LocalDateTime CURRENT_TIME = LocalDateTime.now();

        for(TrackTagApplyRequestDto trackTagApplyRequestDto : changeTrackTagRequestDto.getTrackTagApplyRequestDtoList()) {
            trackTagRepository.save(TrackTag.builder()
                            .tagName(trackTagApplyRequestDto.getTagName())
                            .uploadedDt(CURRENT_TIME)
                            .track(trackInfoResponseDto.getTrack())
                    .build());
        }

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @Override
    public ResponseEntity<?> removeTrack(Long userId, RemoveTrackRequestDto removeTrackRequestDto) {
        User user = GET_VALID_USER(userId);
        TrackInfoResponseDto trackInfoResponseDto = GET_TRACK(user, removeTrackRequestDto.getTrackId());



        Track updatedTrack = trackInfoResponseDto.getTrack();
        updatedTrack.setVisible(false);

        trackRepository.save(updatedTrack);

        return ResponseEntity.status(HttpStatus.OK).body(TrackAlertResponseDto.builder()
                        .ko("트랙이 성공적으로 삭제되었습니다. 삭제한 트랙은 복구할 수 없습니다.")
                        .en("The track has been successfully deleted. Deleted tracks cannot be recovered.")
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
