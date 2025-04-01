package com.gnu.pbl2.utils;

import com.gnu.pbl2.exception.handler.CoverLetterHandler;
import com.gnu.pbl2.response.code.status.ErrorStatus;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class SpellCheckerUtil {

    public String getPassportKey() {
        try {
            String url = "https://search.naver.com/search.naver?where=nexearch&sm=top_hty&fbm=0&ie=utf8&query=네이버+맞춤법+검사기";
            // 네이버 검색 결과 페이지 가져오기 (User-Agent 설정)
            Document doc = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36")
                    .get();

            // passportKey 가져올 정규식 패턴과 매쳐
            String htmlText = doc.html();
            Pattern pattern = Pattern.compile("passportKey=([a-f0-9]{40})");
            Matcher matcher = pattern.matcher(htmlText);

            if (matcher.find()) {
                return matcher.group(1);
            } else {
                throw new CoverLetterHandler(ErrorStatus.SPELLCHECKER_PASSPORTKEY_NOT_FOUND);
            }
        } catch (IOException e) {
            throw new CoverLetterHandler(ErrorStatus.SPELLCHECKER_INTERNAL_SERVER_ERROR);
        }
    }

}
