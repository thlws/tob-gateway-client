package io.github.thlws.gateway.client.dto;

import lombok.Data;

/**
 * 令牌响应
 * @author tanghl@msn.com
 */
@Data
public class TobToken {

    private String token;
    private String appKey;
    private String expireUnit;
    private int expireTime;
    private String createTime;
}
