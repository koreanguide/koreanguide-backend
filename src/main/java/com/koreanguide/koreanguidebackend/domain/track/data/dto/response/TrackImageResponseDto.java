package com.koreanguide.koreanguidebackend.domain.track.data.dto.response;

import com.koreanguide.koreanguidebackend.domain.auth.data.dto.response.BaseResponseDto;
import com.koreanguide.koreanguidebackend.domain.track.data.entity.TrackImage;
import com.koreanguide.koreanguidebackend.domain.track.data.entity.TrackTag;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TrackImageResponseDto {
    private String url;
}
