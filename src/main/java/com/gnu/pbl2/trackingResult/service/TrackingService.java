package com.gnu.pbl2.trackingResult.service;

import com.gnu.pbl2.exception.handler.TrackingHandler;
import com.gnu.pbl2.interview.entity.Interview;
import com.gnu.pbl2.response.code.status.ErrorStatus;
import com.gnu.pbl2.trackingResult.dto.TrackingResponseDto;
import com.gnu.pbl2.trackingResult.entity.Tracking;
import com.gnu.pbl2.trackingResult.repository.TrackingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Slf4j
public class TrackingService {

    private final TrackingRepository trackingRepository;

    @Value("${external.django.url}")
    private String djangoUrl;


    public void trackingRequest(MultipartFile multipartFile, Interview interview) {
        log.info("[TrackinsService] tracking 시작");

        RestTemplate restTemplate = new RestTemplate();

        String url = djangoUrl + "tracking/tracking";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();

        try {
            body.add("video", new ByteArrayResource(multipartFile.getBytes()) {
                @Override
                public String getFilename() {
                    return multipartFile.getOriginalFilename();
                }
            });

            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

            ResponseEntity<TrackingResponseDto> response = restTemplate.postForEntity(url, requestEntity, TrackingResponseDto.class);

            TrackingResponseDto score = response.getBody();
            Tracking tracking = new Tracking();
            tracking.setInterview(interview);
            tracking.setScore(score.getScore());

            trackingRepository.save(tracking);
            log.info("[TrackinsService] tracking 저장완료");

        } catch (Exception e) {
            log.error("[TrackinsService] tracking 서버에러");
            throw new TrackingHandler(ErrorStatus.TRACKING_INTERNAL_SERVER_ERROR);
        }




    }
}
