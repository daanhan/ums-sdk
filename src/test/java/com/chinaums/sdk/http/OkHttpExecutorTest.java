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


package com.chinaums.sdk.http;

import com.chinaums.sdk.config.Environment;
import com.chinaums.sdk.config.UmsPayConfig;
import com.chinaums.sdk.exception.UmsPayNetworkException;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * HTTP执行器测试
 */
class OkHttpExecutorTest {
    
    private MockWebServer server;
    private OkHttpExecutor httpClient;
    
    @BeforeEach
    void setUp() throws IOException {
        server = new MockWebServer();
        server.start();
        
        UmsPayConfig config = UmsPayConfig.builder()
            .appId("test-app-id")
            .appKey("test-app-key")
            .mid("898201612345678")
            .tid("88880001")
            .environment(Environment.TEST)
            .build();
        
        httpClient = new OkHttpExecutor(config);
    }
    
    @AfterEach
    void tearDown() throws IOException {
        server.shutdown();
    }
    
    @Test
    void testPostSuccess() throws UmsPayNetworkException {
        String mockResponse = "{\"errCode\":\"SUCCESS\",\"message\":\"成功\"}";
        server.enqueue(new MockResponse()
            .setBody(mockResponse)
            .setResponseCode(200));
        
        String url = server.url("/test").toString();
        String content = "{\"test\":\"data\"}";
        
        HttpResponse response = httpClient.post(url, content, "test-signature");
        
        assertTrue(response.isSuccess());
        assertEquals(200, response.getCode());
        assertEquals(mockResponse, response.getBody());
    }
    
    @Test
    void testPostFailure() throws UmsPayNetworkException {
        String mockResponse = "{\"errCode\":\"ERROR\",\"message\":\"失败\"}";
        server.enqueue(new MockResponse()
            .setBody(mockResponse)
            .setResponseCode(400));
        
        String url = server.url("/test").toString();
        String content = "{\"test\":\"data\"}";
        
        HttpResponse response = httpClient.post(url, content, "test-signature");
        
        assertFalse(response.isSuccess());
        assertEquals(400, response.getCode());
        assertEquals(mockResponse, response.getBody());
    }
    
    @Test
    void testGetSuccess() throws UmsPayNetworkException {
        String mockResponse = "{\"errCode\":\"SUCCESS\",\"message\":\"成功\"}";
        server.enqueue(new MockResponse()
            .setBody(mockResponse)
            .setResponseCode(200));
        
        String url = server.url("/test").toString();
        String content = "{\"test\":\"data\"}";
        
        HttpResponse response = httpClient.get(url, content, "test-signature");
        
        assertTrue(response.isSuccess());
        assertEquals(200, response.getCode());
        assertEquals(mockResponse, response.getBody());
    }
    
    @Test
    void testGetFailure() throws UmsPayNetworkException {
        String mockResponse = "{\"errCode\":\"ERROR\",\"message\":\"失败\"}";
        server.enqueue(new MockResponse()
            .setBody(mockResponse)
            .setResponseCode(404));
        
        String url = server.url("/test").toString();
        String content = "{\"test\":\"data\"}";
        
        HttpResponse response = httpClient.get(url, content, "test-signature");
        
        assertFalse(response.isSuccess());
        assertEquals(404, response.getCode());
        assertEquals(mockResponse, response.getBody());
    }
    
    @Test
    void testHttpResponseIsSuccess() {
        HttpResponse response1 = new HttpResponse(200, "body");
        assertTrue(response1.isSuccess());
        
        HttpResponse response2 = new HttpResponse(201, "body");
        assertTrue(response2.isSuccess());
        
        HttpResponse response3 = new HttpResponse(299, "body");
        assertTrue(response3.isSuccess());
        
        HttpResponse response4 = new HttpResponse(300, "body");
        assertFalse(response4.isSuccess());
        
        HttpResponse response5 = new HttpResponse(400, "body");
        assertFalse(response5.isSuccess());
        
        HttpResponse response6 = new HttpResponse(500, "body");
        assertFalse(response6.isSuccess());
    }
    
    @Test
    void testHttpResponseGetters() {
        HttpResponse response = new HttpResponse(200, "test-body");
        assertEquals(200, response.getCode());
        assertEquals("test-body", response.getBody());
    }
}
