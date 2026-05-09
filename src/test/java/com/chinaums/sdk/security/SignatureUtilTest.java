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

import static org.junit.jupiter.api.Assertions.*;

/**
 * 签名工具类测试
 */
class SignatureUtilTest {
    
    @Test
    void testSign() throws UmsPayAuthException {
        SignatureUtil util = new SignatureUtil("test-app-id", "test-app-key");
        String content = "{\"test\":\"data\"}";
        
        SignatureUtil.SignatureResult result = util.sign(content);
        
        assertNotNull(result);
        assertNotNull(result.getTimestamp());
        assertNotNull(result.getNonce());
        assertNotNull(result.getSignature());
        assertEquals(14, result.getTimestamp().length());
        assertEquals(32, result.getNonce().length());
    }
    
    @Test
    void testSignWithEmptyContent() throws UmsPayAuthException {
        SignatureUtil util = new SignatureUtil("test-app-id", "test-app-key");
        String content = "";
        
        SignatureUtil.SignatureResult result = util.sign(content);
        
        assertNotNull(result);
        assertNotNull(result.getTimestamp());
        assertNotNull(result.getNonce());
        assertNotNull(result.getSignature());
    }
    
    @Test
    void testSignDeterministic() throws UmsPayAuthException {
        SignatureUtil util = new SignatureUtil("test-app-id", "test-app-key");
        String content = "{\"test\":\"data\"}";
        
        SignatureUtil.SignatureResult result1 = util.sign(content);
        
        // 等待1秒以确保时间戳不同
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        SignatureUtil.SignatureResult result2 = util.sign(content);
        
        // nonce和signature应该不同（时间戳可能相同如果在同一秒内）
        assertNotEquals(result1.getNonce(), result2.getNonce());
        assertNotEquals(result1.getSignature(), result2.getSignature());
    }
    
    @Test
    void testSignWithDifferentAppId() throws UmsPayAuthException {
        SignatureUtil util1 = new SignatureUtil("app-id-1", "test-app-key");
        SignatureUtil util2 = new SignatureUtil("app-id-2", "test-app-key");
        String content = "{\"test\":\"data\"}";
        
        SignatureUtil.SignatureResult result1 = util1.sign(content);
        SignatureUtil.SignatureResult result2 = util2.sign(content);
        
        // 不同的appId应该产生不同的签名
        assertNotEquals(result1.getSignature(), result2.getSignature());
    }
    
    @Test
    void testSignWithDifferentAppKey() throws UmsPayAuthException {
        SignatureUtil util1 = new SignatureUtil("test-app-id", "app-key-1");
        SignatureUtil util2 = new SignatureUtil("test-app-id", "app-key-2");
        String content = "{\"test\":\"data\"}";
        
        SignatureUtil.SignatureResult result1 = util1.sign(content);
        SignatureUtil.SignatureResult result2 = util2.sign(content);
        
        // 不同的appKey应该产生不同的签名
        assertNotEquals(result1.getSignature(), result2.getSignature());
    }
    
    @Test
    void testSignatureResultGetters() {
        String timestamp = "20260427120000";
        String nonce = "1234567890abcdef1234567890abcdef";
        String signature = "test-signature";
        
        SignatureUtil.SignatureResult result = 
            new SignatureUtil.SignatureResult(timestamp, nonce, signature);
        
        assertEquals(timestamp, result.getTimestamp());
        assertEquals(nonce, result.getNonce());
        assertEquals(signature, result.getSignature());
    }
    
    @Test
    void testOfficialExampleSignature() throws UmsPayAuthException {
        String appId = "12345678901234567890123456789012";
        String appKey = "67890123456789012345678901234567";
        String timestamp = "20170101120000";
        String nonce = "09876543210987654321098765432109";
        String content = "A";
        
        String contentSha256 = SignatureUtil.sha256HexStatic(content);
        String signStr = appId + timestamp + nonce + contentSha256;
        String signature = SignatureUtil.hmacSha256Base64Static(signStr, appKey);
        
        assertEquals("GINsCTyNKTpEI9KXO16KqZJ64fOyAytEKl8aaR/Dy08=", signature);
    }
    
    @Test
    void testVerifyResponseSignatureSuccess() throws UmsPayAuthException {
        String appId = "12345678901234567890123456789012";
        String appKey = "67890123456789012345678901234567";
        String timestamp = "20170101120000";
        String nonce = "09876543210987654321098765432109";
        String body = "A";
        
        String contentSha256 = SignatureUtil.sha256HexStatic(body);
        String signStr = appId + timestamp + nonce + contentSha256;
        String signature = SignatureUtil.hmacSha256Base64Static(signStr, appKey);
        
        String authorization = String.format(
            "OPEN-BODY-SIG AppId=\"%s\",Timestamp=\"%s\",Nonce=\"%s\",Signature=\"%s\"",
            appId, timestamp, nonce, signature);
        
        boolean result = SignatureUtil.verifyResponseSignature(authorization, body, appKey);
        assertTrue(result);
    }
    
    @Test
    void testVerifyResponseSignatureWithWrongBody() throws UmsPayAuthException {
        String appId = "12345678901234567890123456789012";
        String appKey = "67890123456789012345678901234567";
        String timestamp = "20170101120000";
        String nonce = "09876543210987654321098765432109";
        String body = "A";
        
        String contentSha256 = SignatureUtil.sha256HexStatic(body);
        String signStr = appId + timestamp + nonce + contentSha256;
        String signature = SignatureUtil.hmacSha256Base64Static(signStr, appKey);
        
        String authorization = String.format(
            "OPEN-BODY-SIG AppId=\"%s\",Timestamp=\"%s\",Nonce=\"%s\",Signature=\"%s\"",
            appId, timestamp, nonce, signature);
        
        boolean result = SignatureUtil.verifyResponseSignature(authorization, "B", appKey);
        assertFalse(result);
    }
    
    @Test
    void testVerifyResponseSignatureWithWrongKey() throws UmsPayAuthException {
        String appId = "12345678901234567890123456789012";
        String appKey = "67890123456789012345678901234567";
        String timestamp = "20170101120000";
        String nonce = "09876543210987654321098765432109";
        String body = "A";
        
        String contentSha256 = SignatureUtil.sha256HexStatic(body);
        String signStr = appId + timestamp + nonce + contentSha256;
        String signature = SignatureUtil.hmacSha256Base64Static(signStr, appKey);
        
        String authorization = String.format(
            "OPEN-BODY-SIG AppId=\"%s\",Timestamp=\"%s\",Nonce=\"%s\",Signature=\"%s\"",
            appId, timestamp, nonce, signature);
        
        boolean result = SignatureUtil.verifyResponseSignature(
            authorization, body, "wrong-key-0000000000000000000000000");
        assertFalse(result);
    }
    
    @Test
    void testVerifyResponseSignatureWithNullAuthorization() throws UmsPayAuthException {
        boolean result = SignatureUtil.verifyResponseSignature(null, "body", "key");
        assertFalse(result);
    }
    
    @Test
    void testVerifyResponseSignatureWithEmptyAuthorization() throws UmsPayAuthException {
        boolean result = SignatureUtil.verifyResponseSignature("", "body", "key");
        assertFalse(result);
    }
    
    @Test
    void testVerifyResponseSignatureWithInvalidAuthorization() throws UmsPayAuthException {
        boolean result = SignatureUtil.verifyResponseSignature("invalid-auth", "body", "key");
        assertFalse(result);
    }
    
    @Test
    void testExtractParam() {
        String authorization = "OPEN-BODY-SIG AppId=\"test-app-id\",Timestamp=\"20170101120000\",Nonce=\"abc123\",Signature=\"sig-value\"";
        
        assertEquals("test-app-id", SignatureUtil.extractParam(authorization, "AppId"));
        assertEquals("20170101120000", SignatureUtil.extractParam(authorization, "Timestamp"));
        assertEquals("abc123", SignatureUtil.extractParam(authorization, "Nonce"));
        assertEquals("sig-value", SignatureUtil.extractParam(authorization, "Signature"));
    }
    
    @Test
    void testExtractParamNotFound() {
        String authorization = "OPEN-BODY-SIG AppId=\"test-app-id\"";
        
        assertNull(SignatureUtil.extractParam(authorization, "Timestamp"));
        assertNull(SignatureUtil.extractParam(authorization, "NotExist"));
    }
    
    @Test
    void testSha256HexStatic() throws UmsPayAuthException {
        String result = SignatureUtil.sha256HexStatic("A");
        assertNotNull(result);
        assertEquals(64, result.length());
    }
    
    @Test
    void testBytesToHex() {
        byte[] bytes = new byte[]{(byte) 0x01, (byte) 0x23, (byte) 0xab, (byte) 0xff};
        String hex = SignatureUtil.bytesToHex(bytes);
        assertEquals("0123abff", hex);
    }
}
