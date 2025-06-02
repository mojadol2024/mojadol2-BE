package com.gnu.pbl2.pdf.controller;

import com.gnu.pbl2.pdf.service.PdfService;
import com.gnu.pbl2.response.ApiResponse;
import com.gnu.pbl2.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/mojadol/api/v1/pdf")
public class PdfController {

    private final PdfService pdfService;
    private final JwtUtil jwtUtil;


    @GetMapping("/create")
    public ResponseEntity<?> createPdf(@RequestHeader("Authorization") String accessToken,
                                       @RequestParam Long coverLetterId) {

        Long userId = jwtUtil.extractUserId(accessToken);

        byte[] response = pdfService.createPdf(userId, coverLetterId);

        // 답변 피드백

        // 태도 피드백

        return ResponseEntity.ok().body(response);
    }

}
