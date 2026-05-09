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
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 通知验签工具类测试
 */
class NotificationVerifierTest {
    
    @Test
    void testVerifySha256Success() throws UmsPayAuthException {
        String notifyKey = "test-notify-key";
        Map<String, String> params = new LinkedHashMap<>();
        params.put("merOrderId", "TEST123456");
        params.put("status", "TRADE_SUCCESS");
        params.put("totalAmount", "100");
        params.put("signType", "SHA256");
        
        String signStr = NotificationVerifier.buildSignString(params);
        String expectedSign = SignatureUtil.sha256HexStatic(signStr + notifyKey);
        params.put("sign", expectedSign);
        
        assertTrue(NotificationVerifier.verify(params, notifyKey));
    }
    
    @Test
    void testVerifyMd5Success() throws UmsPayAuthException {
        String notifyKey = "test-notify-key";
        Map<String, String> params = new LinkedHashMap<>();
        params.put("merOrderId", "TEST123456");
        params.put("status", "TRADE_SUCCESS");
        params.put("totalAmount", "100");
        params.put("signType", "MD5");
        
        String signStr = NotificationVerifier.buildSignString(params);
        String expectedSign = md5Hex(signStr + notifyKey);
        params.put("sign", expectedSign);
        
        assertTrue(NotificationVerifier.verify(params, notifyKey));
    }
    
    @Test
    void testVerifyWithWrongSign() throws UmsPayAuthException {
        String notifyKey = "test-notify-key";
        Map<String, String> params = new LinkedHashMap<>();
        params.put("merOrderId", "TEST123456");
        params.put("status", "TRADE_SUCCESS");
        params.put("signType", "SHA256");
        params.put("sign", "wrong-sign-value");
        
        assertFalse(NotificationVerifier.verify(params, notifyKey));
    }
    
    @Test
    void testVerifyWithWrongKey() throws UmsPayAuthException {
        String notifyKey = "test-notify-key";
        Map<String, String> params = new LinkedHashMap<>();
        params.put("merOrderId", "TEST123456");
        params.put("status", "TRADE_SUCCESS");
        params.put("signType", "SHA256");
        
        String signStr = NotificationVerifier.buildSignString(params);
        String expectedSign = SignatureUtil.sha256HexStatic(signStr + notifyKey);
        params.put("sign", expectedSign);
        
        assertFalse(NotificationVerifier.verify(params, "wrong-notify-key"));
    }
    
    @Test
    void testVerifyWithNullParams() throws UmsPayAuthException {
        assertFalse(NotificationVerifier.verify(null, "test-key"));
    }
    
    @Test
    void testVerifyWithEmptyParams() throws UmsPayAuthException {
        Map<String, String> params = new HashMap<>();
        assertFalse(NotificationVerifier.verify(params, "test-key"));
    }
    
    @Test
    void testVerifyWithMissingSign() throws UmsPayAuthException {
        String notifyKey = "test-notify-key";
        Map<String, String> params = new LinkedHashMap<>();
        params.put("merOrderId", "TEST123456");
        params.put("signType", "SHA256");
        
        assertFalse(NotificationVerifier.verify(params, notifyKey));
    }
    
    @Test
    void testBuildSignStringSortsByKey() {
        Map<String, String> params = new LinkedHashMap<>();
        params.put("zebra", "z");
        params.put("alpha", "a");
        params.put("middle", "m");
        
        String result = NotificationVerifier.buildSignString(params);
        assertEquals("alpha=a&middle=m&zebra=z", result);
    }
    
    @Test
    void testBuildSignStringExcludesSign() {
        Map<String, String> params = new LinkedHashMap<>();
        params.put("merOrderId", "TEST123");
        params.put("sign", "some-sign-value");
        params.put("signType", "SHA256");
        
        String result = NotificationVerifier.buildSignString(params);
        assertEquals("merOrderId=TEST123", result);
    }
    
    @Test
    void testBuildSignStringExcludesEmptyValue() {
        Map<String, String> params = new LinkedHashMap<>();
        params.put("merOrderId", "TEST123");
        params.put("status", "");
        params.put("totalAmount", "100");
        
        String result = NotificationVerifier.buildSignString(params);
        assertEquals("merOrderId=TEST123&totalAmount=100", result);
    }
    
    @Test
    void testBuildSignStringExcludesNullValue() {
        Map<String, String> params = new LinkedHashMap<>();
        params.put("merOrderId", "TEST123");
        params.put("status", null);
        params.put("totalAmount", "100");
        
        String result = NotificationVerifier.buildSignString(params);
        assertEquals("merOrderId=TEST123&totalAmount=100", result);
    }
    
    @Test
    void testVerifyWithSpecialCharacters() throws UmsPayAuthException {
        String notifyKey = "test-notify-key";
        Map<String, String> params = new LinkedHashMap<>();
        params.put("merOrderId", "TEST+123/456=");
        params.put("status", "TRADE_SUCCESS");
        params.put("signType", "SHA256");
        
        String signStr = NotificationVerifier.buildSignString(params);
        String expectedSign = SignatureUtil.sha256HexStatic(signStr + notifyKey);
        params.put("sign", expectedSign);
        
        assertTrue(NotificationVerifier.verify(params, notifyKey));
    }
    
    @Test
    void testVerifyDefaultSignTypeIsMd5() throws UmsPayAuthException {
        String notifyKey = "test-notify-key";
        Map<String, String> params = new LinkedHashMap<>();
        params.put("merOrderId", "TEST123456");
        params.put("status", "TRADE_SUCCESS");
        
        String signStr = NotificationVerifier.buildSignString(params);
        String expectedSign = md5Hex(signStr + notifyKey);
        params.put("sign", expectedSign);
        
        assertTrue(NotificationVerifier.verify(params, notifyKey));
    }
    
    @Test
    void testVerifySignCaseInsensitive() throws UmsPayAuthException {
        String notifyKey = "test-notify-key";
        Map<String, String> params = new LinkedHashMap<>();
        params.put("merOrderId", "TEST123456");
        params.put("signType", "SHA256");
        
        String signStr = NotificationVerifier.buildSignString(params);
        String expectedSign = SignatureUtil.sha256HexStatic(signStr + notifyKey);
        params.put("sign", expectedSign.toUpperCase());
        
        assertTrue(NotificationVerifier.verify(params, notifyKey));
    }
    
    private String md5Hex(String content) throws UmsPayAuthException {
        try {
            java.security.MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
            byte[] hash = digest.digest(content.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            return SignatureUtil.bytesToHex(hash);
        } catch (java.security.NoSuchAlgorithmException e) {
            throw new UmsPayAuthException("MD5算法不可用", e);
        }
    }
}
