package com.koreanguide.koreanguidebackend.domain.admin.service.Impl;

import com.koreanguide.koreanguidebackend.domain.admin.data.dto.AllTrackResponseDto;
import com.koreanguide.koreanguidebackend.domain.admin.data.dto.AllUserResponseDto;
import com.koreanguide.koreanguidebackend.domain.admin.service.AdminService;
import com.koreanguide.koreanguidebackend.domain.auth.data.entity.User;
import com.koreanguide.koreanguidebackend.domain.auth.data.repository.UserRepository;
import com.koreanguide.koreanguidebackend.domain.track.data.entity.Track;
import com.koreanguide.koreanguidebackend.domain.track.data.entity.TrackImage;
import com.koreanguide.koreanguidebackend.domain.track.data.entity.TrackTag;
import com.koreanguide.koreanguidebackend.domain.track.data.repository.TrackImageRepository;
import com.koreanguide.koreanguidebackend.domain.track.data.repository.TrackRepository;
import com.koreanguide.koreanguidebackend.domain.track.data.repository.TrackTagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AdminServiceImpl implements AdminService {
    private final UserRepository userRepository;
    private final TrackRepository trackRepository;
    private final TrackImageRepository trackImageRepository;
    private final TrackTagRepository trackTagRepository;

    @Autowired
    public AdminServiceImpl(UserRepository userRepository, TrackRepository trackRepository,
                            TrackImageRepository trackImageRepository, TrackTagRepository trackTagRepository) {
        this.userRepository = userRepository;
        this.trackRepository = trackRepository;
        this.trackImageRepository = trackImageRepository;
        this.trackTagRepository = trackTagRepository;
    }

    @Override
    public ResponseEntity<?> getAllUser() {
        List<User> users = userRepository.findAll();
        List<AllUserResponseDto> allUserResponseDtoList = new ArrayList<>();

        for(User user : users) {
            allUserResponseDtoList.add(AllUserResponseDto.builder()
                            .id(user.getId())
                            .email(user.getEmail())
                            .nickname(user.getNickname())
                            .accessedAt(user.getLastAccessTime())
                            .createdAt(user.getCreatedAt())
                    .build());
        }

        return ResponseEntity.status(HttpStatus.OK).body(allUserResponseDtoList);
    }

    @Override
    public ResponseEntity<?> getAllTrack() {
        List<Track> tracks = trackRepository.findAll();
        List<AllTrackResponseDto> allTrackResponseDtoList = new ArrayList<>();

        for(Track track : tracks) {
            List<TrackTag> trackTagList = trackTagRepository.findAllByTrack(track);
            List<TrackImage> trackImageList = trackImageRepository.findAllByTrack(track);
            AllTrackResponseDto allTrackResponseDto = new AllTrackResponseDto();

            allTrackResponseDto.setId(track.getId());
            allTrackResponseDto.setUser(track.getUser().getId());
            allTrackResponseDto.setTitle(track.getTrackTitle());
            allTrackResponseDto.setPreview(track.getTrackPreview());
            allTrackResponseDto.setContent(track.getTrackContent());
            allTrackResponseDto.setPrimaryImage(track.getPrimaryImageUrl());
            allTrackResponseDto.setAutoTranslate(track.isAutoTranslate());
            allTrackResponseDto.setCreatedAt(track.getCreatedAt());
            allTrackResponseDto.setUpdatedAt(track.getUpdatedAt());

            StringBuilder trackTags = new StringBuilder();
            int trackSize = trackTagList.size();

            for(int i = 0; i < trackSize; i++) {
                trackTags.append(trackTagList.get(i).getTagName());

                if(i < trackSize - 1) {
                    trackTags.append(", ");
                }
            }

            String TRACK_TAGS = trackTags.toString();

            allTrackResponseDto.setTag(TRACK_TAGS);

            StringBuilder trackImages = new StringBuilder();
            int imageSize = trackImageList.size();
            for (int i = 0; i < imageSize; i++) {
                trackImages.append(trackImageList.get(i).getImageUrl());

                if(i < imageSize - 1) {
                    trackImages.append(", ");
                }
            }
            String TRACK_IMAGES = trackImages.toString();

            allTrackResponseDto.setAddedImage(TRACK_IMAGES);

            allTrackResponseDtoList.add(allTrackResponseDto);
        }

        return ResponseEntity.status(HttpStatus.OK).body(allTrackResponseDtoList);
    }
}
