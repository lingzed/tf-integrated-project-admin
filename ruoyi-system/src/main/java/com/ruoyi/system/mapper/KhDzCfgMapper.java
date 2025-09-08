package com.ruoyi.system.mapper;

import com.ruoyi.system.domain.statement.po.KhDzCfg;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface KhDzCfgMapper {
    /**
     * 通过code查询
     * @param code
     * @return
     */
    String selectByCode(String code);
}
