package back.Quest.controller.chat;

import back.Quest.config.common.ApiResponse;
import back.Quest.config.kafka.ChatProducer;
import back.Quest.model.dto.chat.ChatDto;
import back.Quest.security.JwtProvider;
import back.Quest.service.chat.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class ChatController {
    private final ChatProducer chatProducer;
    private final ChatService chatService;
    private final JwtProvider jwtProvider;


    @MessageMapping("/chat/message")
    public void sendMessage(ChatDto.MessageRequest request) {
        chatService.validateRoom(request.roomId(), request.senderId());
        chatProducer.send(request);
    }

    @MessageMapping("/chat/enter")
    public void enter(ChatDto.MessageRequest request) {
        chatService.validateRoom(request.roomId(), request.senderId());
        chatProducer.send(new ChatDto.MessageRequest(
                request.roomId(),
                request.senderId(),
                request.senderNickname(),
                request.senderNickname() + "님이 입장했습니다.",
                ChatDto.MessageType.ENTER
        ));
    }

    @MessageMapping("/chat/leave")
    public void leave(ChatDto.MessageRequest request) {
        chatService.validateRoom(request.roomId(), request.senderId());
        chatService.leaveChat(request.roomId(), request.senderId());
        chatProducer.send(new ChatDto.MessageRequest(
                request.roomId(),
                request.senderId(),
                request.senderNickname(),
                request.senderNickname() + "님이 퇴장했습니다.",
                ChatDto.MessageType.LEAVE
        ));
    }

    @PostMapping("/api/v1/chat/room")
    public ApiResponse<String> createRoom(
            @RequestHeader("Authorization") String bearerToken,
            @RequestBody ChatDto.ChatRoomRequest request
    ) {
        String token = jwtProvider.resolveToken(bearerToken);
        Long memberNo = jwtProvider.getMemberNo(token);

        chatService.createRoom(memberNo, request);
        return ApiResponse.ok("생성 완료");
    }

    @DeleteMapping("/api/v1/chat/delete/{roomId}")
    public ApiResponse<String> deleteRoom(
            @PathVariable Long roomId,
            @RequestHeader("Authorization") String bearerToken
    ) {
        String token = jwtProvider.resolveToken(bearerToken);
        Long memberNo = jwtProvider.getMemberNo(token);

        chatService.deleteChatRoom(memberNo,roomId);
        return ApiResponse.ok("삭제 완료");
    }


    @GetMapping("/api/v1/chat/myChat")
    public ApiResponse<List<ChatDto.ChatRoomResponse>> myChatRoom(
            @RequestHeader("Authorization") String bearerToken
    ) {
        String token = jwtProvider.resolveToken(bearerToken);
        Long memberNo = jwtProvider.getMemberNo(token);

        List<ChatDto.ChatRoomResponse> response = chatService.myChatRoom(memberNo);
        return ApiResponse.ok(response);
    }

    @GetMapping("/api/v1/chat/all")
    public ApiResponse<List<ChatDto.ChatRoomResponse>> findAll() {
        List<ChatDto.ChatRoomResponse> response = chatService.findAll();
        return ApiResponse.ok(response);
    }

    @PostMapping("/api/v1/chat/{roomId}/read")
    public ApiResponse<?> markRead(
            @RequestHeader("Authorization") String bearerToken,
            @PathVariable Long roomId,
            @RequestParam Long messageId
    ) {
        String token = jwtProvider.resolveToken(bearerToken);
        Long memberNo = jwtProvider.getMemberNo(token);

        chatService.markRead(memberNo, roomId, messageId);
        return ApiResponse.ok("읽음");
    }

    @GetMapping("/api/v1/chat/{roomId}/unread")
    public ApiResponse<Integer> unReadCount(
            @PathVariable Long roomId,
            @RequestHeader("Authorization") String bearerToken
    ) {
        String token = jwtProvider.resolveToken(bearerToken);
        Long memberNo = jwtProvider.getMemberNo(token);

        int count = chatService.unReadCount(roomId, memberNo);
        return ApiResponse.ok(count);
    }


    @PostMapping("/api/v1/chat/join/{roomId}")
    public ApiResponse<?> joinRoom(
            @RequestHeader("Authorization") String bearerToken,
            @PathVariable Long roomId,
            @RequestBody(required = false) ChatDto.JoinRequest request
    ) {
        String token = jwtProvider.resolveToken(bearerToken);
        Long memberNo = jwtProvider.getMemberNo(token);

        String password = (request != null) ? request.password() : null;
        chatService.joinRoom(roomId, memberNo, password);

        return ApiResponse.ok("들어가짐");
    }
}
