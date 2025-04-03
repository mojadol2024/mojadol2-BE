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
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InterviewService {

    private final UploadUtil uploadUtil;
    private final InterviewRepository interviewRepository;
    private final CoverLetterRepository coverLetterRepository;

    private final String directoryName = "interview-videos";

    public InterviewResponseDto saveVideo(MultipartFile file, Long coverLetterId) {
        try {
            // url이 있을려면 id값이 필요함 id값이 있을려면 url을 저장해야함 ?? 그럼 빈값이나 default값을 저장하고 이후에 update해야함? db2번 가는거 개에반데 방법이 없긴해
            // UUID를 pk로 주고하는 방법도 있지만 그건 보기 너무 불편함.. 혹시나 UUID로 해야한다면 바꿔도 되긴해
            CoverLetter coverLetter = coverLetterRepository.findById(coverLetterId)
                    .orElseThrow(() -> new InterviewHandler(ErrorStatus.COVER_LETTER_NOT_FOUND));

            Interview interview = new Interview();
            interview.setCoverLetter(coverLetter);

            //일단 interviewId값이 필요해서 default값을 저장
            Interview tempInterview = interviewRepository.saveAndFlush(interview);

            // directory 구조 : home/bgt/pbl2/interview-videos/interviewId/영상
            String postDirectory = uploadUtil.postDirectory(directoryName, tempInterview.getInterviewId());

            // session 연결
            ChannelSftp channelSftp = uploadUtil.sessionConnect(postDirectory);
            uploadUtil.recreateDirectory(channelSftp, postDirectory);
            String remoteFilePath = uploadUtil.save(file, channelSftp, postDirectory);

            tempInterview.setVideoUrl(directoryName + "/" + tempInterview.getInterviewId() + "/"+ remoteFilePath); // 원격 서버의 파일 경로 저장

            Interview response = interviewRepository.save(tempInterview);

            response.setVideoUrl(uploadUtil.filePath(response.getVideoUrl()));

            return InterviewResponseDto.toDto(response);

        } catch (Exception e) {
            e.printStackTrace();
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
        } catch (Exception e) {
            e.printStackTrace();
            throw new InterviewHandler(ErrorStatus.INTERVIEW_DELETE_ERROR);
        }
    }

    @Scheduled(cron = "0 0 0 * * ?") // 매일 자정 실행
    @Transactional
    public void deleteExpiredVideos() {
        try {
            LocalDateTime deleteDay = LocalDateTime.now().minusDays(30);
            List<Interview> expiredInterviews = interviewRepository.findByIsDeletedAndDeletedAtBefore(0, deleteDay);

            for (Interview interview : expiredInterviews) {
                String postDirectory = uploadUtil.postDirectory(directoryName, interview.getInterviewId());
                ChannelSftp channelSftp = uploadUtil.sessionConnect(postDirectory);
                uploadUtil.deleteDirectory(channelSftp, postDirectory); // 디렉토리 삭제

                interviewRepository.delete(interview);
            }
        }catch (Exception e) {
            throw new InterviewHandler(ErrorStatus.INTERVIEW_SCHEDULE_ERROR);
        }

    }
}
