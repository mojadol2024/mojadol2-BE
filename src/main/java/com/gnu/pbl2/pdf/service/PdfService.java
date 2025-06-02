package com.gnu.pbl2.pdf.service;

import com.gnu.pbl2.coverLetter.entity.CoverLetter;
import com.gnu.pbl2.coverLetter.repository.CoverLetterRepository;
import com.gnu.pbl2.exception.handler.CoverLetterHandler;
import com.gnu.pbl2.exception.handler.UserHandler;
import com.gnu.pbl2.pdf.dto.PdfResponseDto;
import com.gnu.pbl2.response.code.status.ErrorStatus;
import com.gnu.pbl2.user.entity.User;
import com.gnu.pbl2.user.repository.UserRepository;
import com.gnu.pbl2.utils.TimeUtil;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
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

import static org.jsoup.nodes.Document.OutputSettings.Syntax.html;

@Service
@Slf4j
@RequiredArgsConstructor
public class PdfService {

    private final UserRepository userRepository;
    private final CoverLetterRepository coverLetterRepository;


    public byte[] createPdf(Long userId, Long coverLetterId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserHandler(ErrorStatus.USER_NOT_FOUND));

        CoverLetter coverLetter = coverLetterRepository.findById(coverLetterId)
                .orElseThrow(() -> new CoverLetterHandler(ErrorStatus.COVER_LETTER_NOT_FOUND));

        try {

            String html = createHtml(user, coverLetter);

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.useFastMode();
            builder.withHtmlContent(html, null);
            builder.toStream(out);
            builder.run();

            return out.toByteArray();

        }catch (Exception e) {
            return null;
        }
    }

    public String createHtml(User user, CoverLetter coverLetter) {
        return "<!DOCTYPE html>\n" +
                "<html lang=\"ko\">\n" +
                "<head>\n" +
                "  <meta charset=\"UTF-8\" />\n" +
                "  <title>면접 결과 리포트</title>\n" +
                "  <style>\n" +
                "    * {\n" +
                "      box-sizing: border-box;\n" +
                "    }\n" +
                "\n" +
                "    body {\n" +
                "      font-family: 'Noto Sans KR', sans-serif;\n" +
                "      background-color: #fff;\n" +
                "      margin: 0;\n" +
                "      padding: 0;\n" +
                "    }\n" +
                "\n" +
                "    .page-wrapper {\n" +
                "      width: 794px;           /* A4 폭 */\n" +
                "      height: 1123px;         /* A4 높이 */\n" +
                "      padding: 60px 40px;\n" +
                "      margin: 0 auto;\n" +
                "      position: relative;\n" +
                "      background: url('logo_mask.png') no-repeat center center;\n" +
                "      background-size: 60%;\n" +
                "    }\n" +
                "\n" +
                "    .logo-mask-text {\n" +
                "      position: fixed;\n" +
                "      top: 70%;\n" +
                "      left: 50%;\n" +
                "      transform: translate(-50%, -50%);\n" +
                "      font-size: 100px;\n" +
                "      font-weight: 800;\n" +
                "      color: rgba(0, 0, 0, 0.05);\n" +
                "      pointer-events: none;\n" +
                "      z-index: 0;\n" +
                "      white-space: nowrap;\n" +
                "    }\n" +
                "\n" +
                "    .header {\n" +
                "      text-align: center;\n" +
                "      position: relative;\n" +
                "      z-index: 1;\n" +
                "    }\n" +
                "\n" +
                "    .logo {\n" +
                "      font-size: 32px;\n" +
                "      font-weight: bold;\n" +
                "      margin-bottom: 8px;\n" +
                "    }\n" +
                "\n" +
                "    .logoHighlight {\n" +
                "      color: #51E0C4;\n" +
                "    }\n" +
                "\n" +
                "    h2 {\n" +
                "      font-size: 18px;\n" +
                "      margin-bottom: 20px;\n" +
                "    }\n" +
                "\n" +
                "    table {\n" +
                "      width: 100%;\n" +
                "      border-collapse: collapse;\n" +
                "      margin-top: 24px;\n" +
                "      position: relative;\n" +
                "      z-index: 1;\n" +
                "    }\n" +
                "\n" +
                "    th, td {\n" +
                "      border: 1px solid #ccc;\n" +
                "      padding: 6px 10px;\n" +
                "      font-size: 13.5px;\n" +
                "      text-align: left;\n" +
                "    }\n" +
                "\n" +
                "    th {\n" +
                "      background-color: #f2f2f2;\n" +
                "    }\n" +
                "\n" +
                "    .section-title {\n" +
                "      background-color: #e6f7f6;\n" +
                "      font-weight: bold;\n" +
                "      text-align: center;\n" +
                "    }\n" +
                "\n" +
                "    .footer {\n" +
                "      margin-top: 40px;\n" +
                "      font-size: 14px;\n" +
                "      position: relative;\n" +
                "      z-index: 1;\n" +
                "    }\n" +
                "\n" +
                "    .signature-line {\n" +
                "      border: 1px solid #ccc;\n" +
                "      height: 60px;\n" +
                "      margin-top: 10px;\n" +
                "    }\n" +
                "  </style>\n" +
                "</head>\n" +
                "<body>\n" +
                "\n" +
                "  <div class=\"page-wrapper\">\n" +
                "    <div class=\"logo-mask-text\">면접의 정석</div>\n" +
                "\n" +
                "    <div class=\"header\">\n" +
                "      <div class=\"logo\">\n" +
                "        면접의<span class=\"logoHighlight\">정석</span>\n" +
                "      </div>\n" +
                "      <h2>AI 면접 결과 리포트</h2>\n" +
                "    </div>\n" +
                "\n" +
                "    <table>\n" +
                "      <tr>\n" +
                "        <th>"+ user.getEmail() +"</th>\n" +
                "        <td>2024080064</td>\n" +
                "        <th rowspan=\"2\">면접태도 점수</th>\n" +
                "        <td rowspan=\"2\">0%</td>\n" +
                "      </tr>\n" +
                "      <tr>\n" +
                "        <th>" + user.getUsername() +"</th>\n" +
                "        <td>자필로 기재하세요</td>\n" +
                "      </tr>\n" +
                "      <tr>\n" +
                "        <th>검사번호</th>\n" +
                "        <td colspan=\"3\">00310672367</td>\n" +
                "      </tr>\n" +
                "      <tr>\n" +
                "        <th>문서명</th>\n" +
                "        <td>지원자의 강점에 대해 소개하세요.txt</td>\n" +
                "        <th>검사명</th>\n" +
                "        <td>면접태도 검사</td>\n" +
                "      </tr>\n" +
                "      <tr>\n" +
                "        <th>평가유형</th>\n" +
                "        <td colspan=\"3\">✔ 면접질문 생성 ✔ 면접태도 분석</td>\n" +
                "      </tr>\n" +
                "      <tr>\n" +
                "        <th>발급일자</th>\n" +
                "        <td>"+ LocalDateTime.now() +"\n" +
                "      </tr>\n" +
                "      <tr>\n" +
                "        <th>비고</th>\n" +
                "        <td colspan=\"3\"></td>\n" +
                "      </tr>\n" +
                "    </table>\n" +
                "\n" +
                "    <table>\n" +
                "      <tr class=\"section-title\"><td colspan=\"2\">면접 태도 분석</td></tr>\n" +
                "      <tr>\n" +
                "        <th>동공 흔들림 횟수</th>\n" +
                "        <td>7회</td>\n" +
                "      </tr>\n" +
                "      <tr>\n" +
                "        <th>말이 느린 구간 수</th>\n" +
                "        <td>3회 (WPM &lt; 100)</td>\n" +
                "      </tr>\n" +
                "      <tr>\n" +
                "        <th>말이 빠른 구간 수</th>\n" +
                "        <td>5회 (WPM &gt; 180)</td>\n" +
                "      </tr>\n" +
                "    </table>\n" +
                "\n" +
                "    <p style=\"margin-top: 8px; font-size: 13px;\">\n" +
                "      ※ WPM = 사용자의 답변 단어 수 / 사용자의 답변 시간(분)\n" +
                "    </p>\n" +
                "\n" +
                "    <div class=\"footer\">\n" +
                "      <p><strong>검토 의견</strong></p>\n" +
                "      <div class=\"signature-line\"></div>\n" +
                "    </div>\n" +
                "  </div>\n" +
                "\n" +
                "</body>\n" +
                "</html>\n";
    }

}
