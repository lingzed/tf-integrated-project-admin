package com.ruoyi.common.utils.http;

import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class OkHttpUtil {
    private static final Logger log = LoggerFactory.getLogger(OkHttpUtil.class);
    private static final int DEFAULT_TIMEOUT_SECS = 12;
    private static final MediaType JSON_MEDIA_TYPE = MediaType.parse("application/json");
    private static final OkHttpClient DEFAULT_CLIENT = createClient(DEFAULT_TIMEOUT_SECS);

    private static OkHttpClient createClient(int timeoutSecs) {
        return new OkHttpClient.Builder()
                .connectionPool(new ConnectionPool(50, 10, TimeUnit.MINUTES))
                .followRedirects(false)
                .retryOnConnectionFailure(false)
                .connectTimeout(timeoutSecs, TimeUnit.SECONDS)
                .readTimeout(timeoutSecs, TimeUnit.SECONDS)
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

    private static String executeRequest(OkHttpClient client, Request request, String errorMsgPrefix) {
        Response response;
        ResponseBody body = null;
        try {
            response = client.newCall(request).execute();
            body = response.body();
            return body != null ? body.string() : "";
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

    public static String getRequest(String url) {
        return getRequest(url, DEFAULT_TIMEOUT_SECS);
    }

    public static String getRequest(String url, int timeoutSecs) {
        OkHttpClient client = (timeoutSecs == DEFAULT_TIMEOUT_SECS) ? DEFAULT_CLIENT : createClient(timeoutSecs);
        Request request = getRequestBuilder(null).url(url).get().build();
        return executeRequest(client, request, "GET请求");
    }

    public static String postRequest(String url, String jsonBody, Map<String, String> headers) {
        return postRequest(url, jsonBody, headers, DEFAULT_TIMEOUT_SECS);
    }

    public static String postRequest(String url, String jsonBody, Map<String, String> headers, int timeoutSecs) {
        OkHttpClient client = (timeoutSecs == DEFAULT_TIMEOUT_SECS) ? DEFAULT_CLIENT : createClient(timeoutSecs);
        RequestBody body = RequestBody.create(JSON_MEDIA_TYPE, jsonBody);
        Request request = getRequestBuilder(headers).url(url).post(body).build();
        return executeRequest(client, request, "POST请求");
    }

    public static String putRequest(String url, String jsonBody, Map<String, String> headers) {
        return putRequest(url, jsonBody, headers, DEFAULT_TIMEOUT_SECS);
    }

    public static String putRequest(String url, String jsonBody, Map<String, String> headers, int timeoutSecs) {
        OkHttpClient client = (timeoutSecs == DEFAULT_TIMEOUT_SECS) ? DEFAULT_CLIENT : createClient(timeoutSecs);
        RequestBody body = RequestBody.create(JSON_MEDIA_TYPE, jsonBody);
        Request request = getRequestBuilder(headers).url(url).put(body).build();
        return executeRequest(client, request, "PUT请求");
    }
}

