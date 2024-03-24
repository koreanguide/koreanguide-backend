package com.koreanguide.koreanguidebackend.domain.auth.data.entity;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class KakaoUser {
    private String email;
    private String profileUrl;
    private String nickname;
}
