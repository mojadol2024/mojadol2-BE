package com.gnu.pbl2.question.dto;

import com.gnu.pbl2.question.entity.Question;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class QuestionResponseDto {
    private Long questionId;
    private String content;
    private Integer is_answered;


    public static QuestionResponseDto toDto(Question question) {
        return QuestionResponseDto.builder()
                .content(question.getContent())
                .questionId(question.getQuestionId())
                .is_answered(question.getIs_answered())
                .build();
    }
}
