package com.gnu.pbl2.interview.controller;

import com.gnu.pbl2.interview.dto.InterviewResponseDto;
import com.gnu.pbl2.interview.service.InterviewService;
import com.gnu.pbl2.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/mojadol/api/v1/interview")
public class InterviewController {

    private final InterviewService interviewService;

    @PostMapping("/upload")
    public ResponseEntity<?> uploadVideo(@RequestParam("video") MultipartFile video,
                                         @RequestParam("id") Long coverLetterId) {

        InterviewResponseDto responseDto = interviewService.saveVideo(video, coverLetterId);

        return ResponseEntity.ok().body(ApiResponse.onSuccess(responseDto));
    }

    @PostMapping("/delete")
    public ResponseEntity<?> deleteVideo(@RequestParam("id") Long interviewId) {

        interviewService.deleteVideo(interviewId);

        return ResponseEntity.ok().body(ApiResponse.onSuccess("삭제되었습니다."));
    }
}
