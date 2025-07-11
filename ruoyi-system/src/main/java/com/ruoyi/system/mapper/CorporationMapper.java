package com.ruoyi.system.mapper;

import com.ruoyi.system.domain.Corporation;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CorporationMapper {

    /**
     * 通过corp_code查询
     * @param corpCode
     * @return
     */
    Corporation selectByCode(String corpCode);
}
