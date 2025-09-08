package com.ruoyi.system.service.impl;

import com.ruoyi.common.core.domain.entity.Corporation;
import com.ruoyi.system.mapper.CorporationMapper;
import com.ruoyi.system.service.CorporationService;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CorporationServiceImpl implements CorporationService {
    @Resource
    private CorporationMapper corporationMapper;

    @Override
    public Corporation findByCode(String corpCode) {
        return corporationMapper.selectByCode(corpCode);
    }

    @Override
    public Long findByCodes(List<String> corpCodes) {
        return corporationMapper.countByCorpCodes(corpCodes);
    }

    @Override
    public List<Corporation> findByCorpLevel(Integer corpLevel) {
        return corporationMapper.selectByCorpLevel(corpLevel);
    }

    @Override
    public List<Corporation> findByUserIdAndCorpLevel(Long userId, Integer corpLevel) {
        return corporationMapper.selectByUserIdAndCorpLevel(userId, corpLevel);
    }

    @Override
    public void assignCorpOption(Long userId, List<Integer> corpId) {
        // 过滤出需要分配的公司id
        List<Integer> needAssign = findByIDList(corpId).stream()
                .map(Corporation::getId).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(needAssign)) return;
        // 当前用户有的公司选项列表
        List<Integer> currCorpIdList = findOptionByUserId(userId);
        // 过滤出没有的
        List<Integer> haveNot = needAssign.stream()
                .filter(corp -> !currCorpIdList.contains(corp))
                .collect(Collectors.toList());
        if (haveNot.isEmpty()) return;
        corporationMapper.insertCorpOption(userId, haveNot);
    }

    @Override
    public List<Integer> findOptionByUserId(Long userId) {
        return corporationMapper.selectOptionByUserId(userId);
    }

    @Override
    public void delOption(Long userId, List<Integer> corpId) {
        corporationMapper.deleteOption(userId, corpId);
    }

    @Override
    public List<Corporation> findByIDList(List<Integer> idList) {
        return corporationMapper.selectByIDList(idList);
    }

    @Override
    public List<Corporation> findAll() {
        return corporationMapper.selectAll();
    }
}
