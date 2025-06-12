package io.github.thlws.gateway.client.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * hmcSha1加密工具类
 *
 * @author wf
 */
@Slf4j
public class HmcSha1Util {

    private static final String MAC_NAME = "HmacSHA1";
    private static final Charset ENCODING = StandardCharsets.UTF_8;

    private HmcSha1Util(){}

    /**
     * Sign string.
     *
     * @param userKey    the userKey
     * @param reqUrl     the req url
     * @param timestamp  the timestamp
     * @param param      the param
     * @param userSecret the user secret
     * @return the string
     */
    public static String sign(String userKey, String reqUrl, String timestamp, String param, String userSecret) {
        ArrayList<String> params = new ArrayList<>();
        params.add(reqUrl);
        params.add(timestamp);
        params.add(param);
        return hmacSign(params, userKey, userSecret);
    }

    /**
     * Hmac sign string.
     *
     * @param params     the params
     * @param userKey    the userKey
     * @param userSecret the user secret
     * @return the string
     */
    public static String hmacSign(List<String> params, String userKey, String userSecret) {
        params.add(userKey);
        Collections.sort(params);

        StringBuilder sb = new StringBuilder();
        for (String param : params) {
            sb.append(param);
        }

        byte[] data = hmacSha1(sb.toString().getBytes(ENCODING), userSecret.getBytes(ENCODING));
        return Base64.encodeBase64URLSafeString(data);

    }

    /**
     * hmac方式的签名
     *
     * @param inDataBytes inDataBytes
     * @param keyBytes keyBytes
     * @return byte
     */
    private static byte[] hmac(byte[] inDataBytes, byte[] keyBytes) {
        if (inDataBytes != null && inDataBytes.length > 0) {
            if (keyBytes != null && keyBytes.length > 0) {
                try {
                    SecretKeySpec signingKey = new SecretKeySpec(keyBytes, HmcSha1Util.MAC_NAME);
                    Mac mac = Mac.getInstance(HmcSha1Util.MAC_NAME);
                    mac.init(signingKey);
                    return mac.doFinal(inDataBytes);
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            } else {
                throw new RuntimeException("param keyBytes can't be null");
            }
        } else {
            throw new RuntimeException("param inDataBytes can't be null");
        }
    }

    /**
     * hmac
     *
     * @param inDataBytes inDataBytes
     * @param keyBytes keyBytes
     * @return byte
     */
    private static byte[] hmacSha1(byte[] inDataBytes, byte[] keyBytes) {
        return hmac(inDataBytes, keyBytes);
    }

}
