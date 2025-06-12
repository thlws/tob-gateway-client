package io.github.thlws.gateway.client.utils;


import io.github.thlws.gateway.client.enums.Method;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Objects;

@Slf4j
public class RequestUtil {

    public static Request.Builder buildRequest(Method method, String url, Map<String, Object> params) {
        HttpUrl.Builder httpBuilder = Objects.requireNonNull(HttpUrl.parse(url)).newBuilder();
        if (params != null) {
            for (Map.Entry<String, Object> param : params.entrySet()) {
                httpBuilder.addQueryParameter(param.getKey(), String.valueOf(param.getValue()));
            }
        }
        return new Request.Builder()
                .url(httpBuilder.build())
                .method(method.name(), new FormBody.Builder().build());
    }

    public static Request.Builder buildRequest(String json, Method method) {
        if (Objects.isNull(method)) {
            throw new RuntimeException("http method 必须设置");
        }
        MediaType mediaType = MediaType.parse("application/json;charset=utf-8");
        assert mediaType != null;
        RequestBody requestBody = RequestBody.create(mediaType,json);
        return new Request.Builder()
                .method(method.name(), requestBody);
    }

    public static String submitRequest(OkHttpClient okHttpClient, Request request) {
        try(Response response = okHttpClient.newCall(request).execute()) {
            return new String(Objects.requireNonNull(response.body()).bytes(), StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }



}