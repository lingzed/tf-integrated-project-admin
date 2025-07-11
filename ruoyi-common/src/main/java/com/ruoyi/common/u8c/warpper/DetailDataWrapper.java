package com.ruoyi.common.u8c.warpper;

import com.ruoyi.common.constant.PeriodConstants;
import com.ruoyi.common.u8c.AuxBalance;
import com.ruoyi.common.u8c.Detail;
import com.ruoyi.common.u8c.Voucher;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DetailDataWrapper implements ByPeriodWrapper<Detail> {
    private Map<String, List<Detail>> detailListMap;

    public DetailDataWrapper() {
        detailListMap = new HashMap<>();
    }

    @Override
    public void setCurrPeriod(List<Detail> list) {
        detailListMap.put(ByPeriodWrapper.CURR_PERIOD, list);
    }

    @Override
    public List<Detail> getCurrPeriod() {
        return detailListMap.get(ByPeriodWrapper.CURR_PERIOD);
    }

    @Override
    public void setPrePeriod(List<Detail> list) {
        detailListMap.put(ByPeriodWrapper.PRE_PERIOD, list);
    }

    @Override
    public List<Detail> getPrePeriod() {
        return detailListMap.get(ByPeriodWrapper.PRE_PERIOD);
    }

    @Override
    public void setPeriod12(List<Detail> list) {
        detailListMap.put(ByPeriodWrapper.PERIOD_12, list);
    }

    @Override
    public List<Detail> getPeriod12() {
        return detailListMap.get(ByPeriodWrapper.PERIOD_12);
    }

    @Override
    public void setPreYearSamePeriod(List<Detail> list) {
        detailListMap.put(ByPeriodWrapper.PRE_YEAR_SAME_PERIOD, list);
    }

    @Override
    public List<Detail> getPreYearSamePeriod() {
        return detailListMap.get(ByPeriodWrapper.PRE_YEAR_SAME_PERIOD);
    }
}
