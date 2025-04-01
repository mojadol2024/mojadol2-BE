package com.gnu.pbl2.user.entity.enums;

import lombok.Getter;

@Getter
public enum Tier {
    FREE("Free Membership"),
    GOLD("Gold Membership");

    private final String description;

    Tier(String description) {
        this.description = description;
    }

}
