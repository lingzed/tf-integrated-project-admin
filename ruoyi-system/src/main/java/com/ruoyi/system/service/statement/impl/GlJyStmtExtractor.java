package com.ruoyi.system.service.statement.impl;

import com.ruoyi.common.u8c.AssVo;
import com.ruoyi.common.u8c.Detail;
import com.ruoyi.common.u8c.warpper.FinancialDataWrapper;
import com.ruoyi.common.utils.PeriodUtil;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.domain.statement.CellWriter;
import com.ruoyi.system.domain.statement.RowColHeadIndex;
import com.ruoyi.system.domain.statement.StmtGenContext;
import com.ruoyi.system.domain.statement.cfg.StmtCfgWrapper;
import com.ruoyi.system.service.statement.BaseStmtExtractor;
import com.ruoyi.system.service.statement.ValueExtractor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


/**
 * 关联交易表提取器
 */
@Component
public class GlJyStmtExtractor extends BaseStmtExtractor implements ValueExtractor<Object> {
    private static final Logger log = LoggerFactory.getLogger(GlJyStmtExtractor.class);
    private static final Pattern PATTERN = Pattern.compile("^\\d+条$");

    @Override
    public List<CellWriter<Object>> extract(FinancialDataWrapper data, RowColHeadIndex rcHeadIndex,
                                            StmtCfgWrapper stmtCfg, Integer index, StmtGenContext context) {
        List<CellWriter<Object>> cellWrites = new ArrayList<>();

        Map<String, Map<String, BigDecimal>> kxbAmountMap = new HashMap<>();   // 客商&项目&部门科目余额映射
        Map<String, BigDecimal> colAmountCountMap = new HashMap<>();  // 对应列总计

        int ki = colIndex(rcHeadIndex, "客户名称");
        int xi = colIndex(rcHeadIndex, "项目");
        int bi = colIndex(rcHeadIndex, "部门");

        if (ki < 0 || xi < 0 || bi < 0) return cellWrites;

        boolean notM1 = context.periodMonth() != 1; // 是否不是1月
        String previousPeriod = PeriodUtil.getPreviousPeriod(context.period()); // 上期

        List<Detail> detailList = data.getDetailDataWrapper().getCurrPeriod();
        for (Detail detail : detailList) {
            List<AssVo> ass = detail.getAss();
            if (CollectionUtils.isEmpty(ass)) continue;

            // rowKey一定要按照客商_项目_部门的顺序连接，因为写入就是这个顺序，如果按照列表中的顺序，那么两个键对不上
            // 还有一种方法，保持这里的顺序，然后改行列头对应数据映射中的顺序，让其和这里的一致
            List<String> assNames = ass.stream()
                    .filter(a -> !a.getChecktypecode().equals("J04Ass"))    // 过滤掉行业类型
                    .map(AssVo::getCheckvaluename)
                    .collect(Collectors.toList());
            String rowKey = String.join("_", assNames);
            // 一行对应列的数的映射
            Map<String, BigDecimal> aRowAmountMap = kxbAmountMap.computeIfAbsent(rowKey, k -> new HashMap<>());

            String subjCode = detail.getAccsubj_code();
            // 以计收结尾为正，否则为负数
            String suffix = detail.getExplanation().endsWith("计收") ? "+" : "-";
            BigDecimal amountD = detail.getLocalcreditamount();   // 贷方数
            String dqKey = subjCode + "_locM" + suffix;  // 本月键
            mergeBigDecimal(aRowAmountMap, dqKey, amountD);
            String key = subjCode + "_accum" + suffix;  // 累计键
            mergeBigDecimal(aRowAmountMap, key, amountD);
        }

        // 不是1月就需要合并上月数
        if (notM1) {
            merge(kxbAmountMap, previousPeriod, context);
        }

        int rowI = 1;
        for (Map.Entry<String, Map<String, BigDecimal>> entry : kxbAmountMap.entrySet()) {
            String[] split = entry.getKey().split("_");
            String kName = split[1];  // 客商
            String xName = split[2];  // 项目
            String bName = split[0];  // 部门名称

            addCellWriter(cellWrites, rowI, ki, kName);
            addCellWriter(cellWrites, rowI, xi, xName);
            addCellWriter(cellWrites, rowI, bi, bName);

            int r = rowI;
            entry.getValue().forEach((colKey, val) -> {
                int ci = colIndex(rcHeadIndex, colKey); // 列对应的索引
                if (ci < 0) return;
                addCellWriter(cellWrites, r, ci, val);
                mergeBigDecimal(colAmountCountMap, colKey, val); // 记录当前列数的总额
            });
            rowI++;
        }
        int lastRowI = rowI;
        addCellWriter(cellWrites, lastRowI, 0, lastRowI - 1 + "条");
        colAmountCountMap.forEach((colKey, val) -> addCellWriter(cellWrites, lastRowI, colIndex(rcHeadIndex, colKey), val));
        return cellWrites;
    }

    private void merge(Map<String, Map<String, BigDecimal>> kxbAmountMap, String previousPeriod, StmtGenContext context) {
        Map<String, Map<String, String>> mapData = context.rcHeadMapData();
        if (MapUtils.isEmpty(mapData)) {
            log.warn("关联交易表_{}行列头映射的数据集为空，将以本期数据写入", previousPeriod);
            return;
        }

        mapData.forEach((rowKey, colValMap) -> {
            // 转换当前行的值到 BigDecimal Map
            Map<String, BigDecimal> currentRowData = convertBd(colValMap);

            // 匹配"???条"这样的key，因为这是最后一行的合计行，需要过滤调这行
            if (PATTERN.matcher(rowKey).matches()) return;

            // 获取或初始化目标行的 Map
            Map<String, BigDecimal> targetRowData = kxbAmountMap.computeIfAbsent(
                    rowKey,
                    k -> new HashMap<>()
            );

            // 合并数据（如果 key 冲突，则相加）
            currentRowData.forEach((colKey, value) ->
                    targetRowData.merge(colKey, value, BigDecimal::add)
            );
        });
    }

    private Map<String, BigDecimal> convertBd(Map<String, String> colValMap) {
        if (MapUtils.isEmpty(colValMap)) return new HashMap<>();
        return colValMap.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,                      // Key 保持不变
                        entry -> StringUtils.isEmpty(entry.getValue())
                                ? BigDecimal.ZERO
                                : new BigDecimal(entry.getValue())  // Value 转换逻辑
                ));
    }
}
