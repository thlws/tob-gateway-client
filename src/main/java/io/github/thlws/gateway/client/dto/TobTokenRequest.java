package io.github.thlws.gateway.client.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 认证请求
 * @author tanghl@msn.com
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TobTokenRequest {

    private String appKey;

    private String timestamp;

    private String sign;

}
