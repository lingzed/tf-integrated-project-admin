package com.ruoyi.common.u8c.subj;

/**
 * 包装AccSubjParentVOWrapper，科目查询接口返回的最外层
 */
public class AccSubjParentVOWrapWrapper {
    private AccSubjParentVOWrapper accsubjParentVO; // AccSubjParentVO一层包装

    public AccSubjParentVOWrapper getAccsubjParentVO() {
        return accsubjParentVO;
    }

    public void setAccsubjParentVO(AccSubjParentVOWrapper accsubjParentVO) {
        this.accsubjParentVO = accsubjParentVO;
    }
}
