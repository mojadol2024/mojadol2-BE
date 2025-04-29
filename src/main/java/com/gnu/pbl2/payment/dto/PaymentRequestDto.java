package com.gnu.pbl2.payment.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentRequestDto {
    private Integer amount;
    private Long paymentId;
}
