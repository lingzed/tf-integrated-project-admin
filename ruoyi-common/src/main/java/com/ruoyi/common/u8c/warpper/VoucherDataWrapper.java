package com.ruoyi.common.u8c.warpper;

import com.ruoyi.common.u8c.Voucher;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VoucherDataWrapper implements ByPeriodWrapper<Voucher> {
    private Map<String, List<Voucher>> voucherListMap;

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
    public void setPeriod1(List<Voucher> list) {
        voucherListMap.put(ByPeriodWrapper.PERIOD_1, list);
    }

    @Override
    public List<Voucher> getPeriod1() {
        return voucherListMap.get(ByPeriodWrapper.PERIOD_1);
    }

    @Override
    public void setPreYearSamePeriod(List<Voucher> list) {
        voucherListMap.put(ByPeriodWrapper.PRE_YEAR_SAME_PERIOD, list);
    }

    @Override
    public List<Voucher> getPreYearSamePeriod() {
        return voucherListMap.get(ByPeriodWrapper.PRE_YEAR_SAME_PERIOD);
    }

    @Override
    public void setFirstPeriodToCurr(List<Voucher> list) {
        voucherListMap.put(ByPeriodWrapper.FIRST_PERIOD_TO_CURR, list);
    }

    @Override
    public List<Voucher> getFirstPeriodToCurr() {
        return voucherListMap.get(ByPeriodWrapper.FIRST_PERIOD_TO_CURR);
    }

    @Override
    public void setPreYearFirstToCurr(List<Voucher> list) {
        voucherListMap.put(ByPeriodWrapper.PRE_YEAR_FIRST_TO_CURR, list);
    }

    @Override
    public List<Voucher> getPreYearFirstToCurr() {
        return voucherListMap.get(ByPeriodWrapper.PRE_YEAR_FIRST_TO_CURR);
    }

    @Override
    public List<Voucher> getPreYear() {
        return voucherListMap.get(ByPeriodWrapper.PRE_YEAR);
    }

    @Override
    public void setPPreYear(List<Voucher> list) {
        voucherListMap.put(ByPeriodWrapper.P_PRE_YEAR, list);
    }

    @Override
    public List<Voucher> getPPreYear() {
        return voucherListMap.get(ByPeriodWrapper.P_PRE_YEAR);
    }

    @Override
    public void setPreYear(List<Voucher> list) {
        voucherListMap.put(ByPeriodWrapper.PRE_YEAR, list);
    }
}
