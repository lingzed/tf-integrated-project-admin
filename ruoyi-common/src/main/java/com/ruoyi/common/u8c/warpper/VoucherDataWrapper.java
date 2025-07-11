package com.ruoyi.common.u8c.warpper;

import com.ruoyi.common.u8c.Voucher;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VoucherDataWrapper implements ByPeriodWrapper<Voucher> {
    private Map<String,List<Voucher>> voucherListMap;
    
    public VoucherDataWrapper() {
        voucherListMap = new HashMap<>();
    }

    @Override
    public void setCurrPeriod(List<Voucher> list) {
        voucherListMap.put(ByPeriodWrapper.CURR_PERIOD, list);
    }

    @Override
    public List<Voucher> getCurrPeriod() {
        return voucherListMap.get(ByPeriodWrapper.CURR_PERIOD);
    }

    @Override
    public void setPrePeriod(List<Voucher> list) {
        voucherListMap.put(ByPeriodWrapper.PRE_PERIOD, list);
    }

    @Override
    public List<Voucher> getPrePeriod() {
        return voucherListMap.get(ByPeriodWrapper.PRE_PERIOD);
    }

    @Override
    public void setPeriod12(List<Voucher> list) {
        voucherListMap.put(ByPeriodWrapper.PERIOD_12, list);
    }

    @Override
    public List<Voucher> getPeriod12() {
        return voucherListMap.get(ByPeriodWrapper.PERIOD_12);
    }

    @Override
    public void setPreYearSamePeriod(List<Voucher> list) {
        voucherListMap.put(ByPeriodWrapper.PRE_YEAR_SAME_PERIOD, list);
    }

    @Override
    public List<Voucher> getPreYearSamePeriod() {
        return voucherListMap.get(ByPeriodWrapper.PRE_YEAR_SAME_PERIOD);
    }
}
