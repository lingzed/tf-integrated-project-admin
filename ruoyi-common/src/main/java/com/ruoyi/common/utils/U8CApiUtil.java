package com.ruoyi.common.utils;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.common.constant.MsgConstants;
import com.ruoyi.common.constant.U8CApiConstants;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.u8c.AssVo;
import com.ruoyi.common.u8c.AuxBalance;
import com.ruoyi.common.u8c.Voucher;
import com.ruoyi.common.u8c.query.AuxBalanceQuery;
import com.ruoyi.common.u8c.query.SubjectQuery;
import com.ruoyi.common.u8c.query.VoucherQuery;
import com.ruoyi.common.u8c.response.DataDetailForQuery;
import com.ruoyi.common.u8c.response.U8CResponse;
import com.ruoyi.common.u8c.subj.AccSubjParentVOWrapWrapper;
import com.ruoyi.common.utils.http.OkHttpUtil;
import com.ruoyi.common.utils.json.JsonUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.collections4.SetUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.Subject;
import java.util.*;

/**
 * U8C接口工具类
 */
public class U8CApiUtil {
    private static final Logger log = LoggerFactory.getLogger(U8CApiUtil.class);
    private static final Integer DEFAULT_TIMEOUT = 30;  // 默认超时
    private static final String SCHEME = "0003";    // 主体账簿
    private static final String GROUP = "0001"; // 集团
    private static final Integer BIG_PAGE_NOW = 10000;     // 用于查询总数的页码
    private static final Integer LOW_PAGE_SIZE = 1;        // 用于查询总数的每页条目
    private static final String AUX_BALANCE_QUERY_LABEL = "辅助余额查询";
    private static final String SUBJ_QUERY_LABEL = "科目查询";
    private static final String VOUCHER_QUERY_LABEL = "凭证查询";

    /**
     * u8c基础post请求
     * @param url
     * @param param
     * @param headers
     * @param timeout
     * @return
     */
    private static String U8CBasePost(String url, String param, Map<String, String> headers, Integer timeout) {
        if (MapUtils.isEmpty(headers)) {
            headers = U8CApiConstants.U8C_API_BASE_REQUEST_HEADS;
        } else {
            headers.putAll(U8CApiConstants.U8C_API_BASE_REQUEST_HEADS);
        }
        return OkHttpUtil.postRequest(url, param, headers, timeout);
    }

    /**
     * 从响应的数据中得到data字段
     * @param res
     * @return
     */
    private static String getDataFromResponse(String res) {
        U8CResponse response = JSON.parseObject(res, U8CResponse.class);
        String status = response.getStatus();
        if (status.equals("falied")) {
            throw new ServiceException(String.format(MsgConstants.U8C_API_RESPONSE_ERROR, response.getErrormsg()));
        }
        return response.getData();
    }

    /**
     * 从响应数据中得到datas字段，将其中的列表解析并返回
     * 若响应数据没有datas字段，则返回空列表
     * @param response  接口响应的字符串
     * @param corpCode    公司编码
     * @param queryLabel    查询标识
     * @param tClass    列表中的元素类型
     * @return
     * @param <T>
     */
    private static <T> List<T> getListFromData(String response, String corpCode, String queryLabel, Class<T> tClass) {
        String data = getDataFromResponse(response);
        DataDetailForQuery dataDetailForQuery = JSON.parseObject(data, DataDetailForQuery.class);
        String datas = dataDetailForQuery.getDatas();
        if (StringUtils.isEmpty(datas)) {
            String prefix = StringUtils.isEmpty(corpCode) ? "" : "公司【" + corpCode + "】";
            log.info(prefix + "{}响应的数据无datas字段，返回空列表", queryLabel);
            return Collections.emptyList();
        }
        return JSON.parseArray(datas).toJavaList(tClass);
    }

    /**
     * 从响应数据中得到datas字段，将其中的数据总数解析并返回
     * @param response     接口响应的字符串
     * @return
     */
    private static Long getTotalFormData(String response) {
        String data = getDataFromResponse(response);
        DataDetailForQuery dataDetailForQuery = JSON.parseObject(data, DataDetailForQuery.class);
        String allCount = dataDetailForQuery.getAllcount(); // 总数
        return Long.parseLong(allCount);
    }

    /**
     * 基础查询-辅助余额
     * @param corpCode  公司编码
     * @param headers   请求头
     * @param timeout   超时时间
     * @return
     */
    private static String baseQueryAuxBalance(String corpCode, String startPeriod, String endPeriod, Set<String> subjSet,
                                              List<AssVo> assVoList, Map<String, String> headers, Integer timeout) {
        AuxBalanceQuery auxBalanceQuery = new AuxBalanceQuery();
        auxBalanceQuery.setPk_corp(corpCode);
        auxBalanceQuery.setPk_glorgbook(corpCode + "-0003");
        auxBalanceQuery.setStartPeriod(startPeriod);
        auxBalanceQuery.setEndPeriod(endPeriod);
        auxBalanceQuery.setPk_accsubj(subjSet);
        auxBalanceQuery.setIncludeErrorVoucher("true");
        auxBalanceQuery.setIncludeUntallyVoucher("true");
        auxBalanceQuery.setIncludeInstVoucher("false");
        auxBalanceQuery.setAssvo(assVoList);
        String param = JSON.toJSONString(auxBalanceQuery);
        log.info("公司【{}】{}... 请求参数为: {}", corpCode, AUX_BALANCE_QUERY_LABEL, param);
        timeout = timeout == null || timeout <= 0 ? DEFAULT_TIMEOUT : timeout;
        return U8CBasePost(U8CApiConstants.API_URL_QUERY_AUX_BALANCE, param, headers, timeout);
    }

    /**
     * 辅助余额查询
     * @param corpCode
     * @param startPeriod
     * @param endPeriod
     * @param subjSet
     * @param assVoList
     * @param timeout
     * @return
     */
    public static List<AuxBalance> queryAuxBalance(String corpCode, String startPeriod, String endPeriod, Set<String> subjSet,
                                                   List<AssVo> assVoList, Integer timeout) {
        String res = baseQueryAuxBalance(corpCode, startPeriod, endPeriod, subjSet, assVoList, null, timeout);
        return getListFromData(res, corpCode, AUX_BALANCE_QUERY_LABEL, AuxBalance.class);
    }

    /**
     * 辅助余额查询
     * @param corpCode
     * @param startPeriod
     * @param endPeriod
     * @param subjSet
     * @param assVoList
     * @return
     */
    public static List<AuxBalance> queryAuxBalance(String corpCode, String startPeriod, String endPeriod, Set<String> subjSet,
                                                   List<AssVo> assVoList) {
        return queryAuxBalance(corpCode, startPeriod, endPeriod, subjSet, assVoList, null);
    }

    /**
     * 基础查询-科目
     * @param headers
     * @param timeout
     * @return
     */
    private static String baseQuerySubject(Integer pageNow, Integer pageSize, Map<String, String> headers, Integer timeout) {
        SubjectQuery subjectQuery = new SubjectQuery();
        subjectQuery.setPk_subjscheme(SCHEME);
        subjectQuery.setSubjcode(SetUtils.emptySet());
        subjectQuery.setPage_now(String.valueOf(pageNow));
        subjectQuery.setPage_size(String.valueOf(pageSize));
        subjectQuery.setUnitcode("A");
        String param = JSON.toJSONString(subjectQuery);
        log.info("{}... 请求参数为: {}", SUBJ_QUERY_LABEL, param);
        timeout = timeout == null || timeout <= 0 ? DEFAULT_TIMEOUT : timeout;
        return U8CBasePost(U8CApiConstants.API_URL_QUERY_SUBJECT, param, headers, timeout);
    }

    /**
     * 返回科目总数
     * @return
     */
    public static Long getSubjTotal() {
        return getTotalFormData(baseQuerySubject(BIG_PAGE_NOW, LOW_PAGE_SIZE, null, null));
    }

    /**
     * 返回科目二层包装对象列表
     * @param pageNow   当前页
     * @param pageSize  每页条目
     * @param timeout   超时时间
     * @return
     */
    public static List<AccSubjParentVOWrapWrapper> subjectQuery(Integer pageNow, Integer pageSize, Integer timeout) {
        String response = baseQuerySubject(pageNow, pageSize, null, timeout);
        return getListFromData(response, null, SUBJ_QUERY_LABEL, AccSubjParentVOWrapWrapper.class);
    }

    /**
     * 返回科目二层包装对象列表
     * @param pageNow   当前页
     * @param pageSize  每页条目
     * @return
     */
    public static List<AccSubjParentVOWrapWrapper> subjectQuery(Integer pageNow, Integer pageSize) {
        return subjectQuery(pageNow, pageSize, null);
    }

    /**
     * 基础凭证查询
     * @param corpCode
     * @param subjCode
     * @param startPeriod
     * @param endPeriod
     * @param pageNow
     * @param pageSize
     * @param headers
     * @param timeout
     * @return
     */
    private static String baseQueryVoucher(String corpCode, String subjCode, String startPeriod, String endPeriod,
                                           Integer pageNow, Integer pageSize, Map<String, String> headers, Integer timeout) {
        VoucherQuery voucherQuery = new VoucherQuery();
        voucherQuery.setPk_glorgbook(corpCode + "-0003");
        voucherQuery.setSubjcode(subjCode);
        voucherQuery.setPage_now(pageNow);
        voucherQuery.setPage_size(pageSize);
        voucherQuery.setPrepareddate_from(startPeriod);
        voucherQuery.setPrepareddate_to(endPeriod);
        String params = JSON.toJSONString(voucherQuery);
        log.info("公司【{}】{}... 请求参数为: {}", corpCode, VOUCHER_QUERY_LABEL, params);
        timeout = timeout == null || timeout <= 0 ? DEFAULT_TIMEOUT : timeout;
        return U8CBasePost(U8CApiConstants.API_URL_QUERY_VOUCHER, params, headers, timeout);
    }

    /**
     * 获取凭证总数
     * @param corpCode
     * @param subjCode
     * @param startPeriod
     * @param endPeriod
     * @return
     */
    public static long getVoucherTotal(String corpCode, String subjCode, String startPeriod, String endPeriod) {
        return getTotalFormData(baseQueryVoucher(corpCode, subjCode, startPeriod, endPeriod, BIG_PAGE_NOW, LOW_PAGE_SIZE,
                null, null));
    }

    /**
     * 凭证查询
     * @param corpCode
     * @param subjCode
     * @param startPeriod
     * @param endPeriod
     * @param pageNow
     * @param pageSize
     * @param timeout
     * @return
     */
    public static List<Voucher> queryVoucher(String corpCode, String subjCode, String startPeriod, String endPeriod,
                                             Integer pageNow, Integer pageSize, Integer timeout) {
        String res = baseQueryVoucher(corpCode, subjCode, startPeriod, endPeriod, pageNow, pageSize, null, timeout);
        return getListFromData(res, corpCode, VOUCHER_QUERY_LABEL, Voucher.class);
    }

    /**
     * 凭证查询
     * @param corpCode
     * @param subjCode
     * @param startPeriod
     * @param endPeriod
     * @param pageNow
     * @param pageSize
     * @return
     */
    public static List<Voucher> queryVoucher(String corpCode, String subjCode, String startPeriod, String endPeriod,
                                             Integer pageNow, Integer pageSize) {
        return queryVoucher(corpCode, subjCode, startPeriod, endPeriod, pageNow, pageSize, null);
    }
}
