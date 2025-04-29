package com.gnu.pbl2.scheduler;

import com.gnu.pbl2.user.entity.User;
import com.gnu.pbl2.user.repository.UserRepository;
import com.gnu.pbl2.voucher.entity.Voucher;
import com.gnu.pbl2.voucher.entity.enums.VoucherTier;
import com.gnu.pbl2.voucher.repository.VoucherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class VoucherScheduler {

    private final UserRepository userRepository;
    private final VoucherRepository voucherRepository;

    @Scheduled(cron = "0 0 0 1 * ?")  // 매월 1일 00:00
    public void refreshFreeVouchers() {
        List<User> users = userRepository.findByDeletedTimeIsNull();

        for (User user : users) {
            // 기존 FREE 바우처 삭제
            voucherRepository.deleteByUserAndType(user, VoucherTier.FREE);

            // 새 FREE 바우처 발급
            voucherRepository.save(new Voucher(user, VoucherTier.FREE));
        }
    }
}
