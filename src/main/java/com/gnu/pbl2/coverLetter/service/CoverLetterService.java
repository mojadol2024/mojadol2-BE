package com.gnu.pbl2.coverLetter.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gnu.pbl2.coverLetter.dto.CoverLetterRequestDto;
import com.gnu.pbl2.coverLetter.dto.CoverLetterResponseDto;
import com.gnu.pbl2.coverLetter.entity.CoverLetter;
import com.gnu.pbl2.coverLetter.repository.CoverLetterRepository;
import com.gnu.pbl2.exception.handler.CoverLetterHandler;
import com.gnu.pbl2.response.code.status.ErrorStatus;
import com.gnu.pbl2.user.entity.User;
import com.gnu.pbl2.user.repository.UserRepository;
import com.gnu.pbl2.utils.SpellCheckerUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Slf4j
public class CoverLetterService {

    private final CoverLetterRepository coverLetterRepository;
    private final UserRepository userRepository;
    private final SpellCheckerUtil spellCheckerUtil;

    public CoverLetterResponseDto letterWrite(CoverLetterRequestDto coverLetterRequestDto, Long id) {
        try {
            User user = userRepository.findById(id)
                    .orElseThrow(() -> new CoverLetterHandler(ErrorStatus.USER_NOT_FOUND));

            CoverLetter coverLetter = new CoverLetter(coverLetterRequestDto.getData(), user, coverLetterRequestDto.getTitle());
            CoverLetter savedCoverLetter = coverLetterRepository.save(coverLetter);

            log.info("자소서 저장 완료: coverLetterId={}", savedCoverLetter.getCoverLetterId());

            return new CoverLetterResponseDto(savedCoverLetter);
        } catch (CoverLetterHandler e) {
            throw e;
        } catch (Exception e) {
            log.error("자소서 저장 중 내부 에러", e);
            throw new CoverLetterHandler(ErrorStatus.COVER_LETTER_INTERNAL_SERVER_ERROR);
        }
    }

    public CoverLetterResponseDto letterUpdate(CoverLetterRequestDto coverLetterRequestDto, Long id) {
        try {
            CoverLetter coverLetter = coverLetterRepository.findById(coverLetterRequestDto.getCoverLetterId())
                    .orElseThrow(() -> new CoverLetterHandler(ErrorStatus.COVER_LETTER_NOT_FOUND));

            letterUserCheck(coverLetter.getUser(), id);

            coverLetter.setTitle(coverLetterRequestDto.getTitle());
            coverLetter.setData(coverLetterRequestDto.getData());
            CoverLetter updatedCoverLetter = coverLetterRepository.save(coverLetter);

            log.info("자소서 수정 완료: coverLetterId={}", updatedCoverLetter.getCoverLetterId());

            return new CoverLetterResponseDto(updatedCoverLetter);
        } catch (CoverLetterHandler e) {
            throw e;
        } catch (Exception e) {
            log.error("자소서 수정 중 내부 에러", e);
            throw new CoverLetterHandler(ErrorStatus.COVER_LETTER_INTERNAL_SERVER_ERROR);
        }
    }

    public void letterDelete(Long coverLetterId, Long id) {
        try {
            CoverLetter coverLetter = coverLetterRepository.findById(coverLetterId)
                    .orElseThrow(() -> new CoverLetterHandler(ErrorStatus.COVER_LETTER_NOT_FOUND));

            letterUserCheck(coverLetter.getUser(), id);
            coverLetter.setIsDeleted(0);
            coverLetterRepository.save(coverLetter);

            log.info("자소서 삭제 완료: coverLetterId={}", coverLetterId);
        } catch (CoverLetterHandler e) {
            throw e;
        } catch (Exception e) {
            log.error("자소서 삭제 중 내부 에러", e);
            throw new CoverLetterHandler(ErrorStatus.COVER_LETTER_INTERNAL_SERVER_ERROR);
        }
    }

    public Map<String, Object> letterList(Pageable pageable, Long userId) {
        try {
            Page<CoverLetter> page = coverLetterRepository.findByUserId(userId, pageable);

            List<CoverLetterResponseDto> content = page.getContent().stream()
                    .map(CoverLetterResponseDto::new)
                    .collect(Collectors.toList());

            Map<String, Object> result = new HashMap<>();
            result.put("content", content);
            result.put("first", page.isFirst());
            result.put("last", page.isLast());

            log.info("자소서 리스트 조회 성공: userId={}, size={}", userId, content.size());

            return result;
        } catch (Exception e) {
            log.error("자소서 리스트 조회 중 내부 에러", e);
            throw new CoverLetterHandler(ErrorStatus.COVER_LETTER_INTERNAL_SERVER_ERROR);
        }
    }

    public CoverLetterResponseDto letterDetail(Long coverLetterId, Long userId) {
        try {
            CoverLetter coverLetter = coverLetterRepository.findByCoverLetterIdAndIsDeleted(coverLetterId, 1)
                    .orElseThrow(() -> new CoverLetterHandler(ErrorStatus.COVER_LETTER_NOT_FOUND));

            letterUserCheck(coverLetter.getUser(), userId);

            log.info("자소서 상세 조회 성공: coverLetterId={}", coverLetterId);

            return new CoverLetterResponseDto(coverLetter);
        } catch (CoverLetterHandler e) {
            throw e;
        } catch (Exception e) {
            log.error("자소서 상세 조회 중 내부 에러", e);
            throw new CoverLetterHandler(ErrorStatus.COVER_LETTER_INTERNAL_SERVER_ERROR);
        }
    }

    public Map<String, Object> checkSpelling(String text) {
        String url = "https://m.search.naver.com/p/csearch/ocontent/util/SpellerProxy";

        String passportKey = spellCheckerUtil.getPassportKey();
        try {
            RestTemplate restTemplate = new RestTemplate();

            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add("q", text);
            params.add("where", "nexearch");
            params.add("color_blindness", "0");
            params.add("passportKey", passportKey);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            headers.add("User-Agent", "Mozilla/5.0");

            HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(params, headers);

            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

            String responseBody = response.getBody();

            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, Object> resultMap = objectMapper.readValue(responseBody, Map.class);

            Map<String, Object> message = (Map<String, Object>) resultMap.get("message");
            Map<String, Object> result = (Map<String, Object>) message.get("result");

            Map<String, Object> finalResult = Map.of(
                    "errata_count", result.get("errata_count"),
                    "origin_html", result.get("origin_html"),
                    "html", result.get("html"),
                    "notag_html", result.get("notag_html")
            );

            log.info("맞춤법 검사 완료: errata_count={}", result.get("errata_count"));

            return finalResult;
        } catch (Exception e) {
            log.error("맞춤법 검사 중 내부 에러", e);
            throw new CoverLetterHandler(ErrorStatus.SPELLCHECKER_INTERNAL_SERVER_ERROR);
        }
    }

    private boolean letterUserCheck(User user, Long id) {
        if (user.getUserId().equals(id)) {
            return true;
        } else {
            throw new CoverLetterHandler(ErrorStatus.COVER_LETTER_BAD_REQUEST);
        }
    }
}
