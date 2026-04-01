package back.Quest.mapper.missing;

import back.Quest.model.dto.missing.MissingDto;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface MissingMapper {

    int insertMissing(MissingDto.MissingRequest request);

    int updateMissing(
            @Param("missingNo") Long missingNo,
            @Param("request") MissingDto.MissingRequest request
    );


    int deleteMissing(Long missingNo);

    List<MissingDto.MissingResponse> findAll();

    MissingDto.MissingResponse findById(Long missingNo);

    List<MissingDto.MissingResponse> findSearch(MissingDto.MissingSearchRequest request);
}
