package back.Quest.service.auth.impl;

import back.Quest.config.exception.CustomException;
import back.Quest.mapper.auth.AuthMapper;
import back.Quest.model.dto.auth.AuthDto;
import back.Quest.redis.RedisTokenService;
import back.Quest.security.JwtProvider;
import back.Quest.service.auth.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final AuthMapper authMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final RedisTokenService redisTokenService;

    @Override
    @Transactional
    public void signUp(AuthDto.SignUpRequest request) {
        log.info("Sign Request id : {}",request.id());

        if (authMapper.existsById(request.id())) {
            throw new CustomException.DuplicateException("이미 존재하는 ID 입니다.");
        }

        if(authMapper.existsByEmail(request.email())) {
            throw new CustomException.DuplicateException("이미 존재하는 Email 입니다.");
        }

        authMapper.insertMember(AuthDto.MemberCreateRequest.from(request));

        Long memberNo = authMapper.lastInsertId();
        String encodePw = passwordEncoder.encode(request.pw());

        authMapper.insertLogin(AuthDto.LoginCreateRequest.of(
                request.id(),
                encodePw,
                memberNo
        ));
    }

    @Override
    public AuthDto.LoginResponse login(AuthDto.LoginRequest request) {
        log.info("Login Request id:  {}",request.id());

        AuthDto.LoginInfo info = authMapper.findId(request.id());

        if(info == null) {
            log.warn("Not found");
            throw new CustomException.NotFoundException("존재하지 않습니다.");
        }

        if(!passwordEncoder.matches(request.pw(), info.pw())) {
            log.error("Not match");
            throw new CustomException.InvalidRequestException("일치하지 않습니다.");
        }

        String accessToken = jwtProvider.createAccessToken(
                info.id(),
                info.memberNo(),
                "ROLE_USER"
        );

        String refreshToken = jwtProvider.createRefreshToken(
                info.id(),
                info.memberNo()
        );

        long refreshTokenExpirationMills = 7L * 24 * 60 * 60 * 1000;

        redisTokenService.saveRefreshToken(
                info.id(),
                refreshToken,
                refreshTokenExpirationMills
        );

        log.info("Login Response Ready");
        return AuthDto.LoginResponse.of(accessToken,refreshToken);
    }


    @Override
    public void logout(String bearerToken) {
        String id = jwtProvider.getUserId(bearerToken);
        redisTokenService.deleteRefreshToken(id);

        long expiration = jwtProvider.getExpiration(bearerToken);

        if (expiration > 0) {
            redisTokenService.setBlackList(bearerToken, expiration);
            log.info("AccessToken 블랙리스트 등록 완료 : {}",id);
        }
    }



    @Override
    public AuthDto.LoginResponse reissue(String refreshToken) {
        log.info("Token Reissue Request");

        if(!jwtProvider.validateToken(refreshToken)) {
            throw new CustomException.InvalidRequestException("유효하지 않거나 만료된 refreshToken 입니다.");
        }

        String id = jwtProvider.getUserId(refreshToken);
        String saveRefreshToken = redisTokenService.getRefreshToken(id);

        if (saveRefreshToken == null || !saveRefreshToken.equals(refreshToken)) {
            throw new CustomException.InvalidRequestException("유효하지 않은 토큰");
        }

        AuthDto.LoginInfo info = authMapper.findId(id);

        String newAccessToken = jwtProvider.createAccessToken(
                info.id(),
                info.memberNo(),
                "ROLE_USER"
        );

        String newRefreshToken = jwtProvider.createRefreshToken(info.id(), info.memberNo());
        long refreshTokenExpirationMills = 7L * 24 * 60 * 60 * 1000;

        redisTokenService.saveRefreshToken(id, newRefreshToken,refreshTokenExpirationMills);
        log.info("Token Reissue Success ID : {}",id);
        return AuthDto.LoginResponse.of(newAccessToken, newRefreshToken);
    }
}
