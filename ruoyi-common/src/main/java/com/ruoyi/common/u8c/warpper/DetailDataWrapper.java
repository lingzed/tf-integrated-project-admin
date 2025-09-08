package com.ruoyi.common.u8c.warpper;

import com.ruoyi.common.u8c.Detail;

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
    public void setPeriod1(List<Detail> list) {
        detailListMap.put(ByPeriodWrapper.PERIOD_1, list);
    }

    @Override
    public List<Detail> getPeriod1() {
        return detailListMap.get(ByPeriodWrapper.PERIOD_1);
    }

    @Override
    public void setPreYearSamePeriod(List<Detail> list) {
        detailListMap.put(ByPeriodWrapper.PRE_YEAR_SAME_PERIOD, list);
    }

    @Override
    public List<Detail> getPreYearSamePeriod() {
        return detailListMap.get(ByPeriodWrapper.PRE_YEAR_SAME_PERIOD);
    }

    @Override
    public void setFirstPeriodToCurr(List<Detail> list) {
        detailListMap.put(ByPeriodWrapper.FIRST_PERIOD_TO_CURR, list);
    }

    @Override
    public List<Detail> getFirstPeriodToCurr() {
        return detailListMap.get(ByPeriodWrapper.FIRST_PERIOD_TO_CURR);
    }

    @Override
    public void setPreYearFirstToCurr(List<Detail> list) {
        detailListMap.put(ByPeriodWrapper.PRE_YEAR_FIRST_TO_CURR, list);
    }

    @Override
    public List<Detail> getPreYearFirstToCurr() {
        return detailListMap.get(ByPeriodWrapper.PRE_YEAR_FIRST_TO_CURR);
    }

    @Override
    public List<Detail> getPreYear() {
        return detailListMap.get(ByPeriodWrapper.PRE_YEAR);
    }

    @Override
    public void setPPreYear(List<Detail> list) {
        detailListMap.put(ByPeriodWrapper.P_PRE_YEAR, list);
    }

    @Override
    public List<Detail> getPPreYear() {
        return detailListMap.get(ByPeriodWrapper.P_PRE_YEAR);
    }

    @Override
    public void setPreYear(List<Detail> list) {
        detailListMap.put(ByPeriodWrapper.PRE_YEAR, list);
    }
}
