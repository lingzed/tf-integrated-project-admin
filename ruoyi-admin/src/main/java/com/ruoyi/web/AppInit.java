package com.ruoyi.web;

import com.alibaba.fastjson2.JSON;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.system.service.StatementCfgService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Set;

/**
 * 启动时初始化
 */
@Component
public class AppInit implements ApplicationRunner {
    private static final Logger log = LoggerFactory.getLogger(AppInit.class);
    @Resource
    private StatementCfgService statementCfgService;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        initStatementCfg();
    }

    /**
     * 初始化所有的报表配置缓存
     */
    @Log(title = "报表管理", businessType = BusinessType.INIT)
    private void initStatementCfg() {
        log.info("开始初始化所有报表配置缓存...");
        statementCfgService.refreshAllCfg();
        log.info("报表配置初始化完成");
    }
}
