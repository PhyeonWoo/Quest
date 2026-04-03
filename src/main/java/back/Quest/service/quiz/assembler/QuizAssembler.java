package back.Quest.service.quiz.assembler;

import back.Quest.model.dto.quiz.QuizDto;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

public class QuizAssembler {
    public static List<QuizDto.QuizResponse> toGroup(List<QuizDto.QuizFlatResponse> list) {
        return list.stream()
                .collect(Collectors.groupingBy(
                        QuizDto.QuizFlatResponse::quizNo,
                        LinkedHashMap::new,
                        Collectors.toList()
                ))
                .entrySet().stream()
                .map(e -> new QuizDto.QuizResponse(
                        e.getKey(),
                        e.getValue().getFirst().memberNo(),
                        e.getValue().getFirst().question(),
                        e.getValue().stream()
                                .map(flat -> new QuizDto.DistractResponse(
                                        flat.distractNo(),
                                        flat.quizNo(),
                                        flat.validation(),
                                        flat.text()
                                ))
                                .toList()
                ))
                .toList();
    }

    private QuizAssembler() {}
}