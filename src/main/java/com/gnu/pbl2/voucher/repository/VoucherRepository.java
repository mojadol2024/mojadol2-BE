package com.gnu.pbl2.voucher.repository;

import com.gnu.pbl2.user.entity.User;
import com.gnu.pbl2.voucher.entity.Voucher;
import com.gnu.pbl2.voucher.entity.enums.VoucherTier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VoucherRepository extends JpaRepository<Voucher, Long> {

    void deleteByUserAndType(User user, VoucherTier type);

}
