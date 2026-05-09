/*
 * Copyright (c) 2026 ChinaUMS
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://opensource.org/licenses/MIT
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package com.chinaums.sdk.security;

import com.chinaums.sdk.exception.UmsPayAuthException;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * 通知验签工具类
 * 
 * 用于验证银联商务支付通知回调数据的签名。
 * 支持SHA256和MD5两种签名方式。
 * 
 * 验签流程：
 * 1. 通知参数按key的ASCII字典序排序
 * 2. 排序后以 key=value&key=value 格式拼接（排除sign参数）
 * 3. 在拼接字符串末尾追加通讯密钥key
 * 4. 根据signType参数选择SHA256或MD5计算签名
 * 5. 比较计算的签名与通知中的sign参数是否一致
 * 
 * @author ChinaUMS SDK Team
 * @version 1.0.0
 * @since 1.0.0
 */
public class NotificationVerifier {
    
    private static final String SIGN_TYPE_SHA256 = "SHA256";
    private static final String SIGN_TYPE_MD5 = "MD5";
    
    /**
     * 验证通知签名
     * 
     * @param params 通知参数Map
     * @param notifyKey 通讯密钥
     * @return 验证通过返回true，否则返回false
     * @throws UmsPayAuthException 验证过程出错时抛出
     */
    public static boolean verify(Map<String, String> params, String notifyKey) 
            throws UmsPayAuthException {
        if (params == null || params.isEmpty()) {
            return false;
        }
        
        String sign = params.get("sign");
        String signType = params.get("signType");
        
        if (sign == null || sign.isEmpty()) {
            return false;
        }
        
        if (signType == null || signType.isEmpty()) {
            signType = SIGN_TYPE_MD5;
        }
        
        String signStr = buildSignString(params);
        String calculatedSign;
        
        if (SIGN_TYPE_SHA256.equalsIgnoreCase(signType)) {
            calculatedSign = sha256Hex(signStr + notifyKey);
        } else {
            calculatedSign = md5Hex(signStr + notifyKey);
        }
        
        return calculatedSign.equalsIgnoreCase(sign);
    }
    
    /**
     * 构建待签名字符串
     * 
     * 参数按key的ASCII字典序排序，排除sign参数，
     * 以 key=value&key=value 格式拼接。
     * 参数值使用原始值（非URL编码值）。
     * 
     * @param params 通知参数Map
     * @return 待签名字符串
     */
    public static String buildSignString(Map<String, String> params) {
        Map<String, String> sortedParams = new TreeMap<>(params);
        sortedParams.remove("sign");
        sortedParams.remove("signType");
        
        List<String> parts = new ArrayList<>();
        for (Map.Entry<String, String> entry : sortedParams.entrySet()) {
            if (entry.getValue() != null && !entry.getValue().isEmpty()) {
                parts.add(entry.getKey() + "=" + entry.getValue());
            }
        }
        
        return join(parts, "&");
    }
    
    /**
     * SHA-256哈希计算（返回十六进制字符串）
     * 
     * @param content 待哈希的内容
     * @return 十六进制哈希值
     * @throws UmsPayAuthException 哈希计算失败时抛出
     */
    private static String sha256Hex(String content) throws UmsPayAuthException {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(content.getBytes(StandardCharsets.UTF_8));
            return SignatureUtil.bytesToHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new UmsPayAuthException("SHA-256算法不可用", e);
        }
    }
    
    /**
     * MD5哈希计算（返回十六进制字符串）
     * 
     * @param content 待哈希的内容
     * @return 十六进制哈希值
     * @throws UmsPayAuthException 哈希计算失败时抛出
     */
    private static String md5Hex(String content) throws UmsPayAuthException {
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            byte[] hash = digest.digest(content.getBytes(StandardCharsets.UTF_8));
            return SignatureUtil.bytesToHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new UmsPayAuthException("MD5算法不可用", e);
        }
    }
    
    /**
     * 拼接字符串列表
     * 
     * @param parts 字符串列表
     * @param delimiter 分隔符
     * @return 拼接后的字符串
     */
    private static String join(List<String> parts, String delimiter) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < parts.size(); i++) {
            if (i > 0) {
                sb.append(delimiter);
            }
            sb.append(parts.get(i));
        }
        return sb.toString();
    }
}
