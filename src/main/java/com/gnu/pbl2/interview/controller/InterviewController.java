package com.gnu.pbl2.interview.controller;

import com.gnu.pbl2.interview.dto.InterviewResponseDto;
import com.gnu.pbl2.interview.service.InterviewService;
import com.gnu.pbl2.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/mojadol/api/v1/interview")
public class InterviewController {

    private final InterviewService interviewService;

    @PostMapping("/upload")
    public ResponseEntity<?> uploadVideo(@RequestParam("video") MultipartFile video,
                                         @RequestParam("id") Long coverLetterId) {
        log.info("uploadVideo 요청 진입: coverLetterId={}", coverLetterId);

        InterviewResponseDto responseDto = interviewService.saveVideo(video, coverLetterId);

        return ResponseEntity.ok().body(ApiResponse.onSuccess(responseDto));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteVideo(@PathVariable Long id) {
        log.info("deleteVideo 요청 진입: interviewId={}", id);

        interviewService.deleteVideo(id);

        return ResponseEntity.ok().body(ApiResponse.onSuccess("삭제되었습니다."));
    }

    @GetMapping("/list/{id}")
    public ResponseEntity<?> interviewsList(@PathVariable Long id) {
        log.info("interviewsList 요청 진입: coverLetterId={}", id);

        List<InterviewResponseDto> responseDtos = interviewService.interviewsList(id);

        return ResponseEntity.ok().body(ApiResponse.onSuccess(responseDtos));
    }

    @GetMapping("/detail/{id}")
    public ResponseEntity<?> interviewDetail(@PathVariable Long id) {
        log.info("interviewDetail 요청 진입: interviewId={}", id);

        InterviewResponseDto response = interviewService.interviewDetail(id);

        return ResponseEntity.ok().body(ApiResponse.onSuccess(response));
    }
}

