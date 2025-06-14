package com.gnu.pbl2.trackingResult.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TrackingResponseDto {
    private float score;
    private Integer center;
    private Integer left;
    private Integer right;
    private Integer blinking;
    private Integer frameCount;
}
