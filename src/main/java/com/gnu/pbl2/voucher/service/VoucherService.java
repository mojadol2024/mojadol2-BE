package com.gnu.pbl2.voucher.service;

import com.gnu.pbl2.exception.handler.CoverLetterHandler;
import com.gnu.pbl2.payment.entity.Payment;
import com.gnu.pbl2.response.code.status.ErrorStatus;
import com.gnu.pbl2.user.entity.User;
import com.gnu.pbl2.user.repository.UserRepository;
import com.gnu.pbl2.voucher.entity.Voucher;
import com.gnu.pbl2.voucher.entity.enums.VoucherTier;
import com.gnu.pbl2.voucher.repository.VoucherRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.gnu.pbl2.voucher.entity.Voucher.createFreeVoucher;
import static com.gnu.pbl2.voucher.entity.Voucher.createPaidVoucher;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class VoucherService {

    private final VoucherRepository voucherRepository;



    public void freeVoucher(User user) {
        voucherRepository.save(createFreeVoucher(user, VoucherTier.FREE));

    }

    public Voucher goldVoucher(User user, Integer quantity) {
        return voucherRepository.save(createPaidVoucher(user, quantity, VoucherTier.GOLD));
    }

    public void cancelVoucherForPayment(Payment payment) {
        if (payment.getVoucher() != null) {
            payment.getVoucher().setDeletedFlag(0);
            voucherRepository.save(payment.getVoucher());
        }
    }

    public void minusVoucher(User user, VoucherTier type) {

        Voucher voucher = voucherRepository.findValidByUserAndType(user, type, PageRequest.of(0, 1)).stream().findFirst().orElse(null);


        if(voucher == null) {
            throw new CoverLetterHandler(ErrorStatus.VOUCHER_NOT_FOUND);
        }

        voucher.setTotalCount(voucher.getTotalCount() - 1);
        if (voucher.getTotalCount() == 0) {
            voucher.setDeletedFlag(0);
            voucherRepository.save(voucher);
        } else {
            voucherRepository.save(voucher);
        }
    }

}
