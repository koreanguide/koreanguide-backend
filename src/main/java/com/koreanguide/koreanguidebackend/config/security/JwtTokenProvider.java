package com.koreanguide.koreanguidebackend.config.security;

import com.koreanguide.koreanguidebackend.domain.auth.data.entity.User;
import com.koreanguide.koreanguidebackend.domain.auth.data.repository.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenProvider {
    private final UserDetailsService userDetailsService;
    private final UserRepository userRepository;

    @Value("${springboot.jwt.secret}")
    private String secretKey;

//    Access Token: 1시간 유효
    private final long accessTokenValidMillisecond = 1000L * 60 * 60;

//    Refresh Token: 2주 유효
    private final long refreshTokenValidMillisecond = 1000L * 60 * 60 * 24 * 14;

    public String createAccessToken(String email, List<String> roles) {
        return createToken(email, roles, accessTokenValidMillisecond);
    }

    public String createRefreshToken(String email) {
        return createToken(email, new ArrayList<>(), refreshTokenValidMillisecond);
    }

    private String createToken(String email, List<String> roles, long validMillisecond) {
        log.info("JwtTokenProvider / createToken(): 토큰 생성 시작");
        Claims claims = Jwts.claims().setSubject(email);
        claims.put("roles", roles);

        Date now = new Date();
        String token = Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + validMillisecond))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();

        log.info("JwtTokenProvider / createToken(): 토큰 생성 완료");
        return token;
    }

    public Authentication getAuthentication(String token) {
        log.info("JwtTokenProvider / getAuthentication(): 토큰 인증 정보 조회 시작");
        UserDetails userDetails = userDetailsService.loadUserByUsername(this.getUserEmail(token));
        log.info("JwtTokenProvider / getAuthentication(): 토큰 인증 정보 조회 완료, 대상 사용자: {}", userDetails.getUsername());
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    public String getUserEmail(String token){
        log.info("JwtTokenProvider / getUserEmail(): 토큰 기반 회원 구별 정보 추출");
        String info = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().getSubject();
        log.info("JwtTokenProvider / getUserEmail(): 토큰 기반 회원 구별 정보 추출 완료, info : {}", info);
        return info;
    }

    public Long getUserIdByToken(String token) {
        String info = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().getSubject();
        User user = userRepository.getByEmail(info);
        return user.getId();
    }

    public String refreshToken(String refreshToken, String email) {
        log.info("JwtTokenProvider / refreshToken(): 리프레시 토큰으로 액세스 토큰 재발급 시작");

        if (validateToken(refreshToken)) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(email);
            List<String> roles = userDetails.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList());

            return createAccessToken(email, roles);
        } else {
            log.error("JwtTokenProvider / refreshToken(): 리프레시 토큰이 유효하지 않음");
            throw new RuntimeException("Refresh Token이 유효하지 않습니다.");
        }
    }

//    HTTP Header에서 Token 추출
    public String resolveToken(HttpServletRequest request) {
        log.info("JwtTokenProvider / resolveToken(): HTTP 헤더에서 Token 값 추출");
        return request.getHeader("X-AUTH-TOKEN");
    }

//    Token 유효성 검사
    public boolean validateToken(String token) {
        log.info("JwtTokenProvider / validateToken(): 토큰 유효 체크 시작");

        try {
            Jws<Claims> claimsJws = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
            log.info("JwtTokenProvider / validateToken(): 토큰 유효 체크 완료");
            return !claimsJws.getBody().getExpiration().before(new Date());
        } catch (Exception e) {
            log.info("JwtTokenProvider / validateToken(): 토큰 유효 체크 예외 발생");
            return false;
        }
    }
}
