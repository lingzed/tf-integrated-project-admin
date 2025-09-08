package com.ruoyi.system.service.statement.impl.extractor;

import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 税金及附加sheet提取逻辑
 */
@Component("SJJFJ")
public class SJJFJExtractorHandle extends SimpleCodeAndBnLjExtractorHandle {

    @Override
    protected void addColHdMap(Map<String, String> colHdMap, String colHdCode) {
        if (colHdCode.contains("+")) {
            String[] split = colHdCode.split("\\+");
            for (String subCode : split) {
                colHdMap.put(subCode, colHdCode);
            }
        } else {
            colHdMap.put(colHdCode, colHdCode);
        }
    }
}
