package com.gnu.pbl2.coverLetter.dto;

import com.gnu.pbl2.coverLetter.entity.CoverLetter;
import com.gnu.pbl2.voucher.entity.enums.VoucherTier;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CoverLetterResponseDto {
    private Long coverLetterId;
    private String title;
    private String data;
    private Integer isDeleted;
    private VoucherTier useVoucher;

    public CoverLetterResponseDto(CoverLetter coverLetter) {
        this.title = coverLetter.getTitle();
        this.coverLetterId = coverLetter.getCoverLetterId();
        this.data = coverLetter.getData();
        this.isDeleted = coverLetter.getIsDeleted();
        this.useVoucher = coverLetter.getUseVoucher();
    }

}
