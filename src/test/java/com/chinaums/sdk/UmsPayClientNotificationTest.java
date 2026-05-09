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


package com.chinaums.sdk;

import com.chinaums.sdk.config.Environment;
import com.chinaums.sdk.config.UmsPayConfig;
import com.chinaums.sdk.exception.UmsPayAuthException;
import com.chinaums.sdk.security.NotificationVerifier;
import com.chinaums.sdk.security.SignatureUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * UmsPayClient通知验证测试类
 */
class UmsPayClientNotificationTest {
    
    private UmsPayClient client;
    private String notifyKey;
    
    @BeforeEach
    void setUp() {
        notifyKey = "test-notify-key-12345";
        
        UmsPayConfig config = UmsPayConfig.builder()
            .appId("test-app-id")
            .appKey("test-app-key")
            .mid("898201612345678")
            .tid("88880001")
            .environment(Environment.TEST)
            .notifyKey(notifyKey)
            .build();
        
        client = new UmsPayClient(config);
    }
    
    @Test
    void testVerifyNotificationSuccess() throws Exception {
        Map<String, String> params = new LinkedHashMap<>();
        params.put("merOrderId", "TEST123456");
        params.put("status", "TRADE_SUCCESS");
        params.put("totalAmount", "100");
        params.put("signType", "SHA256");
        
        String signStr = NotificationVerifier.buildSignString(params);
        String expectedSign = SignatureUtil.sha256HexStatic(signStr + notifyKey);
        params.put("sign", expectedSign);
        
        assertTrue(client.verifyNotification(params));
    }
    
    @Test
    void testVerifyNotificationFailure() throws Exception {
        Map<String, String> params = new LinkedHashMap<>();
        params.put("merOrderId", "TEST123456");
        params.put("status", "TRADE_SUCCESS");
        params.put("signType", "SHA256");
        params.put("sign", "wrong-sign-value");
        
        assertFalse(client.verifyNotification(params));
    }
    
    @Test
    void testVerifyNotificationWithoutNotifyKey() {
        UmsPayConfig config = UmsPayConfig.builder()
            .appId("test-app-id")
            .appKey("test-app-key")
            .mid("898201612345678")
            .tid("88880001")
            .environment(Environment.TEST)
            .build();
        
        UmsPayClient clientWithoutKey = new UmsPayClient(config);
        
        Map<String, String> params = new LinkedHashMap<>();
        params.put("merOrderId", "TEST123456");
        params.put("sign", "some-sign");
        
        assertThrows(UmsPayAuthException.class, () -> {
            clientWithoutKey.verifyNotification(params);
        });
    }
    
    @Test
    void testVerifyNotificationWithMd5() throws Exception {
        Map<String, String> params = new LinkedHashMap<>();
        params.put("merOrderId", "TEST123456");
        params.put("status", "TRADE_SUCCESS");
        params.put("signType", "MD5");
        
        String signStr = NotificationVerifier.buildSignString(params);
        String expectedSign = md5Hex(signStr + notifyKey);
        params.put("sign", expectedSign);
        
        assertTrue(client.verifyNotification(params));
    }
    
    private String md5Hex(String content) throws Exception {
        java.security.MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
        byte[] hash = digest.digest(content.getBytes(java.nio.charset.StandardCharsets.UTF_8));
        return SignatureUtil.bytesToHex(hash);
    }
}
