package com.gnu.pbl2.trackingResult.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gnu.pbl2.exception.handler.TrackingHandler;
import com.gnu.pbl2.interview.entity.Interview;
import com.gnu.pbl2.response.code.status.ErrorStatus;
import com.gnu.pbl2.trackingResult.dto.SttResponseDto;
import com.gnu.pbl2.trackingResult.dto.TrackingResponseDto;
import com.gnu.pbl2.trackingResult.entity.Tracking;
import com.gnu.pbl2.trackingResult.repository.TrackingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.HttpHeaders;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class TrackingService {

    private final TrackingRepository trackingRepository;

    @Value("${external.django.url}")
    private String djangoUrl;

    private final RestTemplate restTemplate = new RestTemplate(new BufferingClientHttpRequestFactory(new SimpleClientHttpRequestFactory()));


    public void trackingRequest(File file, Interview interview) {
        log.info("[TrackingService] tracking 시작");

        byte[] fileBytes;
        try {
            fileBytes = Files.readAllBytes(file.toPath());
        } catch (IOException e) {
            log.error("[TrackingService] 파일 읽기 실패", e);
            throw new TrackingHandler(ErrorStatus.TRACKING_FILE_READ_FAILED);
        }

        String base64Encoded = Base64.getEncoder().encodeToString(fileBytes);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("filename", file.getName());
        requestBody.put("contentType", "application/octet-stream");
        requestBody.put("fileData", base64Encoded);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);

        ObjectMapper objectMapper = new ObjectMapper();
        TrackingResponseDto trackingResponseDto;
        try {
            String trackingResponse = restTemplate.postForObject(djangoUrl + "tracking/tracking", requestEntity, String.class);
            trackingResponseDto = objectMapper.readValue(trackingResponse, TrackingResponseDto.class);
            log.info("[TrackingService] Tracking 응답 결과: {}", trackingResponse);
        } catch (RestClientException e) {
            log.error("[TrackingService] Tracking Django 서버 통신 실패", e);
            throw new TrackingHandler(ErrorStatus.TRACKING_DJANGO_COMMUNICATION_FAILED);
        } catch (JsonProcessingException e) {
            log.error("[TrackingService] Tracking 응답 파싱 실패", e);
            throw new TrackingHandler(ErrorStatus.TRACKING_RESPONSE_PARSING_FAILED);
        }

        Tracking tracking = new Tracking();
        tracking.setScore(trackingResponseDto.getScore());
        tracking.setCenter(trackingResponseDto.getCenter());
        tracking.setLeft(trackingResponseDto.getLeft());
        tracking.setRight(trackingResponseDto.getRight());
        tracking.setFrameCount(trackingResponseDto.getFrameCount());
        tracking.setInterview(interview);

        SttResponseDto sttResponseDto;
        try {
            String sttResponse = restTemplate.postForObject(djangoUrl + "speech/stt", requestEntity, String.class);
            sttResponseDto = objectMapper.readValue(sttResponse, SttResponseDto.class);
            log.info("[TrackingService] Stt 응답 결과: {}", sttResponseDto);
        } catch (RestClientException e) {
            log.error("[TrackingService] STT Django 서버 통신 실패", e);
            throw new TrackingHandler(ErrorStatus.STT_DJANGO_COMMUNICATION_FAILED);
        } catch (JsonProcessingException e) {
            log.error("[TrackingService] STT 응답 파싱 실패", e);
            throw new TrackingHandler(ErrorStatus.STT_RESPONSE_PARSING_FAILED);
        }

        tracking.setText(sttResponseDto.getText());
        tracking.setWpm(sttResponseDto.getWpm());
        tracking.setFeedback(sttResponseDto.getFeedback());
        tracking.setDurationSec(sttResponseDto.getDuration_sec());
        tracking.setSpeedLabel(sttResponseDto.getSpeed_label());

        try {
            trackingRepository.save(tracking);
        } catch (Exception e) {
            log.error("[TrackingService] Tracking DB 저장 실패", e);
            throw new TrackingHandler(ErrorStatus.TRACKING_DB_SAVE_FAILED);
        }
    }


}