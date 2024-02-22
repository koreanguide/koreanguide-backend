package com.koreanguide.koreanguidebackend.domain.profile.data.dto.response;

import com.koreanguide.koreanguidebackend.domain.auth.data.enums.SeoulCountry;
import com.koreanguide.koreanguidebackend.domain.profile.data.dto.enums.SubwayLine;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProfileResponseDto {
    private String profileUrl;
    private String nickName;
    private String introduce;
    private String firstLang;
    private String secondLang;
    private String nearSubway;
    private SubwayLine subwayLine;
    private String birth;
    private SeoulCountry address;
}
