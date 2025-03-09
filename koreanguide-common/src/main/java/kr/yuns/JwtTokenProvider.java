package kr.yuns;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

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

    public String createAccessToken(String email, List<String> roles) {
        return createToken(email, roles, accessTokenValidMillisecond);
    }

    public String createRefreshToken(String email) {
        return createToken(email, new ArrayList<>(), refreshTokenValidMillisecond);
    }

    private String createToken(String email, List<String> roles, long validMillisecond) {
        Date now = new Date();

        return Jwts.builder()
                    .signWith(Jwts.SIG.HS512.key().build())
                    .claims().subject(email).add("roles", roles).and()
                    .issuedAt(now)
                    .expiration(new Date(now.getTime() + validMillisecond))
                    .compact();
    }

    public Authentication getAuthentication(String token) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(this.getUserEmail(token));
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    public String getUserEmail(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(Jwts.SIG.HS512.key().build())
                .build()
                .parseSignedClaims(token)
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

    //    HTTP Header에서 Token 추출
    public String resolveToken(HttpServletRequest request) {
        return request.getHeader("X-AUTH-TOKEN");
    }

    //    Token 유효성 검사
    public boolean validateToken(String token) {
        try {
            Claims claims = Jwts.parser()
                .verifyWith(Jwts.SIG.HS512.key().build())
                .build()
                .parseSignedClaims(token)
                .getPayload();
            return !claims.getExpiration().before(new Date());
        } catch (Exception e) {
            return false;
        }
    }
}