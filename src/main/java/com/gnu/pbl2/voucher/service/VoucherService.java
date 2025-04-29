package com.gnu.pbl2.voucher.service;

import com.gnu.pbl2.payment.entity.Payment;
import com.gnu.pbl2.user.entity.User;
import com.gnu.pbl2.voucher.entity.Voucher;
import com.gnu.pbl2.voucher.entity.enums.VoucherTier;
import com.gnu.pbl2.voucher.repository.VoucherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class VoucherService {

    private final VoucherRepository voucherRepository;


    public void freeVoucher(User user) {
        voucherRepository.save(new Voucher(user, VoucherTier.FREE));
    }

    public void goldVoucher(User user, Payment payment) {
        voucherRepository.save(new Voucher(user, payment, VoucherTier.GOLD));
    }

    public void cancelVoucherForPayment(Payment payment) {
        if (payment.getVoucher() != null) {
            voucherRepository.delete(payment.getVoucher());
        }
    }

}
