package com.ruoyi.system.mapper;

import com.ruoyi.common.annotation.DataSource;
import com.ruoyi.common.enums.DataSourceType;
import com.ruoyi.common.u8c.query.DetailWrapperQuery;
import com.ruoyi.common.u8c.warpper.DetailWrapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
@DataSource(DataSourceType.U8_CLOUD)
public interface DetailWrapperMapper {

    List<DetailWrapper> select(DetailWrapperQuery query);
}
