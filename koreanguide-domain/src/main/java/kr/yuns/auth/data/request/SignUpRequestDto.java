package kr.yuns.auth.data.request;

import kr.yuns.auth.data.enums.SeoulCountry;
import kr.yuns.auth.data.enums.UserRole;
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
//    인증 키
    // private String authKey;
//    시군구
    private SeoulCountry country;
}