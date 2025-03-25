package kr.yuns.seoul.data.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ParkResponseDto {
    private List<ParkListResponseDto> data;
    private List<ParkListResponseDto> recommend;
}