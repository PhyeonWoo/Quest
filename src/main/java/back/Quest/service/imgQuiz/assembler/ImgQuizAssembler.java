package back.Quest.service.imgQuiz.assembler;

import back.Quest.model.dto.imgQuiz.ImgQuizDto;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

public class ImgQuizAssembler {
    public static List<ImgQuizDto.ImgQuizResponse> toGroup(List<ImgQuizDto.ImgQuizFlatResponse> list) {
        return list.stream()
                .collect(Collectors.groupingBy(
                        ImgQuizDto.ImgQuizFlatResponse::imgQuizNo,
                        LinkedHashMap::new,
                        Collectors.toList()
                ))
                .entrySet().stream()
                .map(e -> new ImgQuizDto.ImgQuizResponse(
                        e.getKey(),
                        e.getValue().getFirst().memberNo(),
                        e.getValue().getFirst().imgUrl(),
                        e.getValue().getFirst().question(),
                        e.getValue().stream()
                                .map(flat -> new ImgQuizDto.ImgQuizDistractResponse(
                                        flat.imgDistractNo(),
                                        flat.imgQuizNo(),
                                        flat.validation(),
                                        flat.text()
                                ))
                                .toList()
                ))
                .toList();
    }
    private ImgQuizAssembler() {}
}
