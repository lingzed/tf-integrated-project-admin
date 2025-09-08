package com.ruoyi.system.service.impl;

import com.ruoyi.common.u8c.query.DetailWrapperQuery;
import com.ruoyi.common.u8c.warpper.DetailWrapper;
import com.ruoyi.system.mapper.DetailWrapperMapper;
import com.ruoyi.system.service.DetailWrapperService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class DetailWrapperServiceImpl implements DetailWrapperService {
    @Resource
    private DetailWrapperMapper detailWrapperMapper;

    @Override
    public List<DetailWrapper> find(DetailWrapperQuery query) {
        return detailWrapperMapper.select(query);
    }
}
