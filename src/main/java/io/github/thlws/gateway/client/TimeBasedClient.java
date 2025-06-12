package io.github.thlws.gateway.client;

import com.alibaba.fastjson2.JSON;
import io.github.thlws.gateway.client.Interceptor.NetworkInterceptor;
import io.github.thlws.gateway.client.dto.CustomHeader;
import io.github.thlws.gateway.client.dto.ExecuteRequest;
import io.github.thlws.gateway.client.dto.TobTokenRequest;
import io.github.thlws.gateway.client.dto.TobTokenResult;
import io.github.thlws.gateway.client.enums.Method;
import io.github.thlws.gateway.client.utils.MD5Util;
import io.github.thlws.gateway.client.utils.RequestUtil;
import okhttp3.ConnectionPool;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 基于 时间 鉴权客户端
 *
 * @author tanghl@msn.com
 */
public class TimeBasedClient {

    private final static String TOKEN_GENERATE_API = "/api/validation/tob/token/generate";

    private final String baseUrl;

    private final String appKey;

    private final String appSecret;

    private final OkHttpClient okHttpClient;


    /**
     * Instantiates a new Tob gateway token client.
     *
     * @param baseUrl   the base url
     * @param appKey    the app key
     * @param appSecret the app secret
     */
    public TimeBasedClient(String baseUrl, String appKey, String appSecret) {
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
     * Instantiates a new Token based client.
     *
     * @param baseUrl      the base url
     * @param appKey       the app key
     * @param appSecret    the app secret
     * @param okHttpClient the ok http client
     */
    public TimeBasedClient(String baseUrl, String appKey, String appSecret, OkHttpClient okHttpClient) {
        this.baseUrl = baseUrl;
        this.appKey = appKey;
        this.appSecret = appSecret;
        this.okHttpClient = okHttpClient;
    }

    private String signForTokenGenerateApi(Long timestamp) {
        StringJoiner joiner = new StringJoiner("#");
        String text = joiner.add(appKey).add(String.valueOf(timestamp)).add(appSecret).toString();
        return MD5Util.md5(text);
    }

    /**
     * 基于时间鉴权方式获取令牌
     *
     * @return token string
     */
    public String acquireToken() {

        Long timestamp = System.currentTimeMillis();
        String url = baseUrl + TOKEN_GENERATE_API;
        String sign = signForTokenGenerateApi(timestamp);
        TobTokenRequest request = TobTokenRequest.builder().appKey(appKey).timestamp(String.valueOf(timestamp)).sign(sign).build();

        Request.Builder builder = RequestUtil.buildRequest(JSON.toJSONString(request), Method.POST).url(url);
        String body = RequestUtil.submitRequest(okHttpClient, builder.build());
        TobTokenResult tokenResult = JSON.parseObject(body, TobTokenResult.class);
        Objects.requireNonNull(tokenResult, "token获取失败");
        Objects.requireNonNull(tokenResult.getData(), "token获取失败");

        return tokenResult.getData().getToken();
    }

    /**
     * 自定义发起请求
     * token 需要在 header中传递
     *
     * @param request 请求对象
     * @return 响应结果 string
     */
    public String execute(ExecuteRequest request){

        String url = baseUrl + request.getUri();
        Request requestBody = RequestUtil.buildRequest(request.getBody(),request.getMethod())
                .url(url).headers(Headers.of(request.getHeaders())).build();
        return RequestUtil.submitRequest(okHttpClient, requestBody);

    }


    /**
     * Post 请求.
     *
     * @param body    the body
     * @param uri     the uri
     * @param headers the headers
     * @return the string
     */
    public String post(String body, String uri, CustomHeader... headers) {
        String url = baseUrl + uri;
        Request request = RequestUtil.buildRequest(body,Method.POST).url(url)
                .headers(parseHeaders(headers)).build();
        return RequestUtil.submitRequest(okHttpClient, request);
    }

    /**
     * Get 请求.
     *
     * @param uri     the uri
     * @param headers the headers
     * @return the string
     */
    public String get(String uri,CustomHeader ... headers) {
        return get(null, uri,headers);
    }

    /**
     * Get 请求，带url参数.
     *
     * @param params  the param map
     * @param uri     the uri
     * @param headers the headers
     * @return the string
     */
    public String get(Map<String, Object> params, String uri,CustomHeader ... headers) {
        String url = baseUrl + uri;
        Request request = RequestUtil.buildRequest(Method.GET,url,params)
                .headers(parseHeaders(headers)).build();
        return RequestUtil.submitRequest(okHttpClient, request);
    }

    /**
     * Put 请求.
     *
     * @param token   the token
     * @param body    the body
     * @param uri     the uri
     * @param headers the headers
     * @return the string
     */
    public String put(String token,String body, String uri,CustomHeader... headers) {
        String url = baseUrl + uri;
        Request request = RequestUtil.buildRequest(body,Method.PUT).url(url)
                .headers(parseHeaders(headers)).build();
        return RequestUtil.submitRequest(okHttpClient, request);
    }

    /**
     * Patch 请求.
     *
     * @param body    the body
     * @param uri     the uri
     * @param headers the headers
     * @return the string
     */
    public String patch(String body, String uri,CustomHeader... headers) {
        String url = baseUrl + uri;
        Request request = RequestUtil.buildRequest(body,Method.PATCH).url(url)
                .headers(parseHeaders(headers)).build();
        return RequestUtil.submitRequest(okHttpClient, request);
    }

    /**
     * Delete 请求.
     *
     * @param uri     the uri
     * @param headers the headers
     * @return the string
     */
    public String delete(String uri,CustomHeader... headers) {
        return delete(null, uri,headers);
    }

    /**
     * Delete 请求，带url参数.
     *
     * @param params  the param map
     * @param uri     the uri
     * @param headers the headers
     * @return the string
     */
    public String delete(Map<String, Object> params, String uri,CustomHeader... headers) {
        String url = baseUrl + uri;
        Request request = RequestUtil.buildRequest(Method.DELETE,url,params)
                .headers(parseHeaders(headers)).build();
        return RequestUtil.submitRequest(okHttpClient, request);
    }

    private Headers parseHeaders(CustomHeader... headers){
        if (Objects.isNull(headers) || headers.length == 0) {
            return Headers.of(Collections.emptyMap());
        }
        return Headers.of(Arrays.stream(headers).collect(Collectors.toMap(CustomHeader::getName, CustomHeader::getValue)));
    }

}
