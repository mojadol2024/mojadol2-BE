package com.gnu.pbl2.interview.service;

import com.gnu.pbl2.coverLetter.entity.CoverLetter;
import com.gnu.pbl2.coverLetter.repository.CoverLetterRepository;
import com.gnu.pbl2.exception.handler.InterviewHandler;
import com.gnu.pbl2.interview.dto.InterviewRequestDto;
import com.gnu.pbl2.interview.dto.InterviewResponseDto;
import com.gnu.pbl2.interview.entity.Interview;
import com.gnu.pbl2.interview.repository.InterviewRepository;
import com.gnu.pbl2.response.code.status.ErrorStatus;
import com.gnu.pbl2.utils.UploadUtil;
import com.jcraft.jsch.ChannelSftp;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
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
    private final CoverLetterRepository coverLetterRepository;

    private final String directoryName = "interview-videos";

    public InterviewResponseDto saveVideo(MultipartFile file, Long coverLetterId) {
        try {
            CoverLetter coverLetter = coverLetterRepository.findById(coverLetterId)
                    .orElseThrow(() -> new InterviewHandler(ErrorStatus.COVER_LETTER_NOT_FOUND));

            Interview interview = new Interview();
            interview.setCoverLetter(coverLetter);

            Interview tempInterview = interviewRepository.saveAndFlush(interview);
            String postDirectory = uploadUtil.postDirectory(directoryName, tempInterview.getInterviewId());

            ChannelSftp channelSftp = uploadUtil.sessionConnect(postDirectory);
            uploadUtil.recreateDirectory(channelSftp, postDirectory);
            String remoteFilePath = uploadUtil.save(file, channelSftp, postDirectory);

            tempInterview.setVideoUrl(directoryName + "/" + tempInterview.getInterviewId() + "/"+ remoteFilePath);
            Interview response = interviewRepository.save(tempInterview);
            response.setVideoUrl(uploadUtil.filePath(response.getVideoUrl()));

            log.info("영상 저장 완료: interviewId={}, videoUrl={}", response.getInterviewId(), response.getVideoUrl());

            return InterviewResponseDto.toDto(response);

        } catch (Exception e) {
            log.error("영상 저장 실패: coverLetterId={}, error={}", coverLetterId, e.getMessage());
            throw new InterviewHandler(ErrorStatus.INTERVIEW_SAVE_ERROR);
        }
    }

    @Transactional
    public void deleteVideo(Long interviewId) {
        try {
            Interview interview = interviewRepository.findById(interviewId)
                    .orElseThrow(() -> new InterviewHandler(ErrorStatus.INTERVIEW_NOT_FOUND));

            interview.setIsDeleted(0);
            interview.setDeletedAt(LocalDateTime.now());

            log.info("영상 삭제 처리 완료: interviewId={}", interviewId);
        } catch (Exception e) {
            log.error("영상 삭제 실패: interviewId={}, error={}", interviewId, e.getMessage());
            throw new InterviewHandler(ErrorStatus.INTERVIEW_DELETE_ERROR);
        }
    }

    public List<InterviewResponseDto> interviewsList(Long coverLetterId) {
        try {
            List<Interview> interviews = interviewRepository.findByCoverLetterIdAndDeletedAt(coverLetterId, 1);

            List<InterviewResponseDto> response = new ArrayList<>();
            for (Interview interview: interviews) {
                interview.setVideoUrl(uploadUtil.filePath(interview.getVideoUrl()));
                response.add(InterviewResponseDto.toDto(interview));
            }

            log.info("인터뷰 리스트 조회 성공: coverLetterId={}, count={}개", coverLetterId, response.size());

            return response;
        } catch (Exception e) {
            log.error("인터뷰 리스트 조회 실패: coverLetterId={}, error={}", coverLetterId, e.getMessage());
            throw new InterviewHandler(ErrorStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public InterviewResponseDto interviewDetail(Long interviewId) {
        Interview response = interviewRepository.findById(interviewId)
                .orElseThrow(() -> new InterviewHandler(ErrorStatus.INTERVIEW_NOT_FOUND));

        log.info("인터뷰 상세 조회 성공: interviewId={} ", interviewId);

        return InterviewResponseDto.toDto(response);
    }

    @Scheduled(cron = "0 0 0 * * ?")
    @Transactional
    public void deleteExpiredVideos() {
        try {
            LocalDateTime deleteDay = LocalDateTime.now().minusDays(30);
            List<Interview> expiredInterviews = interviewRepository.findByIsDeletedAndDeletedAtBefore(0, deleteDay);

            for (Interview interview : expiredInterviews) {
                String postDirectory = uploadUtil.postDirectory(directoryName, interview.getInterviewId());
                ChannelSftp channelSftp = uploadUtil.sessionConnect(postDirectory);
                uploadUtil.deleteDirectory(channelSftp, postDirectory);

                interviewRepository.delete(interview);
                log.info("30일 경과 인터뷰 삭제 완료: interviewId={} ", interview.getInterviewId());
            }
        }catch (Exception e) {
            log.error("30일 경과 인터뷰 삭제 실패: error={}", e.getMessage());
            throw new InterviewHandler(ErrorStatus.INTERVIEW_SCHEDULE_ERROR);
        }

    }
}