package com.koreanguide.koreanguidebackend.domain.track.data.dto.response;

import com.koreanguide.koreanguidebackend.domain.track.data.entity.Track;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TrackInfoResponseDto {
    private boolean useAble;
    private boolean visible;
    private boolean admin;
    private Track track;
}
