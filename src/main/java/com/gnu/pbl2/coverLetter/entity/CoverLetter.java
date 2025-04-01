package com.gnu.pbl2.coverLetter.entity;

import com.gnu.pbl2.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class CoverLetter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long coverLetterId;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, length = 5000)
    private String data;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private Integer isDeleted = 1;  // 기본값 1 삭제 안된거 1 삭제된거 0 enum으로 해도 될거 같긴한데 불편하면 문의 부탁

    public CoverLetter(String data, User user, String title) {
        this.title = title;
        this.data = data;
        this.user = user;
        this.isDeleted = 1;
    }
}
