package com.ruoyi.system.service.statement;

import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.u8c.*;
import com.ruoyi.common.u8c.cust.Customer;
import com.ruoyi.common.u8c.cust.CustomerWrapper;
import com.ruoyi.common.u8c.query.DetailWrapperQuery;
import com.ruoyi.common.u8c.warpper.DetailWrapper;
import com.ruoyi.common.utils.PeriodUtil;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.U8CApiUtil;
import com.ruoyi.system.service.DetailWrapperService;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Semaphore;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class KhDzQueryService {
    private static final long EXPIRE_TIME = 3600 * 24 * 3;  // 3天
    public static final String AB_INIT_RESULT = "AB_INIT_RESULT";
    public static final String AB_RESULT = "AB_RESULT";
    public static final String DW_RESULT = "DW_RESULT";
    @Resource
    private RedisCache redisCache;
    @Resource(name = "requestThreadPoolExecutor")
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;
    @Resource
    private DetailWrapperService detailWrapperService;
    @Resource
    private KhDzWriteService khDzWriteService;

    /**
     * 获取客户名称对应的客户编码
     *
     * @param ctrName
     * @return
     */
    public String getCustomerCode(String ctrName) {
        String key = String.format("AuxItem:customer:code:%d", Math.abs(ctrName.hashCode()));
        String res = redisCache.getCacheStr(key);
        if (StringUtils.isEmpty(res)) {
            List<CustomerWrapper> customerWrappers = U8CApiUtil.customerQuery(ctrName);
            res = Optional.ofNullable(customerWrappers)
                    .map(cwList -> cwList.get(0))
                    .map(CustomerWrapper::getParentvo)
                    .map(Customer::getCustcode).orElse(null);
            if (res != null) {
                redisCache.setCacheStr(key, res);
                redisCache.expire(key, EXPIRE_TIME);   // 有效期3天
            }
        }
        return res;
    }

    /**
     * 获取项目名称对应的项目编码
     *
     * @param jbfName
     * @return
     */
    public String getJobPjtCode(String jbfName) {
        String key = String.format("AuxItem:jobBasFil:code:%d", Math.abs(jbfName.hashCode()));
        String res = redisCache.getCacheStr(key);
        if (StringUtils.isEmpty(res)) {
            List<JobBasFil> jbfList = U8CApiUtil.jobBasFilQuery4Pjt(jbfName);
            res = Optional.ofNullable(jbfList)
                    .map(list -> list.get(0))
                    .map(JobBasFil::getJobcode)
                    .orElse(null);
            if (res != null) {
                redisCache.setCacheStr(key, res);
                redisCache.expire(key, EXPIRE_TIME);   // 有效期3天
            }
        }
        return res;
    }

    private List<AssVo> assVoList(String[]... items) {
        return Stream.of(items).map(item -> {
            AssVo assVo = new AssVo();
            assVo.setChecktypecode(item[0]);
            assVo.setCheckvaluecode(item[1]);
            return assVo;
        }).collect(Collectors.toList());
    }

    public Map<String, Object> fetchData(List<String> corpCodes, String ctrCode, String ctrName, String jobPjtCode, String jobPjtName, List<String> periodList) throws ExecutionException, InterruptedException {
        Set<String> subjSet = khDzWriteService.getSubjSet();
        String startDate = periodList.get(0);
        String endDate = periodList.get(periodList.size() - 1);
        Semaphore semaphore = new Semaphore(10);

        // 辅助余额查询-期初
//        CompletableFuture<List<AuxBalance>> initFuture = fetchInitAuxBalance(corpCode, startDate, subjSet, ctrCode, semaphore);
        // 辅助余额查询-按期间范围
        CompletableFuture<Map<String, List<AuxBalance>>> abFuture = fetchAbByPeriodRange(corpCodes, subjSet, ctrCode, jobPjtCode, periodList, semaphore);
        // 凭证分录明显查询
        CompletableFuture<List<DetailWrapper>> dwFuture = findDetailWrapper(corpCodes, startDate, endDate, subjSet, ctrName, jobPjtName, semaphore);
//        CompletableFuture.allOf(initFuture, abFuture, dwFuture).join();
        CompletableFuture.allOf(abFuture, dwFuture).join();

        Map<String, Object> result = new HashMap<>();
//        result.put(AB_INIT_RESULT, initFuture.join());
        result.put(AB_RESULT, abFuture.join());
        result.put(DW_RESULT, dwFuture.join());
        return result;
    }

    /**
     * 辅助余额查询-期初
     *
     * @param corpCode
     * @param initPeriod
     * @param subjSet
     * @param ctrCode
     * @param semaphore
     * @return
     */
    private CompletableFuture<List<AuxBalance>> fetchInitAuxBalance(String corpCode, String initPeriod, Set<String> subjSet,
                                                                    String ctrCode, Semaphore semaphore) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                semaphore.acquire();
                return U8CApiUtil.queryAuxBalance(corpCode, initPeriod, initPeriod, subjSet, assVoList(new String[]{"73", ctrCode}));
            } catch (InterruptedException e) {
                throw new ServiceException(e.getMessage());
            } finally {
                semaphore.release();
            }
        }, threadPoolTaskExecutor);
    }

    /**
     * 辅助余额查询-按期间范围
     *
     * @param corpCodes
     * @param subjSet
     * @param ctrCode
     * @param jobPjtCode
     * @param periodList
     * @param semaphore
     * @return
     */
    private CompletableFuture<Map<String, List<AuxBalance>>> fetchAbByPeriodRange(List<String> corpCodes, Set<String> subjSet, String ctrCode,
                                                                                  String jobPjtCode, List<String> periodList, Semaphore semaphore) {
        List<AssVo> assVos = assVoList(new String[]{"73", ctrCode}, new String[]{"J06Ass", jobPjtCode});
        List<CompletableFuture<Map.Entry<String, List<AuxBalance>>>> futures = new ArrayList<>();
        periodList.forEach(p -> corpCodes.forEach(corpCode -> {
            CompletableFuture<Map.Entry<String, List<AuxBalance>>> future = CompletableFuture.supplyAsync(() -> {
                try {
                    semaphore.acquire();
                    List<AuxBalance> abList = U8CApiUtil.queryAuxBalance(corpCode, p, p, subjSet, assVos);
                    return CollectionUtils.isEmpty(abList) ? null : new AbstractMap.SimpleEntry<>(p, abList);
                } catch (InterruptedException e) {
                    throw new ServiceException(e.getMessage());
                } finally {
                    semaphore.release();
                }
            }, threadPoolTaskExecutor);
            futures.add(future);
        }));

        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[]{}))
                .thenApply(v -> {
                    Map<String, List<AuxBalance>> abResult = new HashMap<>();
                    futures.forEach(f -> {
                        Map.Entry<String, List<AuxBalance>> entry = f.join();
                        if (entry != null) {
                            abResult.merge(entry.getKey(), entry.getValue(), (o, n) -> {
                                o.addAll(n);
                                return o;
                            });
                        }
                    });
                    return abResult;
                });
    }

    /**
     * 凭证分录明显查询
     *
     * @param corpCodes
     * @param startDate
     * @param endDate
     * @param subjSet
     * @param ctrName
     * @param jobPjtName
     * @param semaphore
     * @return
     */
    private CompletableFuture<List<DetailWrapper>> findDetailWrapper(List<String> corpCodes, String startDate, String endDate, Set<String> subjSet, String ctrName,
                                                                     String jobPjtName, Semaphore semaphore) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                semaphore.acquire();
                DetailWrapperQuery query = new DetailWrapperQuery();
                query.setGlBookCodes(corpCodes);
                query.setStartDate(startDate + "-01");
                query.setEndDate(PeriodUtil.lastDayOfPeriod(endDate));
                query.setSubjCodes(subjSet);
                query.setCtrName(ctrName);
                query.setJobPjtName(jobPjtName);
                return detailWrapperService.find(query);
            } catch (InterruptedException e) {
                throw new ServiceException(e.getMessage());
            } finally {
                semaphore.release();
            }
        }, threadPoolTaskExecutor);
    }
}
