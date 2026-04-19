package back.Quest.model.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.apache.ibatis.type.Alias;

public class AuthDto {

    @Schema(description = "회원가입 요청")
    public record SignUpRequest(
            @Schema(description = "사용자 ID (5~20자)", example = "user123")
            @NotBlank(message = "빈칸이면 안됩니다.")
            @Size(min = 5, max = 20, message = "id는 5자 이상 20자 이하여야 합니다.")
            String id,

            @Schema(description = "비밀번호 (8자 이상, 영문+숫자+특수문자 포함)", example = "Pass123!")
            @NotBlank(message = "빈칸이면 안됩니다.")
            @Size(min = 8, message = "비밀번호는 8자 이상이어야 합니다.")
            @Pattern(
                    regexp = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[@$!%*?&])[a-zA-Z\\d@$!%*?&]+$",
                    message = "비밀번호는 영문, 숫자, 특수문자를 포함해야 합니다."
            )
            String pw,

            @Schema(description = "전화번호 (10~11자리 숫자)", example = "01012345678")
            @NotBlank(message = "빈칸이면 안됩니다.")
            @Pattern(regexp = "^\\d{10,11}$", message = "전화번호 형식이 올바르지 않습니다.")
            String phoneNumber,

            @Schema(description = "이름", example = "홍길동")
            @NotBlank(message = "빈칸이면 안됩니다.")
            String name,

            @Schema(description = "이메일", example = "hong@email.com")
            @NotBlank(message = "빈칸이면 안됩니다.")
            String email
    ) {}

    public record MemberCreateRequest(
            String email,
            String phoneNumber,
            String name
    ) {
        public static MemberCreateRequest from(SignUpRequest request) {
            return new MemberCreateRequest(
                    request.email(),
                    request.phoneNumber(),
                    request.name()
            );
        }
    }

    public record LoginCreateRequest(
            String id,
            String pw,
            Long memberNo
    ) {
        public static LoginCreateRequest of(String id, String encodedPw, Long memberNo) {
            return new LoginCreateRequest(id, encodedPw, memberNo);
        }
    }

    @Alias("LoginInfo")
    public record LoginInfo(
            String id,
            String pw,
            Long memberNo
    ) {}

    @Schema(description = "로그인 요청")
    public record LoginRequest(
            @Schema(description = "사용자 ID", example = "user123")
            @NotBlank String id,
            @Schema(description = "비밀번호", example = "Pass123!")
            @NotBlank String pw
    ) {}

    @Schema(description = "로그인 응답")
    public record LoginResponse(
            @Schema(description = "토큰 타입", example = "Bearer ")
            String grantType,
            @Schema(description = "AccessToken (30분 유효)")
            String accessToken,
            @Schema(description = "RefreshToken (7일 유효)")
            String refreshToken
    ) {
        public static LoginResponse of(String accessToken, String refreshToken) {
            return new LoginResponse("Bearer ", accessToken, refreshToken);
        }
    }
}
