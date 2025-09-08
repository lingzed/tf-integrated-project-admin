package com.ruoyi.system.service;

import com.ruoyi.common.core.domain.entity.Corporation;

import java.util.List;

public interface CorporationService {
    /**
     * 通过corpCode查询
     * @param corpCode
     * @return
     */
    Corporation findByCode(String corpCode);

    /**
     * 通过corpCodes查询总数
     * @param corpCodes
     * @return
     */
    Long findByCodes(List<String> corpCodes);

    /**
     * 通过公司级次查询选择列表
     * @param corpLevel
     * @return
     */
    List<Corporation> findByCorpLevel(Integer corpLevel);

    /**
     * 通过用户id和公司级次查询
     * @param userId
     * @param corpLevel
     * @return
     */
    List<Corporation> findByUserIdAndCorpLevel(Long userId, Integer corpLevel);

    /**
     * 分配用户的公司选项
     * @param userId
     * @param corpId
     */
    void assignCorpOption(Long userId, List<Integer> corpId);

    /**
     * 通过用户id查询选项的公司id
     * @param userId
     * @return
     */
    List<Integer> findOptionByUserId(Long userId);

    /**
     * 删除公司选项
     * @param userId
     * @param corpId
     */
    void delOption(Long userId, List<Integer> corpId);

    /**
     * 通过id列表查询
     * @param idList
     * @return
     */
    List<Corporation> findByIDList(List<Integer> idList);

    /**
     * 查询所有
     * @return
     */
    List<Corporation> findAll();
}
