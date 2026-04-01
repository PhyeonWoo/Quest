package back.Quest.service.missing.impl;

import back.Quest.config.exception.CustomException;
import back.Quest.mapper.missing.MissingMapper;
import back.Quest.model.dto.missing.MissingDto;
import back.Quest.model.dto.missing.MissingStatus;
import back.Quest.service.missing.MissingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class MissingServiceImpl implements MissingService {
    private final MissingMapper missingMapper;



    @Override
    @Transactional
    public void insertMissing(MissingDto.MissingRequest request) {
        log.info("Insert Missing Request");

        int insertMissing = missingMapper.insertMissing(request);

        if (insertMissing == 0) {
            log.error("Insert Missing Request Error");
            throw new CustomException.InvalidRequestException("Insert Missing Request Error");
        }

        log.info("Insert Missing Success");
    }


    @Override
    @Transactional
    public void updateMissing(Long missingNo, MissingDto.MissingRequest request) {
        if (!missingNo.equals(request.missingNo())) {
            log.error("ID Mismatch Error: Path={}, Body={}", missingNo, request.missingNo());
            throw new CustomException.InvalidRequestException("요청 식별자가 일치하지 않습니다.");
        }

        log.info("Update Missing Request for ID: {}", missingNo);

        MissingDto.MissingResponse current = missingMapper.findById(missingNo);
        if (current == null) {
            log.warn("Missing report not found ID: {}", missingNo);
            throw new CustomException.InvalidRequestException("해당 게시글이 존재하지 않습니다.");
        }

        if (current.status() == MissingStatus.CLOSED) {
            log.warn("Attempted to update a CLOSED case ID: {}", missingNo);
            throw new CustomException.InvalidRequestException("이미 종결된 사건은 수정할 수 없습니다.");
        }

        int result = missingMapper.updateMissing(missingNo, request);
        if (result == 0) {
            log.error("Database update failed for ID: {}", missingNo);
            throw new CustomException.InvalidRequestException("수정 처리 중 오류가 발생했습니다.");
        }

        log.info("Update Missing Success: {}", missingNo);
    }


    @Override
    @Transactional
    public void deleteMissing(Long missingNo) {
        log.info("Delete Missing Request");

        MissingDto.MissingResponse current = missingMapper.findById(missingNo);
        if (current == null) {
            log.warn("Missing not found ID : {}",missingNo);
            throw new CustomException.InvalidRequestException("해당 게시글이 존재하지 않습니다.");
        }
        if (current.status() == MissingStatus.CLOSED) {
            log.warn("Attempted to update a CLOSED case ID : {}",missingNo);
            throw new CustomException.InvalidRequestException("종결된 사건은 수정이 불가능 합니다.");
        }

        int result = missingMapper.deleteMissing(missingNo);

        if (result == 0) {
            log.error("Delete failed: ID: {}", missingNo);
            throw new CustomException.InvalidRequestException("삭제 대상을 찾을 수 없습니다.");
        }

        log.info("Delete Missing Success: {}", missingNo);
    }



    @Override
    public List<MissingDto.MissingResponse> findAll() {
        log.info("Find MissingList Request");
        List<MissingDto.MissingResponse> response = missingMapper.findAll();

        if (response == null || response.isEmpty()) {
            log.warn("Not Found");
            return Collections.emptyList();
        }

        log.info("Missing List Response Success : {}",response.size());
        return response;
    }

    @Override
    public List<MissingDto.MissingResponse> findByKeyword(MissingDto.MissingSearchRequest request) {
        if (request.keywords() == null) {
            throw new CustomException.InvalidRequestException("오류");
        }
        List<MissingDto.MissingResponse> responses = missingMapper.findSearch(request);

        if (responses == null || responses.isEmpty()) {
            throw new CustomException.NotFoundException("존재하지 않음");
        }
        log.info("Search Missing List Response Success : {}",responses.size());
        return responses;
    }
}
