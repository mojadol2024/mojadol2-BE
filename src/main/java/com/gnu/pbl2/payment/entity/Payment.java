package com.gnu.pbl2.payment.entity;

import com.gnu.pbl2.user.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
    private Integer amount;

    @Column(nullable = false)
    private Integer completed; // 결제완료 1  / 결제취소 0

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public Payment(Integer amount) {
        this.amount = amount;
        this.completed = 1;
    }
}
