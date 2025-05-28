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

    @Column(nullable = false)
    private Integer totalCount;  // 총 제공량 (1개 또는 10개 등, 유저에게 제공되는 이용권의 수)

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VoucherTier type;  // 이용권의 종류 (FREE: 무료, GOLD: 결제 이용권)

    @Column(nullable = false)
    private LocalDateTime issuedAt;  // 이용권이 발급된 날짜와 시간

    @Column
    private LocalDateTime expiredAt;  // 이용권의 만료 기한

    @OneToOne(mappedBy = "voucher", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Payment payment;


    // 무료 이용권 생성자
    public static Voucher createFreeVoucher(User user, VoucherTier type) {
        Voucher v = new Voucher(user, type);
        v.totalCount = 1;
        v.issuedAt = LocalDateTime.now();
        v.expiredAt = LocalDateTime.now().plusMonths(1);
        return v;
    }

    public static Voucher createPaidVoucher(User user, Integer quantity, VoucherTier type) {
        Voucher v = new Voucher(user, type);
        v.totalCount = 10 * quantity;
        v.issuedAt = LocalDateTime.now();
        v.expiredAt = LocalDateTime.now().plusMonths(1);
        return v;
    }

    private Voucher(User user, VoucherTier type) {
        this.user = user;
        this.type = type;
    }

}
