package com.ruoyi.system.service.impl;

import com.ruoyi.common.constant.MsgConstants;
import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.json.JsonUtil;
import com.ruoyi.system.domain.PageBean;
import com.ruoyi.system.domain.statementcfg.StatementCfg;
import com.ruoyi.system.domain.statementcfg.dto.StatementCfgDto;
import com.ruoyi.system.domain.statementcfg.query.StatementCfgQuery;
import com.ruoyi.system.domain.statementcfg.vo.StatementCfgVo;
import com.ruoyi.system.mapper.StatementCfgMapper;
import com.ruoyi.system.service.StatementCfgService;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

@Service
public class StatementCfgServiceImpl implements StatementCfgService {
    private static final Logger log = LoggerFactory.getLogger(StatementCfgServiceImpl.class);
    private static final Map<String, String> oldJsonStrCache = new ConcurrentHashMap<>();
    private static final ReentrantLock lock = new ReentrantLock();

    @Resource
    private StatementCfgMapper statementCfgMapper;

    @Resource
    private RedisCache redisCache;

    /**
     * 条件查询
     * 返回: PageBean<StatementCfg>
     *
     * @param statementCfgQuery
     */
    @Override
    public PageBean<StatementCfg> findPageByCondition(StatementCfgQuery statementCfgQuery) {
        List<StatementCfg> list = findListByCondition(statementCfgQuery);
        Long total = countByCondition(statementCfgQuery);
        return PageBean.of(total, statementCfgQuery.getPageNum(), statementCfgQuery.getPageSize(), list);
    }

    /**
     * 条件查询
     * 返回: List<StatementCfg>
     *
     * @param statementCfgQuery
     */
    @Override
    public List<StatementCfg> findListByCondition(StatementCfgQuery statementCfgQuery) {
        return statementCfgMapper.selectByCondition(statementCfgQuery);
    }

    /**
     * 条件查询
     * 返回: PageBean<StatementCfgVo>
     *
     * @param statementCfgQuery
     */
    @Override
    public PageBean<StatementCfgVo> findVoPageByCondition(StatementCfgQuery statementCfgQuery) {
        List<StatementCfgVo> voList = findVoListByCondition(statementCfgQuery);
        Long total = countByCondition(statementCfgQuery);
        return PageBean.of(total, statementCfgQuery.getPageNum(), statementCfgQuery.getPageSize(), voList);
    }

    /**
     * 条件查询
     * 返回: List<StatementCfgVo>
     *
     * @param statementCfgQuery
     */
    @Override
    public List<StatementCfgVo> findVoListByCondition(StatementCfgQuery statementCfgQuery) {
        return findListByCondition(statementCfgQuery).stream().map(e -> {
            StatementCfgVo voBean = new StatementCfgVo();
            BeanUtils.copyProperties(e, voBean);
            return voBean;
        }).collect(Collectors.toList());
    }

    /**
     * 条件计数
     *
     * @param statementCfgQuery
     */
    @Override
    public Long countByCondition(StatementCfgQuery statementCfgQuery) {
        return statementCfgMapper.countByCondition(statementCfgQuery);
    }

    /**
     * 查询所有
     *
     */
    @Override
    public List<StatementCfg> findAll() {
        return statementCfgMapper.selectAll();
    }

    /**
     * 查询所有，返回vo
     *
     */
    @Override
    public List<StatementCfgVo> findVoAll() {
        return findAll().stream().map(s -> {
            StatementCfgVo vo = new StatementCfgVo();
            BeanUtils.copyProperties(s, vo);
            return vo;
        }).collect(Collectors.toList());
    }

    /**
     * 计数总数
     *
     */
    @Override
    public Long countAll() {
        return statementCfgMapper.countAll();
    }

    /**
     * 通过id查询
     * 返回: StatementCfg
     *
     * @param id
     */
    @Override
    public StatementCfg findById(Integer id) {
        return statementCfgMapper.selectById(id);
    }

    /**
     * 通过id列表查询
     * @param idList
     * @return
     */
    @Override
    public List<StatementCfg> findByIds(List<Integer> idList) {
        return statementCfgMapper.selectByIds(idList);
    }

    /**
     * 通过id查询
     * 返回: StatementCfgVo
     *
     * @param id
     */
    @Override
    public StatementCfgVo findVoById(Integer id) {
        StatementCfgVo voBean = new StatementCfgVo();
        BeanUtils.copyProperties(findById(id), voBean);
        return voBean;
    }

    /**
     * 通过id查询
     * 返回: StatementCfg
     *
     * @param cfgCode
     */
    @Override
    public StatementCfg findByCfgCode(String cfgCode) {
        return statementCfgMapper.selectByCfgCode(cfgCode);
    }

    /**
     * 添加
     *
     * @param statementCfgDto
     */
    @Override
    public void add(StatementCfgDto statementCfgDto) {
        statementCfgMapper.insert(statementCfgDto);
    }

    /**
     * 批量添加
     *
     * @param list
     */
    @Override
    public void batchAdd(List<StatementCfg> list) {
        statementCfgMapper.batchInsert(list);
    }

    /**
     * 编辑
     *
     * @param statementCfgDto
     */
    @Override
    public Integer edit(StatementCfgDto statementCfgDto) {
        return statementCfgMapper.update(statementCfgDto);
    }

    /**
     * 批量编辑
     *
     * @param list
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchEdit(List<StatementCfg> list) {
        statementCfgMapper.batchUpdate(list);
    }

    /**
     * 删除
     *
     * @param list
     */
    @Override
    public void delete(List<Integer> list) {
        statementCfgMapper.delete(list);
    }

    /**
     * 刷新redis中的所有的报表配置缓存
     */
    @Override
    public void refreshAllCfg() {
        // 筛选出待刷新的配置
        List<StatementCfg> needRefresh = findAll().stream()
                .filter(cfg -> StringUtils.isNotEmpty(cfg.getCfgCode()))
                .filter(cfg -> {
                    if (StringUtils.isEmpty(cfg.getCfgContent())) {
                        log.warn("当前配置编码: {}, 对应的{}", cfg.getCfgContent(), MsgConstants.STMT_CFG_CONTENT_IS_EMPTY);
                        return false;
                    }
                    return true;
                })
                .collect(Collectors.toList());

        needRefresh.forEach(cfg -> refreshSingleCfg(cfg.getCfgCode(), cfg.getCfgContent()));
    }

    /**
     * 更新报表配置
     * @param dto
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void editStmtCfg(StatementCfgDto dto) {
        Integer cfgId = dto.getCfgId();
        StatementCfg byId = findById(cfgId);
        if (byId == null) {
            throw new ServiceException(String.format(MsgConstants.ID_NOT_EXIST, cfgId));
        }
        String newContent = dto.getCfgContent();
        boolean isChanged = !newContent.equals(byId.getCfgContent());

        // 更新数据库
        if (edit(dto) == 0) {
            throw new ServiceException(MsgConstants.UPDATE_FAILED);
        }

        // 更新缓存
        if (isChanged) {
            refreshSingleCfg(byId.getCfgCode(), newContent);
        }
    }

    /**
     * 添加报表配置
     * @param statementCfgDto
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addStmtCfg(StatementCfgDto statementCfgDto) {
        String cfgCode = statementCfgDto.getCfgCode();
        StatementCfg statementCfg = findByCfgCode(statementCfgDto.getCfgCode());
        if (statementCfg != null) {
            throw new SecurityException(String.format(MsgConstants.STMT_CFG_CODE_EXIST, cfgCode));
        }

        // 新增
        add(statementCfgDto);

        // 加入缓存
        addSingleCfg(statementCfgDto.getCfgCode(), statementCfgDto.getCfgContent());
    }

    /**
     * 删除报表配置
     * @param cfgIdList
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delStmtCfg(List<Integer> cfgIdList) {
        List<StatementCfg> byIds = findByIds(cfgIdList);
        if (CollectionUtils.isEmpty(byIds)) return;

        // 筛选出待删除的配置key
        List<String> needDel = byIds.stream()
                .map(StatementCfg::getCfgCode)
                .collect(Collectors.toList());

        // 删除配置
        delete(cfgIdList);

        // 删除缓存
        redisCache.deleteObject(needDel);
        log.info("删除配置项缓存: [{}]", String.join(", ", needDel));
    }

    /**
     * 获取缓存的配置
     * 返回对象
     * @param key
     */
    @Override
    public <T> T getObjStmtCfgCache(String key, Class<T> tClass) {
        String content = redisCache.getCacheStr(key);
        if (StringUtils.isEmpty(content)) {
            throw new SecurityException(String.format(MsgConstants.STMT_CFG_CACHE_CONTENT_IS_EMPTY, key));
        }
        return JsonUtil.getObjFormJson(content, tClass);
    }

    /**
     * 获取缓存的配置
     * 返回列表
     * @param key
     */
    @Override
    public <T> List<T> getListStmtCfgCache(String key, Class<T> tClass) {
        String content = redisCache.getCacheStr(key);
        if (StringUtils.isEmpty(content)) {
            throw new SecurityException(String.format(MsgConstants.STMT_CFG_CACHE_CONTENT_IS_EMPTY, key));
        }
        return JsonUtil.getListFromJson(content, tClass);
    }

    /**
     * 获取缓存的配置
     * 返回Map
     * @param key
     */
    @Override
    public <T> Map<String, T> getMapStmtCfgCache(String key, Class<T> tClass) {
        String content = redisCache.getCacheStr(key);
        if (StringUtils.isEmpty(content)) {
            throw new SecurityException(String.format(MsgConstants.STMT_CFG_CACHE_CONTENT_IS_EMPTY, key));
        }
        return JsonUtil.getMapFormJson(content, tClass);
    }

    /**
     * 获取缓存的配置
     * 返回一层嵌套Map, 各层key均为字符串
     * @param key
     */
    @Override
    public <T> Map<String, Map<String, T>> getNestedMapStmtCfgCache(String key, Class<T> tClass) {
        String content = redisCache.getCacheStr(key);
        if (StringUtils.isEmpty(content)) {
            throw new SecurityException(String.format(MsgConstants.STMT_CFG_CACHE_CONTENT_IS_EMPTY, key));
        }
        return JsonUtil.getNestedMapFromJson(content, tClass);
    }


    /**
     * 刷新单个缓存
     * @param cfgCode
     * @param newVal
     */
    private void refreshSingleCfg(String cfgCode, String newVal) {
        // 缓存中旧值与新值一致，不更新
        if (newVal.equals(redisCache.getCacheStr(cfgCode))) return;
        lock.lock();
        try {
            redisCache.setCacheStr(cfgCode, newVal);
        } finally {
            lock.unlock();
        }
        log.info("配置项缓存【{}】更新成功", cfgCode);
    }

    /**
     * 添加单个缓存
     * @param cfgCode
     * @param val
     */
    private void addSingleCfg(String cfgCode, String val) {
        lock.lock();
        try {
            redisCache.setCacheStr(cfgCode, val);
        } finally {
            lock.unlock();
        }
        log.info("配置项【{}】缓存成功", cfgCode);
    }
}