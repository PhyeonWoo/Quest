package back.Quest.service.missing;

import back.Quest.model.dto.missing.MissingDto;

import java.util.List;

public interface MissingService {
    void insertMissing(MissingDto.MissingRequest request);
    void updateMissing(Long missingNo, MissingDto.MissingRequest request);
    void deleteMissing(Long missingNo);

    List<MissingDto.MissingResponse> findAll();

    List<MissingDto.MissingResponse> findByKeyword(MissingDto.MissingSearchRequest request);
}
