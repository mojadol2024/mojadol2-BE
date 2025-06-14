package com.gnu.pbl2.interview.controller;

import com.gnu.pbl2.interview.dto.InterviewResponseDto;
import com.gnu.pbl2.interview.entity.Interview;
import com.gnu.pbl2.interview.service.InterviewService;
import com.gnu.pbl2.response.ApiResponse;
import com.gnu.pbl2.trackingResult.service.TrackingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/mojadol/api/v1/interview")
public class InterviewController {

    private final InterviewService interviewService;
    private final TrackingService trackingService;

    @PostMapping("/upload")
    public ResponseEntity<?> uploadVideo(@RequestParam("video") MultipartFile video,
                                         @RequestParam("id") Long questionId) {
        log.info("uploadVideo 요청 진입: questionId={}", questionId);

        InterviewResponseDto interview = interviewService.saveVideo(video, questionId);

        return ResponseEntity.ok().body(ApiResponse.onSuccess(interview));
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

        Map<String, Object> response = interviewService.interviewDetail(id);

        return ResponseEntity.ok().body(ApiResponse.onSuccess(response));
    }
}

