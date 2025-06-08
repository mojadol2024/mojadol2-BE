package com.gnu.pbl2.question.entity;

import com.gnu.pbl2.coverLetter.entity.CoverLetter;
import com.gnu.pbl2.interview.entity.Interview;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long questionId;

    @Column
    private String content;

    @Column
    private Integer is_answered = 0; // 0답변안한거 1답변한거

    @ManyToOne
    @JoinColumn(name = "cover_letter_id", nullable = false)
    private CoverLetter coverLetter;

    @OneToOne(mappedBy = "question", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Interview interview;
}
