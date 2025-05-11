package com.gnu.pbl2.coverLetter.dto;

import com.gnu.pbl2.coverLetter.entity.CoverLetter;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CoverLetterResponseDto {
    private Long coverLetterId;
    private String title;
    private String data;

    public CoverLetterResponseDto(CoverLetter coverLetter) {
        this.title = coverLetter.getTitle();
        this.coverLetterId = coverLetter.getCoverLetterId();
        this.data = coverLetter.getData();
    }

}
