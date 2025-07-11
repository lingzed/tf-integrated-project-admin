package com.ruoyi.common.u8c.subj;

/**
 * 包装AccSubjParentVO一层
 */
public class AccSubjParentVOWrapper {
    private AccSubjParentVO accsubjParentVO;    // 最内层的科目对象

    public AccSubjParentVO getAccsubjParentVO() {
        return accsubjParentVO;
    }

    public void setAccsubjParentVO(AccSubjParentVO accsubjParentVO) {
        this.accsubjParentVO = accsubjParentVO;
    }
}
