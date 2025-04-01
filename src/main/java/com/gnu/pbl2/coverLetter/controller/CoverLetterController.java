package com.gnu.pbl2.coverLetter.controller;

import com.gnu.pbl2.coverLetter.dto.CoverLetterRequestDto;
import com.gnu.pbl2.coverLetter.dto.CoverLetterResponseDto;
import com.gnu.pbl2.coverLetter.service.CoverLetterService;
import com.gnu.pbl2.exception.handler.CoverLetterHandler;
import com.gnu.pbl2.response.ApiResponse;
import com.gnu.pbl2.response.code.status.ErrorStatus;
import com.gnu.pbl2.utils.JwtUtil;
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
    private final JwtUtil jwtUtil;

    // 자소서 작성 유저토큰 파싱해서 그 유저의 게시글만 글쓰기 할 수 있어야함
    @PostMapping("/write")
    public ResponseEntity<?> letterWrite(@RequestHeader("Authorization") String accessToken, @RequestBody CoverLetterRequestDto coverLetterRequestDto) {
        Long userId = jwtUtil.extractUserId(accessToken);

        CoverLetterResponseDto responseDto = coverLetterService.letterWrite(coverLetterRequestDto, userId);

        return ResponseEntity.ok(ApiResponse.onSuccess(responseDto));
    }

    // 자소서 업데이트 유저토큰 파싱해서 그 유저의 게시글만 수정 할 수 있어야함
    @PostMapping("/update")
    public ResponseEntity<?> letterUpdate(@RequestHeader("Authorization") String accessToken, @RequestBody CoverLetterRequestDto coverLetterRequestDto) {
        Long userId = jwtUtil.extractUserId(accessToken);

        CoverLetterResponseDto responseDto = coverLetterService.letterUpdate(coverLetterRequestDto, userId);

        return ResponseEntity.ok(ApiResponse.onSuccess(responseDto));
    }

    // 자소서 삭제 유저토큰 파싱해서 그 유저의 게시글만 삭제 할 수 있어야함
    @PostMapping("/delete")
    public ResponseEntity<?> letterDelete(@RequestHeader("Authorization") String accessToken, @RequestBody CoverLetterRequestDto coverLetterRequestDto) {
        Long userId = jwtUtil.extractUserId(accessToken);
        coverLetterService.letterDelete(coverLetterRequestDto, userId);

        return ResponseEntity.ok(ApiResponse.onSuccess("삭제 성공"));
    }

    // 유저토큰 파싱해서 그 유저의 게시글만 보여줘야함
    @GetMapping("/list")
    public ResponseEntity<?> letterList(@RequestParam(defaultValue = "0") int page,
                                        @RequestParam(defaultValue = "9") int size,
                                        @RequestHeader("Authorization") String accessToken) {

        Long userId = jwtUtil.extractUserId(accessToken);
        Pageable pageable = PageRequest.of(page, size);
        Map<String, Object> response =  coverLetterService.letterList(pageable, userId);

        return ResponseEntity.ok(ApiResponse.onSuccess(response));
    }


    // 유저토큰 파싱해서 유저의 게시글이 맞는지 확인 후 보여줘야함
    @GetMapping("/detail")
    public ResponseEntity<?> letterDetail(@RequestHeader("Authorization") String accessToken,
                                          @RequestParam Long coverLetterId) {
        Long userId = jwtUtil.extractUserId(accessToken);
        CoverLetterResponseDto response = coverLetterService.letterDetail(coverLetterId, userId);

        return ResponseEntity.ok(ApiResponse.onSuccess(response));
    }

    // 맞춤법 검사기
    @PostMapping("/SpellChecker")
    public ResponseEntity<?> SpellChecker(@RequestBody CoverLetterRequestDto coverLetterRequestDto) {
        Map<String, Object> response = coverLetterService.checkSpelling(coverLetterRequestDto.getData());

        return ResponseEntity.ok(ApiResponse.onSuccess(response));
    }

}
