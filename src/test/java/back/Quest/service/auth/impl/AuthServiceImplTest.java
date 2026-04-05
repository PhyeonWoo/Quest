package back.Quest.service.auth.impl;

import back.Quest.config.exception.CustomException;
import back.Quest.mapper.auth.AuthMapper;
import back.Quest.model.dto.auth.AuthDto;
import back.Quest.redis.RedisTokenService;
import back.Quest.security.JwtProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
@DisplayName("인증/인가 서비스 테스트")
public class AuthServiceImplTest {

    @Mock
    private AuthMapper authMapper;

    @Mock
    private JwtProvider jwtProvider;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private RedisTokenService redisTokenService;

    @InjectMocks
    private AuthServiceImpl authService;

    // ==================== 회원가입 테스트 ====================

    @Test
    @DisplayName("회원가입 성공")
    void testSignUpSuccess() {

        //given
        AuthDto.SignUpRequest request = new AuthDto.SignUpRequest(
                "test123",
                "password123!",
                "01012345678",
                "TestUser1",
                "test@example.com"
        );
        when(authMapper.existsById("test123")).thenReturn(false);
        when(authMapper.existsByEmail("test@example.com")).thenReturn(false);
        when(authMapper.lastInsertId()).thenReturn(1L);
        when(passwordEncoder.encode(any())).thenReturn("encoded_password");

        // when & then
        assertThatNoException().isThrownBy(() -> authService.singUp(request));

        // 검증
        verify(authMapper, times(1)).insertMember(any());
        verify(authMapper, times(1)).insertLogin(any());
    }


    @Test
    @DisplayName("회원가입 실패 - 중복 ID")
    void testSignUpFailIdDuplicated() {

        AuthDto.SignUpRequest request = new AuthDto.SignUpRequest(
                "test123",
                "password123!",
                "01012345678",
                "TestUser1",
                "test@example.com"
        );

        when(authMapper.existsById("test123")).thenReturn(true);

        assertThatThrownBy(() -> authService.singUp(request))
                .isInstanceOf(CustomException.DuplicateException.class)
                .hasMessage("이미 존재하는 ID 입니다.");

        verify(authMapper, never()).insertMember(any());
    }


    @Test
    @DisplayName("회원가입 실패 - 중복 Email")
    void testSignUpFailEmailDuplicated() {
        AuthDto.SignUpRequest request = new AuthDto.SignUpRequest(
                "test123",
                "password123!",
                "01012345678",
                "TestUser1",
                "test@example.com"
        );

        when(authMapper.existsById("test123")).thenReturn(false);
        when(authMapper.existsByEmail("test@example.com")).thenReturn(true);

        assertThatThrownBy(() -> authService.singUp(request))
                .isInstanceOf(CustomException.DuplicateException.class)
                .hasMessage("이미 존재하는 Email 입니다.");

        verify(authMapper, never()).insertMember(any());
    }

    // ==================== 로그인 테스트 ====================

    @Test
    @DisplayName("로그인 성공")
    void testLoginSuccess() {
        String rawPassword = "password123!";
        String encodedPassword = "encoded_password";

        AuthDto.LoginInfo loginInfo = new AuthDto.LoginInfo(
                "test123",
                encodedPassword,
                1L
        );

        when(authMapper.findId("test123")).thenReturn(loginInfo);
        when(passwordEncoder.matches(rawPassword, encodedPassword)).thenReturn(true);

        when(jwtProvider.createAccessToken("test123", 1L, "ROLE_USER"))
                .thenReturn("accessToken_mock");

        when(jwtProvider.createRefreshToken("test123",1L))
                .thenReturn("refreshToken_mock");

        AuthDto.LoginResponse response = authService.login(
                new AuthDto.LoginRequest("test123",rawPassword)
        );

        assertThat(response).isNotNull();
        assertThat(response.accessToken()).isEqualTo("accessToken_mock");
        assertThat(response.refreshToken()).isEqualTo("refreshToken_mock");
    }




    @Test
    @DisplayName("로그인 실패 - 회원 없음")
    void testLoginFailNotFound() {

        String userId = "notExist";
        when(authMapper.findId(userId)).thenReturn(null);

        assertThatThrownBy(() -> authService.login(
                new AuthDto.LoginRequest(userId,"Passwords123!")
        ))
                .isInstanceOf(CustomException.NotFoundException.class)
                .hasMessage("존재하지 않습니다.");

        verify(jwtProvider, never()).createAccessToken(any(), any(), any());
    }


    @Test
    @DisplayName("로그인 실패 - 비밀번호 오류")
    void testLoginPasswordMiss() {
        AuthDto.LoginInfo loginInfo = new AuthDto.LoginInfo(
                "test123",
                "encoded_password",
                1L
        );

        when(authMapper.findId("test123")).thenReturn(loginInfo);
        when(passwordEncoder.matches("WrongPassword","encoded_password")).thenReturn(false);

        assertThatThrownBy(() -> authService.login(
                new AuthDto.LoginRequest("test123","WrongPassword")
        ))
                .isInstanceOf(CustomException.InvalidRequestException.class)
                .hasMessage("일치하지 않습니다.");

        verify(jwtProvider, never()).createAccessToken(any(), any(), any());
    }


    // ==================== 로그아웃 테스트 ====================

    @Test
    @DisplayName("로그아웃 성공")
    void testLogoutSuccess() {
        String bearerToken = "Bearer accessToken_mock";

        when(jwtProvider.getUserId(bearerToken)).thenReturn("test123");
        when(jwtProvider.getExpiration(bearerToken)).thenReturn(10000L);

        authService.logout(bearerToken);

        verify(redisTokenService, times(1)).deleteRefreshToken("test123");
        verify(redisTokenService, times(1)).setBlackList(bearerToken, 10000L); // 블랙리스트 등록
    }


    @Test
    @DisplayName("로그아웃 실패 - 만료된 토큰")
    void testLogoutExpiredToken() {
        String bearerToken = "Bearer expiredToken";

        when(jwtProvider.getUserId(bearerToken)).thenReturn("test123");
        when(jwtProvider.getExpiration(bearerToken)).thenReturn(0L);

        authService.logout(bearerToken);

        verify(redisTokenService, times(1)).deleteRefreshToken("test123");
        verify(redisTokenService, never()).setBlackList(any(), anyLong());
    }

    // ==================== 토큰 재발급 테스트 ====================

    @Test
    @DisplayName("토큰 재발급 성공")
    void testReissueSuccess() {
        String refreshToken = "refreshToken_mock";
        String newAccessToken = "newAccessToken_mock";
        String newRefreshToken = "newRefreshToken_mock";

        AuthDto.LoginInfo loginInfo = new AuthDto.LoginInfo(
                "test123",
                "encoded_password",
                1L
        );

        when(jwtProvider.validateToken(refreshToken)).thenReturn(true);
        when(jwtProvider.getUserId(refreshToken)).thenReturn("test123");

        when(redisTokenService.getRefreshToken("test123")).thenReturn(refreshToken);

        when(authMapper.findId("test123")).thenReturn(loginInfo);

        when(jwtProvider.createAccessToken("test123",1L, "ROLE_USER")).thenReturn(newAccessToken);
        when(jwtProvider.createRefreshToken("test123",1L)).thenReturn(newRefreshToken);

        AuthDto.LoginResponse response = authService.reissue(refreshToken);

        assertThat(response).isNotNull();
        assertThat(response.accessToken()).isEqualTo(newAccessToken);
        assertThat(response.refreshToken()).isEqualTo(newRefreshToken);

        verify(redisTokenService, times(1)).saveRefreshToken(eq("test123"), eq(newRefreshToken), anyLong());
    }


    @Test
    @DisplayName("토큰 재발급 실패 - 유효하지 않은 refreshToken")
    void testReissueFailToken() {
        String refreshToken = "refreshToken_mock";

        when(jwtProvider.validateToken(refreshToken)).thenReturn(false);

        assertThatThrownBy(() -> authService.reissue(refreshToken))
                .isInstanceOf(CustomException.InvalidRequestException.class)
                .hasMessage("유효하지 않거나 만료된 refreshToken 입니다.");

        verify(redisTokenService, never()).getRefreshToken(null);
    }
}
