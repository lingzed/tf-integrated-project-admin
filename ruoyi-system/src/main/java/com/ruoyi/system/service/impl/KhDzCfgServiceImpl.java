package com.ruoyi.system.service.impl;

import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.domain.statement.po.KhDzCfg;
import com.ruoyi.system.mapper.KhDzCfgMapper;
import com.ruoyi.system.service.KhDzCfgService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class KhDzCfgServiceImpl implements KhDzCfgService {
    @Resource
    private KhDzCfgMapper khDzCfgMapper;
    @Resource
    private RedisCache redisCache;

    @Override
    public String findByCode(String code) {
        String content = redisCache.getCacheStr(code);
        if (StringUtils.isEmpty(content)) {
            content = khDzCfgMapper.selectByCode(code);
            redisCache.setCacheStr(code, content);
        }
        return content;
    }
}
