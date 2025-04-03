package com.gnu.pbl2.interview.controller;

import com.gnu.pbl2.interview.dto.InterviewResponseDto;
import com.gnu.pbl2.interview.service.InterviewService;
import com.gnu.pbl2.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

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

    @GetMapping("/delete")
    public ResponseEntity<?> deleteVideo(@RequestParam("id") Long interviewId) {

        interviewService.deleteVideo(interviewId);

        return ResponseEntity.ok().body(ApiResponse.onSuccess("삭제되었습니다."));
    }

    // 본인 자소서 1개당 질문에 대한 영상 list pagenation안해도 될거 같음 자소서당 질문이 많아도 15개니까
    @GetMapping("/list")
    public ResponseEntity<?> interviewsList(@RequestParam("id") Long coverLetterId) {

        List<InterviewResponseDto> responseDtos = interviewService.interviewsList(coverLetterId);

        return ResponseEntity.ok().body(ApiResponse.onSuccess(responseDtos));
    }

    // 영상 재생 할 수 있는 상세 페이지
    @GetMapping("/detail")
    public ResponseEntity<?> interviewDetail(@RequestParam("id") Long interviewId) {
        InterviewResponseDto response = interviewService.interviewDetail(interviewId);

        return ResponseEntity.ok().body(ApiResponse.onSuccess(response));
    }

}
