package com.ruoyi.common.utils;

import com.alibaba.fastjson2.JSON;
import com.ruoyi.common.constant.MsgConstants;
import com.ruoyi.common.constant.U8CApiConstants;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.u8c.*;
import com.ruoyi.common.u8c.cust.CustomerWrapper;
import com.ruoyi.common.u8c.query.*;
import com.ruoyi.common.u8c.response.DataDetailForQuery;
import com.ruoyi.common.u8c.response.U8CResponse;
import com.ruoyi.common.u8c.subj.AccSubjParentVOWrapWrapper;
import com.ruoyi.common.utils.http.OkHttpUtil;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.collections4.SetUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.print.DocFlavor;
import java.util.*;

/**
 * U8C接口工具类
 */
public class U8CApiUtil {
    private static final Logger log = LoggerFactory.getLogger(U8CApiUtil.class);
    private static final String SCHEME = "0003";    // 主体账簿
    private static final Integer BIG_PAGE_NOW = 10000;     // 用于查询总数的页码
    private static final Integer LOW_PAGE_SIZE = 1;        // 用于查询总数的每页条目
    private static final String AUX_BALANCE_QUERY_LABEL = "辅助余额查询";
    private static final String SUBJ_BALANCE_QUERY_LABEL = "科目余额查询";
    private static final String SUBJ_QUERY_LABEL = "科目查询";
    private static final String VOUCHER_QUERY_LABEL = "凭证查询";
    private static final String VOUCHER_TOTAL_QUERY_LABEL = "凭证总数查询";
    private static final String CUSTOMER_QUERY_LABEL = "客户查询";
    private static final String JOB_BAS_FIL_QUERY_LABEL = "项目查询";

    /**
     * U8C基础POST请求<br>
     * postman和okhttp请求U8C接口的响应结果有差异，postman返回的财务数据类似是字符串，而okhttp返回的类似是数字<br>
     * 并且postman返回的是全量数据，而okhttp返回的是有效数据，即某条数据如果它的数据字段的值都是0，那么这个条数据不会返回
     *
     * @param url
     * @param param
     * @param headers
     * @param readTimeout
     * @return
     */
    private static String U8CBasePost(String url, String param, Map<String, String> headers, Integer readTimeout) {
        if (MapUtils.isEmpty(headers)) {
            headers = U8CApiConstants.U8C_API_BASE_REQUEST_HEADS;
        } else {
            headers.putAll(U8CApiConstants.U8C_API_BASE_REQUEST_HEADS);
        }
        return OkHttpUtil.postRequest(url, param, headers, null, readTimeout);
    }

    /**
     * 从响应的数据中得到data字段
     *
     * @param res
     * @return
     */
    private static String getDataFromResponse(String res) {
        U8CResponse response = JSON.parseObject(res, U8CResponse.class);
//        log.info("data字段: {}", response.getData());
        String status = response.getStatus();
        if (status.equals("falied")) {
            throw new ServiceException(String.format(MsgConstants.U8C_API_RESPONSE_ERROR, response.getErrormsg()));
        }
        return response.getData();
    }

    /**
     * 从响应数据中得到datas字段，将其中的列表解析并返回<br>
     * 若响应数据没有datas字段，则返回空列表
     *
     * @param response   接口响应的字符串
     * @param corpCode   公司编码
     * @param queryLabel 查询标识
     * @param tClass     列表中的元素类型
     * @param <T>
     * @return
     */
    private static <T> List<T> getListFromData(String response, String corpCode, String queryLabel, Class<T> tClass) {
        String data = getDataFromResponse(response);
        return getListFromDataDetail(data, corpCode, queryLabel, tClass);
    }

    private static <T> List<T> getListFromDataDetail(String dataDetail, String corpCode, String queryLabel, Class<T> tClass) {
        DataDetailForQuery dataDetailForQuery = JSON.parseObject(dataDetail, DataDetailForQuery.class);
        String datas = dataDetailForQuery.getDatas();
        if (StringUtils.isEmpty(datas)) {
            String prefix = StringUtils.isEmpty(corpCode) ? "" : "公司【" + corpCode + "】";
            log.info(prefix + "{}响应的数据无datas字段，返回空列表", queryLabel);
            return new ArrayList<>();
        }
        return JSON.parseArray(datas).toJavaList(tClass);
    }

    /**
     * 从响应数据中得到datas字段，将其中的数据总数解析并返回
     *
     * @param response 接口响应的字符串
     * @return
     */
    private static Long getTotalFormData(String response) {
        String data = getDataFromResponse(response);
        return getCountFormDataDetail(data, true);
    }

    /**
     * 从响应数据中得到datas字段，将其中的返回总数解析并返回
     *
     * @param response
     * @return
     */
    private static Long getRetCountFormData(String response) {
        String data = getDataFromResponse(response);
        return getCountFormDataDetail(data, false);
    }

    private static Long getCountFormDataDetail(String dataDetail, boolean isAll) {
        DataDetailForQuery dataDetailForQuery = JSON.parseObject(dataDetail, DataDetailForQuery.class);
        String allCount = isAll ? dataDetailForQuery.getAllcount() : dataDetailForQuery.getRetcount(); // 总数
        return Long.parseLong(allCount);
    }

    /**
     * 基础查询-辅助余额
     *
     * @param corpCode    公司编码
     * @param headers     请求头
     * @param readTimeout 读取超时，默认30s
     * @return
     */
    private static String baseQueryAuxBalance(String corpCode, String startPeriod, String endPeriod, Set<String> subjSet,
                                              List<AssVo> assVoList, Map<String, String> headers, Integer readTimeout) {
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
        return U8CBasePost(U8CApiConstants.API_URL_QUERY_AUX_BALANCE, param, headers, readTimeout);
    }

    /**
     * 辅助余额查询
     *
     * @param corpCode    公司编码
     * @param startPeriod 开始期间，格式：yyyy-MM
     * @param endPeriod   结束期间，格式：yyyy-MM
     * @param subjSet     科目编码列表
     * @param assVoList   辅助核算列表
     * @param readTimeout 读取超时，默认30s
     * @return
     */
    public static List<AuxBalance> queryAuxBalance(String corpCode, String startPeriod, String endPeriod, Set<String> subjSet,
                                                   List<AssVo> assVoList, Integer readTimeout) {
        String res = baseQueryAuxBalance(corpCode, startPeriod, endPeriod, subjSet, assVoList, null, readTimeout);
        return getListFromData(res, corpCode, AUX_BALANCE_QUERY_LABEL, AuxBalance.class);
    }

    /**
     * 辅助余额查询
     *
     * @param corpCode    公司编码
     * @param startPeriod 开始期间，格式：yyyy-MM
     * @param endPeriod   结束期间，格式：yyyy-MM
     * @param subjSet     科目编码列表
     * @param assVoList   辅助核算列表
     * @return
     */
    public static List<AuxBalance> queryAuxBalance(String corpCode, String startPeriod, String endPeriod, Set<String> subjSet,
                                                   List<AssVo> assVoList) {
        return queryAuxBalance(corpCode, startPeriod, endPeriod, subjSet, assVoList, null);
    }

    /**
     * 基础查询-科目
     *
     * @param headers     请求头
     * @param readTimeout 读取超时，默认30s
     * @return
     */
    private static String baseQuerySubject(Integer pageNow, Integer pageSize, Map<String, String> headers, Integer readTimeout) {
        SubjectQuery subjectQuery = new SubjectQuery();
        subjectQuery.setPk_subjscheme(SCHEME);
        subjectQuery.setSubjcode(SetUtils.emptySet());
        subjectQuery.setPage_now(String.valueOf(pageNow));
        subjectQuery.setPage_size(String.valueOf(pageSize));
        subjectQuery.setUnitcode("A");
        String param = JSON.toJSONString(subjectQuery);
        log.info("{}... 请求参数为: {}", SUBJ_QUERY_LABEL, param);
        return U8CBasePost(U8CApiConstants.API_URL_QUERY_SUBJECT, param, headers, readTimeout);
    }

    /**
     * 返回科目总数
     *
     * @return
     */
    public static Long getSubjTotal() {
        return getTotalFormData(baseQuerySubject(BIG_PAGE_NOW, LOW_PAGE_SIZE, null, null));
    }

    /**
     * 返回科目二层包装对象列表
     *
     * @param pageNow     当前页
     * @param pageSize    每页条目
     * @param readTimeout 读取超时，默认30s
     * @return
     */
    public static List<AccSubjParentVOWrapWrapper> subjectQuery(Integer pageNow, Integer pageSize, Integer readTimeout) {
        String response = baseQuerySubject(pageNow, pageSize, null, readTimeout);
        return getListFromData(response, null, SUBJ_QUERY_LABEL, AccSubjParentVOWrapWrapper.class);
    }

    /**
     * 返回科目二层包装对象列表
     *
     * @param pageNow  当前页
     * @param pageSize 每页条目
     * @return
     */
    public static List<AccSubjParentVOWrapWrapper> subjectQuery(Integer pageNow, Integer pageSize) {
        return subjectQuery(pageNow, pageSize, null);
    }

    /**
     * 基础凭证查询
     *
     * @param corpCode    公司编码
     * @param subjCode    科目编码
     * @param startPeriod 开始期间，格式：yyyy-MM
     * @param endPeriod   结束期间，格式：yyyy-MM
     * @param pageNow     当前页
     * @param pageSize    每页条目
     * @param headers     请求头
     * @param readTimeout 读取超时，默认30s
     * @return
     */
    private static String baseQueryVoucher(String corpCode, Set<String> subjCode, String startPeriod, String endPeriod,
                                           Integer pageNow, Integer pageSize, Map<String, String> headers, Integer readTimeout) {
        VoucherQuery voucherQuery = new VoucherQuery();
        voucherQuery.setPk_glorgbook(corpCode + "-0003");
        voucherQuery.setSubjcode(subjCode);
        voucherQuery.setPage_now(pageNow);
        voucherQuery.setPage_size(pageSize);
        voucherQuery.setPrepareddate_from(startPeriod);
        voucherQuery.setPrepareddate_to(endPeriod);
        String params = JSON.toJSONString(voucherQuery);
        if (Objects.equals(pageNow, BIG_PAGE_NOW) && Objects.equals(pageSize, LOW_PAGE_SIZE)) {
            log.info("公司【{}】{}... 请求参数为: {}", corpCode, VOUCHER_TOTAL_QUERY_LABEL, params);
        }else{
            log.info("公司【{}】{}... 请求参数为: {}", corpCode, VOUCHER_QUERY_LABEL, params);
        }
        return U8CBasePost(U8CApiConstants.API_URL_QUERY_VOUCHER, params, headers, readTimeout);
    }

    /**
     * 获取凭证总数
     *
     * @param corpCode    公司编码
     * @param subjCode    科目编码
     * @param startPeriod 开始期间，格式：yyyy-MM
     * @param endPeriod   结束期间，格式：yyyy-MM
     * @return
     */
    public static long getVoucherTotal(String corpCode, String subjCode, String startPeriod, String endPeriod) {
        return getVoucherTotal(corpCode, subjCode, startPeriod, endPeriod);
    }

    public static long getVoucherTotal(String corpCode, Set<String> subjCode, String startPeriod, String endPeriod) {
        return getTotalFormData(baseQueryVoucher(corpCode, subjCode, startPeriod, endPeriod, BIG_PAGE_NOW, LOW_PAGE_SIZE,
                null, null));
    }

    /**
     * 凭证查询
     *
     * @param corpCode    公司编码
     * @param subjCode    科目编码
     * @param startPeriod 开始期间，格式：yyyy-MM
     * @param endPeriod   结束期间，格式：yyyy-MM
     * @param pageNow     当前页
     * @param pageSize    每页条目
     * @param readTimeout 读取超时，默认30s
     * @return
     */
    public static List<Voucher> queryVoucher(String corpCode, String subjCode, String startPeriod, String endPeriod,
                                             Integer pageNow, Integer pageSize, Integer readTimeout) {
        return queryVoucher(corpCode, Collections.singleton(subjCode), startPeriod, endPeriod, pageNow, pageSize, readTimeout);
    }

    /**
     * 凭证查询
     *
     * @param corpCode
     * @param subjCode
     * @param startPeriod
     * @param endPeriod
     * @param pageNow
     * @param pageSize
     * @param readTimeout
     * @return
     */
    public static List<Voucher> queryVoucher(String corpCode, Set<String> subjCode, String startPeriod, String endPeriod,
                                             Integer pageNow, Integer pageSize, Integer readTimeout) {
        String res = baseQueryVoucher(corpCode, subjCode, startPeriod, endPeriod, pageNow, pageSize, null, readTimeout);
        return getListFromData(res, corpCode, VOUCHER_QUERY_LABEL, Voucher.class);
    }

    /**
     * 凭证查询
     *
     * @param corpCode    公司编码
     * @param subjCode    科目编码
     * @param startPeriod 开始期间，格式：yyyy-MM
     * @param endPeriod   结束期间，格式：yyyy-MM
     * @param pageNow     当前页
     * @param pageSize    每页条目
     * @return
     */
    public static List<Voucher> queryVoucher(String corpCode, String subjCode, String startPeriod, String endPeriod,
                                             Integer pageNow, Integer pageSize) {
        return queryVoucher(corpCode, subjCode, startPeriod, endPeriod, pageNow, pageSize, null);
    }

    public static List<Voucher> queryVoucher(String corpCode, Set<String> subjCode, String startPeriod, String endPeriod,
                                             Integer pageNow, Integer pageSize) {
        return queryVoucher(corpCode, subjCode, startPeriod, endPeriod, pageNow, pageSize, null);
    }

    public static List<Voucher> queryVoucherWithPage(String corpCode, Set<String> subjCode, String startPeriod, String endPeriod, Integer pageSize, Integer readTimout) {
        int page = 1;
        List<Voucher> result = new ArrayList<>();
        while (true) {
            String res = baseQueryVoucher(corpCode, subjCode, startPeriod, endPeriod, page++, pageSize, null, readTimout);
            String dataFromResponse = getDataFromResponse(res);
            long retCount = getCountFormDataDetail(dataFromResponse, false);
            if (retCount == 0) break;
            result.addAll(getListFromDataDetail(dataFromResponse, corpCode, VOUCHER_QUERY_LABEL, Voucher.class));
        }
        return result;
    }

    public static List<Voucher> queryVoucherWithPage(String corpCode, Set<String> subjCode, String startPeriod, String endPeriod, Integer pageSize) {
        return queryVoucherWithPage(corpCode, subjCode, startPeriod, endPeriod, pageSize, null);
    }


    /**
     * 基础科目余额查询
     *
     * @param corpCode    公司编码
     * @param startPeriod 开始期间，格式：yyyy-MM
     * @param endPeriod   结束期间，格式：yyyy-MM
     * @param from        科目编码从
     * @param to          科目编码至
     * @param headers     请求头
     * @param readTimeout 读取超时，默认30s
     * @return
     */
    private static String baseQuerySubjBalance(String corpCode, String startPeriod, String endPeriod, String from, String to,
                                               Map<String, String> headers, Integer readTimeout) {
        SubjBalanceQuery subjBalanceQuery = new SubjBalanceQuery();
        subjBalanceQuery.setPk_corp(corpCode);
        subjBalanceQuery.setPk_glorgbook(corpCode + "-0003");
        subjBalanceQuery.setStartPeriod(startPeriod);
        subjBalanceQuery.setEndPeriod(endPeriod);
        subjBalanceQuery.setAccsubjcodeFrom(from);
        subjBalanceQuery.setAccsubjcodeTo(to);
        String params = JSON.toJSONString(subjBalanceQuery);
        log.info("公司【{}】{}... 请求参数为: {}", corpCode, SUBJ_BALANCE_QUERY_LABEL, params);
        return U8CBasePost(U8CApiConstants.API_URL_QUERY_AUX_BALANCE, params, headers, readTimeout);
    }

    /**
     * 科目余额查询
     *
     * @param corpCode    公司编码
     * @param startPeriod 开始期间，格式：yyyy-MM
     * @param endPeriod   结束期间，格式：yyyy-MM
     * @param from        科目编码从
     * @param to          科目编码至
     * @param readTimeout 读取超时，默认30s
     * @return
     */
    public static List<SubjBalance> subjBalanceQuery(String corpCode, String startPeriod, String endPeriod, String from, String to,
                                                     Integer readTimeout) {
        String res = baseQuerySubjBalance(corpCode, startPeriod, endPeriod, from, to, null, readTimeout);
        return getListFromData(res, corpCode, SUBJ_BALANCE_QUERY_LABEL, SubjBalance.class);
    }

    /**
     * 科目余额查询
     *
     * @param corpCode
     * @param startPeriod
     * @param endPeriod
     * @param from
     * @param to
     * @return
     */
    public static List<SubjBalance> subjBalanceQuery(String corpCode, String startPeriod, String endPeriod, String from, String to) {
        return subjBalanceQuery(corpCode, startPeriod, endPeriod, from, to, null);
    }

    /**
     * 基础客户查询
     *
     * @param cName
     * @param isFuzzy
     * @param headers
     * @param readTimeout
     * @return
     */
    private static String baseQueryCustomer(String cName, boolean isFuzzy, Map<String, String> headers, Integer readTimeout) {
        CustomerQuery query = new CustomerQuery();
        if (isFuzzy) {
            query.setCustname(cName);   // 模糊查询
        } else {
            query.setCustnameis(cName); // 精确查询
        }
        String params = JSON.toJSONString(query);
        log.info("{}... 请求参数为: {}", CUSTOMER_QUERY_LABEL, params);
        return U8CBasePost(U8CApiConstants.API_URL_QUERY_CUSTOMER, params, headers, readTimeout);
    }

    /**
     * 客户查询-精确查询
     *
     * @param cName
     * @param headers
     * @param readTimeout
     * @return
     */
    public static List<CustomerWrapper> customerQuery(String cName, Map<String, String> headers, Integer readTimeout) {
        String res = baseQueryCustomer(cName, false, headers, readTimeout);
        return getListFromData(res, null, CUSTOMER_QUERY_LABEL, CustomerWrapper.class);
    }

    /**
     * 客户查询-精确查询
     *
     * @param cName
     * @return
     */
    public static List<CustomerWrapper> customerQuery(String cName) {
        return customerQuery(cName, null, null);
    }

    /**
     * 基础项目查询
     *
     * @param jobName
     * @param jobType
     * @param isFuzzy
     * @param headers
     * @param readTimeout
     * @return
     */
    private static String baseQueryJobBasFil(String jobName, String jobType, boolean isFuzzy, Map<String, String> headers, Integer readTimeout) {
        JobQuery query = new JobQuery();
        // 是否模糊匹配
        if (isFuzzy) {
            query.setName(jobName);
        } else {
            query.setJobname(jobName);
        }
        query.setJobtypecode(jobType);
        String params = JSON.toJSONString(query);
        log.info("{}... 请求参数为: {}", JOB_BAS_FIL_QUERY_LABEL, params);
        return U8CBasePost(U8CApiConstants.API_URL_QUERY_JOB_BAS_FIL, params, headers, readTimeout);
    }

    /**
     * 项目查询-精确查询
     *
     * @param jobName
     * @param jobType
     * @param headers
     * @param readTimeout
     * @return
     */
    public static List<JobBasFil> jobBasFilQuery(String jobName, String jobType, Map<String, String> headers, Integer readTimeout) {
        String res = baseQueryJobBasFil(jobName, jobType, false, headers, readTimeout);
        return getListFromData(res, null, JOB_BAS_FIL_QUERY_LABEL, JobBasFil.class);
    }

    /**
     * 项目查询，类型为项目-精确查询
     *
     * @param jobName
     * @param readTimeout
     * @return
     */
    public static List<JobBasFil> jobBasFilQuery4Pjt(String jobName, Integer readTimeout) {
        return jobBasFilQuery(jobName, "06", null, readTimeout);
    }

    /**
     * 项目查询，类型为项目-精确查询
     *
     * @param jobName
     * @return
     */
    public static List<JobBasFil> jobBasFilQuery4Pjt(String jobName) {
        return jobBasFilQuery4Pjt(jobName, null);
    }

    /**
     * 项目查询-模糊查询
     *
     * @param jobName
     * @param jobType
     * @param headers
     * @param readTimeout
     * @return
     */
    public static List<JobBasFil> jobBasFilQueryFuzzy(String jobName, String jobType, Map<String, String> headers, Integer readTimeout) {
        String res = baseQueryJobBasFil(jobName, jobType, true, headers, readTimeout);
        return getListFromData(res, null, JOB_BAS_FIL_QUERY_LABEL, JobBasFil.class);
    }
}
