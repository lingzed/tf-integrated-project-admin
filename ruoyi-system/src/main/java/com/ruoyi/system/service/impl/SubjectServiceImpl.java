package com.ruoyi.system.service.impl;

import com.ruoyi.common.constant.CacheConstants;
import com.ruoyi.common.constant.MsgConstants;
import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.common.enums.u8c.SubjDirection;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.u8c.subj.AccSubjParentVO;
import com.ruoyi.common.u8c.subj.Subject;
import com.ruoyi.common.utils.U8CApiUtil;
import com.ruoyi.system.service.SubjectService;
import io.netty.util.concurrent.ThreadPerTaskExecutor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

@Service
public class SubjectServiceImpl implements SubjectService {
    private static final Logger log = LoggerFactory.getLogger(SubjectServiceImpl.class);
    private static final ReentrantLock lock = new ReentrantLock();
    @Resource(name = "requestThreadPoolExecutor")
    private ThreadPoolTaskExecutor executor;
    @Resource
    private RedisCache redisCache;

    /**
     * 远程拉取所有科目
     * @return
     */
    private Set<Subject> getAllSubject() {
        Set<Subject> result = Collections.synchronizedSet(new HashSet<>());

        // 预请求获取科目总数
        log.info("预请求拉取科目总数...");
        int total = Math.toIntExact(U8CApiUtil.getSubjTotal());
        int pageSize = 500;
        int pageTotal = (total + pageSize - 1) / pageSize;

        List<CompletableFuture<Void>> futures = new ArrayList<>();
        log.info("开始远程拉取科目...");
        for (int i = 1; i <= pageTotal; i++) {
            int page = i;
            CompletableFuture<Void> future = CompletableFuture
                    .supplyAsync(() -> U8CApiUtil.subjectQuery(page, pageSize), executor)
                    .thenAccept(data -> {
                        if (CollectionUtils.isEmpty(data)) return;
                        List<Subject> list = data.stream()
                                .map(wrapSubj -> {
                                    AccSubjParentVO accsubjParentVO = wrapSubj.getAccsubjParentVO().getAccsubjParentVO();
                                    Subject subject = new Subject();
                                    subject.setSubjCode(accsubjParentVO.getSubjcode());
                                    subject.setSubjName(accsubjParentVO.getSubjname());
                                    SubjDirection byCode = SubjDirection.getByCode(accsubjParentVO.getBalanorient());
                                    subject.setSubjDirection(byCode);
                                    return subject;
                                }).collect(Collectors.toList());
                        result.addAll(list);
                    });
            futures.add(future);
        }
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[]{})).join();
        log.info("拉取完成");
        return result;
    }


    @Override
    public void refreshAllSubjCache() {
        Map<String, Subject> subjCache = new HashMap<>();

        Set<Subject> allSubject = getAllSubject();  // 拉取最新的科目列表
        allSubject.forEach(subj -> {
            String subjCode = subj.getSubjCode();
            subjCache.put(subjCode, subj);
        });
        lock.lock();
        try {
            log.info("开始更新所有科目的缓存...");
            redisCache.setCacheMap(CacheConstants.ALL_SUBJ_CACHE_KEY, subjCache);
            log.info("所有科目缓存更新成功");
        } catch (Exception e) {
            log.error("科目缓存刷新失败，异常：{}", e.getMessage());
            throw new ServiceException(MsgConstants.SUBJ_CACHE_REFRESH_FAILED);
        } finally {
            lock.unlock();
        }

    }

    /**
     * 获取所有科目的缓存
     * @return
     */
    private Map<String, Subject> getAllSubjCache() {
        Map<String, Subject> subjCache = redisCache.getCacheMap(CacheConstants.ALL_SUBJ_CACHE_KEY);
        if (MapUtils.isEmpty(subjCache)) {
            throw new ServiceException(MsgConstants.SUBJ_CACHE_IS_EMPTY);
        }
        return subjCache;
    }

    @Override
    public Set<Subject> getAllSubjSet() {
        return new HashSet<>(getAllSubjCache().values());
    }

    @Override
    public Subject getSubject(String subjCode) {
        Map<String, Subject> allSubjCache = getAllSubjCache();
        Subject subject = allSubjCache.get(subjCode);
        if (subject == null) {
            log.warn("科目【{}】对应的缓存不存在", subjCode);
            return null;
        }
        return subject;
    }
}
