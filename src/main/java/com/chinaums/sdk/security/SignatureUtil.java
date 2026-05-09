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
import com.chinaums.sdk.util.DateUtil;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;

/**
 * 签名工具类
 * 
 * 提供银联商务支付API的签名功能，支持SHA-256和HmacSHA256算法。
 * 
 * 签名流程：
 * 1. 对请求内容进行SHA-256哈希
 * 2. 拼接appId、timestamp、nonce和contentSha256
 * 3. 使用appKey进行HmacSHA256签名
 * 4. 对签名结果进行Base64编码
 * 
 * @author ChinaUMS SDK Team
 * @version 1.0.0
 * @since 1.0.0
 */
public class SignatureUtil {
    
    private final String appId;
    private final String appKey;
    
    /**
     * 构造函数
     * 
     * @param appId 应用ID
     * @param appKey 应用密钥
     */
    public SignatureUtil(String appId, String appKey) {
        this.appId = appId;
        this.appKey = appKey;
    }
    
    /**
     * 对内容进行签名
     * 
     * @param content 待签名的内容
     * @return 签名结果对象
     * @throws UmsPayAuthException 签名失败时抛出
     */
    public SignatureResult sign(String content) throws UmsPayAuthException {
        String timestamp = DateUtil.formatTimestamp(new Date());
        String nonce = UUID.randomUUID().toString().replace("-", "");
        
        String contentSha256 = sha256Hex(content);
        String signStr = appId + timestamp + nonce + contentSha256;
        String signature = hmacSha256Base64(signStr, appKey);
        
        return new SignatureResult(timestamp, nonce, signature);
    }
    
    /**
     * 验证服务器应答签名
     * 
     * 从响应头的Authorization字段中解析签名信息，并使用相同的算法重新计算签名进行比对。
     * Authorization格式：OPEN-BODY-SIG AppId="xxx",Timestamp="xxx",Nonce="xxx",Signature="xxx"
     * 
     * @param authorization 响应头中的Authorization字段值
     * @param body 响应体内容
     * @param appKey 应用密钥
     * @return 验证通过返回true，否则返回false
     * @throws UmsPayAuthException 验证过程出错时抛出
     */
    public static boolean verifyResponseSignature(String authorization, String body, String appKey) 
            throws UmsPayAuthException {
        if (authorization == null || authorization.isEmpty()) {
            return false;
        }
        
        String appId = extractParam(authorization, "AppId");
        String timestamp = extractParam(authorization, "Timestamp");
        String nonce = extractParam(authorization, "Nonce");
        String signature = extractParam(authorization, "Signature");
        
        if (appId == null || timestamp == null || nonce == null || signature == null) {
            return false;
        }
        
        String contentSha256 = sha256HexStatic(body);
        String signStr = appId + timestamp + nonce + contentSha256;
        String calculatedSignature = hmacSha256Base64Static(signStr, appKey);
        
        return calculatedSignature.equals(signature);
    }
    
    /**
     * 从Authorization字符串中提取指定参数的值
     * 
     * @param authorization Authorization字符串
     * @param paramName 参数名称
     * @return 参数值，如果不存在返回null
     */
    public static String extractParam(String authorization, String paramName) {
        String prefix = paramName + "=\"";
        int startIndex = authorization.indexOf(prefix);
        if (startIndex < 0) {
            return null;
        }
        startIndex += prefix.length();
        int endIndex = authorization.indexOf("\"", startIndex);
        if (endIndex < 0) {
            return null;
        }
        return authorization.substring(startIndex, endIndex);
    }
    
    /**
     * SHA-256哈希计算（返回十六进制字符串）
     * 
     * @param content 待哈希的内容
     * @return 十六进制哈希值
     * @throws UmsPayAuthException 哈希计算失败时抛出
     */
    private String sha256Hex(String content) throws UmsPayAuthException {
        return sha256HexStatic(content);
    }
    
    /**
     * SHA-256哈希计算（静态方法，返回十六进制字符串）
     * 
     * @param content 待哈希的内容
     * @return 十六进制哈希值
     * @throws UmsPayAuthException 哈希计算失败时抛出
     */
    public static String sha256HexStatic(String content) throws UmsPayAuthException {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(content.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new UmsPayAuthException("SHA-256算法不可用", e);
        }
    }
    
    /**
     * HmacSHA256签名并进行Base64编码
     * 
     * @param data 待签名数据
     * @param key 密钥
     * @return Base64编码的签名结果
     * @throws UmsPayAuthException 签名失败时抛出
     */
    private String hmacSha256Base64(String data, String key) throws UmsPayAuthException {
        return hmacSha256Base64Static(data, key);
    }
    
    /**
     * HmacSHA256签名并进行Base64编码（静态方法）
     * 
     * @param data 待签名数据
     * @param key 密钥
     * @return Base64编码的签名结果
     * @throws UmsPayAuthException 签名失败时抛出
     */
    public static String hmacSha256Base64Static(String data, String key) throws UmsPayAuthException {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKeySpec = new SecretKeySpec(
                key.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            mac.init(secretKeySpec);
            byte[] hash = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (Exception e) {
            throw new UmsPayAuthException("HmacSHA256签名失败", e);
        }
    }
    
    /**
     * 字节数组转十六进制字符串
     * 
     * @param bytes 字节数组
     * @return 十六进制字符串
     */
    public static String bytesToHex(byte[] bytes) {
        StringBuilder result = new StringBuilder();
        for (byte b : bytes) {
            result.append(String.format("%02x", b));
        }
        return result.toString();
    }
    
    /**
     * 签名结果类
     */
    public static class SignatureResult {
        
        private final String timestamp;
        private final String nonce;
        private final String signature;
        
        /**
         * 构造函数
         * 
         * @param timestamp 时间戳
         * @param nonce 随机数
         * @param signature 签名
         */
        public SignatureResult(String timestamp, String nonce, String signature) {
            this.timestamp = timestamp;
            this.nonce = nonce;
            this.signature = signature;
        }
        
        /**
         * 获取时间戳
         * 
         * @return 时间戳
         */
        public String getTimestamp() {
            return timestamp;
        }
        
        /**
         * 获取随机数
         * 
         * @return 随机数
         */
        public String getNonce() {
            return nonce;
        }
        
        /**
         * 获取签名
         * 
         * @return 签名
         */
        public String getSignature() {
            return signature;
        }
    }
}
