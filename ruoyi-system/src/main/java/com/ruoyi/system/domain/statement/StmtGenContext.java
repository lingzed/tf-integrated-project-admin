package com.ruoyi.system.domain.statement;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.ruoyi.common.enums.statement.StatementTpl;
import com.ruoyi.common.u8c.warpper.FinancialDataWrapper;

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
    private static final String P_CORP_CODE = "@@#*>P_CORP_CODE";
    private static final String USER_ID = "@@#*>USER_ID";
    private static final String CUSTOMER_CODE = "@@#*>CUSTOMER_CODE";
    private static final String CUSTOMER_NAME = "@@#*>CUSTOMER_NAME";
    private static final String JOB_PJT_CODE = "@@#*>JOB_PJT_CODE";
    private static final String JOB_PJT_NAME = "@@#*>JOB_PJT_NAME";

    private final Map<String, String> innerMap = new HashMap<>();
    private final Map<String, FinancialDataWrapper> innerFData = new HashMap<>();
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

    public void setPCorpCode(String corpCode) {
        put(P_CORP_CODE, corpCode);
    }

    /**
     * 获取公司编码
     * @return
     */
    public String corpCode() {
        return get(CORP_CODE, String.class);
    }

    public String pCorpCode() {
        return get(P_CORP_CODE, String.class);
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

    /**
     * 当前用户的userId
     * @return
     */
    public long userId() {
        return get(USER_ID, Long.class);
    }

    /**
     * 设置当前用户的userId
     * @param userId
     */
    public void setUserId(long userId) {
        put(USER_ID, userId);
    }

    /**
     * 设置财务数据
     * @param key
     * @param fData
     */
    public void setFData(String key, FinancialDataWrapper fData) {
        innerFData.put(key, fData);
    }

    /**
     * 获取财务数据
     * @param key
     * @return
     */
    public FinancialDataWrapper fData(String key) {
        return innerFData.get(key);
    }

    /**
     * 设置客户编码
     * @param ctrCode
     */
    public void setCustomerCode(String ctrCode) {
        put(CUSTOMER_CODE, ctrCode);
    }

    /**
     * 获取客户编码
     * @return
     */
    public String customerCode() {
        return get(CUSTOMER_CODE, String.class);
    }

    /**
     * 设置客户编码
     * @param ctrName
     */
    public void setCustomerName(String ctrName) {
        put(CUSTOMER_NAME, ctrName);
    }

    /**
     * 获取客户名称
     * @return
     */
    public String customerName() {
        return get(CUSTOMER_NAME, String.class);
    }

    /**
     * 设置项目编码
     * @param jobPjtCode
     */
    public void setJobPjtCode(String jobPjtCode) {
        put(JOB_PJT_CODE, jobPjtCode);
    }

    /**
     * 获取项目编码
     * @return
     */
    public String jobPjtCode() {
        return get(JOB_PJT_CODE, String.class);
    }

    /**
     * 设置项目名称
     * @param jobPjtName
     */
    public void setJobPjtName(String jobPjtName) {
        put(JOB_PJT_NAME, jobPjtName);
    }

    /**
     * 获取项目名称
     * @return
     */
    public String jobPjtName() {
        return get(JOB_PJT_NAME, String.class);
    }
}
