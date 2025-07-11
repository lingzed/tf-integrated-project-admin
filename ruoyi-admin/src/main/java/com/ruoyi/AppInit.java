package com.ruoyi;

import com.alibaba.fastjson2.JSON;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.enums.statement.StatementCfgType;
import com.ruoyi.common.enums.statement.StatementTpl;
import com.ruoyi.common.utils.StmtRelatedUtil;
import com.ruoyi.common.utils.json.JsonUtil;
import com.ruoyi.system.domain.statement.RowColHeadIndex;
import com.ruoyi.system.domain.statement.cfg.RowColHeadIndexCfg;
import com.ruoyi.system.service.StatementCfgService;
import com.ruoyi.system.service.statement.StatementReadService;
import com.ruoyi.system.service.SubjectService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

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
    @Resource
    private StatementReadService statementReadService;
    @Resource
    private RedisCache redisCache;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        initStatementCfg();
//        initAllSubjCache();
//        test();
    }

    /**
     * 初始化所有的报表配置缓存
     */
    @Log(title = "报表管理", businessType = BusinessType.INIT)
    private void initStatementCfg() {
        long start = System.currentTimeMillis();
        log.info("开始初始化所有报表配置缓存...");
        statementCfgService.refreshAllCfg();
        long end = System.currentTimeMillis();
        log.info("报表配置缓存初始化完成，耗时{}ms", end - start);
    }

    @Log(title = "科目管理", businessType = BusinessType.INIT)
    private void initAllSubjCache() {
        long start = System.currentTimeMillis();
        log.info("开始初始化所有科目缓存...");
        subjectService.refreshAllSubjCache();
        long end = System.currentTimeMillis();
        log.info("科目缓存初始化完成，耗时{}ms", end - start);
    }

    public void test() throws Exception {
        StatementTpl statementTpl = StatementTpl.TPL_GGS_GSF_SR_QK_TJB_A;
        String stmtTplFile = StmtRelatedUtil.getStmtTplFile(statementTpl);
        String cfgKey = StmtRelatedUtil.getCfgCode(statementTpl, StatementCfgType.ROW_COL_HEAD_INDEX_CFG);
        String cacheStr = redisCache.getCacheStr(cfgKey);
        List<RowColHeadIndexCfg> listFromJson = JsonUtil.getListFromJson(cacheStr, RowColHeadIndexCfg.class);
        Map<String, RowColHeadIndex> rcHeadMap = statementReadService.extractRowColHead(stmtTplFile, listFromJson);
        log.info("rcHeadMap: {}", JSON.toJSONString(rcHeadMap));
    }
}
