package com.gnu.pbl2.voucher.entity;

import com.gnu.pbl2.payment.entity.Payment;
import com.gnu.pbl2.user.entity.User;
import com.gnu.pbl2.voucher.entity.enums.VoucherTier;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "voucher")
public class Voucher {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long voucherId;  // 이용권 고유 ID

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;  // 해당 이용권을 소유한 유저

    @OneToOne
    @JoinColumn(name = "payment_id")  // 결제와 연결
    private Payment payment;  // 결제 정보 추가

    @Column(nullable = false)
    private Integer totalCount;  // 총 제공량 (1개 또는 10개 등, 유저에게 제공되는 이용권의 수)

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VoucherTier type;  // 이용권의 종류 (FREE: 무료, GOLD: 결제 이용권)

    @Column(nullable = false)
    private LocalDateTime issuedAt;  // 이용권이 발급된 날짜와 시간

    @Column
    private LocalDateTime expiredAt;  // 이용권의 만료 기한


    // 무료 이용권 생성자
    public Voucher(User user, VoucherTier type) {
        this.user = user;
        this.type = type;
        this.totalCount = 1;
        this.issuedAt = LocalDateTime.now();
        this.expiredAt = LocalDateTime.now().plusMonths(1);
    }

    // 유료 이용권 생성자
    public Voucher(User user, Payment payment, VoucherTier type) {
        this.user = user;
        this.type = type;
        this.totalCount = 10;
        this.payment = payment;
        this.issuedAt = LocalDateTime.now();
        this.expiredAt = LocalDateTime.now().plusMonths(1);
    }

}
