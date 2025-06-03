package com.gnu.pbl2.trackingResult.dto;

import com.gnu.pbl2.trackingResult.entity.Tracking;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class TrackingClientResponseDto {
    private String text;
    private float duration_sec;
    private float wpm;
    private String speed_label;
    private String feedback;
    private float score;
    private Long trackingId;

    public static TrackingClientResponseDto toDto(Tracking tracking) {
        return TrackingClientResponseDto.builder()
                .text(tracking.getText())
                .duration_sec(tracking.getDurationSec())
                .wpm(tracking.getWpm())
                .speed_label(tracking.getSpeedLabel())
                .feedback(tracking.getFeedback())
                .score(tracking.getScore())
                .trackingId(tracking.getTrackingId())
                .build();
    }
}
