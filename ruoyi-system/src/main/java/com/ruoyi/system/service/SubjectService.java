package com.ruoyi.system.service;

import com.ruoyi.common.u8c.subj.Subject;

import java.util.List;
import java.util.Set;

/**
 * 科目业务
 */
public interface SubjectService {

    /**
     * 刷新所有科目缓存
     */
    void refreshAllSubjCache();

    /**
     * 获取缓存的所有科目的列表
     * @return
     */
    Set<Subject> getAllSubjSet();

    /**
     * 返回缓存中的具体科目
     * @param subjCode  科目编码
     * @return
     */
    Subject getSubject(String subjCode);
}
