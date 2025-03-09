package kr.yuns.auth.data.request;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class SignInRequestDto {
    private String email;
    private String password;
}