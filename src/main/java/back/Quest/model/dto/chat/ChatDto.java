package back.Quest.model.dto.chat;

import java.time.LocalDateTime;

public class ChatDto {

    public enum MessageType{
        CHAT, ENTER, LEAVE
    }

    public record ChatRoomRequest(
            String name,
            String password
    ) {
        public static ChatRoomRequest of(String name, String password) {
            return new ChatRoomRequest(name,password);
        }
    }


    public record ChatRoomResponse(
            Long roomId,
            Long memberNo, // 생성자
            String name // 방 이름

    ) {}


    // Kafka 전송용
    public record MessageRequest(
            Long roomId,
            Long senderId, // 메시지 전송자 (memberNo)
            String senderNickname, // 메시지 전송자 닉네임 (QUEST_MEMBER의 nickname)
            String content, // 내영
            MessageType type
    ){}


    public record JoinRequest(
            String password
    ) {}

    // 채팅 응답
    public record MessageResponse(
            Long messageId,
            Long roomId,
            Long senderId,
            String senderNickname,
            String content,
            MessageType type,
            LocalDateTime createdDt
    ) {}
}
