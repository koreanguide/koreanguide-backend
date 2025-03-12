package kr.yuns.profile.data.dto.request;

import kr.yuns.profile.data.enums.SubwayLine;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChangeNearSubwayRequestDto {
    private SubwayLine subwayLine;
    private String station;
}