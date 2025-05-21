package com.gnu.pbl2.pdf.service;

import com.gnu.pbl2.exception.handler.UserHandler;
import com.gnu.pbl2.pdf.dto.PdfResponseDto;
import com.gnu.pbl2.response.code.status.ErrorStatus;
import com.gnu.pbl2.user.entity.User;
import com.gnu.pbl2.user.repository.UserRepository;
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

import static org.jsoup.nodes.Document.OutputSettings.Syntax.html;

@Service
@Slf4j
@RequiredArgsConstructor
public class PdfService {

    private final UserRepository userRepository;


    public byte[] createPdf(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserHandler(ErrorStatus.USER_NOT_FOUND));

        try {

            String html = createHtml(user);

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



    public String createHtml(User user) {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<html xmlns=\"http://www.w3.org/1999/xhtml\" lang=\"ko\">\n" +
                "  <head>\n" +
                "    <meta charset=\"UTF-8\" />\n" +
                "    <title>면접 결과 확인서</title>\n" +
                "    <style type=\"text/css\">\n" +
                "      @import url('https://fonts.googleapis.com/css2?family=Noto+Sans+KR&amp;display=swap');\n" +
                "\n" +
                "      body {\n" +
                "        font-family: 'Noto Sans KR', sans-serif;\n" +
                "        margin: 40px;\n" +
                "      }\n" +
                "      h1, h2 {\n" +
                "        text-align: left;\n" +
                "        color: #000;\n" +
                "        line-height: 1.4;\n" +
                "      }\n" +
                "      h1 {\n" +
                "        font-size: 20px;\n" +
                "        margin-bottom: 5px;\n" +
                "      }\n" +
                "      h2 {\n" +
                "        font-size: 16px;\n" +
                "        margin-bottom: 20px;\n" +
                "      }\n" +
                "      table {\n" +
                "        border-collapse: collapse;\n" +
                "        width: 100%;\n" +
                "        margin-bottom: 30px;\n" +
                "      }\n" +
                "      th, td {\n" +
                "        border: 1px solid #ddd;\n" +
                "        padding: 10px;\n" +
                "        font-size: 13px;\n" +
                "      }\n" +
                "      th {\n" +
                "        background-color: #51E0C4;\n" +
                "        color: white;\n" +
                "        text-align: left;\n" +
                "      }\n" +
                "      .highlight {\n" +
                "        color: #51E0C4;\n" +
                "        font-weight: bold;\n" +
                "      }\n" +
                "      .section-title {\n" +
                "        background-color: #f1f1f1;\n" +
                "        padding: 8px;\n" +
                "        font-weight: bold;\n" +
                "      }\n" +
                "      .feedback-table th, .feedback-table td {\n" +
                "        text-align: center;\n" +
                "      }\n" +
                "      .confirm-box {\n" +
                "        float: right;\n" +
                "        border: 1px solid #aaa;\n" +
                "        width: 120px;\n" +
                "        height: 80px;\n" +
                "        text-align: center;\n" +
                "        padding: 10px;\n" +
                "        font-size: 13px;\n" +
                "      }\n" +
                "    </style>\n" +
                "  </head>\n" +
                "  <body>\n" +
                "    <div>\n" +
                "      <h1>면접의 정석</h1>\n" +
                "      <h2>화상 면접 검사<br/>결과 확인서</h2>\n" +
                "      <div class=\"confirm-box\">\n" +
                "        확인<br/><br/>\n" +
                "        성명<br/>\n" +
                "        서명\n" +
                "      </div>\n" +
                "\n" +
                "      <table>\n" +
                "        <tr>\n" +
                "          <th>이메일</th>\n" +
                "          <td colspan=\"2\">test@example.com</td>\n" +
                "          <th>점수</th>\n" +
                "          <td class=\"highlight\">good</td>\n" +
                "        </tr>\n" +
                "        <tr>\n" +
                "          <th>성명</th>\n" +
                "          <td>홍길동</td>\n" +
                "          <th>검사번호</th>\n" +
                "          <td colspan=\"2\">00294400434</td>\n" +
                "        </tr>\n" +
                "        <tr>\n" +
                "          <th>검사명</th>\n" +
                "          <td>미입력</td>\n" +
                "          <th>문서형</th>\n" +
                "          <td colspan=\"2\">[면접제출문서]</td>\n" +
                "        </tr>\n" +
                "        <tr>\n" +
                "          <th>비교범위</th>\n" +
                "          <td>[현재첨부문서]</td>\n" +
                "          <th>평가유형</th>\n" +
                "          <td>✔</td>\n" +
                "          <td>발급형태</td>\n" +
                "        </tr>\n" +
                "        <tr>\n" +
                "          <th>발급일자</th>\n" +
                "          <td>2025.03.06 17:32</td>\n" +
                "          <th>검사일자</th>\n" +
                "          <td colspan=\"2\">2025.03.06 17:31</td>\n" +
                "        </tr>\n" +
                "        <tr>\n" +
                "          <th>비고</th>\n" +
                "          <td colspan=\"4\"></td>\n" +
                "        </tr>\n" +
                "      </table>\n" +
                "\n" +
                "      <table class=\"feedback-table\">\n" +
                "        <tr>\n" +
                "          <th colspan=\"3\">피드백</th>\n" +
                "        </tr>\n" +
                "        <tr>\n" +
                "          <th>어절</th>\n" +
                "          <th>문장</th>\n" +
                "          <th>의견</th>\n" +
                "        </tr>\n" +
                "        <tr>\n" +
                "          <td colspan=\"3\" style=\"height: 100px;\"></td>\n" +
                "        </tr>\n" +
                "      </table>\n" +
                "\n" +
                "      <div class=\"section-title\">검토 의견</div>\n" +
                "      <div style=\"border: 1px solid #ddd; height: 100px;\"></div>\n" +
                "    </div>\n" +
                "  </body>\n" +
                "</html>\n";
    }

}
