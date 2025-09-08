package com.ruoyi.system.service.statement.impl.extractor;

import com.ruoyi.common.enums.u8c.SubjDirection;
import org.springframework.stereotype.Component;

/**
 * 长期待摊费用分类变动sheet提取逻辑-V1
 */
@Component("CQDTFYFLBD")
public class CQDTFYFLBDExtractorHandleV1 extends FLBDExtractorHandle {
    @Override
    protected SubjDirection confirmDirection(String code, String pCode) {
        return pCode.equals("180101") ? SubjDirection.DEBIT : SubjDirection.CREDIT;
    }

    @Override
    protected int changeRowIdx(String rowKey, String colKey, int oldRi) {
        if (rowKey.equals("三、本年提取(摊销)") && colKey.equals("180101")) {
            return oldRi - 1;
        } else {
            return oldRi;
        }
    }
}
