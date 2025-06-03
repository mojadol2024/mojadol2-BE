package com.gnu.pbl2.trackingResult.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SttResponseDto {
    private String text;
    private int word_count;
    private float duration_sec;
    private float wpm;
    private String speed_label;
    private String feedback;
}
