package com.gnu.pbl2.interview.service;

import com.gnu.pbl2.exception.handler.InterviewHandler;
import com.gnu.pbl2.interview.dto.InterviewResponseDto;
import com.gnu.pbl2.interview.entity.Interview;
import com.gnu.pbl2.interview.repository.InterviewRepository;
import com.gnu.pbl2.kafka.IKafkaProducer;
import com.gnu.pbl2.kafka.KafkaProducer;
import com.gnu.pbl2.kafka.dto.KafkaVideoPayload;
import com.gnu.pbl2.response.code.status.ErrorStatus;
import com.gnu.pbl2.utils.UploadUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class InterviewService {

    private final UploadUtil uploadUtil;
    private final InterviewRepository interviewRepository;
    private final IKafkaProducer kafkaProducer;


    public void saveVideo(MultipartFile file, Long questionId) {
        try {

            // Kafka로 전송할 Payload 구성
            KafkaVideoPayload payload = KafkaVideoPayload.builder()
                    .questionId(questionId)
                    .fileBytes(file.getBytes())
                    .originalFilename(file.getOriginalFilename())
                    .build();

            // Kafka로 비동기 전송
            kafkaProducer.send(payload);

            log.info("Kafka 전송 완료: questionId={}, fileName={}", questionId, file.getOriginalFilename());

        } catch (Exception e) {
            log.error("영상 저장 실패: questionId={}, error={}", questionId, e.getMessage());
            throw new InterviewHandler(ErrorStatus.INTERVIEW_SAVE_ERROR);
        }
    }

    @Transactional(rollbackOn = Exception.class)
    public void deleteVideo(Long interviewId) {
        Interview interview = interviewRepository.findById(interviewId)
                .orElseThrow(() -> new InterviewHandler(ErrorStatus.INTERVIEW_NOT_FOUND));
        try {


            interview.setIsDeleted(0);
            interview.setDeletedAt(LocalDateTime.now());

            log.info("영상 삭제 처리 완료: interviewId={}", interviewId);
        } catch (Exception e) {
            log.error("영상 삭제 실패: interviewId={}, error={}", interviewId, e.getMessage());
            throw new InterviewHandler(ErrorStatus.INTERVIEW_DELETE_ERROR);
        }
    }

    // 이거 안쓰지 않을까 싶음 아마도
    public List<InterviewResponseDto> interviewsList(Long questionId) {
        try {
            List<Interview> interviews = interviewRepository.findByQuestionIdAndDeletedAt(questionId, 1);

            List<InterviewResponseDto> response = new ArrayList<>();
            for (Interview interview: interviews) {
                interview.setVideoUrl(uploadUtil.filePath(interview.getVideoUrl()));
                response.add(InterviewResponseDto.toDto(interview));
            }

            log.info("인터뷰 리스트 조회 성공: questionId={}, count={}개", questionId, response.size());

            return response;
        } catch (Exception e) {
            log.error("인터뷰 리스트 조회 실패: questionId={}, error={}", questionId, e.getMessage());
            throw new InterviewHandler(ErrorStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public InterviewResponseDto interviewDetail(Long interviewId) {
        Interview response = interviewRepository.findById(interviewId)
                .orElseThrow(() -> new InterviewHandler(ErrorStatus.INTERVIEW_NOT_FOUND));

        log.info("인터뷰 상세 조회 성공: interviewId={} ", interviewId);

        return InterviewResponseDto.toDto(response);
    }
}