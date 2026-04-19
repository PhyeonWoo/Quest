package back.Quest.mapper.auth;

import back.Quest.model.dto.auth.AuthDto;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AuthMapper {
    //LOGIN DB 생성
    void insertLogin(AuthDto.LoginCreateRequest request);

    // MEMBER DB 생성
    void insertMember(AuthDto.MemberCreateRequest request);

    //마지막 ID 조회
    Long lastInsertId();

    boolean existsById(String id);
    boolean existsByEmail(String email);

    AuthDto.LoginInfo findId(String id);
}
