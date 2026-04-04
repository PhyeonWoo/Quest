package back.Quest.mapper.imgQuiz;

import back.Quest.model.dto.imgQuiz.ImgQuizDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ImgQuizMapper {

    //이미지 퀴즈 생성
    void insertImgQuiz(
            @Param("memberNo") Long memberNo,
            @Param("imgUrl") String imgUrl,
            @Param("request") ImgQuizDto.ImgQuizRequest request
    );

    //ID 주입
    Long selectLastId();

    // 이미지 퀴즈 보기 생성
    int insertImgDistract(
            @Param("imgQuizNo") Long imgQuizNo,
            @Param("request") List<ImgQuizDto.ImgQuizDistractRequest> request
    );

    // 이미지 퀴즈 삭제
    int deleteImgQuiz(
            @Param("memberNo") Long memberNo,
            @Param("imgQuizNo") Long imgQuizNo
    );

    // memberNo로 내가 쓴 퀴즈 전체 조회
    List<ImgQuizDto.ImgQuizFlatResponse> myQuiz(Long memberNo);

    // 이미지 퀴즈번호로 단건 조회
    List<ImgQuizDto.ImgQuizFlatResponse> findById(Long imgQuizNo);
}
