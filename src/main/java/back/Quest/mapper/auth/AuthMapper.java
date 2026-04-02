package back.Quest.mapper.auth;

import back.Quest.model.dto.auth.AuthDto;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface AuthMapper {
    //LOGIN DB 생성
    @Insert("INSERT INTO RECALL_LOGIN (member_no, id, pw, role)" +
            "VALUES (#{memberNo}, #{id}, #{pw}, 'ROLE_USER)")
    void insertLogin(AuthDto.LoginCreateRequest request);

    // MEMBER DB 생성
    @Insert("INSERT INTO RECALL_MEMBER (email, name, phone_number)" +
            "VALUES (#{email}, #{name}, #{phoneNumber})")
    void insertMember(AuthDto.MemberCreateRequest request);

    //마지막 ID 조회
    @Select("SELECT LAST_INSERT_ID()")
    Long lastInsertId();

    boolean existsById(String id);
    boolean existsByEmail(String email);

    AuthDto.LoginInfo findId(String id);
}
