package com.gnu.pbl2.pdf.service;

import com.gnu.pbl2.coverLetter.entity.CoverLetter;
import com.gnu.pbl2.coverLetter.repository.CoverLetterRepository;
import com.gnu.pbl2.exception.handler.CoverLetterHandler;
import com.gnu.pbl2.exception.handler.PdfHandler;
import com.gnu.pbl2.exception.handler.UserHandler;
import com.gnu.pbl2.interview.entity.Interview;
import com.gnu.pbl2.pdf.dto.PdfAverageDto;
import com.gnu.pbl2.pdf.dto.PdfResponseDto;
import com.gnu.pbl2.question.entity.Question;
import com.gnu.pbl2.response.code.status.ErrorStatus;
import com.gnu.pbl2.user.entity.User;
import com.gnu.pbl2.user.repository.UserRepository;
import com.gnu.pbl2.utils.TimeUtil;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.tomcat.util.http.fileupload.ByteArrayOutputStream;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static org.jsoup.nodes.Document.OutputSettings.Syntax.html;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class PdfService {

    private final UserRepository userRepository;
    private final CoverLetterRepository coverLetterRepository;


    public byte[] createPdf(Long userId, Long coverLetterId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserHandler(ErrorStatus.USER_NOT_FOUND));

        CoverLetter coverLetter = coverLetterRepository.findById(coverLetterId)
                .orElseThrow(() -> new CoverLetterHandler(ErrorStatus.COVER_LETTER_NOT_FOUND));

        try {
            log.info("[Pdf create service] userId = {}, coverLetterId = {}", userId, coverLetterId);

            String html = createHtml(user, coverLetter);

            System.out.println(html);

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.withHtmlContent(html, null);
            builder.useFont(() -> PdfService.class.getResourceAsStream("/fonts/NotoSansKR-VariableFont_wght.ttf"), "Noto Sans KR");

            builder.toStream(out);
            builder.run();

            return out.toByteArray();

        }catch (Exception e) {
            log.error("[Pdf create service Exception]",  e);
            throw new PdfHandler(ErrorStatus.PDF_BAD_REQUEST);
        }
    }

    public String createHtml(User user, CoverLetter coverLetter) {

        List<Question> questions = coverLetter.getQuestions();
        List<Interview> interviews = new ArrayList<>();

        for(Question question : questions) {
            if (question.getInterview() != null) {
                interviews.add(question.getInterview());
            }
        }

        if (interviews.isEmpty()) {
            log.error("[Create PDF] Interview Null ");
            throw new PdfHandler(ErrorStatus.PDF_BAD_REQUEST);
        }

        PdfAverageDto averageDto = PdfAverageDto.analyze(interviews);

        String response =  "<!DOCTYPE html>\n" +
                "<html lang=\"ko\">\n" +
                "<head>\n" +
                "  <meta charset=\"UTF-8\" />\n" +
                "  <title>면접 결과 리포트</title>\n" +
                "  <style>\n" +
                "    * { box-sizing: border-box; }\n" +
                "body {\n" +
                "  font-family: 'Noto Sans KR', sans-serif;\n" +
                "  background-color: #fff;\n" +
                "  margin: 0;\n" +
                "  padding: 0;\n" +
                "}\n" +
                "@page {\n" +
                "   size: A4;\n" +
                "   margin: 0;\n" +
                "}\n" +
                ".page-wrapper {\n" +
                "  width: 210mm;\n" +
                "  height: 257mm;\n" +
                "  padding: 20mm 25mm;\n" +
                "  margin: 0 auto;\n" +
                "  position: relative;\n" +
                "  background-size: 60%;\n" +
                "}\n" +
                ".logo-mask-text {\n" +
                "  position: fixed;\n" +
                "  top: 70%;\n" +
                "  left: 50%;\n" +
                "  transform: translate(-50%, -50%);\n" +
                "  font-size: 100px;\n" +
                "  font-weight: 800;\n" +
                "  color: #e6e6e6;\n" +
                "  white-space: nowrap;\n" +
                "}\n" +
                ".header {\n" +
                "  text-align: center;\n" +
                "  position: relative;\n" +
                "}\n" +
                ".logo {\n" +
                "  font-size: 32px;\n" +
                "  font-weight: bold;\n" +
                "  margin-bottom: 8px;\n" +
                "}\n" +
                ".logoHighlight { color: #51E0C4; }\n" +
                "h2 { font-size: 18px; margin-bottom: 20px; }\n" +
                "table {\n" +
                "  width: 100%;\n" +
                "  border-collapse: collapse;\n" +
                "  margin-top: 24px;\n" +
                "  position: relative;\n" +
                "}\n" +
                "th, td {\n" +
                "  border: 1px solid #ccc;\n" +
                "  padding: 6px 10px;\n" +
                "  font-size: 13.5px;\n" +
                "  text-align: left;\n" +
                "}\n" +
                "th { background-color: #f2f2f2; }\n" +
                ".section-title {\n" +
                "  background-color: #e6f7f6;\n" +
                "  font-weight: bold;\n" +
                "  text-align: center;\n" +
                "}\n" +
                ".footer {\n" +
                "  margin-top: 40px;\n" +
                "  font-size: 14px;\n" +
                "  position: relative;\n" +
                "}\n" +
                ".page-break {\n" +
                "  page-break-before: always;\n" +
                "}\n" +
                "  </style>\n" +
                "</head>\n" +
                "<body>\n" +
                "  <div class=\"page-wrapper\">\n" +
                "    <div class=\"logo-mask-text\">면접의 정석</div>\n" +
                "\n" +
                "    <div class=\"header\">\n" +
                "      <div class=\"logo\">면접의<span class=\"logoHighlight\">정석</span></div>\n" +
                "      <h2>AI 면접 결과 리포트</h2>\n" +
                "    </div>\n" +
                "\n" +
                "    <table>\n" +
                "      <tr><th>성명</th><td>" + user.getUsername() + "</td><th>이메일</th><td>" + user.getEmail() + "</td></tr>\n" +
                "      <tr><th>문서명</th><td colspan=\"3\">"+ coverLetter.getTitle() +"</td></tr>\n" +
                "      <tr><th>평가 및 지원 기능</th><td colspan=\"3\">✔ 맞춤형 질문 생성 ✔ 면접 태도 분석 (동공 분석, 발화 속도)</td></tr>\n" +
                "      <tr><th>발급일자</th><td colspan=\"3\">" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + "</td></tr>\n" +
                "    </table>\n" +
                "\n" +
                "    <table>\n" +
                "      <tr class=\"section-title\"><td colspan=\"3\">면접 태도 평균 분석</td></tr>\n" +
                "      <tr><th style=\"white-space: nowrap;\">동공 분석</th><td colspan=\"2\">" + averageDto.getEyeScore() + "</td></tr>\n" +
                "      <tr><th rowspan=\"3\" style=\"white-space: nowrap;\">발화 속도</th><th style=\"white-space: nowrap;\">평균 발화 속도</th><td>" + averageDto.getWpmAverage() + "</td></tr>\n" +
                "      <tr><th style=\"white-space: nowrap;\">속도 일관성</th><td>" + averageDto.getWpmConsistency() +"</td></tr>\n" +
                "      <tr><th style=\"white-space: nowrap;\">종합 피드백</th><td>" + averageDto.getEyeFeedback() +"\n" + averageDto.getWpmFeedback() +"</td></tr>\n" +
                "    </table>\n" +
                "\n" +
                "    <p style=\"margin-top: 8px; font-size: 13px;\">※ WPM = 사용자의 답변 단어 수 / 사용자의 답변 시간(분)</p>\n" +
                "\n" +
                "    <div class=\"page-break\"></div>\n" +
                "\n";

                for (int i = 0; i < interviews.size(); i++) {
                    Interview interview = interviews.get(i);
                    response += "    <table>\n" +
                            "      <tr class=\"section-title\"><td colspan=\"3\">면접 태도 세부 정보</td></tr>\n" +
                            "      <tr><th style=\"white-space: nowrap; width: 50px;\">" + (i + 1) + "</th><td>" + questions.get(i).getContent() + "</td></tr>\n" +
                            "      <tr>\n" +
                            "        <td colspan=\"2\">\n" +
                            "          총 " + interview.getTracking().getFrameCount() + "개 프레임 중 정면 응시는 " + interview.getTracking().getCenter() + "회였으며, 동공이 좌우로 흔들린 횟수는 각각 " + interview.getTracking().getLeft() + "회, " + interview.getTracking().getRight() + "회입니다. 평균 시선 점수는 " + interview.getTracking().getScore() + "점입니다.\n" +
                            "        </td>\n" +
                            "      </tr>\n" +
                            "      <tr>\n" +
                            "        <td colspan=\"2\">\n" +
                            "          WPM: " + interview.getTracking().getWpm() + " (" + interview.getTracking().getSpeedLabel() + "), " + interview.getTracking().getFeedback() + "\n" +
                            "        </td>\n" +
                            "      </tr>\n" +
                            "    </table>\n\n";
                }

                response +=
                "    <div style=\"margin-top: 50px; padding-top: 10px;\">\n" +
                "      <h3 style=\"font-size: 16px; margin-bottom: 10px;\">\uD83D\uDCCC 분석 기준</h3>\n" +
                "\n" +
                "      <p style=\"margin-bottom: 6px;\"><strong>- 동공 분석</strong></p>\n" +
                "      <p style=\"margin-top: 0;\">\n" +
                "      동공 분석 AI는 사용자가 카메라를 잘 응시하고 있는지 평가합니다. 화면을 잘 응시하고 있는지, 시선이 흔들리지 않는지, 눈을 얼마나 깜박이는지 등을 분석하여 점수를 냅니다.\n" +
                "      </p>\n" +
                "\n" +
                "      <table style=\"margin-top: 12px;\">\n" +
                "      <tr>\n" +
                "          <th>점수 구간</th> <th>피드백 내용</th>\n" +
                "      </tr>\n" +
                "      <tr>\n" +
                "          <td>0~30점</td> <td>정면 응시 비율이 매우 낮아, 면접 시 시선을 집중하여 카메라를 바라보는 연습이 시급합니다.</td>\n" +
                "      </tr>\n" +
                "      <tr>\n" +
                "          <td>31~50점</td> <td>시선이 자주 흔들려 면접관과의 아이컨택 유지에 어려움이 있어 개선이 요구됩니다.</td>\n" +
                "      </tr>\n" +
                "      <tr>\n" +
                "          <td>51~80점</td> <td>전반적으로 안정된 시선을 보였으나 간헐적인 흔들림이 있어 지속적인 주시 연습이 필요합니다.</td>\n" +
                "      </tr>\n" +
                "      <tr>\n" +
                "          <td>81~100점</td> <td>시선이 전반적으로 안정적이고 자연스럽게 유지되어 면접 태도로서 우수한 수준입니다.</td>\n" +
                "      </tr>\n" +
                "      </table>\n" +
                "\n" +
                "      <p style=\"margin-top: 20px; margin-bottom: 6px;\"><strong>- 발화 속도</strong></p>\n" +
                "      <p style=\"margin-top: 0;\">\n" +
                "        WPM(말의 빠르기) 측정 방법은 (전체 단어 수) / (전체 말한 시간 초 / 60) 입니다. 예를 들어, 60초 동안 150단어를 말했다면 150WPM, 30초 동안 70 단어를 말했다면 (70 / 0.5분) 140 WPM 입니다. 면접이나 자기소개 영상에서는 또박또박 말하면서도 지루하지 않게 들리는 속도인 140~170 WPM 정도가 가장 적절합니다.\n" +
                "      </p>\n" +
                "\n" +
                "      <table style=\"margin-top: 12px;\">\n" +
                "        <tr>\n" +
                "          <th>WPM 범위</th> <th>설명</th> <th>권장 사용 상황</th>\n" +
                "        </tr>\n" +
                "        <tr>\n" +
                "          <td>&lt; 110 WPM</td> <td>너무 느림</td> <td>발표 초보, 비자연스러운 느낌 가능</td>\n" +
                "        </tr>\n" +
                "        <tr>\n" +
                "          <td>110–140 WPM</td> <td>다소 느림</td> <td>신중한 설명, 강의나 프레젠테이션에 적합</td>\n" +
                "        </tr>\n" +
                "        <tr>\n" +
                "          <td>140–170 WPM</td> <td>표준 속도</td> <td>일상 대화, 인터뷰, 강연에 적절</td>\n" +
                "        </tr>\n" +
                "        <tr>\n" +
                "          <td>170–190 WPM</td> <td>빠르지만 명확성 유지 가능</td> <td>토론, 뉴스 리포트, 열정적인 발표 등</td>\n" +
                "        </tr>\n" +
                "        <tr>\n" +
                "          <td>&gt; 190 WPM</td> <td>너무 빠름</td> <td>듣는 사람의 이해도 저하 가능</td>\n" +
                "        </tr>\n" +
                "      </table>\n" +
                "      <br/>\n" +
                "    </div>\n" +
                "  </div>\n" +
                "</body>\n" +
                "</html>";

        return response;
    }

}
