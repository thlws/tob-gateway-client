package com.risingauto.gateway.tob;


import com.alibaba.fastjson2.JSON;
import io.github.thlws.gateway.client.ParamBasedClient;
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
@DisplayName("Param Client")
public class ParamClientBasedTest {

    /**your baseUrl */
   private final String baseUrl = "your base url";
    @Test
    @DisplayName("普通调用")
    public void normal() {

        String api = "your api uri";
        String appKey = "your appKey";
        String appSecret = "your appSecret";

        //构建请求参数
        HashMap<String, Object> params = new HashMap<>();
        params.put("name", "Rose");
        params.put("age", 18);
        String body = JSON.toJSONString(params);

        //发起请求
        ParamBasedClient tobGatewayClient = new ParamBasedClient(baseUrl,appKey,appSecret);
        String result = tobGatewayClient.post(body, api);
        Assertions.assertNotNull(result);
    }




    @Test
    @DisplayName("带header调用")
    public void withHeader() {

        String api = "your api uri";
        String appKey = "your appKey";
        String appSecret = "your appSecret";

        //构建请求参数
        HashMap<String, Object> params = new HashMap<>();
        params.put("param1", "value1");
        params.put("param2", "value2");
        String body = JSON.toJSONString(params);

        //添加自定义header
        Map<String, String> headers = new HashMap<>();
        headers.put("customer-x-header", "your header value");

        //发起请求
        ExecuteRequest request = ExecuteRequest.builder()
                .headers(headers)
                .method(Method.POST).body(body).uri(api).build();
        ParamBasedClient tobGatewayClient = new ParamBasedClient(baseUrl,appKey,appSecret);
        String result = tobGatewayClient.execute(request);
        Assertions.assertNotNull(result);


    }


    @Test
    @DisplayName("get")
    public void echoGet() {
        String api = "/echo";
        String appKey = "your appKey";
        String appSecret = "your appSecret";

        String json = "{\"data\":\"hello\"}";

        ParamBasedClient tobGatewayClient = new ParamBasedClient(baseUrl,appKey,appSecret);
        String responseBody = tobGatewayClient.get(JSON.toJSONString(json), api);

        System.out.println(responseBody);
    }

    @Test
    @DisplayName("echoPost")
    public void echoPost() {
        String api = "/echo";
        String appKey = "your appKey";
        String appSecret = "your appSecret";

        HashMap<String,String> map = new HashMap<>();
        map.put("data", "hello");

        ParamBasedClient tobGatewayClient = new ParamBasedClient(baseUrl,appKey,appSecret);
        String responseBody = tobGatewayClient.post(JSON.toJSONString(map), api);

        System.out.println("responseBody="+responseBody);
    }

}
