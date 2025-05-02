package com.gnu.pbl2.scheduler;

import com.gnu.pbl2.exception.handler.InterviewHandler;
import com.gnu.pbl2.interview.entity.Interview;
import com.gnu.pbl2.interview.repository.InterviewRepository;
import com.gnu.pbl2.response.code.status.ErrorStatus;
import com.gnu.pbl2.utils.UploadUtil;
import com.jcraft.jsch.ChannelSftp;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j

public class InterviewScheduler {

    private final InterviewRepository interviewRepository;
    private final UploadUtil uploadUtil;

    @Scheduled(cron = "0 0 0 * * ?")
    @Transactional
    public void deleteExpiredVideos() {
        try {
            LocalDateTime deleteDay = LocalDateTime.now().minusDays(30);
            List<Interview> expiredInterviews = interviewRepository.findByIsDeletedAndDeletedAtBefore(0, deleteDay);

            for (Interview interview : expiredInterviews) {
                String postDirectory = uploadUtil.postDirectory("interview-videos", interview.getInterviewId());
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
