package com.gnu.pbl2.payment.repository;

import com.gnu.pbl2.payment.entity.Payment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    @Query("SELECT p FROM Payment p WHERE p.user.userId = :userId ORDER BY p.paymentDate desc")
    Page<Payment> findByUserId(@Param("userId") Long userId, Pageable pageable);

}
