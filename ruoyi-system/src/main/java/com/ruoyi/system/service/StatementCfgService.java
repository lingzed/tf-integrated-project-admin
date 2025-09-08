package com.ruoyi.system.service;

import com.ruoyi.system.domain.PageBean;
import com.ruoyi.system.domain.statement.po.StatementCfg;
import com.ruoyi.system.domain.statement.dto.StatementCfgDto;
import com.ruoyi.system.domain.statement.query.StatementCfgQuery;
import com.ruoyi.system.domain.statement.vo.StatementCfgVo;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
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
     * 转换为VoList
     * @param list
     * @return
     */
    List<StatementCfgVo> toVoList(List<StatementCfg> list);

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
     * 通过id列表查询
     * @param idList
     * @return
     */
    List<StatementCfg> findByIds(List<Integer> idList);

    /**
     * 通过id查询
     * 返回: StatementCfgVo
     *
     * @param id
     * */
    StatementCfgVo findVoById(Integer id);

    /**
     * 通过cfg_code查询
     * 返回: StatementCfg
     *
     * @param cfgCode
     * */
    StatementCfg findByCfgCode(String cfgCode);

    /**
     * 添加
     *
     * @param statementCfgDto
     * */
    void add(StatementCfgDto statementCfgDto);

    /**
     * 批量添加
     *
     * @param list
     * */
    void batchAdd(List<StatementCfg> list);

    /**
     * 编辑
     *
     * @param statementCfgDto
     * */
    Integer edit(StatementCfgDto statementCfgDto);

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

    /**
     * 刷新redis中的所有的报表配置缓存
     */
    void refreshAllCfg();

    /**
     * 更新报表配置
     * @param statementCfgDto
     */
    void editStmtCfg(StatementCfgDto statementCfgDto);

    /**
     * 添加报表配置
     * @param statementCfgDto
     */
    void addStmtCfg(StatementCfgDto statementCfgDto);

    /**
     * 删除报表配置
     * @param cfgIdList
     */
    void delStmtCfg(List<Integer> cfgIdList);

    /**
     * 获取缓存的配置
     * 返回对象
     * @param cfgCode
     */
    <T> T getObjStmtCfgCache(String cfgCode, Class<T> tClass);

    /**
     * 获取缓存的配置
     * 返回列表
     * @param key
     */
    <T> List<T> getListStmtCfgCache(String key, Class<T> tClass);

    /**
     * 获取缓存的配置
     * 返回Map, key为字符串
     * @param key
     */
    <T> Map<String, T> getMapStmtCfgCache(String key, Class<T> tClass);

    /**
     * 获取缓存的配置
     * 返回一层嵌套Map, 各层key均为字符串
     * @param key
     */
    <T> Map<String, Map<String, T>> getNestedMapStmtCfgCache(String key, Class<T> tClass);
}