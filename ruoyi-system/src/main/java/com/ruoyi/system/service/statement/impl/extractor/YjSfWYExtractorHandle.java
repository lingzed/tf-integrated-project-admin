package com.ruoyi.system.service.statement.impl.extractor;

import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.common.u8c.warpper.FinancialDataWrapper;
import com.ruoyi.system.domain.statement.CellWriter;
import com.ruoyi.system.domain.statement.RowColHeadIndex;
import com.ruoyi.system.domain.statement.StmtGenContext;
import com.ruoyi.system.service.statement.BaseStmtExtractor;
import org.apache.commons.collections4.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 应交税费-物业sheet提取逻辑
 */
@Component("YJSF-WY")
public class YjSfWYExtractorHandle extends BaseStmtExtractor implements ExtractorHandle {
    private static final Logger log = LoggerFactory.getLogger(YjSfWYExtractorHandle.class);
    @Resource
    private RedisCache redisCache;


    @Override
    public void handle(List<CellWriter<Object>> result, FinancialDataWrapper data, RowColHeadIndex rcHeadIndex,
                       List<String> corpList, StmtGenContext context) {
        String cacheKey = "YJSF:" + context.userId() + ":bCountMap";
        Map<String, Map<String, String>> bCountMap = redisCache.getCacheMap(cacheKey);
        if (MapUtils.isEmpty(bCountMap)) {
            log.info("【{}】对应的应交税费数据缓存为空", cacheKey);
            return;
        }

        bCountMap.forEach((rowKey, aRow) -> {
            int ri = rowIndex(rcHeadIndex, rowKey);
            aRow.forEach((colKey, val) -> {
                if (colKey.startsWith("@type")) return; // 因为redis开启了类型序列化，导致包含类型键值对，这里需要排除
                int ci = colIndex(rcHeadIndex, colKey);
                addCellWriter(result, ri, ci, new BigDecimal(val));
            });
        });
        log.info("删除应交税费数据缓存{}", redisCache.deleteObject(cacheKey) ? "成功" : "失败");
    }
}
