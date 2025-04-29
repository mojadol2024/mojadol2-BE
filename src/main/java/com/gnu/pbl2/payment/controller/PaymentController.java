package com.gnu.pbl2.payment.controller;

import com.gnu.pbl2.payment.dto.PaymentRequestDto;
import com.gnu.pbl2.payment.dto.PaymentResponseDto;
import com.gnu.pbl2.payment.service.PaymentService;
import com.gnu.pbl2.response.ApiResponse;
import com.gnu.pbl2.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/mojadol/api/v1/payment")
public class PaymentController {


    private final PaymentService paymentService;
    private final JwtUtil jwtUtil;


    // 결제, 결제취소? 결제내역, 결제내역 상세
    @PostMapping("/pay")
    public ResponseEntity<?> pay(@RequestBody PaymentRequestDto paymentRequestDto,
                                 @RequestHeader("Authorization") String accessToken) {

        Long userId = jwtUtil.extractUserId(accessToken);

        PaymentResponseDto response = paymentService.pay(paymentRequestDto, userId);

        return ResponseEntity.ok().body(ApiResponse.onSuccess(response));
    }

    @PostMapping("/cancel/{id}")
    public ResponseEntity<?> cancel(@PathVariable Long id,
                                    @RequestHeader("Authorization") String accessToken) {

        Long userId = jwtUtil.extractUserId(accessToken);

        PaymentResponseDto response = paymentService.cancel(id, userId);

        return ResponseEntity.ok().body(ApiResponse.onSuccess(response));
    }

    @GetMapping("/list")
    public ResponseEntity<?> list(@RequestParam(defaultValue = "0") int page,
                                  @RequestParam(defaultValue = "9") int size,
                                  @RequestHeader("Authorization") String accessToken) {

        Long userId = jwtUtil.extractUserId(accessToken);

        Pageable pageable = PageRequest.of(page, size);

        Map<String, Object> response = paymentService.list(userId, pageable);

        return ResponseEntity.ok().body(ApiResponse.onSuccess(response));
    }

    @GetMapping("/detail/{id}")
    public ResponseEntity<?> detail(@PathVariable Long id,
                                    @RequestHeader("Authorization") String accessToken) {

        Long userId = jwtUtil.extractUserId(accessToken);
        PaymentResponseDto response = paymentService.detail(id, userId);

        return ResponseEntity.ok().body(ApiResponse.onSuccess(response));
    }
}
