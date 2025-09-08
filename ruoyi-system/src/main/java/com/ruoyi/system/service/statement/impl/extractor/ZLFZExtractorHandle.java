package com.ruoyi.system.service.statement.impl.extractor;

import com.ruoyi.common.enums.u8c.SubjDirection;
import com.ruoyi.system.domain.statement.StmtGenContext;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 租赁负债sheet提取逻辑
 */
@Component("ZLFZ")
public class ZLFZExtractorHandle extends SimpleCodeAnd3chExtractorHandle {

//    @Override
//    protected void change(Map<String, HeadKeyValHandle> handleMap, StmtGenContext cxt) {
//        String corpCode = cxt.corpCode();
//        handleMap.put("本期增加", (sb, c, pc, direction) -> {
//            // A04是借方，其他公司以科目原始方向为准
//            // 若一开始就是A04先走，那么handleMap中"本期增加"的handle就会被覆盖为A04，后续再有其他公司进来，如果不重新覆盖就会用
//            // A04的handle，因此当公司编码不是A04时，需要重新覆盖回去
////            direction = corpCode != null && corpCode.equals("A04") ? SubjDirection.DEBIT : direction;
//            if (corpCode != null && corpCode.equals("A04")) {
//                return currBalance(sb, SubjDirection.CREDIT);
//            } else {
//                return currBalance(sb, direction);
//            }
//        });
//    }
}
