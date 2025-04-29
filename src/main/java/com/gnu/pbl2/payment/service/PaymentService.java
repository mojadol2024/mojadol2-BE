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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.sql.SQLOutput;
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

    public PaymentResponseDto pay(PaymentRequestDto paymentRequestDto, Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserHandler(ErrorStatus.USER_NOT_FOUND));
        Payment payment = new Payment(paymentRequestDto.getAmount());

        payment.setUser(user);

        Payment response = paymentRepository.save(payment);

        return PaymentResponseDto.toDto(response);
    }

    public PaymentResponseDto cancel(Long paymentId, Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserHandler(ErrorStatus.USER_NOT_FOUND));

        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new PaymentHandler(ErrorStatus.PAYMENT_NOT_FOUND));

        if (!user.getUserId().equals(payment.getUser().getUserId())) {
            throw new PaymentHandler(ErrorStatus.PAYMENT_FORBIDDEN);
        }

        payment.setCompleted(0);
        paymentRepository.save(payment);

        return PaymentResponseDto.toDto(payment);
    }

    public Map<String, Object> list(Long userId, Pageable pageable) {

        Page<Payment> page = paymentRepository.findByUserId(userId, pageable);

        List<PaymentResponseDto> content = page.getContent().stream()
                .map(PaymentResponseDto::toDto)
                .collect(Collectors.toList());

        Map<String, Object> result = new HashMap<>();
        result.put("content", content);
        result.put("first", page.isFirst());
        result.put("last", page.isLast());

        return result;
    }

    public PaymentResponseDto detail(Long paymentId, Long userId) {

        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new PaymentHandler(ErrorStatus.PAYMENT_NOT_FOUND));

        if (!payment.getUser().getUserId().equals(userId)) {
            throw new PaymentHandler(ErrorStatus.PAYMENT_FORBIDDEN);
        }
        return PaymentResponseDto.toDto(payment);
    }



}
