package kr.yuns;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.crypto.SecretKey;

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtTokenProvider {
    private final UserDetailsService userDetailsService;

    @Value("${jwt.secret}")
    private String secretKey;

    //    Access Token: 1시간 유효
    private final long accessTokenValidMillisecond = 1000L * 60 * 60;

    //    Refresh Token: 2주 유효
    private final long refreshTokenValidMillisecond = 1000L * 60 * 60 * 24 * 14;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    public String createAccessToken(String email, List<String> roles) {
        return createToken(email, roles, accessTokenValidMillisecond);
    }

    public String createRefreshToken(String email) {
        return createToken(email, new ArrayList<>(), refreshTokenValidMillisecond);
    }

    // private String createToken(String email, List<String> roles, long validMillisecond) {
    //     Date now = new Date();

    //     return Jwts.builder()
    //         .signWith(Jwts.SIG.HS512.key().build())
    //         .claims().subject(email).add("roles", roles).and()
    //         .issuedAt(now)
    //         .expiration(new Date(now.getTime() + validMillisecond))
    //         .compact();
    // }

    @SuppressWarnings("deprecation")
    public String createToken(String email, List<String> roles, long validMillisecond) {
        Date now = new Date();
    
        return Jwts.builder()
                .signWith(SignatureAlgorithm.HS512, secretKey.getBytes())  // 서명 알고리즘 및 키 지정
                .claim("roles", roles)
                .setSubject(email)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + validMillisecond))
                .compact(); // JWT 토큰 생성
    }

    public Authentication getAuthentication(String token) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(this.getUserEmailByToken(token));
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    public String getUserEmailByToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    
        return claims.getSubject();
    }

    public String getUserEmail(HttpServletRequest request) {
        Claims claims = Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(resolveToken(request))
                .getPayload();
    
        return claims.getSubject();
    }

    public String refreshToken(String refreshToken, String email) {
        if (validateToken(refreshToken)) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(email);
            List<String> roles = userDetails.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList());

            return createAccessToken(email, roles);
        } else {
            throw new RuntimeException("Refresh Token이 유효하지 않습니다.");
        }
    }

    public String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
    

    public String convertToken(String token) {
        return token.substring(7);
    }

    //    Token 유효성 검사
    public boolean validateToken(String token) {
        try {
            Claims claims = Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
            return !claims.getExpiration().before(new Date());
        } catch (Exception e) {
            return false;
        }
    }
}