package com.gnu.pbl2.coverLetter.dto;

import com.gnu.pbl2.coverLetter.entity.CoverLetter;
import com.gnu.pbl2.voucher.entity.enums.VoucherTier;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CoverLetterRequestDto {
    private Long coverLetterId;
    private String title;
    private String data;
    private Long userId;
    private VoucherTier useVoucher;

}
