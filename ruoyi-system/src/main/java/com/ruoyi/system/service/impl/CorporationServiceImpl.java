package com.ruoyi.system.service.impl;

import com.ruoyi.system.domain.Corporation;
import com.ruoyi.system.mapper.CorporationMapper;
import com.ruoyi.system.service.CorporationService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class CorporationServiceImpl implements CorporationService {
    @Resource
    private CorporationMapper corporationMapper;

    @Override
    public Corporation findByCode(String corpCode) {
        return corporationMapper.selectByCode(corpCode);
    }
}
