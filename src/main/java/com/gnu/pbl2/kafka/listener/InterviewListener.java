package com.gnu.pbl2.kafka.listener;

import com.gnu.pbl2.interview.process.InterviewUploadProcessor;
import com.gnu.pbl2.kafka.KafkaProducer;
import com.gnu.pbl2.kafka.dto.KafkaVideoPayload;
import com.gnu.pbl2.utils.CustomMultipartFile;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RequiredArgsConstructor
@Component
public class InterviewListener {

    private final InterviewUploadProcessor interviewUploadProcessor;

    @KafkaListener(topics = "interview-video", groupId = "video-group")
    public void listen(KafkaVideoPayload payload) {
        try {
            MultipartFile multipartFile = new CustomMultipartFile(
                    payload.getOriginalFilename(),
                    payload.getFileBytes(),
                    "video/mp4"
            );

            interviewUploadProcessor.process(multipartFile, payload.getInterview());
            log.info("영상 처리 완료: {}", payload.getOriginalFilename());
        } catch (Exception e) {
            log.error("영상 처리 실패: {}", e.getMessage());
        }
    }
}
