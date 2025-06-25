package com.ruoyi.system.service.impl;

import com.ruoyi.system.domain.PageBean;
import com.ruoyi.system.domain.statementcfg.StatementCfg;
import com.ruoyi.system.domain.statementcfg.query.StatementCfgQuery;
import com.ruoyi.system.domain.statementcfg.vo.StatementCfgVo;
import com.ruoyi.system.mapper.StatementCfgMapper;
import com.ruoyi.system.service.StatementCfgService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class StatementCfgServiceImpl implements StatementCfgService {
    private static final Logger log = LoggerFactory.getLogger(StatementCfgServiceImpl.class);

    @Resource
    private StatementCfgMapper statementCfgMapper;

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
        return PageBean.of(total, statementCfgQuery.getPage(), statementCfgQuery.getPageSize(), list);
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
        return PageBean.of(total, statementCfgQuery.getPage(), statementCfgQuery.getPageSize(), voList);
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
     * 添加
     * 
     * @param statementCfg
     */
    @Override
    public void add(StatementCfg statementCfg) {
        statementCfgMapper.insert(statementCfg);
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
     * @param statementCfg
     */
    @Override
    public void edit(StatementCfg statementCfg) {
        statementCfgMapper.update(statementCfg);
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
}