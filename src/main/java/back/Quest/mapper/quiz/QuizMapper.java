package back.Quest.mapper.quiz;

import back.Quest.model.dto.quiz.QuizDto;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface QuizMapper {
    // 퀴즈 추가
    void insertQuiz(
            @Param("memberNo") Long memberNo,
            @Param("request") QuizDto.QuizRequest request
    );
    Long selectLastInsertId();

    // 퀴즈 보기 추가
    int insertDistract(
            @Param("quizNo") Long quizNo,
            @Param("request") List<QuizDto.QuizDistractRequest> request
    );

    // 퀴즈 삭제
    int deleteQuiz(
            @Param("memberNo") Long memberNo,
            @Param("quizNo") Long quizNo
    );

    // memberNo로 내가 쓴 퀴즈 전체 조회 (flat 데이터)
    List<QuizDto.QuizFlatResponse> myQuiz(Long memberNo);


    // quizNo로 퀴즈 단건 조회 (flat 데이터)
    List<QuizDto.QuizFlatResponse>  findById(Long quizNo);

//    /**
//     * 문제 ID로 모든 선택지 조회
//     */
//    List<QuizDto.DistractResponse> selectDistractsByQuestionId(@Param("questionId") Long quizNo);
//
//    /**
//     * 퀴즈를 평탄화된 형식으로 조회
//     */
//    List<QuizDto.QuizFlatResponse> selectQuizFlatByQuizNo(@Param("quizNo") Long quizNo);
}