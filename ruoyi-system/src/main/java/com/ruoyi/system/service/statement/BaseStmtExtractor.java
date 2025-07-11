package com.ruoyi.system.service.statement;


import com.ruoyi.common.constant.CacheConstants;
import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.common.enums.u8c.SubjDirection;
import com.ruoyi.common.u8c.AuxBalance;
import com.ruoyi.common.u8c.Balance;
import com.ruoyi.common.u8c.warpper.FinancialDataWrapper;
import com.ruoyi.system.domain.statement.CellWriter;
import com.ruoyi.common.u8c.subj.Subject;
import com.ruoyi.system.domain.statement.RowColHeadIndex;
import org.apache.commons.collections4.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 基础报表提取器
 */
@Resource
public class BaseStmtExtractor {
    private static final Logger log = LoggerFactory.getLogger(BaseStmtExtractor.class);
    @Resource
    private RedisCache redisCache;

    /**
     * 获取列头中键对应的行索引，没有返回-1
     * @param rcHeadIndex   行列头索引映射
     * @param colHeadKey           列头键
     * @return
     */
    protected int rowIndex(RowColHeadIndex rcHeadIndex, String colHeadKey) {
        return getHeadIndex(rcHeadIndex, colHeadKey, true, "列头键【{}】在列头映射中没有对应行索引");
    }

    /**
     * 获取行头中键对应的列索引，没有返回-1
     * @param rcHeadIndex   行列头索引映射
     * @param rowHeadKey           行头键键
     * @return
     */
    protected int colIndex(RowColHeadIndex rcHeadIndex, String rowHeadKey) {
        return getHeadIndex(rcHeadIndex, rowHeadKey, false, "行头键【{}】在行头映射中没有对应列索引");
    }

    // 获取头索引
    private int getHeadIndex(RowColHeadIndex rcHeadIndex, String key, Boolean isRow, String warn) {
        Map<String, Integer> headIndex = isRow ? rcHeadIndex.getColHeadIdx() : rcHeadIndex.getRowHeadIdx();
        if (headIndex == null) return -1;
        Integer i = headIndex.get(key);
        if (i == null) {
            log.warn(warn, key);
            return -1;
        }
        return i;
    }

    /**
     * 从科目缓存中获取对应的科目信息
     * @param subjCode  科目编码
     * @return
     */
    protected Subject getCacheSubject(String subjCode) {
        Map<String, Subject> subjCache = redisCache.getCacheMap(CacheConstants.ALL_SUBJ_CACHE_KEY);
        if (MapUtils.isEmpty(subjCache)) {
            log.warn("科目缓存为空");
            return null;
        }
        return subjCache.get(subjCode);
    }

    /**
     * 获取科目方向
     * @param subjCode  科目编码
     * @return
     */
    protected SubjDirection subjDirection(String subjCode) {
        Subject subject = getCacheSubject(subjCode);
        if (subject == null) {
            log.warn("科目【{}】在缓存中不存在", subjCode);
            return null;
        }
        return subject.getSubjDirection();
    }

    /**
     * bigDecimal减法，避免空指针
     * @param val1
     * @param val2
     * @return
     */
    protected BigDecimal bdSubtract(BigDecimal val1, BigDecimal val2) {
        if (val1 == null) return val2;
        if (val2 == null) return val1;
        return val1.subtract(val2);
    }

    /**
     * 合并bigDecimal
     * @param map
     * @param k
     * @param v
     * @param <T>
     */
    protected <T> void mergeBigDecimal(Map<T, BigDecimal> map, T k, BigDecimal v) {
        if (v == null) return;
        map.merge(k, v, BigDecimal::add);
    }

    /**
     * cellWriter列表中添加
     * @param list  cellWriter列表
     * @param ri    行索引
     * @param ci    列索引
     * @param value 值
     * @param <V>
     */
    protected <V> void addCellWriter(List<CellWriter<V>> list, Integer ri, Integer ci, V value) {
        addCellWriter(list, ri, ci, value, false);
    }

    /**
     * cellWriter列表中添加
     * @param list  cellWriter列表
     * @param ri    行索引
     * @param ci    列索引
     * @param value 值
     * @param isFormula 是否公式
     * @param <V>
     */
    protected <V> void addCellWriter(List<CellWriter<V>> list, Integer ri, Integer ci, V value, Boolean isFormula) {
        if (list == null) return;
        if (ri < 0 || ci < 0) {
            log.warn("行索引或列索引为负数，加入CellWriter列表失败");
            return;
        }
        list.add(CellWriter.of(ri, ci, value, isFormula));
    }

    /**
     * 期初余额
     * 如果是借方取借方期初数，若借方期初数为空，则取贷方期初数的相反数
     * 如果是贷方取贷方期初数，若贷方期初数为空，则取借方相反数
     * @param balance
     * @param direction
     * @return
     */
    protected BigDecimal initBalance(Balance balance, Integer direction) {
        if (balance == null || direction < 0) return null;
        BigDecimal initDebitLocAmount = balance.getInitDebitLocAmount();     // 期初借方
        BigDecimal initCreditLocAmount = balance.getInitCreditLocAmount();   // 期初贷方
        if (initCreditLocAmount == null && initDebitLocAmount == null) return null;
        if (direction == 0) {
            return initDebitLocAmount == null ? initCreditLocAmount.negate() : initDebitLocAmount;
        } else {
            return initCreditLocAmount == null ? initDebitLocAmount.negate() : initCreditLocAmount;
        }
    }

    /**
     * 本期数
     * @param balance       余额
     * @param direction     方向
     * @return
     */
    protected BigDecimal currBalance(Balance balance, SubjDirection direction) {
        return getAmountByAccum(balance, direction, false);
    }

    /**
     * 累计数
     * @param balance       余额
     * @param direction     方向
     * @return
     */
    protected BigDecimal accumBalance(Balance balance, SubjDirection direction) {
        return getAmountByAccum(balance, direction, true);
    }

    /**
     * 获取本期数或累计数
     * @param balance       余额
     * @param direction     方向
     * @param isAccum       是否累计
     * @return
     */
    private BigDecimal getAmountByAccum(Balance balance, SubjDirection direction, Boolean isAccum) {
        isAccum = isAccum != null && isAccum;
        if (direction == SubjDirection.DEBIT) {
            return isAccum ? balance.getDebitAccumLocAmount() : balance.getDebitLocAmount();
        } else {
            return isAccum ? balance.getCreditAccumLocAmount() : balance.getCreditLocAmount();
        }
    }
}
