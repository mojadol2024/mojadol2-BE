package com.gnu.pbl2.coverLetter.controller;

import com.gnu.pbl2.coverLetter.dto.CoverLetterRequestDto;
import com.gnu.pbl2.coverLetter.dto.CoverLetterResponseDto;
import com.gnu.pbl2.coverLetter.service.CoverLetterService;
import com.gnu.pbl2.response.ApiResponse;
import com.gnu.pbl2.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("/mojadol/api/v1/letter")
@Slf4j
public class CoverLetterController {

    private final CoverLetterService coverLetterService;
    private final JwtUtil jwtUtil;

    @PostMapping("/write")
    public ResponseEntity<?> letterWrite(@RequestHeader("Authorization") String accessToken, @RequestBody CoverLetterRequestDto coverLetterRequestDto) {
        Long userId = jwtUtil.extractUserId(accessToken);
        log.info("자소서 작성 요청: userId={}", userId);

        CoverLetterResponseDto responseDto = coverLetterService.letterWrite(coverLetterRequestDto, userId);
        return ResponseEntity.ok(ApiResponse.onSuccess(responseDto));
    }

    @PatchMapping("/update")
    public ResponseEntity<?> letterUpdate(@RequestHeader("Authorization") String accessToken, @RequestBody CoverLetterRequestDto coverLetterRequestDto) {
        Long userId = jwtUtil.extractUserId(accessToken);
        log.info("자소서 수정 요청: userId={}, coverLetterId={}", userId, coverLetterRequestDto.getCoverLetterId());

        CoverLetterResponseDto responseDto = coverLetterService.letterUpdate(coverLetterRequestDto, userId);
        return ResponseEntity.ok(ApiResponse.onSuccess(responseDto));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> letterDelete(@RequestHeader("Authorization") String accessToken,
                                          @PathVariable Long id) {
        Long userId = jwtUtil.extractUserId(accessToken);
        log.info("자소서 삭제 요청: userId={}, coverLetterId={}", userId, id);

        coverLetterService.letterDelete(id, userId);
        return ResponseEntity.ok(ApiResponse.onSuccess("삭제 성공"));
    }

    @PostMapping("/spell-checker")
    public ResponseEntity<?> spellChecker(@RequestBody CoverLetterRequestDto coverLetterRequestDto) {
        log.info("맞춤법 검사 요청");

        Map<String, Object> response = coverLetterService.checkSpelling(coverLetterRequestDto.getData());
        return ResponseEntity.ok(ApiResponse.onSuccess(response));
    }


    @GetMapping("/list")
    public ResponseEntity<?> letterList(@RequestParam(defaultValue = "0") int page,
                                        @RequestParam(defaultValue = "9") int size,
                                        @RequestHeader("Authorization") String accessToken) {
        Long userId = jwtUtil.extractUserId(accessToken);
        log.info("자소서 리스트 요청: userId={}, page={}, size={}", userId, page, size);

        Pageable pageable = PageRequest.of(page, size);
        Map<String, Object> response =  coverLetterService.letterList(pageable, userId);
        return ResponseEntity.ok(ApiResponse.onSuccess(response));
    }

    @GetMapping("/detail/{id}")
    public ResponseEntity<?> letterDetail(@RequestHeader("Authorization") String accessToken,
                                          @PathVariable Long id) {
        Long userId = jwtUtil.extractUserId(accessToken);
        log.info("자소서 상세 조회 요청: userId={}, coverLetterId={}", userId, id);

        Map<String, Object> response = coverLetterService.letterDetail(id, userId);
        return ResponseEntity.ok(ApiResponse.onSuccess(response));
    }
}
