package io.github.thlws.gateway.client;

import com.alibaba.fastjson2.JSON;
import io.github.thlws.gateway.client.Interceptor.NetworkInterceptor;
import io.github.thlws.gateway.client.dto.ExecuteRequest;
import io.github.thlws.gateway.client.enums.Method;
import io.github.thlws.gateway.client.utils.HmcSha1Util;
import io.github.thlws.gateway.client.utils.RequestUtil;
import okhttp3.*;
import org.apache.commons.codec.binary.Base64;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * 参数鉴权 客户端.
 *
 * @author tanghl@msn.com
 */
public class ParamBasedClient {

    private final String baseUrl;

    private final String appKey;

    private final String appSecret;

    private final OkHttpClient okHttpClient;


    /**
     * Instantiates a new Tob gateway client.
     *
     * @param baseUrl   the base url
     * @param appKey    the app key
     * @param appSecret the app secret
     */
    public ParamBasedClient(String baseUrl, String appKey, String appSecret) {
        this.baseUrl = baseUrl;
        this.appKey = appKey;
        this.appSecret = appSecret;
        this.okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(new NetworkInterceptor())
                .connectTimeout(5, TimeUnit.SECONDS)
                .readTimeout(10,TimeUnit.SECONDS)
                .connectionPool(new ConnectionPool(10,10,TimeUnit.MINUTES))
                .build();
    }

    /**
     * Instantiates a new Param based client.
     *
     * @param baseUrl      the base url
     * @param appKey       the app key
     * @param appSecret    the app secret
     * @param okHttpClient the ok http client
     */
    public ParamBasedClient(String baseUrl, String appKey, String appSecret, OkHttpClient okHttpClient) {
        this.baseUrl = baseUrl;
        this.appKey = appKey;
        this.appSecret = appSecret;
        this.okHttpClient = okHttpClient;
    }

    /**
     * 自定义发起请求
     * token 需要在 header中传递
     *
     * @param request 请求对象
     * @return 响应结果 string
     */
    public String execute(ExecuteRequest request){

        Map<String,String> headers = request.getHeaders();
        Map<String, String> authHeaders = buildAuthHeader(request.getBody(), request.getUri());
        if (request.getHeaders() != null) {
            authHeaders.putAll(headers);
        }

        String url = baseUrl + request.getUri();
        Request requestBody = RequestUtil.buildRequest(request.getBody(),request.getMethod())
                .url(url).headers(Headers.of(authHeaders)).build();
        return RequestUtil.submitRequest(okHttpClient, requestBody);
    }


    /**
     * Post string.
     *
     * @param body the body
     * @param uri  the uri
     * @return the string
     */
    public String post(String body,String uri){
        String url = baseUrl + uri;
        Map<String,String> headers = buildAuthHeader(body, uri);
        Request.Builder builder = RequestUtil.buildRequest(body, Method.POST)
                .url(url).headers(Headers.of(headers));
        return RequestUtil.submitRequest(okHttpClient, builder.build());
    }


    /**
     * Get string.
     *
     * @param json the json
     * @param uri  the uri
     * @return the string
     */
    public String get(String json,String uri){

        try {
            if (!JSON.isValid(json)) {
                throw new RuntimeException("参数必须是json格式");
            }

            String finalUrl = buildParamsUrl(json, uri);
            Request request = new Request.Builder().url(finalUrl).get().build();
            Call call = okHttpClient.newCall(request);
            try (Response response = call.execute()) {
                String responseBody = Objects.requireNonNull(response.body()).string(); response.body().string();
                return responseBody;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * Put string.
     *
     * @param body the body
     * @param uri  the uri
     * @return the string
     */
    public String put(String body,String uri){
        String url = baseUrl + uri;
        Map<String,String> headers = buildAuthHeader(body,uri);
        Request request = RequestUtil.buildRequest(body,Method.PUT).headers(Headers.of(headers)).url(url).build();
        return RequestUtil.submitRequest(okHttpClient, request);
    }

    /**
     * Patch string.
     *
     * @param body the body
     * @param uri  the uri
     * @return the string
     */
    public String patch(String body,String uri){
        String url = baseUrl + uri;
        Map<String,String> headers = buildAuthHeader(body,uri);
        Request request = RequestUtil.buildRequest(body,Method.PATCH)
                .headers(Headers.of(headers)).url(url).build();
        return RequestUtil.submitRequest(okHttpClient, request);
    }

    /**
     * Delete string.
     *
     * @param json the json
     * @param uri  the uri
     * @return the string
     */
    public String delete(String json,String uri){
        String url = baseUrl + uri;
        Map<String,String> headers = buildAuthHeader(json,uri);
        Request request = RequestUtil.buildRequest(json,Method.DELETE)
                .headers(Headers.of(headers)).url(url).build();
        return RequestUtil.submitRequest(okHttpClient,request);
    }


    /**
     * Build sign header.
     *
     * @param body        the body
     * @param uri         the uri
     * @return headers
     */
    private Map<String, String> buildAuthHeader(String body, String uri){
        return buildParamsBasedHeader(body, uri);
    }


    private Map<String, String> buildParamsBasedHeader(String body, String uri){
        if (null == body) {
            body = "";
        }
        String timestamp = String.valueOf(System.currentTimeMillis());
        System.out.println("=============");
        System.out.println("body:" + body);
        System.out.println("uri:" + uri);
        System.out.println("timestamp:" + timestamp);
        System.out.println("=============");
        Map<String, String> headers = new HashMap<>(3);

        String sign = HmcSha1Util.sign(appKey, uri, timestamp, body, appSecret);
        headers.put("gw-user-key", appKey);
        headers.put("gw-sign", sign);
        headers.put("gw-timestamp", timestamp);
        return headers;
    }

    /**
     * Build params url string.
     *
     * @param body the body
     * @param uri  the uri
     * @return the string
     */
    public String buildParamsUrl(String body, String uri){

        String timestamp = String.valueOf(System.currentTimeMillis());
        String encodeBody = Base64.encodeBase64URLSafeString(body.getBytes());
        String sign = HmcSha1Util.sign(appKey, uri, timestamp, encodeBody, appSecret);

        return baseUrl +
                uri +
                "?p=" +
                encodeBody +
                "&" +
                "gw-user-key" +
                "=" +
                appKey +
                "&" +
                "gw-sign" +
                "=" +
                sign +
                "&" +
                "gw-timestamp" +
                "=" +
                timestamp;
    }
}
