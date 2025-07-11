package com.ruoyi.common.u8c.warpper;

import com.ruoyi.common.constant.PeriodConstants;
import com.ruoyi.common.u8c.AuxBalance;
import com.ruoyi.common.u8c.SubjBalance;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 科目余额数据包装
 */
public class AuxBalanceDataWrapper implements ByPeriodWrapper<AuxBalance> {
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
    public void setPreYearSamePeriod(List<AuxBalance> list) {
        auxBalanceListMap.put(ByPeriodWrapper.PRE_YEAR_SAME_PERIOD, list);
    }

    @Override
    public List<AuxBalance> getPreYearSamePeriod() {
        return auxBalanceListMap.get(ByPeriodWrapper.PRE_YEAR_SAME_PERIOD);
    }
}
