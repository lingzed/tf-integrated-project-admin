package com.ruoyi.system.service.statement.impl.extractor;

import com.ruoyi.common.enums.u8c.SubjDirection;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;

/**
 * 固定资产分类变动sheet提取逻辑-v1
 */
@Component("GDZCFLBD")
public class GDZCFLBDExtractorHandleV1 extends FLBDExtractorHandle {

    @Override
    protected boolean excludeSubCode(String code, String pCode, Set<String> subjSet) {
        // subj是pSubj的子编码且在subjSet中出现过就排除
        // 这个排除是因为subjSet中的编码存在有父子编码的情况，客户的意思是子编码已经单独出去了，那么父编码里面也应该减去
        return !code.equals(pCode) && subjSet.contains(code);
    }

    @Override
    protected void change(Map<String, HeadKeyValHandle> handleMap) {
        // 1601取借方的数，其它为null
        handleMap.put("零星购置", (sb, c, pc, d) -> pc.equals("1601") ? currBalance(sb, SubjDirection.DEBIT) : null);
        // 非1601取贷方的数，1601为null
        handleMap.put("三、本年提取(摊销)", (sb, c, pc, d) -> pc.equals("1601") ? null : currBalance(sb, SubjDirection.CREDIT));
    }
}
