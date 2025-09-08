package com.ruoyi.system.mapper;

import com.ruoyi.common.core.domain.entity.Corporation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CorporationMapper {

    /**
     * 通过corp_code查询
     * @param corpCode
     * @return
     */
    Corporation selectByCode(String corpCode);

    /**
     * 通过corp_code查询总数
     * @param corpCodes
     * @return
     */
    Long countByCorpCodes(@Param("corpCodes") List<String> corpCodes);

    /**
     * 通过公司级次查询
     * @param corpLevel
     * @return
     */
    List<Corporation> selectByCorpLevel(Integer corpLevel);

    /**
     * 通过用户id和公司级次查询
     * @param userId
     * @param corpLevel
     * @return
     */
    List<Corporation> selectByUserIdAndCorpLevel(@Param("userId") Long userId, @Param("corpLevel") Integer corpLevel);

    /**
     * 添加用户的公司选项
     * @param userId
     * @param corpId
     */
    void insertCorpOption(@Param("userId") Long userId, @Param("corpId") List<Integer> corpId);

    /**
     * 通过用户id查询选项的公司id
     * @param userId
     * @return
     */
    List<Integer> selectOptionByUserId(Long userId);

    /**
     * 删除公司选项
     * @param userId
     * @param corpId
     */
    void deleteOption(@Param("userId") Long userId, @Param("corpId") List<Integer> corpId);

    /**
     * 通过id列表查询
     * @param idList
     * @return
     */
    List<Corporation> selectByIDList(@Param("idList") List<Integer> idList);

    /**
     * 查询所有
     * @return
     */
    List<Corporation> selectAll();
}
