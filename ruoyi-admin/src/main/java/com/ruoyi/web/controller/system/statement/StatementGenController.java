package com.ruoyi.web.controller.system.statement;

import com.ruoyi.common.config.RuoYiConfig;
import com.ruoyi.common.constant.MsgConstants;
import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.PeriodUtil;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.file.FileUtils;
import com.ruoyi.common.utils.spring.SpringUtils;
import com.ruoyi.system.domain.Corporation;
import com.ruoyi.system.service.CorporationService;
import com.ruoyi.system.service.DownloadService;
import com.ruoyi.system.service.statement.impl.BmZjLxCsStmtGen;
import com.ruoyi.system.service.statement.impl.CbJzStmtGen;
import com.ruoyi.system.service.statement.impl.GgsGsfSrQkTjStmtGen;
import com.ruoyi.system.service.statement.impl.GlJyStmtGen;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * 报表生成控制器
 */
@RestController
@RequestMapping("/stmt-gen")
public class StatementGenController extends BaseStmtGenController {
    private static final Logger log = LoggerFactory.getLogger(StatementGenController.class);

    @Resource
    private RedisCache redisCache;
    @Resource
    private DownloadService downloadService;
    @Resource
    private CorporationService corporationService;
    @Resource
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;

    /**
     * 下载生成的报表文件
     * @param response
     * @param fileId
     * @throws IOException
     */
    @GetMapping("/{fileId}")
    public void download(HttpServletResponse response, @PathVariable String fileId) throws IOException {
        String cacheFilename = redisCache.getCacheStr(fileId);
        if (StringUtils.isEmpty(cacheFilename)) {
            throw new SecurityException(MsgConstants.DOWNLOAD_STMT_FILE_NOT_EXISTS);
        }
        Path path = Paths.get(RuoYiConfig.getStatementGenRootPath(), fileId + FileUtils.getNameSuffix(cacheFilename));
        if (Files.notExists(path)) {
            throw new SecurityException(MsgConstants.DOWNLOAD_STMT_FILE_NOT_EXISTS);
        }
        try {
            downloadService.download(response, path.toFile(), cacheFilename);
        } finally {
            threadPoolTaskExecutor.execute(() -> {
                try {
                    delTempStmtFile(path, fileId);
                } catch (IOException e) {
                    log.error("删除临时报表文件失败：path={}, fileId={}", path, fileId, e);
                }
            });
        }
    }

    /**
     * 部门资金利息测算表生成
     * @param corpCode
     * @param period
     * @return
     */
    @GetMapping("/bm-zj-li-cs")
    public SseEmitter bmZjLxCsStmtGen(String corpCode, String period) {
        checkCorpCode(corpCode);
        checkPeriod(period);
        BmZjLxCsStmtGen stmtGen = SpringUtils.getBean(BmZjLxCsStmtGen.class);
        return generateStmt(stmtGen, corpCode, period);
    }

    /**
     * 成本结转表生成
     * @param corpCode
     * @param period
     * @return
     */
    @GetMapping("/cb-jz")
    public SseEmitter cbJzbStmtGen(String corpCode, String period) {
        checkCorpCode(corpCode);
        checkPeriod(period);
        CbJzStmtGen stmtGen = SpringUtils.getBean(CbJzStmtGen.class);
        return generateStmt(stmtGen, corpCode, period);
    }

    /**
     * 各公司各省份收入情况统计表生成
     * @param corpCode
     * @param period
     * @return
     */
    @GetMapping("/gss-gsf-sr-qk-tj")
    public SseEmitter ggsGsfSrQkTjStmtGen(String corpCode, String period) {
        checkCorpCode(corpCode);
        checkPeriod(period);
        GgsGsfSrQkTjStmtGen stmtGen = SpringUtils.getBean(GgsGsfSrQkTjStmtGen.class);
        return generateStmt(stmtGen, corpCode, period);
    }

    /**
     * 关联校验表生成
     * @param corpCode
     * @param period
     * @return
     */
    @GetMapping("/gl-jy")
    public SseEmitter glJyStmtGen(String corpCode, String period) {
        checkCorpCode(corpCode);
        checkPeriod(period);
        GlJyStmtGen stmtGen = SpringUtils.getBean(GlJyStmtGen.class);
        return generateStmt(stmtGen, corpCode, period);
    }

    /**
     * 校验公司编码
     * @param corpCode
     */
    private void checkCorpCode(String corpCode) {
        if (StringUtils.isEmpty(corpCode)) {
            throw new ServiceException(MsgConstants.CORP_CODE_REQUIRED);
        }
        Corporation byCode = corporationService.findByCode(corpCode);
        if (byCode == null) {
            throw new SecurityException(MsgConstants.CORP_CODE_NOT_EXISTS);
        }
    }

    /**
     * 校验期间
     * @param period
     */
    private void checkPeriod(String period) {
        if (StringUtils.isEmpty(period)) {
            throw new ServiceException(MsgConstants.PERIOD_REQUIRED);
        }
        PeriodUtil.check(period);
    }

    /**
     * 删除临时文件和缓存
     * @param path
     * @param fileId
     * @throws IOException
     */
    private void delTempStmtFile(Path path, String fileId) throws IOException {
        if (Files.exists(path)) {
            Files.delete(path);
            log.info("删除生成的临时报表文件：【{}】", path.toAbsolutePath());
        }
        if (redisCache.deleteObject(fileId)) {
            log.info("删除缓存的临时文件ID：【{}】", fileId);
        }
    }
}

