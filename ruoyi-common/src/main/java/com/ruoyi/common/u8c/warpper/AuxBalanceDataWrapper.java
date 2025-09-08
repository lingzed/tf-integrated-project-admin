package com.ruoyi.common.u8c.warpper;

import com.ruoyi.common.u8c.AuxBalance;
import com.ruoyi.common.u8c.SubjBalance;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 科目余额数据包装
 */
public class AuxBalanceDataWrapper implements ByPeriodWrapper<AuxBalance>, ByQuery<AuxBalance>, ByCorpWrapper<AuxBalance> {
    private Map<String, List<AuxBalance>> auxBalanceListMap;  // 不同期间维度对应的辅助余额列表映射

    public AuxBalanceDataWrapper() {
        auxBalanceListMap = new HashMap<>();
    }

    @Override
    public void setCurrPeriod(List<AuxBalance> list) {
        auxBalanceListMap.put(ByPeriodWrapper.CURR_PERIOD, list);
    }

    @Override
    public List<AuxBalance> getCurrPeriod() {
        return auxBalanceListMap.get(ByPeriodWrapper.CURR_PERIOD);
    }

    @Override
    public void setPrePeriod(List<AuxBalance> list) {
        auxBalanceListMap.put(ByPeriodWrapper.PRE_PERIOD, list);
    }

    @Override
    public List<AuxBalance> getPrePeriod() {
        return auxBalanceListMap.get(ByPeriodWrapper.PRE_PERIOD);
    }

    @Override
    public void setPeriod12(List<AuxBalance> list) {
        auxBalanceListMap.put(ByPeriodWrapper.PERIOD_12, list);
    }

    @Override
    public List<AuxBalance> getPeriod12() {
        return auxBalanceListMap.get(ByPeriodWrapper.PERIOD_12);
    }

    @Override
    public void setPeriod1(List<AuxBalance> list) {
        auxBalanceListMap.put(ByPeriodWrapper.PERIOD_1, list);
    }

    @Override
    public List<AuxBalance> getPeriod1() {
        return auxBalanceListMap.get(ByPeriodWrapper.PERIOD_1);
    }

    @Override
    public void setPreYearSamePeriod(List<AuxBalance> list) {
        auxBalanceListMap.put(ByPeriodWrapper.PRE_YEAR_SAME_PERIOD, list);
    }

    @Override
    public List<AuxBalance> getPreYearSamePeriod() {
        return auxBalanceListMap.get(ByPeriodWrapper.PRE_YEAR_SAME_PERIOD);
    }

    @Override
    public void setFirstPeriodToCurr(List<AuxBalance> list) {
        auxBalanceListMap.put(ByPeriodWrapper.FIRST_PERIOD_TO_CURR, list);
    }

    @Override
    public List<AuxBalance> getFirstPeriodToCurr() {
        return auxBalanceListMap.get(ByPeriodWrapper.FIRST_PERIOD_TO_CURR);
    }

    @Override
    public void setPreYearFirstToCurr(List<AuxBalance> list) {
        auxBalanceListMap.put(ByPeriodWrapper.PRE_YEAR_FIRST_TO_CURR, list);
    }

    @Override
    public List<AuxBalance> getPreYearFirstToCurr() {
        return auxBalanceListMap.get(ByPeriodWrapper.PRE_YEAR_FIRST_TO_CURR);
    }

    @Override
    public List<AuxBalance> getPreYear() {
        return auxBalanceListMap.get(ByPeriodWrapper.PRE_YEAR);
    }

    @Override
    public void setPPreYear(List<AuxBalance> list) {
        auxBalanceListMap.put(ByPeriodWrapper.P_PRE_YEAR, list);
    }

    @Override
    public List<AuxBalance> getPPreYear() {
        return auxBalanceListMap.get(ByPeriodWrapper.P_PRE_YEAR);
    }

    @Override
    public void setPreYear(List<AuxBalance> list) {
        auxBalanceListMap.put(ByPeriodWrapper.PRE_YEAR, list);
    }

    @Override
    public void setMain(List<AuxBalance> list) {
        auxBalanceListMap.put(ByQuery.MAIN, list);
    }

    @Override
    public List<AuxBalance> getMain() {
        return auxBalanceListMap.get(ByQuery.MAIN);
    }

    @Override
    public void setSupplement(List<AuxBalance> list) {
        auxBalanceListMap.put(ByQuery.SUPPLEMENT, list);
    }

    @Override
    public List<AuxBalance> getSupplement() {
        return auxBalanceListMap.get(ByQuery.SUPPLEMENT);
    }

    @Override
    public void setByCorpCode(String corpCode, List<AuxBalance> list) {
        auxBalanceListMap.put(corpCode, list);
    }

    @Override
    public List<AuxBalance> getByCorpCode(String corpCode) {
        return auxBalanceListMap.get(corpCode);
    }
}
