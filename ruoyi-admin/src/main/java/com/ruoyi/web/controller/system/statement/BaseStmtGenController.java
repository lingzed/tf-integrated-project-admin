package com.ruoyi.web.controller.system.statement;

import com.alibaba.fastjson2.JSON;
import com.ruoyi.common.constant.MsgConstants;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.entity.Corporation;
import com.ruoyi.common.enums.statement.StatementTpl;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.u8c.AuxBalance;
import com.ruoyi.common.u8c.warpper.DetailWrapper;
import com.ruoyi.common.utils.PeriodUtil;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.domain.statement.StmtGenContext;
import com.ruoyi.common.u8c.warpper.FinancialDataWrapper;
import com.ruoyi.system.domain.statement.RowColHeadIndex;
import com.ruoyi.system.domain.statement.cfg.StmtCfgWrapper;
import com.ruoyi.system.service.CorporationService;
import com.ruoyi.system.service.statement.KhDzQueryService;
import com.ruoyi.system.service.statement.KhDzWriteService;
import com.ruoyi.system.service.statement.impl.StatementGenProcess;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * 基础报表生成控制器
 */
@RestController
public class BaseStmtGenController extends BaseController {
    private static final Logger log = LoggerFactory.getLogger(BaseStmtGenController.class);
    private static final String STATUS_PROGRESS = "progress";
    private static final String STATUS_SUCCESS = "success";
    private static final String STATUS_ERROR = "error";
    @Resource
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;
    @Resource
    private CorporationService corporationService;
    @Resource
    private KhDzQueryService khDzQueryService;
    @Resource
    private KhDzWriteService khDzWriteService;

    protected SseEmitter generateStmt(StatementGenProcess statementGenProcess, String corpCode, String period) {
        SseEmitter emitter = new SseEmitter(0L); // 默认不超时
        /*
         * 因为报表生成用sse连接，而连接又在多线程环境下，默认情况下Spring Security不会把主线程的上下文信息传递到异步线程
         * 没上下文信息就拿不到当前用户的信息，这就导致我们在报表生成的过程中是拿不到当前用户的信息的，所以我们需要手动传递信息过来
         * */
        Long userId = SecurityUtils.getUserId();
        threadPoolTaskExecutor.submit(() -> {
            try {
                // 校验公司和期间
                checkCorpCode(corpCode);
                checkPeriod(period);

                long start = System.currentTimeMillis();
                // 初始化报表生成上下文
                StmtGenContext context = new StmtGenContext();
                context.setCorpCode(corpCode);  // 公司
                context.setPeriod(period);      // 期间
                context.setUserId(userId);      // userId

                emitter.send(progressEvent("准备生成..."));
                emitter.send(progressEvent("正在确认报表模板..."));
                StatementTpl stmtTpl = statementGenProcess.getStmtTpl(context);
                emitter.send(progressEvent(String.format("确认报表模板：【%s】", stmtTpl.name())));
                context.setStatementTpl(stmtTpl);   // 上下文存入报表模板
                // 拿到报表模板文件
                File stmtTplFile = statementGenProcess.getStmtTplFile(stmtTpl, context);

                emitter.send(progressEvent("正在加载报表相关配置..."));
                StmtCfgWrapper stmtCfg = statementGenProcess.loadStmtCfg(context);
                emitter.send(progressEvent("配置加载完成"));

                // 是否执行行列头映射数据集流程
                Boolean withDataTpl = statementGenProcess.withDataTplHandle(context);
                if (withDataTpl != null && withDataTpl) {
                    emitter.send(progressEvent("正在提取报表数据模板中的行列头映射数据集..."));
                    File stmtDataTplFile = statementGenProcess.getStmtDataTplFile(context);
                    Map<String, Map<String, String>> rcHeadMapData = statementGenProcess
                            .getRowColHeadMapData(stmtDataTplFile, stmtCfg, context);
                    log.info("行列头映射数据集：{}", JSON.toJSONString(rcHeadMapData));
                    context.setRcHeadMapData(rcHeadMapData);
                    emitter.send(progressEvent("数据集提取完成"));
                }

                emitter.send(progressEvent("正在提取行列头索引映射..."));
                Map<String, RowColHeadIndex> rcHeadIndexMap = statementGenProcess
                        .getRowColHeadIndexMap(stmtTplFile, stmtCfg, context);
                statementGenProcess.checkRcHeadIndex(rcHeadIndexMap);
                emitter.send(progressEvent("索引映射提取完成"));

                emitter.send(progressEvent("远程拉取数据中..."));
                Map<String, FinancialDataWrapper> dataMap = statementGenProcess.fetchData(rcHeadIndexMap, stmtCfg, context);
                log.info("请求完成");
                emitter.send(progressEvent("数据拉取完成"));

                // 对扩展上下文对象进行扩展
                statementGenProcess.doExpandContext(context);

                log.info("开始写入...");
                emitter.send(progressEvent("正在写入Excel..."));
                byte[] dataByte = statementGenProcess.writeData(stmtTplFile, rcHeadIndexMap, dataMap, stmtCfg, context);
                emitter.send(progressEvent("写入完成"));
                log.info("写入完成");

                emitter.send(progressEvent("正在生成报表文件..."));
                String outFilename = String.format(stmtTpl.getOutFilename(), period);   // 格式化输出文件名，当前期间
                String url = statementGenProcess.outputFile(dataByte, outFilename, context);    // 输出临时文件，生成url
                // 将生成的报表保存为数据模板文件，有就执行，没有就不执行
                String dataTplPath = statementGenProcess.saveDataTpl(dataByte, outFilename, context);

                long end = System.currentTimeMillis();
                emitter.send(successEvent(String.format("完成，耗时%ds", (end - start) / 1000), url));

                emitter.complete();
            } catch (Exception e) {
                processGenErr(emitter, e);
            }
        });

        return emitter;
    }

    protected SseEmitter generateKhDzd(List<String> corpCodes, String ctrName, String jobPjtName, String startDate, String endDate) {
        SseEmitter emitter = new SseEmitter(0L); // 默认不超时
        threadPoolTaskExecutor.submit(() -> {
            try {
                checkParam(corpCodes, ctrName, startDate, endDate);

                long start = System.currentTimeMillis();
                emitter.send(progressEvent("准备生成..."));
                emitter.send(progressEvent("获取客户编码..."));
                String ctrCode = khDzQueryService.getCustomerCode(ctrName);
                emitter.send(progressEvent("客户编码【" + ctrCode + "】"));
                String jobPjtCode = null;
                if (StringUtils.isNotEmpty(jobPjtName)) {
                    emitter.send(progressEvent("获取项目编码..."));
                    jobPjtCode = khDzQueryService.getJobPjtCode(jobPjtName);
                    emitter.send(progressEvent("项目编码【" + jobPjtCode + "】"));
                }
                emitter.send(progressEvent("开始拉取数据..."));
                List<String> periodList = PeriodUtil.firstToCurrRange(startDate, endDate);
                Map<String, Object> result = khDzQueryService.fetchData(corpCodes, ctrCode, ctrName, jobPjtCode, jobPjtName, periodList);
                emitter.send(progressEvent("数据拉取完成"));
                log.info("开始写入...");
                emitter.send(progressEvent("正在写入Excel..."));
                byte[] write = khDzWriteService.write(corpCodes, ctrName, periodList, result);
                emitter.send(progressEvent("写入完成"));
                log.info("写入完成");

                emitter.send(progressEvent("正在生成报表文件..."));
                String url = khDzWriteService.outputFile(write, ctrName, true);

                long end = System.currentTimeMillis();
                emitter.send(successEvent(String.format("完成，耗时%ds", (end - start) / 1000), url));

                emitter.complete();
            } catch (Exception e) {
                log.error("发生异常");
                processGenErr(emitter, e);
            }
        });
        return emitter;
    }

    private void processGenErr(SseEmitter emitter, Exception e) {
        try {
            emitter.send(errorEvent("生成失败：" + e.getMessage()));
            log.info("生成失败：{}", e.getMessage());
        } catch (IOException ignored) {
        }
        emitter.completeWithError(e);
    }

    private SseEmitter.SseEventBuilder progressEvent(String msg) {
        return SseEmitter.event().name(STATUS_PROGRESS).data(result(msg, null));
    }

    private SseEmitter.SseEventBuilder successEvent(String msg, String value) {
        return SseEmitter.event().name(STATUS_SUCCESS).data(result(msg, new String[]{"downloadUrl", value}));
    }

    private SseEmitter.SseEventBuilder errorEvent(String msg) {
        return SseEmitter.event().name(STATUS_ERROR).data(msg);
    }

    private String result(String msg, Object[]... data) {
        Map<String, Object> result = new HashMap<>();
        result.put("msg", msg);
        if (data != null) {
            for (Object[] datum : data) {
                String key = (String) datum[0];
                if (key == null) continue;
                Object val = datum[1];
                result.put(key, val);
            }
        }
        return JSON.toJSONString(result);
    }

    /**
     * 校验公司编码
     *
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
     *
     * @param period
     */
    private void checkPeriod(String period) {
        if (StringUtils.isEmpty(period)) {
            throw new ServiceException(MsgConstants.PERIOD_REQUIRED);
        }
        PeriodUtil.check(period);
    }

    private void checkParam(List<String> corpCodes, String ctrName, String startDate, String endDate) {
        if (CollectionUtils.isEmpty(corpCodes)) {
            throw new ServiceException(MsgConstants.CORP_CODE_REQUIRED);
        }
        Long total = corporationService.findByCodes(corpCodes);
        if (total == null || total != corpCodes.size()) {
            throw new SecurityException(MsgConstants.CORP_CODE_NOT_EXISTS);
        }
        if (StringUtils.isEmpty(startDate) || StringUtils.isEmpty(endDate)) {
            throw new ServiceException(MsgConstants.PERIOD_START_TO_END_REQUIRED);
        }
        PeriodUtil.check(startDate);
        PeriodUtil.check(endDate);
        if (StringUtils.isEmpty(ctrName)) {
            throw new ServiceException(MsgConstants.CTR_NAME_REQUIRED);
        }
    }
}
