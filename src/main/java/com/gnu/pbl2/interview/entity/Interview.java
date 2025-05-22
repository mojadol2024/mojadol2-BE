package com.gnu.pbl2.interview.entity;

import com.gnu.pbl2.coverLetter.entity.CoverLetter;
import com.gnu.pbl2.question.entity.Question;
import com.gnu.pbl2.trackingResult.entity.Tracking;
import com.gnu.pbl2.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "interview")
public class Interview {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long interviewId;

    // 영상 주소 117.뭐시기
    @Column(nullable = false)
    private String videoUrl;

    // 삭제여부 (1: 삭제, 0: 존재)
    @Column(nullable = false)
    private Integer isDeleted;

    @Column
    private LocalDateTime deletedAt;

    @ManyToOne
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;

    @OneToOne(mappedBy = "interview", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Tracking tracking;

    @PrePersist
    public void prePersist() {
        if (this.videoUrl == null || this.videoUrl.isEmpty()) {
            this.videoUrl = "default_url";
        }
        if (this.isDeleted == null) {
            this.isDeleted = 1;
        }
    }
}
