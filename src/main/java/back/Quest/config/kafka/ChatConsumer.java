package back.Quest.config.kafka;

import back.Quest.mapper.chat.ChatMapper;
import back.Quest.model.dto.chat.ChatDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatConsumer {
    private final ChatMapper chatMapper;
    private final ObjectMapper objectMapper;
    private final SimpMessagingTemplate simpMessagingTemplate;

    @KafkaListener(topics = "chat", groupId = "chat")
    public void consumer(String payload) {
        try{
            ChatDto.MessageRequest request = objectMapper.readValue(payload, ChatDto.MessageRequest.class);

            chatMapper.insertMessage(request);
            Long id = chatMapper.lastInsertId();

            ChatDto.MessageResponse response = chatMapper.selectMessage(id);

            simpMessagingTemplate.convertAndSend("/sub/chat/room/" + request.roomId(), response);

        } catch (Exception e) {
            log.error("Consumer 처리 실패: {}", e.getMessage());
        }
    }

}
