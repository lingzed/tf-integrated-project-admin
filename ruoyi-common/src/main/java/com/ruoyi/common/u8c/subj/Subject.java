package com.ruoyi.common.u8c.subj;

import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.enums.u8c.SubjDirection;

import java.util.Objects;

/**
 * 科目实体
 */
public class Subject {
    @Excel(name = "科目编码")
    private String subjCode;    // 科目编码
    @Excel(name = "科目名称")
    private String subjName;    // 科目名称
    @Excel(name = "科目方向")
    private SubjDirection subjDirection;    // 科目方向

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

    public SubjDirection getSubjDirection() {
        return subjDirection;
    }

    public void setSubjDirection(SubjDirection subjDirection) {
        this.subjDirection = subjDirection;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Subject subject = (Subject) o;
        return Objects.equals(subjCode, subject.subjCode) && Objects.equals(subjName, subject.subjName) && subjDirection == subject.subjDirection;
    }

    @Override
    public int hashCode() {
        return Objects.hash(subjCode, subjName, subjDirection);
    }

    public static Subject of(String subjCode, String subjName) {
        Subject subject = new Subject();
        subject.setSubjCode(subjCode);
        subject.setSubjName(subjName);
        return subject;
    }
}
