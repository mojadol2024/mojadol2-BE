package com.gnu.pbl2.kafka.dto;

import com.gnu.pbl2.coverLetter.entity.CoverLetter;
import com.gnu.pbl2.interview.entity.Interview;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class KafkaVideoPayload {
    private byte[] fileBytes;
    private String originalFilename;
    private Long questionId;
}
