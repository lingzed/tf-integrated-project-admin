package com.ruoyi.common.enums.statement;

public enum JsonType {
    STRING((short) 0, "字符串"),
    NUMBER((short) 1, "数字"),
    BOOLEAN((short) 2, "布尔"),
    OBJ((short) 3, "对象"),
    ARRAY((short) 4, "列表"),
    NULL((short) 5, "null");

    private Short type;
    private String desc;

    JsonType(Short type, String desc) {
        this.type = type;
        this.desc = desc;
    }

    public Short getType() {
        return type;
    }

    public String getDesc() {
        return desc;
    }
}
