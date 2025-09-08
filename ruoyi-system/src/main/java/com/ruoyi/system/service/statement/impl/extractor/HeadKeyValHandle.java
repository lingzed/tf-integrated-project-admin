package com.ruoyi.system.service.statement.impl.extractor;

import com.ruoyi.common.enums.u8c.SubjDirection;
import com.ruoyi.common.u8c.SubjBalance;

import java.math.BigDecimal;

/**
 * colKey对应的取值逻辑
 */
public interface HeadKeyValHandle {
    BigDecimal getVal(SubjBalance sb, String code, String pCode, SubjDirection direction);
}
