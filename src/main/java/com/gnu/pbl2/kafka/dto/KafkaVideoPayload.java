package com.gnu.pbl2.kafka.dto;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class KafkaVideoPayload {
    private String videoKey;
    private byte[] fileBytes;
    private String originalFilename;
    private Long coverLetterId;
    private Long interviewId;
}
