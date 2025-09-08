package com.ruoyi.common.utils;

import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * 集合分段器
 */
public class CollectionSplitter {
    /**
     * 将任意 Collection（如 List、Set）均分成指定数量的段
     * @param collection 原始集合（List 或 Set）
     * @param segmentCount 要分成的段数
     * @param <T> 元素类型
     * @return List<List < T>> 均分后的段集合
     */
    public static <T> List<List<T>> splitCollection(Collection<T> collection, int segmentCount) {
        List<List<T>> result = new ArrayList<>();
        if (collection == null || collection.isEmpty() || segmentCount <= 0) {
            return result;
        }

        List<T> dataList = new ArrayList<>(collection);
        int total = dataList.size();
        int baseSize = total / segmentCount;
        int remainder = total % segmentCount;

        int index = 0;
        for (int i = 0; i < segmentCount; i++) {
            int currentSize = baseSize + (i < remainder ? 1 : 0);
            if (currentSize == 0) {
                result.add(Collections.emptyList());
                continue;
            }
            result.add(dataList.subList(index, index + currentSize));
            index += currentSize;
        }

        return result;
    }

    /**
     * 将任意 Collection（如 List、Set）分段
     * 当集合总数 <= 要分成的段数 * 每段大小 时，按每段大小分段
     * 否则按 要分成的段数 分段
     * @param collection        原始集合（List 或 Set）
     * @param maxSegments       要分成的段数
     * @param maxSegmentSize    每段大小
     * @return
     * @param <T>
     */
    public static <T> List<List<T>> splitCollection(Collection<T> collection, int maxSegments, int maxSegmentSize) {
        List<List<T>> result = new ArrayList<>();
        // 处理无效输入
        if (collection == null || collection.isEmpty() || maxSegments <= 0 || maxSegmentSize <= 0) {
            return result;
        }

        List<T> dataList = new ArrayList<>(collection);
        int total = dataList.size();
        int totalCapacity = maxSegments * maxSegmentSize;

        // 情况1：总数小于等于最大容量，按每段大小分段
        if (total <= totalCapacity) {
            int index = 0;
            while (index < total) {
                int end = Math.min(index + maxSegmentSize, total);
                result.add(dataList.subList(index, end));
                index = end;
            }
        }
        // 情况2：总数大于最大容量，按最大段数分段
        else {
            int baseSize = total / maxSegments;
            int remainder = total % maxSegments;
            int index = 0;

            for (int i = 0; i < maxSegments; i++) {
                int currentSize = baseSize + (i < remainder ? 1 : 0);
                result.add(dataList.subList(index, index + currentSize));
                index += currentSize;
            }
        }

        return result;
    }
}
