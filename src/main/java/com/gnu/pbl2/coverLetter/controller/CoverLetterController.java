package com.gnu.pbl2.coverLetter.controller;

import com.gnu.pbl2.coverLetter.dto.CoverLetterRequestDto;
import com.gnu.pbl2.coverLetter.dto.CoverLetterResponseDto;
import com.gnu.pbl2.coverLetter.service.CoverLetterService;
import com.gnu.pbl2.exception.handler.CoverLetterHandler;
import com.gnu.pbl2.response.ApiResponse;
import com.gnu.pbl2.response.code.status.ErrorStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("/mojadol/api/v1/letter")
public class CoverLetterController {

    private final CoverLetterService coverLetterService;

    // 자소서 작성
    @PostMapping("/write")
    public ResponseEntity<?> letterWrite(@RequestBody CoverLetterRequestDto coverLetterRequestDto) {
        try {
            CoverLetterResponseDto responseDto = coverLetterService.letterWrite(coverLetterRequestDto);

            return ResponseEntity.ok(ApiResponse.onSuccess(responseDto));

        } catch (Exception e) {
            throw new CoverLetterHandler(ErrorStatus.COVER_LETTER_INTERNAL_SERVER_ERROR);
        }
    }

    // 자소서 업데이트
    @PostMapping("/update")
    public ResponseEntity<?> letterUpdate(@RequestBody CoverLetterRequestDto coverLetterRequestDto) {
        try {
            CoverLetterResponseDto responseDto = coverLetterService.letterUpdate(coverLetterRequestDto);

            return ResponseEntity.ok(ApiResponse.onSuccess(responseDto));

        } catch (Exception e) {
            throw new CoverLetterHandler(ErrorStatus.COVER_LETTER_INTERNAL_SERVER_ERROR);
        }
    }

    // 자소서 삭제
    @PostMapping("/delete")
    public ResponseEntity<?> letterDelete(@RequestBody CoverLetterRequestDto coverLetterRequestDto) {
        try {
            coverLetterService.letterDelete(coverLetterRequestDto);

            return ResponseEntity.ok(ApiResponse.onSuccess("삭제 성공"));

        } catch (Exception e) {
            throw new CoverLetterHandler(ErrorStatus.COVER_LETTER_INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/list")
    public ResponseEntity<?> letterList(@RequestParam(defaultValue = "0") int page,
                                        @RequestParam(defaultValue = "9") int size,
                                        @RequestParam Long id) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Map<String, Object> response =  coverLetterService.letterList(pageable, id);

            return ResponseEntity.ok(ApiResponse.onSuccess(response));
        } catch (Exception e) {
            throw new CoverLetterHandler(ErrorStatus.COVER_LETTER_INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/detail")
    public ResponseEntity<?> letterDetail(@RequestParam Long id) {
        try {
            CoverLetterResponseDto response = coverLetterService.letterDetail(id);

            return ResponseEntity.ok(ApiResponse.onSuccess(response));
        } catch (Exception e) {
            throw new CoverLetterHandler(ErrorStatus.COVER_LETTER_INTERNAL_SERVER_ERROR);
        }
    }
}
