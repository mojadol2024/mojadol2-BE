package com.gnu.pbl2.coverLetter.service;

import com.gnu.pbl2.coverLetter.dto.CoverLetterRequestDto;
import com.gnu.pbl2.coverLetter.dto.CoverLetterResponseDto;
import com.gnu.pbl2.coverLetter.entity.CoverLetter;
import com.gnu.pbl2.coverLetter.repository.CoverLetterRepository;
import com.gnu.pbl2.exception.handler.CoverLetterHandler;
import com.gnu.pbl2.response.code.status.ErrorStatus;
import com.gnu.pbl2.user.entity.User;
import com.gnu.pbl2.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class CoverLetterService {

    private final CoverLetterRepository coverLetterRepository;
    private final UserRepository userRepository;

    public CoverLetterResponseDto letterWrite(CoverLetterRequestDto coverLetterRequestDto) {
        try {

            User user = userRepository.findById(coverLetterRequestDto.getUserId())
                    .orElseThrow(() -> new CoverLetterHandler(ErrorStatus.COVER_LETTER_INTERNAL_SERVER_ERROR));

            CoverLetter coverLetter = new CoverLetter(coverLetterRequestDto.getData(), user, coverLetterRequestDto.getTitle());
            CoverLetter savedCoverLetter = coverLetterRepository.save(coverLetter);

            return new CoverLetterResponseDto(savedCoverLetter);

        } catch (Exception e) {
            throw new CoverLetterHandler(ErrorStatus.COVER_LETTER_INTERNAL_SERVER_ERROR);
        }
    }

    public CoverLetterResponseDto letterUpdate(CoverLetterRequestDto coverLetterRequestDto) {
        try {
            CoverLetter coverLetter = coverLetterRepository.findById(coverLetterRequestDto.getCoverLetterId())
                    .orElseThrow(() -> new CoverLetterHandler(ErrorStatus.COVER_LETTER_NOT_FOUND));

            coverLetter.setTitle(coverLetterRequestDto.getTitle());
            coverLetter.setData(coverLetterRequestDto.getData());
            CoverLetter updatedCoverLetter = coverLetterRepository.save(coverLetter);

            return new CoverLetterResponseDto(updatedCoverLetter);

        } catch (Exception e) {
            throw new CoverLetterHandler(ErrorStatus.COVER_LETTER_INTERNAL_SERVER_ERROR);
        }
    }

    public void letterDelete(CoverLetterRequestDto coverLetterRequestDto) {
        try {
            CoverLetter coverLetter = coverLetterRepository.findById(coverLetterRequestDto.getCoverLetterId())
                    .orElseThrow(() -> new CoverLetterHandler(ErrorStatus.COVER_LETTER_NOT_FOUND));

            coverLetterRepository.delete(coverLetter);

        } catch (Exception e) {
            throw new CoverLetterHandler(ErrorStatus.COVER_LETTER_INTERNAL_SERVER_ERROR);
        }
    }

    public Map<String, Object> letterList(Pageable pageable, Long coverLetterId) {
        try {
            Page<CoverLetter> page = coverLetterRepository.findByCoverLetterId(coverLetterId, pageable);


            List<CoverLetterResponseDto> content = page.getContent().stream()
                    .map(CoverLetterResponseDto::new)
                    .collect(Collectors.toList());

            Map<String, Object> result = new HashMap<>();
            result.put("content", content);  // ✅ 변환된 DTO 리스트 추가
            result.put("first", page.isFirst());
            result.put("last", page.isLast());

            return result;

        } catch (Exception e) {
            throw new CoverLetterHandler(ErrorStatus.COVER_LETTER_INTERNAL_SERVER_ERROR);
        }
    }

    public CoverLetterResponseDto letterDetail(Long id) {
        try {

            CoverLetter coverLetter = coverLetterRepository.findById(id)
                    .orElseThrow(() -> new CoverLetterHandler(ErrorStatus.COVER_LETTER_NOT_FOUND));

            return new CoverLetterResponseDto(coverLetter);

        } catch (Exception e) {
            throw new CoverLetterHandler(ErrorStatus.COVER_LETTER_INTERNAL_SERVER_ERROR);
        }
    }
}
