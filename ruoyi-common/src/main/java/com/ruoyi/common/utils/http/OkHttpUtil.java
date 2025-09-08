package com.ruoyi.common.utils.http;

import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class OkHttpUtil {
    private static final Logger log = LoggerFactory.getLogger(OkHttpUtil.class);
    private static final int DEFAULT_CONN_TIMEOUT_SECS = 10;    // 默认连接超时
    private static final int DEFAULT_READ_TIMEOUT_SECS = 30;    // 默认读取超时
    private static final MediaType JSON_MEDIA_TYPE = MediaType.parse("application/json");
    private static final OkHttpClient DEFAULT_CLIENT = createClient(DEFAULT_CONN_TIMEOUT_SECS, DEFAULT_READ_TIMEOUT_SECS);
    private static final long THRESHOLD = 1024 * 1024;  // 1MB

    private static OkHttpClient createClient(Integer connTimeout, Integer readTimeout) {
        return new OkHttpClient.Builder()
                // 连接池
                .connectionPool(new ConnectionPool(50, 10, TimeUnit.MINUTES))
                .followRedirects(false)
                .retryOnConnectionFailure(false)    // 请求失败是否重试1次
                // 连接超时
                .connectTimeout(connTimeout == null || connTimeout <= 0 ? DEFAULT_CONN_TIMEOUT_SECS : connTimeout, TimeUnit.SECONDS)
                // 读取超时
                .readTimeout(readTimeout == null || readTimeout <= 0 ? DEFAULT_READ_TIMEOUT_SECS : readTimeout, TimeUnit.SECONDS)
                .build();
    }

    private static Request.Builder getRequestBuilder(Map<String, String> headers) {
        Request.Builder builder = new Request.Builder();
        if (headers != null) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                builder.addHeader(entry.getKey(), entry.getValue());
            }
        }
        return builder;
    }

    /**
     * 执行请求
     *
     * @param client
     * @param request
     * @param errorMsgPrefix
     * @return
     */
    private static String executeRequest(OkHttpClient client, Request request, String errorMsgPrefix) {
        Response response;
        ResponseBody body = null;
        try {
            response = client.newCall(request).execute();
            if (!response.isSuccessful()) {
                throw new RuntimeException(errorMsgPrefix + "响应失败: " + response.code());
            }
            body = response.body();

            long len = body.contentLength();

            // 如果知道长度且小于阈值，直接一次性读
            if (len >= 0 && len <= THRESHOLD) {
                return body.string();
            }

            // 否则流式读取
            try (InputStream in = body.byteStream();
                 BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
                return sb.toString();
            }
        } catch (SocketTimeoutException | ConnectException e) {
            log.error("{}超时, url: {}, request: {}", errorMsgPrefix, request.url(), request, e);
            throw new RuntimeException(errorMsgPrefix + "超时", e);
        } catch (Exception e) {
            log.error("{}异常, url: {}, request: {}", errorMsgPrefix, request.url(), request, e);
            throw new RuntimeException(errorMsgPrefix + "异常", e);
        } finally {
            if (body != null) {
                body.close();
            }
        }
    }

    /**
     * 确认客户端
     *
     * @param connTimeout
     * @param readTimeout
     * @return
     */
    private static OkHttpClient confirmClient(Integer connTimeout, Integer readTimeout) {
        if (connTimeout == null && readTimeout == null) {
            return DEFAULT_CLIENT;
        }
        if (connTimeout != null && readTimeout != null && connTimeout == DEFAULT_CONN_TIMEOUT_SECS && readTimeout == DEFAULT_READ_TIMEOUT_SECS) {
            return DEFAULT_CLIENT;
        }
        return createClient(connTimeout, readTimeout);
    }

    /**
     * GET请求
     *
     * @param url url
     * @return
     */
    public static String getRequest(String url) {
        return getRequest(url, DEFAULT_CONN_TIMEOUT_SECS, DEFAULT_READ_TIMEOUT_SECS);
    }

    /**
     * GET请求
     *
     * @param url     url
     * @param timeout 统一超时(连接超时 && 读取超时)，单位：s
     * @return
     */
    public static String getRequest(String url, Integer timeout) {
        return getRequest(url, timeout, timeout);
    }

    /**
     * GET请求
     *
     * @param url         url
     * @param connTimeout 连接超时，单位：s
     * @param readTimeout 读取超时，单位：s
     * @return
     */
    public static String getRequest(String url, Integer connTimeout, Integer readTimeout) {
        OkHttpClient client = confirmClient(connTimeout, readTimeout);
        Request request = getRequestBuilder(null).url(url).get().build();
        return executeRequest(client, request, "GET请求");
    }

    /**
     * POST请求
     *
     * @param url       url
     * @param bodyParam 请求头参数
     * @param headers   请求头
     * @return
     */
    public static String postRequest(String url, String bodyParam, Map<String, String> headers) {
        return postRequest(url, bodyParam, headers, DEFAULT_CONN_TIMEOUT_SECS, DEFAULT_READ_TIMEOUT_SECS);
    }

    /**
     * POST请求
     *
     * @param url       url
     * @param bodyParam 请求头参数
     * @param headers   请求头
     * @param timeout   统一超时(连接超时 && 读取超时)，单位：s
     * @return
     */
    public static String postRequest(String url, String bodyParam, Map<String, String> headers, Integer timeout) {
        return postRequest(url, bodyParam, headers, timeout, timeout);
    }

    /**
     * POST请求
     *
     * @param url         url
     * @param bodyParam   请求头参数
     * @param headers     请求头
     * @param connTimeout 连接超时，单位：s
     * @param readTimeout 读取超时，单位：s
     * @return
     */
    public static String postRequest(String url, String bodyParam, Map<String, String> headers, Integer connTimeout, Integer readTimeout) {
        OkHttpClient client = confirmClient(connTimeout, readTimeout);
        RequestBody body = RequestBody.create(JSON_MEDIA_TYPE, bodyParam);
        Request request = getRequestBuilder(headers).url(url).post(body).build();
        return executeRequest(client, request, "POST请求");
    }

    /**
     * PUT请求
     *
     * @param url       url
     * @param bodyParam 请求体参数
     * @param headers   请求头
     * @return
     */
    public static String putRequest(String url, String bodyParam, Map<String, String> headers) {
        return putRequest(url, bodyParam, headers, DEFAULT_CONN_TIMEOUT_SECS, DEFAULT_READ_TIMEOUT_SECS);
    }

    /**
     * PUT请求
     *
     * @param url       url
     * @param bodyParam 请求体参数
     * @param headers   请求头
     * @param timeout   统一超时(连接超时 && 读取超时)，单位：s
     * @return
     */
    public static String putRequest(String url, String bodyParam, Map<String, String> headers, Integer timeout) {
        return putRequest(url, bodyParam, headers, timeout, timeout);
    }

    /**
     * PUT请求
     *
     * @param url         url
     * @param bodyParam   请求体参数
     * @param headers     请求头
     * @param connTimeout 连接超时，单位：s
     * @param readTimeout 读取超时，单位：s
     * @return
     */
    public static String putRequest(String url, String bodyParam, Map<String, String> headers, Integer connTimeout, Integer readTimeout) {
        OkHttpClient client = confirmClient(connTimeout, readTimeout);
        RequestBody body = RequestBody.create(JSON_MEDIA_TYPE, bodyParam);
        Request request = getRequestBuilder(headers).url(url).put(body).build();
        return executeRequest(client, request, "PUT请求");
    }
}

