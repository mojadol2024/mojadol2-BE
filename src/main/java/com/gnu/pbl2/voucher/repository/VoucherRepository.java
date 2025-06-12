package com.gnu.pbl2.voucher.repository;

import com.gnu.pbl2.user.entity.User;
import com.gnu.pbl2.voucher.entity.Voucher;
import com.gnu.pbl2.voucher.entity.enums.VoucherTier;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface VoucherRepository extends JpaRepository<Voucher, Long> {

    void deleteByUserAndType(User user, VoucherTier type);

    void deleteByExpiredAtBefore(LocalDateTime time);

    @Query("SELECT v FROM Voucher v WHERE v.user = :user AND v.type = :type AND v.expiredAt > CURRENT_TIMESTAMP AND v.totalCount > 0 ORDER BY v.expiredAt ASC")
    List<Voucher> findValidByUserAndType(@Param("user") User user, @Param("type") VoucherTier type, Pageable pageable);


    List<Voucher> findByUserAndDeletedFlag(User user, int deletedFlag);


}
