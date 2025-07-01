package com.ruoyi.common.utils.json;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ruoyi.common.constant.MsgConstants;
import com.ruoyi.common.exception.ServiceException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

/**
 * json工具类
 */
public class JsonUtil {
    private static final ObjectMapper mapper = new ObjectMapper();

    /**
     * 校验 JSON 字符串是否是合法格式
     */
    public static boolean isValidJson(String jsonStr) {
        try {
            mapper.readTree(jsonStr);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private static void checkJson(String jsonStr) {
        if (isValidJson(jsonStr)) return;
        throw new ServiceException(MsgConstants.JSON_CONT_NOT_LEGAL_FORMAT);
    }

    /**
     * 从json文件中读取List
     * @param filename  json文件
     * @param clazz     List中元素类型
     * @return
     * @param <T>
     * @throws IOException
     */
    public static <T> List<T> getListFromJsonFile(String filename, Class<T> clazz) throws IOException {
        String jsonStr = new String(Files.readAllBytes(Paths.get(filename)), StandardCharsets.UTF_8);
        return getListFromJson(jsonStr, clazz);
    }

    /**
     * 从json字符串中读取list
     * @param jsonStr   json字符串
     * @param clazz     List中元素类型
     * @return
     * @param <T>
     * @throws IOException
     */
    public static <T> List<T> getListFromJson(String jsonStr, Class<T> clazz) {
        checkJson(jsonStr);
        JSONArray jsonArray = JSON.parseArray(jsonStr);
        return jsonArray.toJavaList(clazz);
    }

    /**
     * 从json文件中读取对象
     * @param filename  json文件
     * @param clazz     对象类型
     * @return
     * @param <T>
     * @throws IOException
     */
    public static <T> T getObjFormJsonFile(String filename, Class<T> clazz) throws IOException {
        String jsonStr = new String(Files.readAllBytes(Paths.get(filename)), StandardCharsets.UTF_8);
        return getObjFormJson(jsonStr, clazz);
    }

    /**
     * 从json字符串中读取对象
     * @param jsonStr   json字符串
     * @param clazz     对象类型
     * @return
     * @param <T>
     * @throws IOException
     */
    public static <T> T getObjFormJson(String jsonStr, Class<T> clazz) {
        checkJson(jsonStr);
        return JSON.parseObject(jsonStr, clazz);
    }

    /**
     * 从json文件中读取Map，key为字符串
     * @param filename  json文件
     * @param clazz     value的类型
     * @return
     * @param <T>
     * @throws IOException
     */
    public static <T> Map<String, T> getMapFormJsonFile(String filename, Class<T> clazz) throws IOException {
        String jsonStr = new String(Files.readAllBytes(Paths.get(filename)), StandardCharsets.UTF_8);
        return getMapFormJson(jsonStr, clazz);
    }

    /**
     * 从json字符串中读取Map，key为字符串
     * @param jsonStr   json字符串
     * @param clazz     value的类型
     * @return
     * @param <T>
     */
    public static <T> Map<String, T> getMapFormJson(String jsonStr, Class<T> clazz) {
        checkJson(jsonStr);
        return JSON.parseObject(jsonStr, TypeReference.parametricType(Map.class, String.class, clazz));
    }

    /**
     * 从json文件中读取1层级嵌套Map
     * 外层key和内层key均为字符串
     * @param filename  json文件
     * @param clazz     内层Map的value类型
     * @return
     * @param <T>
     * @throws IOException
     */
    public static <T> Map<String, Map<String, T>> getNestedMapFromJsonFile(String filename, Class<T> clazz) throws IOException {
        String jsonStr = new String(Files.readAllBytes(Paths.get(filename)), StandardCharsets.UTF_8);
        return getNestedMapFromJson(jsonStr, clazz);
    }

    /**
     * 从json字符串中读取1层级嵌套Map
     * 外层key和内层key均为字符串
     * @param jsonStr   json字符串
     * @param clazz     内层Map的value类型
     * @return
     * @param <T>
     * @throws IOException
     */
    public static <T> Map<String, Map<String, T>> getNestedMapFromJson(String jsonStr, Class<T> clazz) {
        checkJson(jsonStr);
        return JSON.parseObject(jsonStr,
                TypeReference.parametricType(Map.class,
                        String.class,
                        TypeReference.parametricType(Map.class, String.class, clazz)
                )
        );
    }
}
