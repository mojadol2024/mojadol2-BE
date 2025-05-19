package com.gnu.pbl2.question.controller;

import com.gnu.pbl2.question.service.QuestionService;
import com.gnu.pbl2.response.ApiResponse;
import com.gnu.pbl2.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/mojadol/api/v1/question")
public class QuestionController {

    private final QuestionService questionService;
    private final JwtUtil jwtUtil;


    @GetMapping("/detail/{id}")
    public ResponseEntity<?> questionDetail(@PathVariable Long id,
                                            @RequestHeader("Authorization") String accessToken) {

        Long userId = jwtUtil.extractUserId(accessToken);

        Map<String, Object> response = questionService.questionDetail(id, userId);

        return ResponseEntity.ok().body(ApiResponse.onSuccess(response));
    }

}
