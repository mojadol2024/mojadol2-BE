package com.gnu.pbl2.payment.service;

import com.gnu.pbl2.exception.handler.PaymentHandler;
import com.gnu.pbl2.exception.handler.UserHandler;
import com.gnu.pbl2.payment.dto.PaymentRequestDto;
import com.gnu.pbl2.payment.dto.PaymentResponseDto;
import com.gnu.pbl2.payment.entity.Payment;
import com.gnu.pbl2.payment.repository.PaymentRepository;
import com.gnu.pbl2.response.code.status.ErrorStatus;
import com.gnu.pbl2.user.entity.User;
import com.gnu.pbl2.user.repository.UserRepository;
import com.gnu.pbl2.voucher.service.VoucherService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository;
    private final VoucherService voucherService;

    public PaymentResponseDto pay(PaymentRequestDto paymentRequestDto, Long userId) {
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> {
                        log.warn("결제 실패 - 사용자 없음: {}", userId);
                        return new UserHandler(ErrorStatus.USER_NOT_FOUND);
                    });

            Payment payment = new Payment();
            payment.setUser(user);
            payment.setPaymentMethod(paymentRequestDto.getPaymentMethod());
            payment.setAmount(paymentRequestDto.getAmount());
            payment.setTitle(paymentRequestDto.getTitle());
            Payment response = paymentRepository.save(payment);

            voucherService.goldVoucher(user, response);

            log.info("결제 성공 - userId: {}, paymentId: {}", userId, response.getPaymentId());
            return PaymentResponseDto.toDto(response);
        } catch (Exception e) {
            log.error("결제 처리 중 예외 발생", e);
            throw e;
        }
    }

    public PaymentResponseDto cancel(Long paymentId, Long userId) {
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> {
                        log.warn("결제 취소 실패 - 사용자 없음: {}", userId);
                        return new UserHandler(ErrorStatus.USER_NOT_FOUND);
                    });

            Payment payment = paymentRepository.findById(paymentId)
                    .orElseThrow(() -> {
                        log.warn("결제 취소 실패 - 결제 없음: {}", paymentId);
                        return new PaymentHandler(ErrorStatus.PAYMENT_NOT_FOUND);
                    });

            if (!user.getUserId().equals(payment.getUser().getUserId())) {
                log.warn("결제 취소 실패 - 권한 없음: userId {}, paymentUserId {}", userId, payment.getUser().getUserId());
                throw new PaymentHandler(ErrorStatus.PAYMENT_FORBIDDEN);
            }

            //0으로 변경해서 삭제
            payment.setCompleted(0);
            paymentRepository.save(payment);
            //바우처 삭제
            voucherService.cancelVoucherForPayment(payment);

            log.info("결제 취소 성공 - userId: {}, paymentId: {}", userId, paymentId);
            return PaymentResponseDto.toDto(payment);
        } catch (Exception e) {
            log.error("결제 취소 중 예외 발생", e);
            throw e;
        }
    }

    public Map<String, Object> list(Long userId, Pageable pageable) {
        try {
            Page<Payment> page = paymentRepository.findByUserId(userId, pageable);

            List<PaymentResponseDto> content = page.getContent().stream()
                    .map(PaymentResponseDto::toDto)
                    .collect(Collectors.toList());

            Map<String, Object> result = new HashMap<>();
            result.put("content", content);
            result.put("first", page.isFirst());
            result.put("last", page.isLast());

            log.info("결제 목록 조회 성공 - userId: {}, 항목 수: {}", userId, content.size());
            return result;
        } catch (Exception e) {
            log.error("결제 목록 조회 중 예외 발생", e);
            throw e;
        }
    }

    public PaymentResponseDto detail(Long paymentId, Long userId) {
        try {
            Payment payment = paymentRepository.findById(paymentId)
                    .orElseThrow(() -> {
                        log.warn("결제 상세 조회 실패 - 결제 없음: {}", paymentId);
                        return new PaymentHandler(ErrorStatus.PAYMENT_NOT_FOUND);
                    });

            if (!payment.getUser().getUserId().equals(userId)) {
                log.warn("결제 상세 조회 실패 - 권한 없음: userId {}, paymentUserId {}", userId, payment.getUser().getUserId());
                throw new PaymentHandler(ErrorStatus.PAYMENT_FORBIDDEN);
            }

            log.info("결제 상세 조회 성공 - paymentId: {}", paymentId);
            return PaymentResponseDto.toDto(payment);
        } catch (Exception e) {
            log.error("결제 상세 조회 중 예외 발생", e);
            throw e;
        }
    }
}
