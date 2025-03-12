package kr.yuns.profile.data.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MyPageResponseDto {
    private String profileUrl;
    private String name;
    private String nickName;
    private String phoneNum;
    private String email;
    private String password;
    private String accountInfo;
    private String blocked;
    private String introduce;
    private boolean isEnable;
}