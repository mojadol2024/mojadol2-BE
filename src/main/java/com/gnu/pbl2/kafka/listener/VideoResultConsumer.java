package com.gnu.pbl2.kafka.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gnu.pbl2.exception.handler.InterviewHandler;
import com.gnu.pbl2.interview.entity.Interview;
import com.gnu.pbl2.interview.repository.InterviewRepository;
import com.gnu.pbl2.kafka.dto.VideoResult;
import com.gnu.pbl2.response.code.status.ErrorStatus;
import com.gnu.pbl2.trackingResult.entity.Tracking;
import com.gnu.pbl2.trackingResult.repository.TrackingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class VideoResultConsumer {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final InterviewRepository interviewRepository;
    private final TrackingRepository trackingRepository;

    @KafkaListener(topics = "interview-video-result", groupId = "spring-consumer-group")
    public void consume(String message) {
        try {
            // JSON 파싱
            VideoResult result = objectMapper.readValue(message, VideoResult.class);

            System.out.println("받은 결과: " + result);
            System.out.println("interviewId: " + result.getInterviewId());
            System.out.println("점수: " + result.getScore());

            Interview interview = interviewRepository.findById(result.getInterviewId())
                    .orElseThrow(() -> new InterviewHandler(ErrorStatus.INTERVIEW_NOT_FOUND));

            if (trackingRepository.existsByInterview(interview)) {
                log.warn("이미 처리된 인터뷰: interviewId={}", result.getInterviewId());
                return;
            }
            Tracking tracking = new Tracking();
            tracking.setScore(result.getScore());
            tracking.setInterview(interview);

            trackingRepository.save(tracking);

        } catch (Exception e) {
            e.printStackTrace();
            log.error("[VideoResultConsumer] : kafka 처리 중 예외 발생");
        }
    }
}
