package com.ruoyi.common.u8c.subj;

import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.enums.u8c.SubjDirection;

/**
 * 科目vo
 */
public class SubjectVo {
    private String subjCode;    // 科目编码
    private String subjName;    // 科目名称
    private String directionCode;   // 方向编码
    private String direction;   // 科目方向

    public String getSubjCode() {
        return subjCode;
    }

    public void setSubjCode(String subjCode) {
        this.subjCode = subjCode;
    }

    public String getSubjName() {
        return subjName;
    }

    public void setSubjName(String subjName) {
        this.subjName = subjName;
    }

    public String getDirectionCode() {
        return directionCode;
    }

    public void setDirectionCode(String directionCode) {
        this.directionCode = directionCode;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }
}
