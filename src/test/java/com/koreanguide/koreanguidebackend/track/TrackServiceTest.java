package com.koreanguide.koreanguidebackend.track;

import com.koreanguide.koreanguidebackend.domain.auth.data.dto.response.BaseResponseDto;
import com.koreanguide.koreanguidebackend.domain.auth.data.entity.User;
import com.koreanguide.koreanguidebackend.domain.auth.data.repository.UserRepository;
import com.koreanguide.koreanguidebackend.domain.track.data.dto.request.TrackApplyRequestDto;
import com.koreanguide.koreanguidebackend.domain.track.data.dto.request.TrackImageApplyRequestDto;
import com.koreanguide.koreanguidebackend.domain.track.data.dto.request.TrackTagApplyRequestDto;
import com.koreanguide.koreanguidebackend.domain.track.data.entity.Track;
import com.koreanguide.koreanguidebackend.domain.track.data.entity.TrackImage;
import com.koreanguide.koreanguidebackend.domain.track.data.entity.TrackTag;
import com.koreanguide.koreanguidebackend.domain.track.data.repository.TrackImageRepository;
import com.koreanguide.koreanguidebackend.domain.track.data.repository.TrackRepository;
import com.koreanguide.koreanguidebackend.domain.track.data.repository.TrackTagRepository;
import com.koreanguide.koreanguidebackend.domain.track.service.TrackService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@SpringBootTest
public class TrackServiceTest {
    @Autowired
    private TrackService trackService;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private TrackRepository trackRepository;

    @MockBean
    private TrackTagRepository trackTagRepository;

    @MockBean
    private TrackImageRepository trackImageRepository;

    @Test
    public void applyTrackTest() {
        // Given
        Long userId = 1L;

        List<TrackImageApplyRequestDto> trackImageApplyRequestDtoList = new ArrayList<>();
        trackImageApplyRequestDtoList.add(TrackImageApplyRequestDto.builder()
                        .imageUrl("test url 1")
                .build());
        trackImageApplyRequestDtoList.add(TrackImageApplyRequestDto.builder()
                .imageUrl("test url 2")
                .build());
        
        List<TrackTagApplyRequestDto> trackTagApplyRequestDtoList = new ArrayList<>();
        trackTagApplyRequestDtoList.add(TrackTagApplyRequestDto.builder()
                        .tagName("test tag 1")
                .build());
        trackTagApplyRequestDtoList.add(TrackTagApplyRequestDto.builder()
                .tagName("test tag 2")
                .build());
        
        TrackApplyRequestDto trackApplyRequestDto = new TrackApplyRequestDto();
        trackApplyRequestDto.setAgreePublicTerms(true);
        trackApplyRequestDto.setAgreeTerms(true);
        trackApplyRequestDto.setAgreePrivacyPolicy(true);
        trackApplyRequestDto.setTrackTitle("test");
        trackApplyRequestDto.setTrackContent("test content");
        trackApplyRequestDto.setTrackPreview("test preview");
        trackApplyRequestDto.setPrimaryImageUrl("test url");
        trackApplyRequestDto.setImages(trackImageApplyRequestDtoList);
        trackApplyRequestDto.setTags(trackTagApplyRequestDtoList);

        User user = new User();
        user.setId(userId);

        // When
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(trackRepository.save(any(Track.class))).thenAnswer(i -> i.getArguments()[0]);
        when(trackTagRepository.save(any(TrackTag.class))).thenAnswer(i -> i.getArguments()[0]);
        when(trackImageRepository.save(any(TrackImage.class))).thenAnswer(i -> i.getArguments()[0]);

        ResponseEntity<BaseResponseDto> result = trackService.applyTrack(userId, trackApplyRequestDto);

        // Then
        assertEquals(HttpStatus.OK, result.getStatusCode());
    }
}
