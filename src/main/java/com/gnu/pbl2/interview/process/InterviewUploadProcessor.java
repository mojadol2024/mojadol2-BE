package com.gnu.pbl2.interview.process;

import com.gnu.pbl2.coverLetter.entity.CoverLetter;
import com.gnu.pbl2.coverLetter.repository.CoverLetterRepository;
import com.gnu.pbl2.exception.handler.InterviewHandler;
import com.gnu.pbl2.interview.entity.Interview;
import com.gnu.pbl2.interview.repository.InterviewRepository;
import com.gnu.pbl2.question.entity.Question;
import com.gnu.pbl2.question.repository.QuestionRepository;
import com.gnu.pbl2.response.code.status.ErrorStatus;
import com.gnu.pbl2.trackingResult.repository.TrackingRepository;
import com.gnu.pbl2.trackingResult.service.TrackingService;
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


    private final TrackingService trackingService;


    // 리팩토링 예정  영상은 uploadUtil로 처리하고 kafka는 python 서버에 요청하는로직에 사용하겠습니다.
    public void process(MultipartFile file, Long questionId) {

    }
}
