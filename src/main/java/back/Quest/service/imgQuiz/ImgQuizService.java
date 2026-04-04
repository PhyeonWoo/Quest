package back.Quest.service.imgQuiz;

import back.Quest.model.dto.imgQuiz.ImgQuizDto;
import back.Quest.model.dto.quiz.QuizDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ImgQuizService {
    void insertImgQuiz(Long memberNo, MultipartFile imgUrl, ImgQuizDto.ImgQuizRequest request);
    void deleteImgQuiz(Long memberNo, Long imgQuizNo);

    List<ImgQuizDto.ImgQuizResponse> myQuiz(Long memberNo);

    ImgQuizDto.ImgQuizResponse findById(Long imgQuizNo);

    ImgQuizDto.ImgSolveResponse solveImgQuiz(Long memberNo, Long imgQuizNo, ImgQuizDto.ImgSolveRequest request);

}
