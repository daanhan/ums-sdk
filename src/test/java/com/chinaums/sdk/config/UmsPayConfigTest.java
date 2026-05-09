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


package com.chinaums.sdk.config;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 配置类测试
 */
class UmsPayConfigTest {
    
    @Test
    void testBuildSuccess() {
        UmsPayConfig config = UmsPayConfig.builder()
            .appId("test-app-id")
            .appKey("test-app-key")
            .mid("898201612345678")
            .tid("88880001")
            .build();
        
        assertEquals("test-app-id", config.getAppId());
        assertEquals("test-app-key", config.getAppKey());
        assertEquals("898201612345678", config.getMid());
        assertEquals("88880001", config.getTid());
        assertEquals("H5DEFAULT", config.getInstMid());
        assertEquals(Environment.PRODUCTION, config.getEnvironment());
        assertEquals(10000, config.getConnectTimeout());
        assertEquals(30000, config.getReadTimeout());
        assertEquals(3, config.getMaxRetries());
        assertTrue(config.isEnableCertValidation());
    }
    
    @Test
    void testBuildWithCustomValues() {
        UmsPayConfig config = UmsPayConfig.builder()
            .appId("test-app-id")
            .appKey("test-app-key")
            .mid("898201612345678")
            .tid("88880001")
            .instMid("CUSTOM")
            .environment(Environment.TEST)
            .connectTimeout(5000)
            .readTimeout(20000)
            .maxRetries(5)
            .enableCertValidation(false)
            .build();
        
        assertEquals("CUSTOM", config.getInstMid());
        assertEquals(Environment.TEST, config.getEnvironment());
        assertEquals(5000, config.getConnectTimeout());
        assertEquals(20000, config.getReadTimeout());
        assertEquals(5, config.getMaxRetries());
        assertFalse(config.isEnableCertValidation());
    }
    
    @Test
    void testBuildWithNullAppId() {
        assertThrows(IllegalArgumentException.class, () -> {
            UmsPayConfig.builder()
                .appKey("test-app-key")
                .mid("898201612345678")
                .tid("88880001")
                .build();
        });
    }
    
    @Test
    void testBuildWithEmptyAppId() {
        assertThrows(IllegalArgumentException.class, () -> {
            UmsPayConfig.builder()
                .appId("")
                .appKey("test-app-key")
                .mid("898201612345678")
                .tid("88880001")
                .build();
        });
    }
    
    @Test
    void testBuildWithNullAppKey() {
        assertThrows(IllegalArgumentException.class, () -> {
            UmsPayConfig.builder()
                .appId("test-app-id")
                .mid("898201612345678")
                .tid("88880001")
                .build();
        });
    }
    
    @Test
    void testBuildWithEmptyAppKey() {
        assertThrows(IllegalArgumentException.class, () -> {
            UmsPayConfig.builder()
                .appId("test-app-id")
                .appKey("")
                .mid("898201612345678")
                .tid("88880001")
                .build();
        });
    }
    
    @Test
    void testBuildWithNullMid() {
        assertThrows(IllegalArgumentException.class, () -> {
            UmsPayConfig.builder()
                .appId("test-app-id")
                .appKey("test-app-key")
                .tid("88880001")
                .build();
        });
    }
    
    @Test
    void testBuildWithEmptyMid() {
        assertThrows(IllegalArgumentException.class, () -> {
            UmsPayConfig.builder()
                .appId("test-app-id")
                .appKey("test-app-key")
                .mid("")
                .tid("88880001")
                .build();
        });
    }
    
    @Test
    void testBuildWithNullTid() {
        assertThrows(IllegalArgumentException.class, () -> {
            UmsPayConfig.builder()
                .appId("test-app-id")
                .appKey("test-app-key")
                .mid("898201612345678")
                .build();
        });
    }
    
    @Test
    void testBuildWithEmptyTid() {
        assertThrows(IllegalArgumentException.class, () -> {
            UmsPayConfig.builder()
                .appId("test-app-id")
                .appKey("test-app-key")
                .mid("898201612345678")
                .tid("")
                .build();
        });
    }
    
    @Test
    void testEnvironmentBaseUrl() {
        assertEquals("https://api-mop.chinaums.com", Environment.PRODUCTION.getBaseUrl());
        assertEquals("https://test-api-open.chinaums.com", Environment.TEST.getBaseUrl());
    }
}
