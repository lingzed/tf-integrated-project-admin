package com.ruoyi.common.u8c.warpper;

import com.ruoyi.common.constant.PeriodConstants;
import com.ruoyi.common.u8c.SubjBalance;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 科目余额数据包装
 */
public class SubjBalanceDataWrapper implements ByPeriodWrapper<SubjBalance> {
    private Map<String, List<SubjBalance>> subjBalanceListMap;  // 不同期间维度对应的科目余额列表映射

    public SubjBalanceDataWrapper() {
        subjBalanceListMap = new HashMap<>();
    }

    @Override
    public void setCurrPeriod(List<SubjBalance> list) {
        subjBalanceListMap.put(ByPeriodWrapper.CURR_PERIOD, list);
    }

    @Override
    public List<SubjBalance> getCurrPeriod() {
        return subjBalanceListMap.get(ByPeriodWrapper.CURR_PERIOD);
    }

    @Override
    public void setPrePeriod(List<SubjBalance> list) {
        subjBalanceListMap.put(ByPeriodWrapper.PRE_PERIOD, list);
    }

    @Override
    public List<SubjBalance> getPrePeriod() {
        return subjBalanceListMap.get(ByPeriodWrapper.PRE_PERIOD);
    }

    @Override
    public void setPeriod12(List<SubjBalance> list) {
        subjBalanceListMap.put(ByPeriodWrapper.PERIOD_12, list);
    }

    @Override
    public List<SubjBalance> getPeriod12() {
        return subjBalanceListMap.get(ByPeriodWrapper.PERIOD_12);
    }

    @Override
    public void setPreYearSamePeriod(List<SubjBalance> list) {
        subjBalanceListMap.put(ByPeriodWrapper.PRE_YEAR_SAME_PERIOD, list);
    }

    @Override
    public List<SubjBalance> getPreYearSamePeriod() {
        return subjBalanceListMap.get(ByPeriodWrapper.PRE_YEAR_SAME_PERIOD);
    }
}
