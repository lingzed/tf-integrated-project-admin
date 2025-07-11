package com.ruoyi.system.service;

import com.ruoyi.system.domain.Corporation;

public interface CorporationService {
    /**
     * 通过corpCode查询
     * @param corpCode
     * @return
     */
    Corporation findByCode(String corpCode);
}
