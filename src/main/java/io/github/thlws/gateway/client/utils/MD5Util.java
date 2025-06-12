package io.github.thlws.gateway.client.utils;

import org.apache.commons.codec.digest.DigestUtils;

/**
 * md5 工具类
 *
 * @author tanghl@msn.com
 */
public class MD5Util {

    /**
     * MD5加密
     *
     * @param text the text
     * @return the string
     */
    public static String md5(String text){
        return DigestUtils.md5Hex(text);
    }

    /**
     * MD5校验
     *
     * @param text the text
     * @param md5  the md 5
     * @return the boolean
     */
    public static boolean verify(String text, String md5){
        //根据传入的密钥进行验证
        String md5Text = md5(text);
        return md5Text.equalsIgnoreCase(md5);
    }

}
