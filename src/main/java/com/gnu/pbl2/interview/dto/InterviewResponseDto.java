package com.gnu.pbl2.interview.dto;

import com.gnu.pbl2.interview.entity.Interview;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
public class InterviewResponseDto {
    private Long interviewId;
    private String videoUrl;
    private Integer isDeleted;


    public static InterviewResponseDto toDto(Interview interview) {
        return InterviewResponseDto.builder()
                .interviewId(interview.getInterviewId())
                .videoUrl(interview.getVideoUrl())
                .isDeleted(interview.getIsDeleted())
                .build();
    }

}
