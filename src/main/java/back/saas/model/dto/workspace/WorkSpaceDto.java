package back.saas.model.dto.workspace;

import java.time.LocalDate;

public class WorkSpaceDto {

    // workspace 요청
    public record CreateRequest(
            Long workSpaceNo,
            Long memberNo,
            String name,
            String description,
            String type, // personal, team
            LocalDate createdDt,
            LocalDate updatedDt,
            LocalDate deletedDt
    ) {}

    // workspace 수정
    public record UpdateRequest(
            Long workSpaceNo,
            Long memberNo,
            String name,
            String description,
            String type, // personal, team
            LocalDate createdDt,
            LocalDate updatedDt,
            LocalDate deletedDt
    ) {}

    /** Controller 응답용 */
    public static class ControllerResponse {
        public Long workspaceNo;
        public String name;
        public String description;
        public String type;
//        public String coverImg;
        public String ownerNickname;
        public LocalDate createdAt;
    }

    /** MyBatis 조회용 - 멤버 목록 */
    public record MemberDetail(
            Long wsMemberNo, //
            Long workspaceNo,
            Long memberNo, //PK DB
            String nickname,  //member DB
            String email, // member DB
            String role, // PK DB
            String joinedAt // PK DB
    ) {}



    /** MyBatis 조회용 flat DTO */
    public record WorkSpaceFlatDto(
            Long workspaceNo,
            Long ownerNo,
            String ownerNickname,
            String name,
            String description,
            String type,
//            String coverImg,
            LocalDate createdDt,
            LocalDate updatedDt
    ) {}




    public record InviteDetail(
            Long inviteNo,
            Long workspaceNo,
            String inviteToken,
            String role,
            String expiredDt,
            int usedCount,
            String inviterNickname // member DB
    ) {}


    /** Controller 응답용 */
    public static class InviteResponse {
        public Long inviteNo;
        public String inviteToken;
        public String inviteLink;
        public String role;
        public String expiredAt;
        public int usedCount;
    }


    /** 초대 링크 생성 요청 */
    public record CreateInviteRequest(
            String role,
//            Integer maxUse,
            int expireHours
    ) {}





}
