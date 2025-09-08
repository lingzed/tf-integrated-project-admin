package com.ruoyi.system.service.statement;


import com.ruoyi.common.constant.CacheConstants;
import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.common.enums.u8c.SubjDirection;
import com.ruoyi.common.u8c.Balance;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.domain.statement.CellWriter;
import com.ruoyi.common.u8c.subj.Subject;
import com.ruoyi.system.domain.statement.RowColHeadIndex;
import org.apache.commons.collections4.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;

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

    /**
     * 获取头索引
     * @param rcHeadIndex   行列头索引映射
     * @param key           头键
     * @param isRowIndex    是否行索引，是则从列头中取行索引，否则从行头中取列索引
     * @param warn
     * @return
     */
    private int getHeadIndex(RowColHeadIndex rcHeadIndex, String key, Boolean isRowIndex, String warn) {
        Map<String, Integer> headIndex = isRowIndex ? rcHeadIndex.getColHeadIdx() : rcHeadIndex.getRowHeadIdx();
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

    protected BigDecimal bdAdd(BigDecimal val1, BigDecimal val2) {
        if (val1 == null) return val2;
        if (val2 == null) return val1;
        return val1.add(val2);
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
     * 如果是贷方取贷方期初数，若贷方期初数为空，则取借方期初数的相反数
     * @param balance
     * @param direction
     * @return
     */
    protected BigDecimal initBalance(Balance balance, SubjDirection direction) {
        if (balance == null || direction == null) return null;
        BigDecimal initDebitLocAmount = balance.getInitDebitLocAmount();     // 期初借方
        BigDecimal initCreditLocAmount = balance.getInitCreditLocAmount();   // 期初贷方
        if (initCreditLocAmount == null && initDebitLocAmount == null) return null;
        if (direction == SubjDirection.DEBIT) {
            return initDebitLocAmount == null ? initCreditLocAmount.negate() : initDebitLocAmount;
        } else {
            return initCreditLocAmount == null ? initDebitLocAmount.negate() : initCreditLocAmount;
        }
    }

    /**
     * 期初余额，根据科目方向，借方取借方数，否则取贷方数
     * @param balance
     * @param subjCode
     * @return
     */
    protected BigDecimal initBalance(Balance balance, String subjCode) {
        SubjDirection subjDirection = subjDirection(subjCode);
        if (subjDirection == SubjDirection.DEBIT) {
            return balance.getInitDebitLocAmount();
        } else {
            return balance.getInitCreditLocAmount();
        }
    }

    /**
     * 差值计算期初余额<br>
     * 通过传入的方向-反方向计算
     * @param balance
     * @param subjDirection
     * @return
     */
    protected BigDecimal subtractInitBalance(Balance balance, SubjDirection subjDirection) {
        BigDecimal initJ = balance.getInitDebitLocAmount(); // 期初借
        BigDecimal initD = balance.getInitCreditLocAmount();    // 期初贷
        return subjDirection == SubjDirection.DEBIT ? bdSubtract(initJ, initD) : bdSubtract(initD, initJ);
    }

    /**
     * 差值计算期初余额<br>
     * 通过科目的原始方向-反方向计算
     * @param balance
     * @param subjCode
     * @return
     */
    protected BigDecimal subtractInitBalance(Balance balance, String subjCode) {
        return subtractInitBalance(balance, subjDirection(subjCode));
    }

    /**
     * 期末余额<br>
     * 按科目方向取对应方向的期末余额，若为空，则取反方向的相反数
     * @param balance
     * @param direction
     * @return
     */
    protected BigDecimal endBalance(Balance balance, SubjDirection direction) {
        if (balance == null || direction == null) return null;
        BigDecimal endDebitLocAmount = balance.getEndDebitLocAmount();     // 期初借方
        BigDecimal endCreditLocAmount = balance.getEndCreditLocAmount();   // 期初贷方
        if (endCreditLocAmount == null && endDebitLocAmount == null) return null;
        if (direction == SubjDirection.DEBIT) {
            return endDebitLocAmount == null ? endCreditLocAmount.negate() : endDebitLocAmount;
        } else {
            return endCreditLocAmount == null ? endDebitLocAmount.negate() : endCreditLocAmount;
        }
    }

    /**
     * 差值计算期末余额<br>
     * 用科目原始方向-反方向计算
     * @param balance
     * @param subjCode
     * @return
     */
    protected BigDecimal subtractEndBalance(Balance balance, String subjCode) {
        return subtractEndBalance(balance, subjDirection(subjCode));
    }

    /**
     * 差值计算期末余额<br>
     * 按传入的方向-反方向计算
     * @param balance
     * @param subjDirection
     * @return
     */
    protected BigDecimal subtractEndBalance(Balance balance, SubjDirection subjDirection) {
        BigDecimal endJ = balance.getEndDebitLocAmount();  // 期末借
        BigDecimal endD = balance.getEndCreditLocAmount();    // 期末贷
        return subjDirection == SubjDirection.DEBIT ? bdSubtract(endJ, endD) : bdSubtract(endD, endJ);
    }

    /**
     * 本期数<br>
     * 通过传入的科目方向取数
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

    protected BigDecimal bdAdd(BigDecimal... val) {
        return Arrays.stream(val)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * BigDecimal双层集合自合并<br>
     * 将BigDecimal双层集合中的键值对进行合并<br>
     * value合并为：将内层的BigDecimal集合进行合并操作然后形成新的集合<br>
     * key合并为：外层的key以连接符进行连接形成新的key<br>
     * 将新的key与新的集合形成键值对存入原始的BigDecimal双层集合中
     * @param bdMap         BigDecimal双层集合
     * @param connector     连接符
     * @param delEntry      完成合并后，是否删除用于合并的键值对
     * @param keys          需要合并的key数组
     */
    protected void selfBdMerge(Map<String, Map<String, BigDecimal>> bdMap, String connector, Boolean delEntry, String... keys) {
        selfBdMerge(bdMap, null, connector, delEntry, keys);
    }

    protected void selfBdMerge(Map<String, Map<String, BigDecimal>> bdMap, String aKey, String connector, Boolean delEntry, String... keys) {
        Map<String, BigDecimal> count = selfBdMerge(bdMap, keys);
        bdMap.put(StringUtils.isEmpty(aKey) ? String.join(connector, keys) : aKey, count);
        if (delEntry != null && delEntry) {
            for (String key : keys) {
                bdMap.remove(key);
            }
        }
    }

    /**
     * BigDecimal双层集合自合并<br>
     * 传入的key数组作为排除的key，其剩下的key进行合并<br>
     * 以传入的aKey作为key
     * @param bdMap         BigDecimal双层集合
     * @param aKey          指定的key，若传则以这个值为key，否则以key加连接符连接形成的值作为key
     * @param delEntry      完成合并后，是否删除用于合并的键值对
     * @param excludeKeys   需要排除的的key数组
     */
    protected void selfBdMergeExcludeKey(Map<String, Map<String, BigDecimal>> bdMap, String aKey, Boolean delEntry, String... excludeKeys) {
        Map<String, BigDecimal> count = new HashMap<>();
        Set<String> keySet = new HashSet<>(Arrays.asList(excludeKeys));
        List<String> needDel = new ArrayList<>();
        bdMap.forEach((key, val) -> {
            if (keySet.contains(key)) return;
            if (MapUtils.isEmpty(val)) return;
//            log.info("key:{}, val:{}", key, val);
            needDel.add(key);
            if (count.isEmpty()) {
                count.putAll(val);
            } else {
                val.forEach((k, v) -> mergeBigDecimal(count, k, v));
            }
        });
        bdMap.put(aKey, count);
        if (delEntry != null && delEntry) {
            needDel.forEach(bdMap::remove);
        }
    }

    /**
     * BigDecimal双层集合自合并<br>
     * 通过传入的key进行合并，生成新的内层Map然后返回
     * @param bdMap
     * @param keys
     * @return
     */
    protected Map<String, BigDecimal> selfBdMerge(Map<String, Map<String, BigDecimal>> bdMap, String... keys) {
        Map<String, BigDecimal> count = new HashMap<>();
        for (String key : keys) {
            Map<String, BigDecimal> subMap = bdMap.get(key);
            if (MapUtils.isEmpty(subMap)) continue;
            if (count.isEmpty()) {
                count.putAll(subMap);
            } else {
                subMap.forEach((k, v) -> mergeBigDecimal(count, k, v));
            }
        }
        return count;
    }

    /**
     * subjSet是一组包含了父子编码的集合，最上级编码长度为4，子编码在此基础上每次迭代2位<br>
     * 传入子编码在subjSet中查找，如果子编码在subjSet里面，则直接返回子编码<br>
     * 如果不在里面，则截取出它的父编码递归去查找，找到后返回父编码<br>
     * 如果最后截取到4位还是找不到，则说明该编码不属于subjSet中的编码体系下，返回null
     * @param code
     * @param subjSet
     * @return
     */
    protected String findPCode(String code, Set<String> subjSet) {
        while (code.length() > 4) {
            if (subjSet.contains(code)) return code;
            code = code.substring(0, code.length() - 2);
        }
        return subjSet.contains(code) ? code : null;
    }
}
