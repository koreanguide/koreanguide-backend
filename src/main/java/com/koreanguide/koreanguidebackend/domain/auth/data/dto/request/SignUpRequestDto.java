package com.koreanguide.koreanguidebackend.domain.auth.data.dto.request;

import com.koreanguide.koreanguidebackend.domain.auth.data.enums.SeoulCountry;
import com.koreanguide.koreanguidebackend.domain.auth.data.enums.UserRole;
import com.koreanguide.koreanguidebackend.domain.auth.data.enums.UserType;
import lombok.*;


@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class SignUpRequestDto {
//    사용자 타입
    private UserRole userRole;
//    닉네임
    private String nickname;
//    이메일 주소
    private String email;
//    비밀번호
    private String password;
//    시군구
    private SeoulCountry country;
}