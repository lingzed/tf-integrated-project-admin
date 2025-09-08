package com.ruoyi.system.service;

import com.ruoyi.system.domain.statement.po.KhDzCfg;

import java.util.List;

public interface KhDzCfgService {
    /**
     * 通过code查询
     * @param code
     * @return
     */
    String findByCode(String code);
}
