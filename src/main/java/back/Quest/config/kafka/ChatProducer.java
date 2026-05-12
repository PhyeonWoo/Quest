package back.Quest.config.kafka;

import back.Quest.model.dto.chat.ChatDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatProducer {
    private final ObjectMapper objectMapper;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final String TOPIC = "chat";

    public void send(ChatDto.MessageRequest request) {
        try {
            String json = objectMapper.writeValueAsString(request);

            kafkaTemplate.send(TOPIC, request.roomId().toString(), json)
                    .whenComplete((result, ex) -> {
                        if(ex != null) {
                            log.error("전송 실패");
                        }
                    });

        } catch (JsonProcessingException e) {
            log.error("Kafka 실패");
        }
    }
}
