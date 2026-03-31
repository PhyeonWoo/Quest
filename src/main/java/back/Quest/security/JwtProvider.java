package back.Quest.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Collections;
import java.util.Date;

@Component
public class JwtProvider {

    private String secret = "hellohello1234hello1234hello12hello1231441415";
    private Key key;

    @PostConstruct
    protected void init() {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }


    /**
     * 공통 토큰 생성 로직
     * @param id 사용자 ID
     * @param memberNo 사용자 고유 번호
     * @param role 권한
     * @param time 만료 시간
     */
    // token 생성
    public String createToken(String id, Long memberNo, String role, long time) {
        Claims claims = Jwts.claims().setSubject(id);
        claims.put("memberNo",memberNo);

        if (role != null) {
            claims.put("role",role);
        }

        Date now = new Date();
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + time))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    // AccessToken 생성 (30분)
    public String createAccessToken(String userId, Long memberNo, String role) {
        return createToken(userId, memberNo, role, 30 * 60 * 1000L);
    }

    // RefreshToken 생성 (7일)
    public String createRefreshToken(String userId, Long memberNo) {
        return createToken(userId, memberNo, null, 7 * 24 * 60 * 60 * 1000L);
    }


    // Token에서 userId 추출
    public String getUserId(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }


    // Token에서 memberNo 추출
    public Long getMemberNo(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJwt(token)
                .getBody()
                .get("memberNo",Long.class);
    }

    // 토큰 유효성 검사
    public boolean validateToken(String token) {
        try{
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJwt(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    // Security 인증 객체 생성
    public Authentication getAuthentication(String token) {
        String id = getUserId(token);
        return new UsernamePasswordAuthenticationToken(id,"",
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));
    }

    public long getExpiration(String token) {
        try {
            Date expiration = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody().getExpiration();

        long now = new Date().getTime();
        return expiration.getTime() - now;
        } catch (JwtException | IllegalArgumentException e) {
            return 0;
        }
    }


    // http 요청 헤더에서 토큰 추출
    public String resolveToken(String bearerToken) {
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

}
