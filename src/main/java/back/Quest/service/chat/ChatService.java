package back.Quest.service.chat;

import back.Quest.model.dto.chat.ChatDto;

import java.util.List;

public interface ChatService {

    ChatDto.ChatRoomResponse createRoom(Long memberNo, ChatDto.ChatRoomRequest request);

    void deleteChatRoom(Long memberNo, Long roomId);

    void markRead(Long memberNo, Long roomId, Long messageId);

    int unReadCount(Long roomId, Long memberNo);

    List<ChatDto.ChatRoomResponse> myChatRoom(Long memberNo);

    List<ChatDto.ChatRoomResponse> findAll();

    void joinRoom(Long roomId, Long memberNo, String password);

    void validateRoom(Long roomId, Long senderId);
}
