package com.gnu.pbl2.payment.dto;

import com.gnu.pbl2.payment.entity.Payment;
import com.gnu.pbl2.voucher.dto.VoucherDto;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class PaymentResponseDto {

    private Long userId;
    private Long paymentId;
    private Integer amount;
    private Integer completed;
    private String title;
    private String paymentMethod;
    private LocalDateTime paymentDate;
    private Integer quantity;
    private VoucherDto voucher;


    public static PaymentResponseDto toDto(Payment payment) {
        PaymentResponseDto response = new PaymentResponseDto();

        response.setPaymentId(payment.getPaymentId());
        response.setAmount(payment.getAmount());
        response.setCompleted(payment.getCompleted());
        response.setUserId(payment.getUser().getUserId());
        response.setTitle(payment.getTitle());
        response.setPaymentMethod(payment.getPaymentMethod());
        response.setPaymentDate(payment.getPaymentDate());
        response.setQuantity(payment.getQuantity());
        if (payment.getVoucher() != null) {
            response.setVoucher(VoucherDto.toDto(payment.getVoucher()));
        }
        return response;
    }
}
