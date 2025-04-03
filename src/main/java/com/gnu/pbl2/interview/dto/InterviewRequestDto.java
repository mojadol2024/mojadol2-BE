package com.gnu.pbl2.interview.dto;

import com.gnu.pbl2.coverLetter.entity.CoverLetter;
import com.gnu.pbl2.interview.entity.Interview;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class InterviewRequestDto {
    private String videoUrl;
    private Long coverLetterId;

}
