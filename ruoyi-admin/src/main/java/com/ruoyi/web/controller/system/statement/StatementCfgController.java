package com.ruoyi.web.controller.system.statement;

import com.github.pagehelper.PageInfo;
import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.constant.MsgConstants;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.common.enums.statement.StatementCfgType;
import com.ruoyi.common.enums.statement.StatementType;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.StmtRelatedUtil;
import com.ruoyi.common.validated.group.Add;
import com.ruoyi.common.validated.group.Edit;
import com.ruoyi.system.domain.PageBean;
import com.ruoyi.system.domain.statement.cfg.RowColHeadIndexCfg;
import com.ruoyi.system.domain.statement.po.StatementCfg;
import com.ruoyi.system.domain.statement.dto.StatementCfgDto;
import com.ruoyi.system.domain.statement.query.StatementCfgQuery;
import com.ruoyi.system.domain.statement.vo.StatementCfgVo;
import com.ruoyi.system.domain.statement.vo.StmtCfgTypeVo;
import com.ruoyi.system.domain.statement.vo.StmtTypeVo;
import com.ruoyi.system.service.StatementCfgService;
import io.swagger.models.auth.In;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 报表配置控制器
 */
@RestController
@RequestMapping("/stmt-cfg")
@PreAuthorize("@ss.hasAnyRoles('admin,gAdmin')")
public class StatementCfgController extends BaseController {

    @Resource
    private StatementCfgService statementCfgService;
    @Resource
    private RedisCache redisCache;

    /**
     * 加载报表配置列表
     * @param corpCode
     * @param statementName
     * @param cfgType
     * @return
     */
    @GetMapping
    public TableDataInfo loadCfgList(String corpCode, String statementName, Short cfgType) {
        startPage();
        StatementCfgQuery query = new StatementCfgQuery();
        query.setCorpCode(corpCode);
        query.setStatementName(statementName);
        query.setCfgType(cfgType);
        List<StatementCfg> list = statementCfgService.findListByCondition(query);
        long total = new PageInfo(list).getTotal();
        return getDataTable(statementCfgService.toVoList(list), total);
    }

//    /**
//     * 获取报表配置
//     * @param corpCode
//     * @return
//     */
//    @GetMapping("/cache")
//    public AjaxResult getStmtCfg(String corpCode) {
//        String key = StmtRelatedUtil.getCfgCode(StatementType.GGS_GSF_SR_QK_TJB, StatementCfgType.ROW_COL_HEAD_INDEX_CFG, corpCode);
//        List<RowColHeadIndexCfg> cfgCacheList = statementCfgService.getListStmtCfgCache(key, RowColHeadIndexCfg.class);
//        log.info("cache: {}", cfgCacheList);
//        return success(cfgCacheList);
//    }

    /**
     * 添加报表配置
     * @param statementCfgDto
     * @return
     */
    @PostMapping
    public AjaxResult addStmtCfg(@Validated(Add.class) @RequestBody StatementCfgDto statementCfgDto) {
        statementCfgService.addStmtCfg(statementCfgDto);
        return success();
    }

    /**
     * 更新报表配置
     * @param statementCfgDto
     * @return
     */
    @PutMapping
    public AjaxResult editStmtCfg(@Validated(Edit.class) @RequestBody StatementCfgDto statementCfgDto) {
        statementCfgService.editStmtCfg(statementCfgDto);
        return success();
    }

    /**
     * 删除报表配置
     * @param idList
     * @return
     */
    @DeleteMapping("/{idList}")
    public AjaxResult delStmtCfg(@PathVariable List<Integer> idList) {
        statementCfgService.delStmtCfg(idList);
        return success();
    }

    /**
     * 加载报表配置的类型列表
     * @return
     */
    @GetMapping("/types")
    public AjaxResult loadStmtCfgTypeList() {
        StatementCfgType[] values = StatementCfgType.values();
        List<StmtCfgTypeVo> typeList = Stream.of(values)
                .map(type -> {
                    StmtCfgTypeVo vo = new StmtCfgTypeVo();
                    vo.setTypeId(type.getCfgType());
                    vo.setTypeName(type.getTypeName());
                    return vo;
                }).collect(Collectors.toList());
        return success(typeList);
    }

    /**
     * 加载报表类型列表
     * @return
     */
    @GetMapping("/stmts")
    public AjaxResult loadStmtTypeList() {
        StatementType[] values = StatementType.values();
        List<StmtTypeVo> typeList = Stream.of(values)
                .map(type -> {
                    StmtTypeVo vo = new StmtTypeVo();
                    vo.setStmtCode(type.getStatementCode());
                    vo.setStmtName(type.getStatementName());
                    return vo;
                }).collect(Collectors.toList());
        return success(typeList);
    }

    /**
     * 对照缓存与数据库是否一致
     * @return
     */
    @GetMapping("contrast/{cfgCode}")
    public AjaxResult contrastDbAndCache(@PathVariable String cfgCode) {
        StatementCfg byCfgCode = statementCfgService.findByCfgCode(cfgCode);
        Map<String, Boolean> res = new HashMap<>();
        if (byCfgCode == null) {
            res.put("isConsistent", false);
            return success(res);
        }
        String dbContent = byCfgCode.getCfgContent();
        String cache = redisCache.getCacheStr(cfgCode);
        res.put("isConsistent", dbContent.equals(cache));
        return success(res);
    }

    /**
     * 手动同步缓存
     * @param cfgCode
     * @return
     */
    @GetMapping("synchronized/{cfgCode}")
    public AjaxResult synchronizedCache(@PathVariable String cfgCode) {
        StatementCfg byCfgCode = statementCfgService.findByCfgCode(cfgCode);
        if (byCfgCode == null) {
            throw new ServiceException(MsgConstants.STMT_CFG_NOT_EXISTS);
        }
        redisCache.setCacheStr(cfgCode, byCfgCode.getCfgContent());
        return success();
    }
}
