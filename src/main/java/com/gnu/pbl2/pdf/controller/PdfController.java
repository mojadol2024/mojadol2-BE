package com.gnu.pbl2.pdf.controller;

import com.gnu.pbl2.pdf.service.PdfService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/mojadol/api/v1/pdf")
public class PdfController {

    private final PdfService pdfService;
}
