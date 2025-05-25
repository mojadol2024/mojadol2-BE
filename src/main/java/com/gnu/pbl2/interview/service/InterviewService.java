package com.gnu.pbl2.interview.service;

import com.gnu.pbl2.exception.handler.InterviewHandler;
import com.gnu.pbl2.interview.dto.InterviewResponseDto;
import com.gnu.pbl2.interview.entity.Interview;
import com.gnu.pbl2.interview.repository.InterviewRepository;
import com.gnu.pbl2.kafka.IKafkaProducer;
import com.gnu.pbl2.kafka.KafkaProducer;
import com.gnu.pbl2.kafka.dto.KafkaVideoPayload;
import com.gnu.pbl2.question.entity.Question;
import com.gnu.pbl2.question.repository.QuestionRepository;
import com.gnu.pbl2.response.code.status.ErrorStatus;
import com.gnu.pbl2.trackingResult.service.TrackingService;
import com.gnu.pbl2.utils.UploadUtil;
import com.jcraft.jsch.ChannelSftp;
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
    //private final IKafkaProducer kafkaProducer;
    private final QuestionRepository questionRepository;
    private final TrackingService trackingService;



    public InterviewResponseDto saveVideo(MultipartFile file, Long questionId) {


            Question question = questionRepository.findById(questionId)
                    .orElseThrow(() -> new InterviewHandler(ErrorStatus.COVER_LETTER_NOT_FOUND));

            Interview interview = new Interview();
            interview.setQuestion(question);
            question.setIs_answered(1);
        try {
            // 임시 인터뷰 저장
            Interview tempInterview = interviewRepository.saveAndFlush(interview);

            String directoryName = "interview-videos";

            // sftp
            String postDirectory = uploadUtil.postDirectory(directoryName, tempInterview.getInterviewId());
            UploadUtil.SftpConnection sftpConnection = uploadUtil.sessionConnect(postDirectory);
            try {
                ChannelSftp channelSftp = sftpConnection.getChannelSftp();
                uploadUtil.recreateDirectory(channelSftp, postDirectory);
                String remoteFilePath = uploadUtil.save(file, channelSftp, postDirectory);

                // 영상 URL 업데이트
                tempInterview.setVideoUrl(directoryName + "/" + tempInterview.getInterviewId() + "/" + remoteFilePath);

            }finally {
                sftpConnection.disconnect();
            }

            Interview interview1 = interviewRepository.save(tempInterview);

            // 인터뷰 작성되면 question answered 1로 변경
            questionRepository.save(question);
            /*
            // Kafka로 전송할 Payload 구성
            KafkaVideoPayload payload = KafkaVideoPayload.builder()
                    .questionId(questionId)
                    .fileBytes(file.getBytes())
                    .originalFilename(file.getOriginalFilename())
                    .build();

            // Kafka로 비동기 전송
            kafkaProducer.send(payload);
            log.info("Kafka 전송 완료: questionId={}, fileName={}", questionId, file.getOriginalFilename());
            */

            trackingService.trackingRequest(file, interview1);

            log.info("영상 저장 완료: interviewId={}, videoUrl={}", tempInterview.getInterviewId(), tempInterview.getVideoUrl());

            return InterviewResponseDto.toDto(interview1);

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