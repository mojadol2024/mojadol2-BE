package com.gnu.pbl2.question.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gnu.pbl2.coverLetter.entity.CoverLetter;
import com.gnu.pbl2.exception.handler.InterviewHandler;
import com.gnu.pbl2.exception.handler.QuestionHandler;
import com.gnu.pbl2.interview.dto.InterviewResponseDto;
import com.gnu.pbl2.interview.entity.Interview;
import com.gnu.pbl2.interview.repository.InterviewRepository;
import com.gnu.pbl2.question.dto.QuestionResponseDto;
import com.gnu.pbl2.question.entity.Question;
import com.gnu.pbl2.question.repository.QuestionRepository;
import com.gnu.pbl2.response.code.status.ErrorStatus;
import com.gnu.pbl2.voucher.entity.enums.VoucherTier;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class QuestionService {

    private final QuestionRepository questionRepository;
    private final InterviewRepository interviewRepository;

    @Value("${external.django.url}")
    private String djangoUrl;

    public void generateQuestion(CoverLetter coverLetter, VoucherTier useVoucher) {
        RestTemplate restTemplate = new RestTemplate();
        String url = djangoUrl + "questions/generate";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("coverLetter", coverLetter.getData());
        requestBody.put("voucher", useVoucher.toString());

        ObjectMapper objectMapper = new ObjectMapper();
        String json;

        try {
            json = objectMapper.writeValueAsString(requestBody);
        } catch (JsonProcessingException e) {
            throw new QuestionHandler(ErrorStatus.QUESTION_JSON_SERIALIZATION_FAILED);
        }

        HttpEntity<String> entity = new HttpEntity<>(json, headers);

        ResponseEntity<Map<String, List<String>>> response;
        try {
            response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    entity,
                    new ParameterizedTypeReference<>() {}
            );
        } catch (RestClientException e) {
            throw new QuestionHandler(ErrorStatus.QUESTION_DJANGO_CONNECTION_FAILED);
        }

        if (response.getStatusCode() != HttpStatus.OK || response.getBody() == null) {
            throw new QuestionHandler(ErrorStatus.QUESTION_BAD_REQUEST);
        }

        try {
            List<String> questions = response.getBody().get("questions");
            for (String q : questions) {
                Question question = new Question();
                question.setContent(q);
                question.setCoverLetter(coverLetter);
                questionRepository.save(question);
            }
        } catch (Exception e) {
            throw new QuestionHandler(ErrorStatus.QUESTION_DB_SAVE_FAILED);
        }
    }

    public Map<String, Object> questionDetail(Long id, Long userId) {
        Question question = questionRepository.findById(id)
                .orElseThrow(() -> new QuestionHandler(ErrorStatus.QUESTION_NOT_FOUND));

        Interview interview = interviewRepository.findByQuestionAndIsDeleted(question,0);

        if (interview == null) {
            throw new InterviewHandler(ErrorStatus.INTERVIEW_NOT_FOUND);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("question", QuestionResponseDto.toDto(question));
        response.put("interview", InterviewResponseDto.toDto(interview));

        return response;
    }

}
