package com.ruoyi.common.enums.u8c;

/**
 * 科目方向
 */
public enum SubjDirection {
    DEBIT("1", "借方"),
    CREDIT("2", "贷方");

    private String code;   // 方向编码
    private String direction;    // 方向

    SubjDirection(String code, String direction) {
        this.code = code;
        this.direction = direction;
    }

    public String getCode() {
        return code;
    }

    public String getDirection() {
        return direction;
    }

    public static SubjDirection getByCode(String code) {
        for (SubjDirection direction : SubjDirection.values()) {
            if (direction.getCode().equals(code)) {
                return direction;
            }
        }
        return null;
    }
}
