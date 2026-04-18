package back.Quest.service.auth;

import back.Quest.model.dto.auth.AuthDto;

public interface AuthService {
    void signUp(AuthDto.SignUpRequest request);
    AuthDto.LoginResponse login(AuthDto.LoginRequest request);
    void logout(String bearerToken);
    AuthDto.LoginResponse reissue(String refreshToken);
}
