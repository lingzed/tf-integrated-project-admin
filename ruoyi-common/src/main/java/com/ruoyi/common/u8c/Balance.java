package com.ruoyi.common.u8c;

import java.math.BigDecimal;

/**
 * 余额接口
 */
public interface Balance {
    /**
     * 期初借方数
     * @return
     */
    BigDecimal getInitDebitLocAmount();

    /**
     * 期初贷方数
     * @return
     */
    BigDecimal getInitCreditLocAmount();

    /**
     * 本期借方
     * @return
     */
    BigDecimal getDebitLocAmount();

    /**
     * 本期贷方
     * @return
     */
    BigDecimal getCreditLocAmount();

    /**
     * 借方累计
     * @return
     */
    BigDecimal getDebitAccumLocAmount();

    /**
     * 贷方累计
     * @return
     */
    BigDecimal getCreditAccumLocAmount();

    /**
     * 期末借方数
     * @return
     */
    BigDecimal getEndDebitLocAmount();

    /**
     * 期末贷方数
     * @return
     */
    BigDecimal getEndCreditLocAmount();
}
