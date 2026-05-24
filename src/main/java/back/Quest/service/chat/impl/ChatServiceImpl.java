package back.Quest.service.chat.impl;

import back.Quest.config.exception.CustomException;
import back.Quest.mapper.chat.ChatMapper;
import back.Quest.model.dto.chat.ChatDto;
import back.Quest.service.chat.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatServiceImpl implements ChatService {
    private final ChatMapper chatMapper;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;


    @Override
    @Transactional
    public ChatDto.ChatRoomResponse createRoom(Long memberNo, ChatDto.ChatRoomRequest request) {
        String pw = (request.password() != null) ? bCryptPasswordEncoder.encode(request.password()) : null;

        chatMapper.createRoom(memberNo, ChatDto.ChatRoomRequest.of(
                request.name(),
                pw
        ));
        Long roomId = chatMapper.lastInsertId();
        chatMapper.joinRoom(roomId, memberNo);
        return new ChatDto.ChatRoomResponse(roomId, memberNo, request.name());
    }

    @Override
    @Transactional
    public void deleteChatRoom(Long memberNo, Long roomId) {
        ChatDto.ChatRoomResponse response = chatMapper.singleRoom(roomId);
        if(response == null) {
            throw new CustomException.NotFoundException("존재하지 않는 채팅방입니다.");
        }
        if(!response.memberNo().equals(memberNo)) {
            throw new CustomException.InvalidRequestException("권한 없음");
        }
        chatMapper.deleteRoom(memberNo,roomId);
    }

    @Override
    public void markRead(Long memberNo, Long roomId, Long messageId) {
        if(messageId == null || messageId <= 0) {
            log.error("유효하지 않은 메시지입니다.");
            throw new CustomException.InvalidRequestException("유효하지 않은 메시지입니다.");
        }
        chatMapper.updateRead(memberNo, roomId, messageId);
    }

    @Override
    public int unReadCount(Long roomId, Long memberNo) {
        if(!chatMapper.existRoomMember(memberNo, roomId)) {
            throw new CustomException.InvalidRequestException("채팅방 회원이 아닙니다.");
        }
        return chatMapper.unReadCount(memberNo, roomId);
    }

    @Override
    public List<ChatDto.ChatRoomResponse> myChatRoom(Long memberNo) {
        List<ChatDto.ChatRoomResponse> response = chatMapper.myChatRoom(memberNo);

        if(response.isEmpty()) {
            log.warn("존재하지 않음");
            return List.of();
        }
        return response;
    }

    @Override
    public List<ChatDto.ChatRoomResponse> findAll() {
        List<ChatDto.ChatRoomResponse> response = chatMapper.findAll();
        if (response.isEmpty()) {
            log.warn("존재하지 않음");
            return List.of();
        }
        return response;
    }

    @Override
    public void joinRoom(Long roomId, Long memberNo, String password) {
        if(chatMapper.existRoomMember(memberNo, roomId)) {
            throw new CustomException.DuplicateException("이미 참여중인 채팅방입니다.");
        }
        String storedPassword = chatMapper.findRoomPassword(roomId);
        if (storedPassword != null && !bCryptPasswordEncoder.matches(password == null ? "" : password, storedPassword)) {
            throw new CustomException.InvalidRequestException("비밀번호가 틀렸습니다.");
        }
        chatMapper.joinRoom(roomId, memberNo);
    }

    @Override
    public void validateRoom(Long roomId, Long senderId) {
        ChatDto.ChatRoomResponse room = chatMapper.singleRoom(roomId);

        if(room == null) {
            log.warn("존재하지 않음");
            throw new CustomException.NotFoundException("존재하지 않는 채팅방입니다.");
        }
        if(!chatMapper.existRoomMember(senderId, roomId)) {
            log.warn("멤버가 아닙니다.");
            throw new CustomException.InvalidRequestException("채팅방 멤버가 아닙니다.");
        }

    }

    // 채팅방 나가기
    @Override
    public void leaveChat(Long roomId, Long memberNo) {
        boolean exists = chatMapper.existRoomMember(roomId, memberNo);
        if(!exists) {
            throw new CustomException.NotFoundException("존재하지 않습니다.");
        }
        chatMapper.leaveChat(roomId, memberNo);
    }

}
