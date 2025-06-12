package com.risingauto.gateway.tob;


import com.alibaba.fastjson2.JSON;
import io.github.thlws.gateway.client.TimeBasedClient;
import io.github.thlws.gateway.client.dto.CustomHeader;
import io.github.thlws.gateway.client.dto.ExecuteRequest;
import io.github.thlws.gateway.client.enums.Method;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * @author tanghl@msn.com
 */
@DisplayName("Time Client ")
public class TimeClientBasedTest {

    /**your baseUrl */
    private final String baseUrl = "your base url";

    @Test
    @DisplayName("time")
    public void token() {

        String api = "/api/fission/events/action";
        String appKey = "feifan-demo";
        String appSecret = "8c4f2c0a349409691eabd6e452ae7de7";

        //获取token并添加headers
        TimeBasedClient gatewayClient = new TimeBasedClient(baseUrl, appKey, appSecret);
        String token = gatewayClient.acquireToken();
        Map<String, String> headers = new HashMap<>();
        headers.put("token", token);

        //构建请求参数
        String body = "{\"action\":\"sign\",\"code\":\"BR001\"}";
        ExecuteRequest request = ExecuteRequest.builder()
                .headers(headers).method(Method.POST).body(body).uri(api).build();
        //发起请求
        String result = gatewayClient.execute(request);

        Assertions.assertNotNull(result);
        Assertions.assertNotNull(token);


    }

    @Test
    @DisplayName("携带自定义headers")
    public void withHeader() {

        String api = "your api uri";
        String appKey = "your appKey";
        String appSecret = "your appSecret";

        HashMap<String, Object> params = new HashMap<>();
        params.put("param1", "value1");
        params.put("param2", "value1");
        String body = JSON.toJSONString(params);

        //声明客户端
        TimeBasedClient gatewayClient = new TimeBasedClient(baseUrl, appKey, appSecret);

        //获取token 并添加自定义header
        String token = gatewayClient.acquireToken();
        Map<String, String> headers = new HashMap<>();
        headers.put("token", token);
        headers.put("your header name", String.valueOf(System.currentTimeMillis()));

        //构建请求参数
        ExecuteRequest request = ExecuteRequest.builder()
                .headers(headers).method(Method.POST).body(body).uri(api).build();
        //发起请求
        String result = gatewayClient.execute(request);
        Assertions.assertNotNull(result);
    }



    @Test
    @DisplayName("easy")
    public void easy() {
        String api = "your api uri";
        String appKey = "your appKey";
        String appSecret = "your appSecret";

        HashMap<String, Object> params = new HashMap<>();
        params.put("param1", "value1");
        params.put("param2", "value2");
        params.put("param3", "value3");
        String body = JSON.toJSONString(params);

        TimeBasedClient gatewayClient = new TimeBasedClient(baseUrl, appKey, appSecret);
        CustomHeader header = CustomHeader.builder().name("your header name").value("app").build();
        String result = gatewayClient.post(body, api,header);
        Assertions.assertNotNull(result);

    }
}
