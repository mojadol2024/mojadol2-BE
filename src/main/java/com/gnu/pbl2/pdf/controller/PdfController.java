package com.gnu.pbl2.pdf.controller;

import com.gnu.pbl2.pdf.service.PdfService;
import com.gnu.pbl2.response.ApiResponse;
import com.gnu.pbl2.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/mojadol/api/v1/pdf-controller")
public class PdfController {

    private final PdfService pdfService;
    private final JwtUtil jwtUtil;


    @GetMapping("/create")
    public ResponseEntity<?> createPdf(@RequestHeader("Authorization") String accessToken,
                                       @RequestParam Long coverLetterId) {
        log.info("[Pdf create] coverLetterId = {}", coverLetterId);

        Long userId = jwtUtil.extractUserId(accessToken);

        byte[] response = pdfService.createPdf(userId, coverLetterId);

        return ResponseEntity.ok().body(response);
    }

}
