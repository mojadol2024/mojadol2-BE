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

            return new CoverLetterResponseDto(savedCoverLetter);
        } catch (CoverLetterHandler e) {
            throw e;
        } catch (Exception e) {
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

            return new CoverLetterResponseDto(updatedCoverLetter);
        } catch (CoverLetterHandler e) {
            throw e;
        } catch (Exception e) {
            throw new CoverLetterHandler(ErrorStatus.COVER_LETTER_INTERNAL_SERVER_ERROR);
        }
    }

    public void letterDelete(CoverLetterRequestDto coverLetterRequestDto, Long id) {
        try {
            CoverLetter coverLetter = coverLetterRepository.findById(coverLetterRequestDto.getCoverLetterId())
                    .orElseThrow(() -> new CoverLetterHandler(ErrorStatus.COVER_LETTER_NOT_FOUND));

            letterUserCheck(coverLetter.getUser(), id);
            coverLetter.setIsDeleted(0);
            coverLetterRepository.save(coverLetter);
        } catch (CoverLetterHandler e) {
            throw e;
        } catch (Exception e) {
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

            return result;

        } catch (Exception e) {
            throw new CoverLetterHandler(ErrorStatus.COVER_LETTER_INTERNAL_SERVER_ERROR);
        }
    }

    public CoverLetterResponseDto letterDetail(Long coverLetterId, Long userId) {
        try {
            CoverLetter coverLetter = coverLetterRepository.findByCoverLetterIdAndIsDeleted(coverLetterId, 1)
                    .orElseThrow(() -> new CoverLetterHandler(ErrorStatus.COVER_LETTER_NOT_FOUND));

            letterUserCheck(coverLetter.getUser(), userId);

            return new CoverLetterResponseDto(coverLetter);
        } catch (CoverLetterHandler e) {
            throw e;
        } catch (Exception e) {
            throw new CoverLetterHandler(ErrorStatus.COVER_LETTER_INTERNAL_SERVER_ERROR);
        }
    }

    public Map<String, Object> checkSpelling(String text) {
        String url = "https://m.search.naver.com/p/csearch/ocontent/util/SpellerProxy";

        String passportKey = spellCheckerUtil.getPassportKey();
        try {
            RestTemplate restTemplate = new RestTemplate();

            // 요청 파라미터 설정
            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add("q", text);
            params.add("where", "nexearch");
            params.add("color_blindness", "0");
            params.add("passportKey", passportKey);

            // HTTP 요청 헤더 설정
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            headers.add("User-Agent", "Mozilla/5.0");

            HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(params, headers);

            // API 요청
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

            // JSON 문자열 가져오기
            String responseBody = response.getBody();

            // JSON 파싱하여 Map으로 변환
            ObjectMapper objectMapper = new ObjectMapper();

            Map<String, Object> resultMap = objectMapper.readValue(responseBody, Map.class);

            // 필요한 데이터 추출
            Map<String, Object> message = (Map<String, Object>) resultMap.get("message");
            Map<String, Object> result = (Map<String, Object>) message.get("result");

            // 최종 결과 Map 생성
            Map<String, Object> finalResult = Map.of(
                    "errata_count", result.get("errata_count"),
                    "origin_html", result.get("origin_html"),
                    "html", result.get("html"),
                    "notag_html", result.get("notag_html")
            );
            return finalResult;
        } catch (Exception e) {
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
