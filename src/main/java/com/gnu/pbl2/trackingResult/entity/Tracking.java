package com.gnu.pbl2.trackingResult.entity;

import com.gnu.pbl2.interview.entity.Interview;
import com.gnu.pbl2.question.entity.Question;
import com.gnu.pbl2.question.service.QuestionService;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Setter
@Getter
@Table(name = "tracking")
public class Tracking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long trackingId;

    @Column(nullable = false)
    private float score;

    @Column(nullable = false)
    private String text;

    @Column(nullable = false)
    private float durationSec;

    @Column(nullable = false)
    private float wpm;

    @Column(nullable = false)
    private String speedLabel;

    @Column(nullable = false)
    private String feedback;

    @Column
    private Integer center;

    @Column
    private Integer left;

    @Column
    private Integer right;

    @Column
    private Integer frameCount;

    @OneToOne
    @JoinColumn(name = "interview_id")
    private Interview interview;

}
