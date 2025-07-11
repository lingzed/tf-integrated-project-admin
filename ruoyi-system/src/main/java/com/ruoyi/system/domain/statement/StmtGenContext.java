package com.ruoyi.system.domain.statement;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.ruoyi.common.enums.statement.StatementTpl;
import com.ruoyi.common.utils.PeriodUtil;

import java.time.YearMonth;
import java.util.HashMap;
import java.util.Map;

/**
 * 报表生成相关上下文
 */
public class StmtGenContext {
    private static final String CORP_CODE = "@@#*>CORP_CODE";
    private static final String PERIOD_STR = "@@#*>PERIOD_STR";
    //    private static final String PERIOD = "@@#*>PERIOD";
    private static final String STMT_TPL = "@@#*>STMT_TPL";
    private static final String RC_HEAD_MAP_DATA = "@@#*>RC_HEAD_MAP_DATA";

    private final Map<String, String> innerMap = new HashMap<>();
    private static final ObjectMapper mapper = new ObjectMapper();

    static {
        // 注册java8时间相关类，让jackson能识别这些类
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    public <T> void put(String key, T value) {
        if (key == null || value == null) return;
        try {
            innerMap.put(key, mapper.writeValueAsString(value));
        } catch (Exception e) {
            throw new RuntimeException("设置上下文时发现错误：对象序列化失败 key=" + key, e);
        }
    }

    public <T> T get(String key, Class<T> clazz) {
        String json = innerMap.get(key);
        if (json == null) return null;
        try {
            return mapper.readValue(json, clazz);
        } catch (Exception e) {
            throw new RuntimeException(String.format("获取上下文时发生错误：key=%s 类型转换失败", key), e);
        }
    }

    public <T> T get(String key, TypeReference<T> typeRef) {
        String json = innerMap.get(key);
        if (json == null) return null;
        try {
            return mapper.readValue(json, typeRef);
        } catch (Exception e) {
            throw new RuntimeException("泛型类型转换失败：" + key, e);
        }
    }

    public boolean contains(String key) {
        return innerMap.containsKey(key);
    }

    public Map<String, String> raw() {
        return innerMap;
    }

    /**
     * 设置公司编码
     * @param corpCode
     */
    public void setCorpCode(String corpCode) {
        put(CORP_CODE, corpCode);
    }

    /**
     * 获取公司编码
     * @return
     */
    public String corpCode() {
        return get(CORP_CODE, String.class);
    }

    /**
     * 设置期间
     * @param period
     */
    public void setPeriod(String period) {
        put(PERIOD_STR, period);
    }

    /**
     * 获取期间字符串
     * @return
     */
    public String periodStr() {
        return get(PERIOD_STR, String.class);
    }

    /**
     * 获取期间
     * @return
     */
    public YearMonth period() {
        return get(PERIOD_STR, YearMonth.class);
    }

    /**
     * 设置报表模板文件枚举项
     * @param statementTpl
     */
    public void setStatementTpl(StatementTpl statementTpl) {
        put(STMT_TPL, statementTpl);
    }

    /**
     * 获取报表模板文件枚举项
     * @return
     */
    public StatementTpl statementTpl() {
        return get(STMT_TPL, StatementTpl.class);
    }

    /**
     * 设置行列头映射数据集
     * @param rcHeadMapData
     */
    public void setRcHeadMapData(Map<String, Map<String, String>> rcHeadMapData) {
        put(RC_HEAD_MAP_DATA, rcHeadMapData);
    }

    /**
     * 获取行列头映射数据集
     * @return
     */
    public Map<String, Map<String, String>> rcHeadMapData() {
        return get(RC_HEAD_MAP_DATA, new TypeReference<Map<String, Map<String, String>>>() {
        });
    }

    /**
     * 期间年
     * @return
     */
    public int periodYear() {
        return period().getYear();
    }

    /**
     * 期间月
     * @return
     */
    public int periodMonth() {
        return period().getMonthValue();
    }
}
