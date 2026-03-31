package back.Quest.model.dto.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class AuthDto {

    // 1. 회원가입 요청 (기존 유지)
    public record SignUpRequest(
            @NotBlank(message = "빈칸이면 안됩니다.")
            @Size(min = 5, max = 20, message = "id는 5자 이상 20자 이하여야 합니다.")
            String id,

            @NotBlank(message = "빈칸이면 안됩니다.")
            @Size(min = 8, message = "비밀번호는 8자 이상이어야 합니다.")
            @Pattern(
                    regexp = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[@$!%*?&])[a-zA-Z\\d@$!%*?&]+$",
                    message = "비밀번호는 영문, 숫자, 특수문자를 포함해야 합니다."
            )
            String pw,

            @NotBlank(message = "빈칸이면 안됩니다.")
            @Pattern(regexp = "^\\d{10,11}$", message = "전화번호 형식이 올바르지 않습니다.")
            String phoneNumber,

            @NotBlank(message = "빈칸이면 안됩니다.")
            String name,

            @NotBlank(message = "빈칸이면 안됩니다.")
            String email
    ) {}

    // 2. 멤버 생성 요청 (팩토리 메서드 추가)
    public record MemberCreateRequest(
            String email,
            String phoneNumber,
            String name
    ) {
        // SignUpRequest로부터 MemberCreateRequest를 생성하는 정적 메서드
        public static MemberCreateRequest from(SignUpRequest request) {
            return new MemberCreateRequest(
                    request.email(),
                    request.phoneNumber(),
                    request.name()
            );
        }
    }

    // 3. Login 테이블 생성 요청 (팩토리 메서드 추가)
    public record LoginCreateRequest(
            String id,
            String pw,
            Long memberNo
    ) {
        // ID, 암호화된 PW, 생성된 회원번호를 받아 객체 생성
        public static LoginCreateRequest of(String id, String encodedPw, Long memberNo) {
            return new LoginCreateRequest(id, encodedPw, memberNo);
        }
    }

    public record LoginInfo(
            String id,
            String pw,
            Long memberNo
    ) {}

    public record LoginRequest(
            @NotBlank String id,
            @NotBlank String pw
    ) {}

    public record LoginResponse(
            String grantType,
            String accessToken,
            String refreshToken
    ) {
        public static LoginResponse of(String accessToken, String refreshToken) {
            return new LoginResponse("Bearer ", accessToken, refreshToken);
        }
    }
}
