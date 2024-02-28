package com.koreanguide.koreanguidebackend.domain.profile.data.dto.request;

import com.koreanguide.koreanguidebackend.domain.auth.data.enums.SeoulCountry;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChangeBrithReqeustDto {
    private String birth;
}
