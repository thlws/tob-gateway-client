package io.github.thlws.gateway.client.dto;

import io.github.thlws.gateway.client.enums.Method;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * 自定义请求
 * @author tanghl@msn.com
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExecuteRequest {

    /**
     * 请求API
     */
    private String uri;

    /**
     * POST,PUT,PATCH 请求体
     */
    private String body;


    /**
     * 请求方法
     */
    private Method method;

    /**
     * 请求Headers
     */
    private Map<String, String> headers;

}
