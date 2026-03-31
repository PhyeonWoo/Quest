package back.Quest.mapper.auth;

import back.Quest.model.dto.auth.AuthDto;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AuthMapper {
    void insertLogin(AuthDto.LoginCreateRequest request);
    void insertMember(AuthDto.MemberCreateRequest request);

    Long lastInsertId();
    boolean existsById(String id);
    boolean existsByEmail(String email);

    AuthDto.LoginInfo findId(String id);
}
