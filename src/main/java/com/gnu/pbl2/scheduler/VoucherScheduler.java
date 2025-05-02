package com.gnu.pbl2.scheduler;

import com.gnu.pbl2.user.entity.User;
import com.gnu.pbl2.user.repository.UserRepository;
import com.gnu.pbl2.voucher.entity.Voucher;
import com.gnu.pbl2.voucher.entity.enums.VoucherTier;
import com.gnu.pbl2.voucher.repository.VoucherRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class VoucherScheduler {

    private final UserRepository userRepository;
    private final VoucherRepository voucherRepository;

    @Scheduled(cron = "0 0 0 1 * ?")  // 매월 1일 00:00
    public void refreshFreeVouchers() {
        List<User> users = userRepository.findByDeletedTimeIsNull();
        log.info("FREE 바우처 리프레시 시작 - 전체 유저 수: {}", users.size());

        for (User user : users) {
            log.info("유저 {}: 기존 FREE 바우처 삭제 시도", user.getUserId());
            voucherRepository.deleteByUserAndType(user, VoucherTier.FREE);
            log.info("유저 {}: 기존 FREE 바우처 삭제 완료", user.getUserId());

            voucherRepository.save(new Voucher(user, VoucherTier.FREE));
            log.info("유저 {}: 새 FREE 바우처 발급 완료", user.getUserId());
        }
        log.info("FREE 바우처 리프레시 완료");
    }
}
