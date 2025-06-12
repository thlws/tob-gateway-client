package io.github.thlws.gateway.client.enums;

/**
 * The enum Auth enum.
 *
 * @author tanghl@msn.com 
 */
public enum AuthEnum {

    /**
     * 基于POST请求 requestBody 参数
     * 每次调用需要针对请求参数进行签名后发送请求
     */
    PARAMS_BASED,

    /**
     * 基于TOKEN
     * 获取token后，header携带token请求，无需对请求参数签名
     */
    TOKEN_BASED;

}
