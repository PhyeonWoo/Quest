package back.Quest.mapper.chat;

import back.Quest.model.dto.chat.ChatDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ChatMapper {
    Long lastInsertId();

    // 채팅 생성
    void createRoom(
            @Param("creatorId") Long creatorId,
            @Param("request") ChatDto.ChatRoomRequest request
    );

    // 채팅방 삭제
    void deleteRoom(
            @Param("creatorId") Long creatorId,
            @Param("roomId") Long roomId
    );

    // 메시지 전송
    void insertMessage(
            ChatDto.MessageRequest request
    );

    ChatDto.MessageResponse selectMessage(
            Long messageId
    );

    void updateRead(
            @Param("memberNo") Long memberNo,
            @Param("roomId")Long roomId,
            @Param("messageId")Long messageId
    );

    int unReadCount(
            @Param("memberNo") Long memberNo,
            @Param("roomId") Long roomId
    );

    List<ChatDto.ChatRoomResponse> myChatRoom(
            Long memberNo
    );

    ChatDto.ChatRoomResponse singleRoom(
            Long roomId
    );

    List<ChatDto.ChatRoomResponse> findAll();

    boolean existRoomMember(
            @Param("memberNo") Long memberNo,
            @Param("roomId") Long roomId);

    void joinRoom(
            @Param("roomId") Long roomId,
            @Param("memberNo") Long memberNo
    );

    String findRoomPassword(Long roomId);

}
