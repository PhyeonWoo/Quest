package back.Quest.controller.auth;


import back.Quest.config.common.ApiResponse;
import back.Quest.model.dto.auth.AuthDto;
import back.Quest.security.JwtProvider;
import back.Quest.service.auth.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final AuthService authService;
    private final JwtProvider jwtProvider;

    @Operation(
            summary = "회원가입",
            description = "정보들을 받아와 회원가입을 합니다."
    )
    @PostMapping
    public ApiResponse<String> signUp(
            @Valid @RequestBody AuthDto.SignUpRequest request
    ) {
        authService.signUp(request);
        return ApiResponse.ok("생성 완료");
    }



    @Operation(
            summary = "로그인",
            description = "ID, PW를 통해 로그인 시도"
    )
    @PostMapping("/login")
    public ApiResponse<AuthDto.LoginResponse> login(
            @Valid @RequestBody AuthDto.LoginRequest request
    ) {
        AuthDto.LoginResponse login = authService.login(request);
        return ApiResponse.ok(login);
    }



    @Operation(
            summary = "로그아웃",
            description = "RefreshToken을 사용해서 로그아웃 시도"
    )
    @PostMapping("/logout")
    public ApiResponse<String> logout(
            @RequestHeader("Authorization") String bearerToken
    ) {
        String accessToken = jwtProvider.resolveToken(bearerToken);
        authService.logout(accessToken);
        return ApiResponse.ok("로그아웃 완료");
    }


    @Operation(
            summary = "토큰 재발급",
            description = "만료된 accessToken 대신 refreshToken을 사용하여 토큰 발급"
    )
    @PostMapping("/reissue")
    public ApiResponse<AuthDto.LoginResponse> reissue(
            @RequestHeader("RefreshToken") String refreshToken
    ) {
        AuthDto.LoginResponse tokens = authService.reissue(refreshToken);
        return ApiResponse.ok(tokens);
    }

}
