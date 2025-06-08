package com.gnu.pbl2.pdf.dto;

import com.gnu.pbl2.coverLetter.entity.CoverLetter;
import com.gnu.pbl2.interview.entity.Interview;
import com.gnu.pbl2.question.entity.Question;
import lombok.Getter;
import lombok.Setter;

import java.util.*;

@Getter
@Setter
public class PdfAverageDto {
    private float eyeScore;
    private String wpmAverage;
    private String wpmConsistency; //속도 일관성
    private String wpmFeedback; // 전체 평균적 피드백
    private String eyeFeedback;

    private static class ConsistencyResult {
        String level; // 매우 안정적, 보통, 불안정  / 평가
        String label; // 출력 텍스트
        ConsistencyResult(String level, String label) {
            this.level = level;
            this.label = label;
        }
    }


    // ▶ 최종 표를 구성하는 전체 결과 반환
    public static PdfAverageDto analyze(List<Interview> interviews) {
        PdfAverageDto result = new PdfAverageDto();

        List<Float> wpmList = new ArrayList<>();
        List<Float> eyeScores = new ArrayList<>();

        for (Interview interview : interviews) {
            wpmList.add(interview.getTracking().getWpm());
            eyeScores.add(interview.getTracking().getScore());
        }

        // Eye 분석
        float eyeAvg = calculateAverageEyeScore(eyeScores);
        float wpmAvg = calculateAverageWpm(wpmList);

        String speedLevel = getSpeedLevel(wpmAvg);
        String averageWpmResult = String.format("%.1f WPM, %s", wpmAvg, speedLevel);

        float range = Collections.max(wpmList) - Collections.min(wpmList);
        ConsistencyResult consistency = evaluateConsistency(range);

        String wpmFeedback = getWpmFeedbackByCombo(speedLevel, consistency.level);

        // eye set
        result.setEyeScore(eyeAvg);
        result.setEyeFeedback(getEyeContactFeedback(eyeAvg));

        // wpm set
        result.setWpmAverage(averageWpmResult);
        result.setWpmFeedback(wpmFeedback);
        result.setWpmConsistency(consistency.label);

        return result;
    }

    // ▶ 동공 점수 피드백
    private static String getEyeContactFeedback(double avgScore) {
        if (avgScore < 0 || avgScore > 100) {
            return "점수 범위는 0~100 사이여야 합니다.";
        }

        Map<String, List<String>> feedbackMap = new HashMap<>();
        feedbackMap.put("0-30", Arrays.asList("시선을 더욱 집중하고 카메라를 직접 응시하세요.", "눈동자가 자주 흔들립니다. 자신감을 가지고 면접관을 바라보세요."));
        feedbackMap.put("31-50", Arrays.asList("카메라를 좀 더 꾸준히 응시할 필요가 있습니다.", "가끔 시선이 흔들립니다. 더욱 자신 있게 카메라를 바라보세요."));
        feedbackMap.put("51-80",
                Arrays.asList("대체로 안정적인 시선입니다. 조금 더 꾸준히 카메라를 바라보면 더욱 좋습니다.", "눈빛에서 자신감이 느껴집니다. 가끔씩만 흔들리는 부분을 보완하세요."));
        feedbackMap.put("81-100",
                Arrays.asList("매우 우수합니다. 면접관을 응시하는 시선이 안정적이고 자신감 있습니다.", "시선 처리가 탁월합니다. 지금처럼 유지하세요."));

        String key;
        if (avgScore <= 30)
            key = "0-30";
        else if (avgScore <= 50)
            key = "31-50";
        else if (avgScore <= 80)
            key = "51-80";
        else
            key = "81-100";

        List<String> options = feedbackMap.get(key);
        return options.get(new Random().nextInt(options.size()));
    }

    // 평균 eyeScore 계산
    private static float calculateAverageEyeScore(List<Float> eyeScores) {
        return (float) eyeScores.stream()
                .mapToDouble(Float::floatValue)
                .average()
                .orElse(0.0);
    }

    // 평균 wpm 계산
    private static float calculateAverageWpm(List<Float> wpmList) {
        return (float) wpmList.stream()
                .mapToDouble(Float::floatValue)
                .average()
                .orElse(0.0);
    }

    // wpmLevel
    private static String getSpeedLevel(float avg) {
        if (avg < 100) return "너무 느린 속도입니다";
        else if (avg < 130) return "다소 느린 속도입니다";
        else if (avg <= 170) return "적절한 속도입니다";
        else if (avg <= 190) return "다소 빠른 속도입니다";
        else return "너무 빠른 속도입니다";
    }

    private static ConsistencyResult evaluateConsistency(float range) {
        if (range < 30)
            return new ConsistencyResult("매우 안정적",
                    String.format("질문 간 WPM 차이 최대 %.1f → 안정적으로 속도를 유지했습니다", range));
        else if (range <= 60)
            return new ConsistencyResult("보통",
                    String.format("질문 간 WPM 차이 최대 %.1f → 속도 변화가 다소 존재합니다", range));
        else
            return new ConsistencyResult("불안정",
                    String.format("질문 간 WPM 차이 최대 %.1f → 속도 조절 연습이 필요합니다", range));
    }

    private static String getWpmFeedbackByCombo(String speedLevel, String consistencyLevel) {
        Map<String, String> feedbackMap = new HashMap<>();
        feedbackMap.put("너무 느린 속도입니다-불안정", "발화 속도가 느리고, 속도 변화도 큽니다. 또렷하게 말하고 일정한 리듬을 연습해보세요.");
        feedbackMap.put("너무 느린 속도입니다-보통", "발화는 느린 편이며 속도 변화도 약간 있습니다. 좀 더 활기찬 말하기가 필요합니다.");
        feedbackMap.put("너무 느린 속도입니다-매우 안정적", "발화는 느리지만 속도는 일정합니다. 자신감을 높여 적극적인 발화를 해보세요.");
        feedbackMap.put("다소 느린 속도입니다-불안정", "다소 느린 말하기와 함께 속도 변화가 커 전달력에 방해가 됩니다. 리듬 조절이 필요합니다.");
        feedbackMap.put("다소 느린 속도입니다-보통", "속도가 약간 느리고, 질문마다 속도 변화도 일부 존재합니다. 발화 안정성을 높여보세요.");
        feedbackMap.put("다소 느린 속도입니다-매우 안정적", "속도는 느리지만 일정하게 유지되었습니다. 전달력 향상을 위해 약간의 속도 개선을 추천합니다.");
        feedbackMap.put("적절한 속도입니다-불안정", "전반적인 속도는 적절하지만 질문 간 속도 차이가 큽니다. 일정한 말하기 속도 유지 연습이 필요합니다.");
        feedbackMap.put("적절한 속도입니다-보통", "적절한 속도를 유지하였고 속도 변화도 크지 않습니다. 안정적인 전달력이 돋보입니다.");
        feedbackMap.put("적절한 속도입니다-매우 안정적", "속도와 일관성 모두 우수합니다. 명확하고 신뢰감 있는 발화였습니다.");
        feedbackMap.put("다소 빠른 속도입니다-불안정", "속도가 빠르고, 속도 변화도 큽니다. 중요한 내용 전달 시에는 더 신중한 발화가 필요합니다.");
        feedbackMap.put("다소 빠른 속도입니다-보통", "속도는 다소 빠르며, 질문마다 일부 편차가 보입니다. 천천히 말하는 연습도 병행해보세요.");
        feedbackMap.put("다소 빠른 속도입니다-매우 안정적", "다소 빠른 속도지만 전체적으로 일정한 흐름을 유지했습니다. 전달력이 좋습니다.");
        feedbackMap.put("너무 빠른 속도입니다-불안정", "속도가 지나치게 빠르고 일정하지 않습니다. 청자의 이해를 돕기 위해 천천히 말하는 연습이 필요합니다.");
        feedbackMap.put("너무 빠른 속도입니다-보통", "빠른 속도로 인해 전달이 어려울 수 있으며, 속도 변화도 다소 존재합니다. 조절 연습이 필요합니다.");
        feedbackMap.put("너무 빠른 속도입니다-매우 안정적", "빠른 속도를 일정하게 유지했지만, 청자의 이해를 위해 속도 조절이 필요합니다.");

        return feedbackMap.getOrDefault(speedLevel + "-" + consistencyLevel, "발화 속도 분석 결과를 확인할 수 없습니다.");
    }
}
