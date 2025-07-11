package com.ruoyi.system.mapper;

import com.ruoyi.system.domain.statement.po.StatementCfg;
import com.ruoyi.system.domain.statement.dto.StatementCfgDto;
import com.ruoyi.system.domain.statement.query.StatementCfgQuery;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

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
     * 通过cfg_code查询
     * @param cfgCode
     * @return
     */
    StatementCfg selectByCfgCode(String cfgCode);

    /**
     * 通过id列表查询
     * @param idList
     * @return
     */
    List<StatementCfg> selectByIds(@Param("idList") List<Integer> idList);

    /**
     * 添加
     *
     * @param statementCfgDto
     */
    void insert(StatementCfgDto statementCfgDto);

    /**
     * 批量添加
     *
     * @param list
     */
    void batchInsert(List<StatementCfg> list);

    /**
     * 编辑
     *
     * @param statementCfgDto
     */
    Integer update(StatementCfgDto statementCfgDto);

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