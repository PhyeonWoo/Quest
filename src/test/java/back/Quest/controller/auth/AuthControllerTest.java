package back.Quest.controller.auth;

import back.Quest.config.exception.CustomException;
import back.Quest.mapper.auth.AuthMapper;
import back.Quest.model.dto.auth.AuthDto;
import back.Quest.security.JwtProvider;
import back.Quest.service.auth.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(controllers = AuthController.class)
@ActiveProfiles("test")
@DisplayName("Auth API HTTP 테스트")
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private JwtProvider jwtProvider;

    @MockitoBean
    private AuthMapper authMapper;

    @Test
    @WithMockUser
    @DisplayName("POST /api/v1/auth - 회원가입 성공")
    void testSignupSuccess() throws Exception {
        AuthDto.SignUpRequest request = new AuthDto.SignUpRequest(
                "test123",
                "Password123!",
                "01012345678",
                "TestUser1",
                "test@example.com"
        );

        mockMvc.perform(
                post("/api/v1/auth")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect((ResultMatcher) jsonPath("$.success").value(true))
                .andExpect((ResultMatcher) jsonPath("$.code").value(200));

        verify(authService, times(1)).singUp(any());
    }



    @Test
    @WithMockUser
    @DisplayName("POST /api/v1/auth - 회원가입 실패: 중복 ID")
    void testSignUpFailIdDuplicated() throws Exception {
        AuthDto.SignUpRequest request = new AuthDto.SignUpRequest(
                "test123",
                "Password123!",
                "01012345678",
                "TestUser1",
                "test@example.com"
        );

        doThrow(new CustomException.DuplicateException("이미 존재하는 ID"))
                .when(authService).singUp(any());

        mockMvc.perform(
                post("/api/v1/auth")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        )
                .andDo(print())
                .andExpect(status().isConflict())
                .andExpect((ResultMatcher) jsonPath("$.success").value(false))
                .andExpect((ResultMatcher) jsonPath("$.code").value(409));
    }


    @Test
    @WithMockUser
    @DisplayName("POST /api/v1/auth - 회원가입 실패: 중복 Email")
    void testSingUpFailEmailDuplicated() throws Exception {
        AuthDto.SignUpRequest request = new AuthDto.SignUpRequest(
                "test123",
                "Password123!",
                "01012345678",
                "TestUser1",
                "test@example.com"
        );

        doThrow(new CustomException.DuplicateException("이미 존재하는 Email"))
                .when(authService).singUp(any());

        mockMvc.perform(
                post("/api/v1/auth")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        )
                .andDo(print())
                .andExpect(status().isConflict())
                .andExpect((ResultMatcher) jsonPath("$.success").value(false))
                .andExpect((ResultMatcher) jsonPath("$.code").value(409));
    }


    @Test
    @WithMockUser
    @DisplayName("POST /api/v1/auth/login - 로그인 성공")
    void testLoginSuccess() throws Exception {
        AuthDto.LoginRequest request = new AuthDto.LoginRequest(
                "test123",
                "Password123!"
        );

        AuthDto.LoginResponse response = new AuthDto.LoginResponse(
                "Bearer ",
                "accessToken_mock",
                "refreshToken_mock"
        );

        when(authService.login(any())).thenReturn(response);

        mockMvc.perform(
                post("/api/v1/auth/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        )
                .andDo(print())
                .andExpect(status().isConflict())
                .andExpect((ResultMatcher) jsonPath("$.success").value(true))
                .andExpect((ResultMatcher) jsonPath("$.code").value(200))
                .andExpect((ResultMatcher) jsonPath("$.data.accessToken").value("accessToken_mock"))
                .andExpect((ResultMatcher) jsonPath("$.data.refreshToken").value("refreshToken_mock"));
    }


    @Test
    @WithMockUser
    @DisplayName("POST /api/v1/auth/login - 로그인 실패: 회원 없음")
    void testLoginFailNotFound() throws Exception {
        AuthDto.LoginRequest request = new AuthDto.LoginRequest(
                "noexist",
                "Password123!"
        );

        doThrow(new CustomException.NotFoundException("존재하지 않습니다."))
                .when(authService).login(any());

        mockMvc.perform(
                post("/api/v1/auth/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        )
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect((ResultMatcher) jsonPath("$.success").value(false))
                .andExpect((ResultMatcher) jsonPath("$.code").value(404));
    }


    @Test
    @WithMockUser
    @DisplayName("POST /api/v1/auth/login - 로그인 실패: 비밀번호 오류")
    void testLoginFailPasswordMiss() throws Exception {
        AuthDto.LoginRequest request = new AuthDto.LoginRequest(
                "test123",
                "WrongPassword!"
        );

        doThrow(new CustomException.InvalidRequestException("비밀번호가 일치하지 않습니다."))
                .when(authService).login(any());

        mockMvc.perform(
                post("/api/v1/auth/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect((ResultMatcher) jsonPath("$.success").value(false))
                .andExpect((ResultMatcher) jsonPath("$.code").value(400));
    }


    @Test
    @WithMockUser
    void testLogoutSuccess() throws Exception {
        mockMvc.perform(
                post("/api/v1/auth/logout")
                        .with(csrf())
                        .header("Authorization", "Bearer validToken")
        )
                .andDo(print())
                .andExpect(status().isNoContent());
        verify(authService, times(1)).logout("Bearer validToken");
    }


    @Test
    @WithMockUser
    void testLogoutExpired() throws Exception {
        doThrow(new CustomException.InvalidRequestException("유효하지 않은 토큰"))
                .when(authService).logout(any());

        mockMvc.perform(
                post("/api/v1/auth/logout")
                        .with(csrf())
                        .header("Authorization", "Bearer expiredToken")
        )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect((ResultMatcher) jsonPath("$.code").value(400));
    }

}