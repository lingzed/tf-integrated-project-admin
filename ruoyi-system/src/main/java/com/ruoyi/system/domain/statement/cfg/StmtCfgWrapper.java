package com.ruoyi.system.domain.statement.cfg;

import java.util.List;
import java.util.Map;

/**
 * 报表配相关置包装
 */
public class StmtCfgWrapper {
    private List<RowColHeadIndexCfg> listIndexCfg;  // 行列头索引配置
    private List<SheetWriteCfg> listWriteCfg;    // 工作表写入配置
    private StatementQueryCfg objQueryCfg;    // 报表查询配置
    private Map<String, StatementQueryCfg> mapQueryCfg;    // 报表查询配置
    private Map<String, RowColHeadDataMapperCfg> mapDataCfg;    // 行列头映射数据集配置
    private RowColHeadDataMapperCfg objDataCfg;    // 行列头映射数据集配置

    public List<RowColHeadIndexCfg> getListIndexCfg() {
        return listIndexCfg;
    }

    public void setListIndexCfg(List<RowColHeadIndexCfg> listIndexCfg) {
        this.listIndexCfg = listIndexCfg;
    }

    public List<SheetWriteCfg> getListWriteCfg() {
        return listWriteCfg;
    }

    public void setListWriteCfg(List<SheetWriteCfg> listWriteCfg) {
        this.listWriteCfg = listWriteCfg;
    }

    public StatementQueryCfg getObjQueryCfg() {
        return objQueryCfg;
    }

    public void setObjQueryCfg(StatementQueryCfg objQueryCfg) {
        this.objQueryCfg = objQueryCfg;
    }

    public Map<String, StatementQueryCfg> getMapQueryCfg() {
        return mapQueryCfg;
    }

    public void setMapQueryCfg(Map<String, StatementQueryCfg> mapQueryCfg) {
        this.mapQueryCfg = mapQueryCfg;
    }

    public Map<String, RowColHeadDataMapperCfg> getMapDataCfg() {
        return mapDataCfg;
    }

    public void setMapDataCfg(Map<String, RowColHeadDataMapperCfg> mapDataCfg) {
        this.mapDataCfg = mapDataCfg;
    }

    public RowColHeadDataMapperCfg getObjDataCfg() {
        return objDataCfg;
    }

    public void setObjDataCfg(RowColHeadDataMapperCfg objDataCfg) {
        this.objDataCfg = objDataCfg;
    }
}
