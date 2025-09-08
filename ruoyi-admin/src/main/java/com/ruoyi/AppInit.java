package com.ruoyi;

import com.alibaba.fastjson2.JSON;
import com.ruoyi.common.config.RuoYiConfig;
import com.ruoyi.common.u8c.JobBasFil;
import com.ruoyi.common.u8c.cust.CustomerWrapper;
import com.ruoyi.common.u8c.query.DetailWrapperQuery;
import com.ruoyi.common.u8c.warpper.DetailWrapper;
import com.ruoyi.common.utils.PeriodUtil;
import com.ruoyi.common.utils.U8CApiUtil;
import com.ruoyi.system.service.DetailWrapperService;
import com.ruoyi.system.service.StatementCfgService;
import com.ruoyi.system.service.SubjectService;
import com.ruoyi.system.service.statement.KhDzWriteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * 启动时初始化
 */
@Component
public class AppInit implements ApplicationRunner {
    private static final Logger log = LoggerFactory.getLogger(AppInit.class);
    @Resource
    private StatementCfgService statementCfgService;
    @Resource
    private SubjectService subjectService;
    @Resource(name = "threadPoolTaskExecutor")
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;

    @Override
    public void run(ApplicationArguments args) {
        Boolean initStmtCfg = RuoYiConfig.InitCache.getInitStmtCfg();
        if (initStmtCfg != null && initStmtCfg) {
            threadPoolTaskExecutor.submit(initStatementCfg());
        }
        Boolean initSubj = RuoYiConfig.InitCache.getInitSubj();
        if (initSubj != null && initSubj) {
            threadPoolTaskExecutor.submit(initAllSubjCache());
        }
    }

    /**
     * 初始化所有的报表配置缓存
     */
    private Runnable initStatementCfg() {
        return () -> {
            try {
                long start = System.currentTimeMillis();
                log.info("开始初始化所有报表配置缓存...");
                statementCfgService.refreshAllCfg();
                long end = System.currentTimeMillis();
                log.info("报表配置缓存初始化完成，耗时{}ms", end - start);
            } catch (Exception e) {
                log.error("报表配置初始化失败: {}", e.getMessage(), e);
            }
        };
    }

    /**
     * 初始化所有科目的缓存
     *
     * @return
     */
    private Runnable initAllSubjCache() {
        return () -> {
            try {
                long start = System.currentTimeMillis();
                log.info("开始初始化所有科目缓存...");
                subjectService.refreshAllSubjCache();
                long end = System.currentTimeMillis();
                log.info("科目缓存初始化完成，耗时{}ms", end - start);
            } catch (Exception e) {
                log.error("科目缓存初始化失败: {}", e.getMessage(), e);
            }
        };
    }
}
