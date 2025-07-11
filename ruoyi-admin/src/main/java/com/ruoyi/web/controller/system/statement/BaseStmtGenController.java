package com.ruoyi.web.controller.system.statement;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.enums.statement.StatementTpl;
import com.ruoyi.system.domain.statement.StmtGenContext;
import com.ruoyi.common.u8c.warpper.FinancialDataWrapper;
import com.ruoyi.system.domain.statement.RowColHeadIndex;
import com.ruoyi.system.domain.statement.cfg.StmtCfgWrapper;
import com.ruoyi.system.service.statement.impl.StatementGenProcess;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Controller;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 * 基础报表生成控制器
 */
@Controller
public class BaseStmtGenController extends BaseController {
    private static final Logger log = LoggerFactory.getLogger(BaseStmtGenController.class);
    private static final String STATUS_PROGRESS = "progress";
    private static final String STATUS_SUCCESS = "success";
    private static final String STATUS_ERROR = "error";
    @Resource
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;

    protected SseEmitter generateStmt(StatementGenProcess statementGenProcess, String corpCode, String period) {
        SseEmitter emitter = new SseEmitter(0L); // 默认不超时

        threadPoolTaskExecutor.submit(() -> {
            try {
                long start = System.currentTimeMillis();
                // 初始化扩展上下文
                StmtGenContext context = new StmtGenContext();
                context.setCorpCode(corpCode);  // 公司
                context.setPeriod(period);      // 期间

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
//                    log.info("行列头映射数据集：{}", JSON.toJSONString(rcHeadMapData));
                    context.setRcHeadMapData(rcHeadMapData);
                    emitter.send(progressEvent("行列头映射数据集提取完成"));
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

                emitter.send(progressEvent("正在写入Excel..."));
                byte[] dataByte = statementGenProcess.writeData(stmtTplFile, rcHeadIndexMap, dataMap, stmtCfg, context);
                emitter.send(progressEvent("写入完成"));

                emitter.send(progressEvent("正在生成报表文件..."));
                String outFilename = String.format(stmtTpl.getOutFilename(), period);   // 格式化输出文件名，当前期间
                String url = statementGenProcess.outputFile(dataByte, outFilename, context);    // 输出临时文件，生成url
                // 将生成的报表保存为数据模板文件，有就执行，没有就不执行
                String dataTplPath = statementGenProcess.saveDataTpl(dataByte, outFilename, context);

                long end = System.currentTimeMillis();
                emitter.send(progressEvent(String.format("完成，耗时%ds", (end - start) / 1000)));
                emitter.send(successEvent(url));

                emitter.complete();
            } catch (Exception e) {
                try {
                    emitter.send(errorEvent("生成失败：" + e.getMessage()));
                } catch (IOException ignored) {
                }
                emitter.completeWithError(e);
            }
        });

        return emitter;
    }

    private SseEmitter.SseEventBuilder progressEvent(String msg) {
        return SseEmitter.event().name(STATUS_PROGRESS).data(msg);
    }

    private SseEmitter.SseEventBuilder successEvent(String msg) {
        return SseEmitter.event().name(STATUS_SUCCESS).data(msg);
    }

    private SseEmitter.SseEventBuilder errorEvent(String msg) {
        return SseEmitter.event().name(STATUS_ERROR).data(msg);
    }
}
