package com.gnu.pbl2.voucher.dto;

import com.gnu.pbl2.voucher.entity.Voucher;
import com.gnu.pbl2.voucher.entity.enums.VoucherTier;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class VoucherDto {
    private Integer totalCount;
    private VoucherTier type;
    private LocalDateTime issuedAt;
    private LocalDateTime expiredAt;
    private Integer deletedFlag;

    public static VoucherDto toDto(Voucher voucher) {
        VoucherDto dto = new VoucherDto();
        dto.setTotalCount(voucher.getTotalCount());
        dto.setType(voucher.getType());
        dto.setIssuedAt(voucher.getIssuedAt());
        dto.setExpiredAt(voucher.getExpiredAt());
        dto.setDeletedFlag(voucher.getDeletedFlag());
        return dto;
    }
}
