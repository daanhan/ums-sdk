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


package com.chinaums.sdk.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 异常类测试
 */
class UmsPayExceptionTest {
    
    @Test
    void testUmsPayException() {
        UmsPayException ex = new UmsPayException("TEST_ERROR", "测试错误");
        assertEquals("TEST_ERROR", ex.getErrorCode());
        assertEquals("测试错误", ex.getMessage());
        assertNotNull(ex.getRequestId());
        assertTrue(ex.getTimestamp() > 0);
    }
    
    @Test
    void testUmsPayExceptionWithCause() {
        Exception cause = new RuntimeException("原始异常");
        UmsPayException ex = new UmsPayException("TEST_ERROR", "测试错误", cause);
        assertEquals("TEST_ERROR", ex.getErrorCode());
        assertEquals("测试错误", ex.getMessage());
        assertEquals(cause, ex.getCause());
        assertNotNull(ex.getRequestId());
        assertTrue(ex.getTimestamp() > 0);
    }
    
    @Test
    void testUmsPayAuthException() {
        UmsPayAuthException ex = new UmsPayAuthException("认证失败");
        assertEquals("AUTH_ERROR", ex.getErrorCode());
        assertEquals("认证失败", ex.getMessage());
    }
    
    @Test
    void testUmsPayAuthExceptionWithCause() {
        Exception cause = new RuntimeException("签名验证失败");
        UmsPayAuthException ex = new UmsPayAuthException("认证失败", cause);
        assertEquals("AUTH_ERROR", ex.getErrorCode());
        assertEquals("认证失败", ex.getMessage());
        assertEquals(cause, ex.getCause());
    }
    
    @Test
    void testUmsPayNetworkException() {
        UmsPayNetworkException ex = new UmsPayNetworkException("网络错误");
        assertEquals("NETWORK_ERROR", ex.getErrorCode());
        assertEquals("网络错误", ex.getMessage());
    }
    
    @Test
    void testUmsPayNetworkExceptionWithCause() {
        Exception cause = new RuntimeException("连接超时");
        UmsPayNetworkException ex = new UmsPayNetworkException("网络错误", cause);
        assertEquals("NETWORK_ERROR", ex.getErrorCode());
        assertEquals("网络错误", ex.getMessage());
        assertEquals(cause, ex.getCause());
    }
    
    @Test
    void testUmsPayBusinessException() {
        UmsPayBusinessException ex = new UmsPayBusinessException("BIZ_ERROR", "业务错误");
        assertEquals("BIZ_ERROR", ex.getErrorCode());
        assertEquals("业务错误", ex.getMessage());
    }
    
    @Test
    void testUmsPayBusinessExceptionWithCause() {
        Exception cause = new RuntimeException("余额不足");
        UmsPayBusinessException ex = new UmsPayBusinessException("BIZ_ERROR", "业务错误", cause);
        assertEquals("BIZ_ERROR", ex.getErrorCode());
        assertEquals("业务错误", ex.getMessage());
        assertEquals(cause, ex.getCause());
    }
    
    @Test
    void testToString() {
        UmsPayException ex = new UmsPayException("TEST_ERROR", "测试错误");
        String str = ex.toString();
        assertTrue(str.contains("TEST_ERROR"));
        assertTrue(str.contains("测试错误"));
        assertTrue(str.contains("requestId"));
        assertTrue(str.contains("timestamp"));
    }
}
