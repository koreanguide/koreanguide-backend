package kr.yuns.track.data.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TrackDeleteInfoResponseDto {
    private Long review;
    private Long view;
    private Long like;
}