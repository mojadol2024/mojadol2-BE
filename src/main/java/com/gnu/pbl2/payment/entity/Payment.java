package com.gnu.pbl2.payment.entity;

import com.gnu.pbl2.user.entity.User;
import com.gnu.pbl2.voucher.entity.Voucher;
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
@Table(name = "payment")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long paymentId;

    @Column(nullable = false)
    private Integer amount; //결제금액

    @Column(nullable = false)
    private Integer completed; // 결제완료 1  / 결제취소 0

    @Column(nullable = false)
    private String title; //결제내용

    @Column(nullable = false)
    private String paymentMethod; //결제수단

    @Column(nullable = false)
    private LocalDateTime paymentDate; //날짜

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "voucher_id")
    private Voucher voucher;

    @Column(nullable = false)
    private Integer quantity;

    public Payment(Integer amount, String title, String paymentMethod, Integer quantity) {
        this.amount = amount;
        this.completed = 1;
        this.title = title;
        this.paymentMethod = paymentMethod;
        this.paymentDate = LocalDateTime.now();
        this.quantity = quantity;
    }
}
