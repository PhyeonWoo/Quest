package back.Quest.config.kafka;

import back.Quest.mapper.chat.ChatMapper;
import back.Quest.model.dto.chat.ChatDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatConsumer {
    private final ChatMapper chatMapper;
    private final ObjectMapper objectMapper;
    private final SimpMessagingTemplate simpMessagingTemplate;

    @Transactional
    @KafkaListener(topics = "chat", groupId = "chat")
    public void consumer(String payload) {
        try{
            ChatDto.MessageRequest request = objectMapper.readValue(payload, ChatDto.MessageRequest.class);

            // useGeneratedKeys로 INSERT와 동시에 ID 반환 → lastInsertId() 별도 쿼리 제거
            Map<String, Object> params = new HashMap<>();
            params.put("roomId", request.roomId());
            params.put("senderId", request.senderId());
            params.put("senderNickname", request.senderNickname());
            params.put("content", request.content());
            params.put("type", request.type());
            chatMapper.insertMessageGetId(params);
            Object rawId = params.get("messageId");
            if (rawId == null) {
                log.error("Consumer: useGeneratedKeys returned null messageId");
                return;
            }
            Long id = ((Number) rawId).longValue();

            ChatDto.MessageResponse response = chatMapper.selectMessage(id);
            if (response == null) {
                log.error("Consumer: selectMessage returned null for id={}", id);
                return;
            }

            simpMessagingTemplate.convertAndSend("/sub/chat/room/" + request.roomId(), response);

        } catch (Exception e) {
            log.error("Consumer 처리 실패: {}", e.getMessage(), e);
        }
    }

}
