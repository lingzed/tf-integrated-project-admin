package com.ruoyi.common.u8c.warpper;

import com.ruoyi.common.u8c.SubjBalance;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 科目余额数据包装
 */
public class SubjBalanceDataWrapper implements ByPeriodWrapper<SubjBalance>, ByCorpWrapper<SubjBalance> {
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
    public void setPeriod1(List<SubjBalance> list) {
        subjBalanceListMap.put(ByPeriodWrapper.PERIOD_1, list);
    }

    @Override
    public List<SubjBalance> getPeriod1() {
        return subjBalanceListMap.get(ByPeriodWrapper.PERIOD_1);
    }

    @Override
    public void setPreYearSamePeriod(List<SubjBalance> list) {
        subjBalanceListMap.put(ByPeriodWrapper.PRE_YEAR_SAME_PERIOD, list);
    }

    @Override
    public List<SubjBalance> getPreYearSamePeriod() {
        return subjBalanceListMap.get(ByPeriodWrapper.PRE_YEAR_SAME_PERIOD);
    }

    @Override
    public void setFirstPeriodToCurr(List<SubjBalance> list) {

    }

    @Override
    public List<SubjBalance> getFirstPeriodToCurr() {
        return Collections.emptyList();
    }

    @Override
    public void setPreYearFirstToCurr(List<SubjBalance> list) {

    }

    @Override
    public List<SubjBalance> getPreYearFirstToCurr() {
        return Collections.emptyList();
    }

    @Override
    public List<SubjBalance> getPreYear() {
        return subjBalanceListMap.get(ByPeriodWrapper.PRE_YEAR);
    }

    @Override
    public void setPPreYear(List<SubjBalance> list) {
        subjBalanceListMap.put(ByPeriodWrapper.P_PRE_YEAR, list);
    }

    @Override
    public List<SubjBalance> getPPreYear() {
        return subjBalanceListMap.get(ByPeriodWrapper.P_PRE_YEAR);
    }

    @Override
    public void setPreYear(List<SubjBalance> list) {
        subjBalanceListMap.put(ByPeriodWrapper.PRE_YEAR, list);
    }

    @Override
    public void setByCorpCode(String corpCode, List<SubjBalance> list) {
        subjBalanceListMap.put(corpCode, list);
    }

    @Override
    public List<SubjBalance> getByCorpCode(String corpCode) {
        return subjBalanceListMap.get(corpCode);
    }
}
