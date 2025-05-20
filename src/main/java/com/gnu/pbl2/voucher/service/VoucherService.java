package com.gnu.pbl2.voucher.service;

import com.gnu.pbl2.payment.entity.Payment;
import com.gnu.pbl2.user.entity.User;
import com.gnu.pbl2.voucher.entity.Voucher;
import com.gnu.pbl2.voucher.entity.enums.VoucherTier;
import com.gnu.pbl2.voucher.repository.VoucherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.gnu.pbl2.voucher.entity.Voucher.createFreeVoucher;
import static com.gnu.pbl2.voucher.entity.Voucher.createPaidVoucher;

@Service
@RequiredArgsConstructor
public class VoucherService {

    private final VoucherRepository voucherRepository;


    public void freeVoucher(User user) {
        voucherRepository.save(createFreeVoucher(user, VoucherTier.FREE));
    }

    public Voucher goldVoucher(User user) {
        return voucherRepository.save(createPaidVoucher(user, VoucherTier.GOLD));
    }

    public void cancelVoucherForPayment(Payment payment) {
        if (payment.getVoucher() != null) {
            voucherRepository.delete(payment.getVoucher());
        }
    }

}
