package com.gnu.pbl2.payment.dto;

import com.gnu.pbl2.payment.entity.Payment;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentResponseDto {

    private Long userId;
    private Long paymentId;
    private Integer amount;
    private Integer completed;


    public static PaymentResponseDto toDto(Payment payment) {
        PaymentResponseDto response = new PaymentResponseDto();

        response.setPaymentId(payment.getPaymentId());
        response.setAmount(payment.getAmount());
        response.setCompleted(payment.getCompleted());
        response.setUserId(payment.getUser().getUserId());

        return response;
    }
}
