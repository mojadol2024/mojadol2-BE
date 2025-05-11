package com.gnu.pbl2.interview.process;

import com.gnu.pbl2.coverLetter.entity.CoverLetter;
import com.gnu.pbl2.coverLetter.repository.CoverLetterRepository;
import com.gnu.pbl2.exception.handler.InterviewHandler;
import com.gnu.pbl2.interview.entity.Interview;
import com.gnu.pbl2.interview.repository.InterviewRepository;
import com.gnu.pbl2.response.code.status.ErrorStatus;
import com.gnu.pbl2.utils.UploadUtil;
import com.jcraft.jsch.ChannelSftp;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Slf4j
public class InterviewUploadProcessor {

    private final UploadUtil uploadUtil;
    private final InterviewRepository interviewRepository;
    private final CoverLetterRepository coverLetterRepository;

    public void process(MultipartFile file, Interview interview) {
        try {

            String directoryName = "interview-videos";

            // 파일 처리
            String postDirectory = uploadUtil.postDirectory(directoryName, interview.getInterviewId());
            ChannelSftp channelSftp = uploadUtil.sessionConnect(postDirectory);
            uploadUtil.recreateDirectory(channelSftp, postDirectory);
            String remoteFilePath = uploadUtil.save(file, channelSftp, postDirectory);

            // 영상 URL 업데이트
            interview.setVideoUrl(directoryName + "/" + interview.getInterviewId() + "/" + remoteFilePath);
            interviewRepository.save(interview);

            log.info("Kafka Consumer - 영상 저장 완료: interviewId={}, videoUrl={}", interview.getInterviewId(), interview.getVideoUrl());

        } catch (Exception e) {
            log.error("Kafka Consumer - 영상 저장 실패: interviewId={}, error={}", interview.getInterviewId(), e.getMessage());
            throw new InterviewHandler(ErrorStatus.INTERVIEW_SAVE_ERROR);
        }
    }
}
