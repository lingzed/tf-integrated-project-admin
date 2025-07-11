package com.ruoyi.common.u8c.warpper;

import java.util.HashMap;
import java.util.Map;

/**
 * 财务数据包装
 */
public class FinancialDataWrapper {
    private SubjBalanceDataWrapper subjBalanceDataWrapper;  // 科目余额数据包装
    private AuxBalanceDataWrapper auxBalanceDataWrapper;    // 辅助余额数据包装
    private VoucherDataWrapper voucherDataWrapper;          // 凭证数据包装
    private DetailDataWrapper detailDataWrapper;            // 辅助明细数据包装

    public SubjBalanceDataWrapper getSubjBalanceDataWrapper() {
        return subjBalanceDataWrapper;
    }

    public void setSubjBalanceDataWrapper(SubjBalanceDataWrapper subjBalanceDataWrapper) {
        this.subjBalanceDataWrapper = subjBalanceDataWrapper;
    }

    public AuxBalanceDataWrapper getAuxBalanceDataWrapper() {
        return auxBalanceDataWrapper;
    }

    public void setAuxBalanceDataWrapper(AuxBalanceDataWrapper auxBalanceDataWrapper) {
        this.auxBalanceDataWrapper = auxBalanceDataWrapper;
    }

    public VoucherDataWrapper getVoucherDataWrapper() {
        return voucherDataWrapper;
    }

    public void setVoucherDataWrapper(VoucherDataWrapper voucherDataWrapper) {
        this.voucherDataWrapper = voucherDataWrapper;
    }

    public DetailDataWrapper getDetailDataWrapper() {
        return detailDataWrapper;
    }

    public void setDetailDataWrapper(DetailDataWrapper detailDataWrapper) {
        this.detailDataWrapper = detailDataWrapper;
    }
}
