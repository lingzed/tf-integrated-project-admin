package com.ruoyi.system.service.statement.impl.extractor;

import org.springframework.stereotype.Component;

/**
 * 无形资产分类变动sheet提取逻辑
 */
@Component("WXZCFLBD")
public class WXZCFLBDExtractorHandle extends FLBDExtractorHandle {

    @Override
    protected int changeRowIdx(String rowKey, String colKey, int oldRi) {
        if (rowKey.equals("三、本年提取(摊销)") && colKey.equals("1701")) {
            return oldRi - 1;
        } else {
            return oldRi;
        }
    }
}
