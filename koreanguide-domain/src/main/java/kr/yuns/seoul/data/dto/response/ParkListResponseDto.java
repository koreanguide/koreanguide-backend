package kr.yuns.seoul.data.dto.response;

import kr.yuns.seoul.data.enums.RiverPark;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ParkListResponseDto {
    private String name;
    private String address;
    private RiverPark riverPark;
}