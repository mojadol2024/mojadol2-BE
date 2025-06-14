package com.gnu.pbl2.utils;

import org.apache.coyote.BadRequestException;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.InitBinder;

import java.beans.PropertyEditorSupport;
import java.util.regex.Pattern;

@ControllerAdvice
public class GlobalInputSanitizer {

    private static final Pattern MALICIOUS_PATTERN = Pattern.compile(
            "(?i)(<|>|\\{|\\}|script|className=|</|--|;|/\\*|\\*/|select|insert|update|delete|drop|alter|create|from|where|union|or\\s+1=1)"
    );

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(String.class, new PropertyEditorSupport() {
            @Override
            public void setAsText(String text) {
                if (text != null && MALICIOUS_PATTERN.matcher(text).find()) {
                    throw new IllegalArgumentException("입력값에 금지된 문자열이 포함되어 있습니다.");
                }
                setValue(text);
            }
        });
    }
}
