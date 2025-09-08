package com.ruoyi.system.service;

import com.ruoyi.common.u8c.query.DetailWrapperQuery;
import com.ruoyi.common.u8c.warpper.DetailWrapper;

import java.util.List;

public interface DetailWrapperService {
    List<DetailWrapper> find(DetailWrapperQuery query);
}
