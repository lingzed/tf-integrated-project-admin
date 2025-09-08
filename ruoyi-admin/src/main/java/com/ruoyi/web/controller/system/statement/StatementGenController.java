package com.ruoyi.web.controller.system.statement;

import com.ruoyi.common.config.RuoYiConfig;
import com.ruoyi.common.constant.MsgConstants;
import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.file.FileUtils;
import com.ruoyi.common.utils.spring.SpringUtils;
import com.ruoyi.system.service.DownloadService;
import com.ruoyi.system.service.statement.impl.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;


import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

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
    @Resource(name = "threadPoolTaskExecutor")
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;

    /**
     * 部门资金利息测算表生成
     *
     * @param corpCode
     * @param period
     * @return
     */
    @GetMapping(value = "/bm-zj-li-cs")
    @PreAuthorize("@ss.hasPermi('stmt:bmZjLiCs:view')")
    public SseEmitter bmZjLxCsStmtGen(String corpCode, String period) {
        BmZjLxCsStmtGen stmtGen = SpringUtils.getBean(BmZjLxCsStmtGen.class);
        return generateStmt(stmtGen, corpCode, period);
    }

    /**
     * 成本结转表生成
     *
     * @param corpCode
     * @param period
     * @return
     */
    @GetMapping("/cb-jz")
    @PreAuthorize("@ss.hasPermi('stmt:cbJz:view')")
    public SseEmitter cbJzbStmtGen(String corpCode, String period) {
        CbJzStmtGen stmtGen = SpringUtils.getBean(CbJzStmtGen.class);
        return generateStmt(stmtGen, corpCode, period);
    }

    /**
     * 各公司各省份收入情况统计表生成
     *
     * @param corpCode
     * @param period
     * @return
     */
    @GetMapping("/gss-gsf-sr-qk-tj")
    @PreAuthorize("@ss.hasPermi('stmt:ggsGsfSrQkTj:view')")
    public SseEmitter ggsGsfSrQkTjStmtGen(String corpCode, String period) {
        GgsGsfSrQkTjStmtGen stmtGen = SpringUtils.getBean(GgsGsfSrQkTjStmtGen.class);
        return generateStmt(stmtGen, corpCode, period);
    }

    /**
     * 关联校验表生成
     *
     * @param corpCode
     * @param period
     * @return
     */
    @GetMapping("/gl-jy")
    @PreAuthorize("@ss.hasPermi('stmt:glJy:view')")
    public SseEmitter glJyStmtGen(String corpCode, String period) {
        GlJyStmtGen stmtGen = SpringUtils.getBean(GlJyStmtGen.class);
        return generateStmt(stmtGen, corpCode, period);
    }

    /**
     * 客户收支差分析明细表生成
     *
     * @param corpCode
     * @param period
     * @return
     */
    @GetMapping("/kh-szc-fx-mx")
    @PreAuthorize("@ss.hasPermi('stmt:khSzcFxMx:view')")
    public SseEmitter khSzcFxMxStmtGen(String corpCode, String period) {
        KhSzcFxMxStmtGen stmtGen = SpringUtils.getBean(KhSzcFxMxStmtGen.class);
        return generateStmt(stmtGen, corpCode, period);
    }

    /**
     * 客户余额表生成
     *
     * @param corpCode
     * @param period
     * @return
     */
    @GetMapping("/kh-ye")
    @PreAuthorize("@ss.hasPermi('stmt:khYe:view')")
    public SseEmitter khYeStmtGen(String corpCode, String period) {
        KhYeStmtGen stmtGen = SpringUtils.getBean(KhYeStmtGen.class);
        return generateStmt(stmtGen, corpCode, period);
    }

    /**
     * 利润分解表生成
     *
     * @param corpCode
     * @param period
     * @return
     */
    @GetMapping("/lr-fj")
    @PreAuthorize("@ss.hasPermi('stmt:lrFj:view')")
    public SseEmitter lrFjStmtGen(String corpCode, String period) {
        LrFjStmtGen stmtGen = SpringUtils.getBean(LrFjStmtGen.class);
        return generateStmt(stmtGen, corpCode, period);
    }

    /**
     * 人工成本及税金表生成
     *
     * @param corpCode
     * @param period
     * @return
     */
    @GetMapping("/rg-cb-sj")
    @PreAuthorize("@ss.hasPermi('stmt:rgCbRjJc:view')")
    public SseEmitter rgCbAndSjStmtGen(String corpCode, String period) {
        RgCbAndSjStmtGen stmtGen = SpringUtils.getBean(RgCbAndSjStmtGen.class);
        return generateStmt(stmtGen, corpCode, period);
    }

    /**
     * 人工成本统计表生成
     *
     * @param corpCode
     * @param period
     * @return
     */
    @GetMapping("/rg-cb-tj")
    @PreAuthorize("@ss.hasPermi('stmt:rgCbTj:view')")
    public SseEmitter rgCbTjStmtGen(String corpCode, String period) {
        RgCbTjStmtGen stmtGen = SpringUtils.getBean(RgCbTjStmtGen.class);
        return generateStmt(stmtGen, corpCode, period);
    }

    /**
     * 应收账款回款率表生成
     *
     * @param corpCode
     * @param period
     * @return
     */
    @GetMapping("/ys-zk-hkl")
    @PreAuthorize("@ss.hasPermi('stmt:ysZkHkl:view')")
    public SseEmitter ysZkHklStmtGen(String corpCode, String period) {
        YsZkHklStmtGen stmtGen = SpringUtils.getBean(YsZkHklStmtGen.class);
        return generateStmt(stmtGen, corpCode, period);
    }

    /**
     * 预算执行情况监控表生成
     *
     * @param corpCode
     * @param period
     * @return
     */
    @GetMapping("/ys-zx-qk-jk")
    @PreAuthorize("@ss.hasPermi('stmt:ysZxQkJk:view')")
    public SseEmitter ysZxQkJkStmtGen(String corpCode, String period) {
        YsZxQkJkStmtGen stmtGen = SpringUtils.getBean(YsZxQkJkStmtGen.class);
        return generateStmt(stmtGen, corpCode, period);
    }

    /**
     * 职能部门取数表生成
     *
     * @param corpCode
     * @param period
     * @return
     */
    @GetMapping("/zn-bm-qs")
    @PreAuthorize("@ss.hasPermi('stmt:znBmQs:view')")
    public SseEmitter znBmQsStmtGen(String corpCode, String period) {
        ZnBmQsStmtGen stmtGen = SpringUtils.getBean(ZnBmQsStmtGen.class);
        return generateStmt(stmtGen, corpCode, period);
    }

    /**
     * 损益分解表生成
     *
     * @param corpCode
     * @param period
     * @return
     */
    @GetMapping("/sy-fj")
    @PreAuthorize("@ss.hasPermi('stmt:syFj:view')")
    public SseEmitter syFjStmtGen(String corpCode, String period) {
        SyFjStmtGen stmtGen = SpringUtils.getBean(SyFjStmtGen.class);
        return generateStmt(stmtGen, corpCode, period);
    }

    /**
     * 月报生成
     *
     * @param corpCode
     * @param period
     * @return
     */
    @GetMapping("/yb")
    @PreAuthorize("@ss.hasPermi('stmt:yb:view')")
    public SseEmitter ybStmtGen(String corpCode, String period) {
        YbStmtGen stmtGen = SpringUtils.getBean(YbStmtGen.class);
        return generateStmt(stmtGen, corpCode, period);
    }

    /**
     * 客户对账单
     *
     * @param corpCodes
     * @param ctrName
     * @param jonPjtName
     * @param startDate
     * @param endDate
     * @return
     */
    @GetMapping("/kh-dzd")
    @PreAuthorize("@ss.hasPermi('stmt:khDzd:view')")
    public SseEmitter khDzdStmtGen(@RequestParam List<String> corpCodes, String ctrName, String jonPjtName, String startDate, String endDate) {
        return generateKhDzd(corpCodes, ctrName, jonPjtName, startDate, endDate);
    }

    /**
     * 下载生成的报表文件
     *
     * @param response
     * @param fileId
     * @throws IOException
     */
    @PostMapping("/download/{fileId}")
    @PreAuthorize("@ss.hasPermi('stmt:gen:download')")
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
     * 删除临时文件和缓存
     *
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

