package com.gnu.pbl2.kafka.listener;

import com.gnu.pbl2.interview.process.InterviewUploadProcessor;
import com.gnu.pbl2.kafka.dto.KafkaVideoPayload;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RequiredArgsConstructor
@Component
@Profile("!local")
public class InterviewListener {

    private final InterviewUploadProcessor interviewUploadProcessor;

    @KafkaListener(topics = "interview-video", groupId = "video-group")
    public void listen(KafkaVideoPayload payload) {
        try {

            log.info("영상 처리 완료: {}", payload.getOriginalFilename());
        } catch (Exception e) {
            log.error("영상 처리 실패: {}", e.getMessage());
        }
    }
}
