package com.ruoyi.system.mapper;

import com.ruoyi.system.domain.statementcfg.StatementCfg;
import com.ruoyi.system.domain.statementcfg.query.StatementCfgQuery;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface StatementCfgMapper {
    /** 
     * 条件查询
     * 
     * @param statementCfgQuery
     */
    List<StatementCfg> selectByCondition(StatementCfgQuery statementCfgQuery);

    /**
     * 条件计数
     * 
     * @param statementCfgQuery
     */
    Long countByCondition(StatementCfgQuery statementCfgQuery);

    /** 
     * 查询所有
     * 
     */
    List<StatementCfg> selectAll();

    /**
     * 计数所有
     * 
     */
    Long countAll();

    /** 
     * 通过id查询
     * 
     * @param id
     */
    StatementCfg selectById(Integer id);

    /** 
     * 添加
     * 
     * @param statementCfg
     */
    void insert(StatementCfg statementCfg);

    /** 
     * 批量添加
     * 
     * @param list
     */
    void batchInsert(List<StatementCfg> list);

    /** 
     * 编辑
     * 
     * @param statementCfg
     */
    void update(StatementCfg statementCfg);

    /** 
     * 批量编辑
     * 
     * @param list
     */
    void batchUpdate(List<StatementCfg> list);

    /** 
     * 删除
     * 
     * @param list
     */
    void delete(List<Integer> list);
}