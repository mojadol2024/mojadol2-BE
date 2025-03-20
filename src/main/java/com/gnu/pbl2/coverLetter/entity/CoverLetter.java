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

    public CoverLetter(String data, User user, String title) {
        this.title = title;
        this.data = data;
        this.user = user;
    }
}
