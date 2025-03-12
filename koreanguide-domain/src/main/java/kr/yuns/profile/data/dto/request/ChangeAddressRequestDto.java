package kr.yuns.profile.data.dto.request;


import kr.yuns.auth.data.enums.SeoulCountry;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChangeAddressRequestDto {
    private SeoulCountry seoulCountry;
}