package com.ruoyi.system.service;
		
import com.ruoyi.system.domain.PageBean;
import com.ruoyi.system.domain.statementcfg.StatementCfg;
import com.ruoyi.system.domain.statementcfg.query.StatementCfgQuery;
import com.ruoyi.system.domain.statementcfg.vo.StatementCfgVo;

import java.util.List;

public interface StatementCfgService {
    /** 
     * 条件查询
     * 返回: PageBean<StatementCfg>
     * 
     * @param statementCfgQuery
     * */
    PageBean<StatementCfg> findPageByCondition(StatementCfgQuery statementCfgQuery);

    /** 
     * 条件查询
     * 返回: List<StatementCfg>
     * 
     * @param statementCfgQuery
     * */
    List<StatementCfg> findListByCondition(StatementCfgQuery statementCfgQuery);

	/** 
     * 条件查询
     * 返回: PageBean<StatementCfgVo>
     * 
     * @param statementCfgQuery
     * */
	PageBean<StatementCfgVo> findVoPageByCondition(StatementCfgQuery statementCfgQuery);

	/** 
     * 条件查询
     * 返回: List<StatementCfgVo>
     * 
     * @param statementCfgQuery
     * */
	List<StatementCfgVo> findVoListByCondition(StatementCfgQuery statementCfgQuery);    

    /** 
     * 条件计数
     * 
     * @param statementCfgQuery
     * */
    Long countByCondition(StatementCfgQuery statementCfgQuery);

    /** 
     * 查询所有
     * 返回: List<StatementCfg>
     * 
     * */
    List<StatementCfg> findAll();
	
	/** 
     * 查询所有
     * 返回: List<StatementCfgVo>
     * 
     * */
    List<StatementCfgVo> findVoAll();

    /**
     * 计数总数
     * 
     */
    Long countAll();

    /** 
     * 通过id查询
     * 返回: StatementCfg
     * 
     * @param id
     * */
    StatementCfg findById(Integer id);

    /** 
     * 通过id查询
     * 返回: StatementCfgVo
     * 
     * @param id
     * */
    StatementCfgVo findVoById(Integer id);

    /** 
     * 添加
     * 
     * @param statementCfg
     * */
    void add(StatementCfg statementCfg);

    /** 
     * 批量添加
     * 
     * @param list
     * */
    void batchAdd(List<StatementCfg> list);

    /** 
     * 编辑
     * 
     * @param statementCfg
     * */
    void edit(StatementCfg statementCfg);

    /** 
     * 批量编辑
     * 
     * @param list
     * */
    void batchEdit(List<StatementCfg> list);

    /** 
     * 删除
     * 
     * @param list
     * */
    void delete(List<Integer> list);
}