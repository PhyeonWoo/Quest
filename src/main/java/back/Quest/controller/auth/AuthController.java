package back.Quest.controller.auth;


import back.Quest.config.common.ApiResponse;
import back.Quest.model.dto.auth.AuthDto;
import back.Quest.security.JwtProvider;
import back.Quest.service.auth.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Auth", description = "인증 API - 회원가입, 로그인, 로그아웃, 토큰 재발급")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final AuthService authService;
    private final JwtProvider jwtProvider;

    @Operation(
            summary = "회원가입",
            description = "ID, 비밀번호, 전화번호, 이름, 이메일을 입력하여 회원가입합니다.\n\n" +
                    "- ID: 5~20자\n" +
                    "- 비밀번호: 8자 이상, 영문+숫자+특수문자 포함\n" +
                    "- 전화번호: 10~11자리 숫자"
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "회원가입 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "입력값 유효성 검사 실패"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "ID 또는 이메일 중복")
    })
    @PostMapping
    public ApiResponse<String> signUp(
            @Valid @RequestBody AuthDto.SignUpRequest request
    ) {
        authService.signUp(request);
        return ApiResponse.ok("생성 완료");
    }



    @Operation(
            summary = "로그인",
            description = "ID와 비밀번호로 로그인합니다. 성공 시 AccessToken(30분)과 RefreshToken(7일)을 반환합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "로그인 성공 - AccessToken, RefreshToken 반환"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "비밀번호 불일치"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "존재하지 않는 ID")
    })
    @PostMapping("/login")
    public ApiResponse<AuthDto.LoginResponse> login(
            @Valid @RequestBody AuthDto.LoginRequest request
    ) {
        AuthDto.LoginResponse login = authService.login(request);
        return ApiResponse.ok(login);
    }



    @Operation(
            summary = "로그아웃",
            description = "AccessToken을 블랙리스트에 등록하고 RefreshToken을 삭제합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "로그아웃 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "유효하지 않은 토큰")
    })
    @PostMapping("/logout")
    public ApiResponse<String> logout(
            @Parameter(description = "Bearer {accessToken}", required = true)
            @RequestHeader("Authorization") String bearerToken
    ) {
        String accessToken = jwtProvider.resolveToken(bearerToken);
        authService.logout(accessToken);
        return ApiResponse.ok("로그아웃 완료");
    }


    @Operation(
            summary = "토큰 재발급",
            description = "RefreshToken으로 새로운 AccessToken과 RefreshToken을 재발급합니다.\n\n" +
                    "헤더에 `Authorization: Bearer {refreshToken}` 형식으로 전송하세요."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "토큰 재발급 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "유효하지 않거나 만료된 RefreshToken"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "존재하지 않는 사용자")
    })
    @PostMapping("/reissue")
    public ApiResponse<AuthDto.LoginResponse> reissue(
            @Parameter(description = "Bearer {refreshToken}", required = true)
            @RequestHeader("Authorization") String bearerToken
    ) {
        String refreshToken = jwtProvider.resolveToken(bearerToken);
        AuthDto.LoginResponse tokens = authService.reissue(refreshToken);
        return ApiResponse.ok(tokens);
    }

}
