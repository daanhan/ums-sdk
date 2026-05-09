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
import com.chinaums.sdk.exception.UmsPayBusinessException;
import com.chinaums.sdk.exception.UmsPayNetworkException;
import com.chinaums.sdk.exception.UmsPayException;
import com.chinaums.sdk.http.HttpClient;
import com.chinaums.sdk.http.HttpResponse;
import com.chinaums.sdk.request.H5PayRequest;
import com.chinaums.sdk.request.QueryRequest;
import com.chinaums.sdk.request.RefundQueryRequest;
import com.chinaums.sdk.request.RefundRequest;
import com.chinaums.sdk.response.H5PayResponse;
import com.chinaums.sdk.response.QueryResponse;
import com.chinaums.sdk.response.RefundQueryResponse;
import com.chinaums.sdk.response.RefundResponse;
import com.chinaums.sdk.security.SignatureUtil;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * UmsPayClient测试类
 */
class UmsPayClientTest {
    
    private MockWebServer server;
    private UmsPayClient client;
    private String baseUrl;
    
    @BeforeEach
    void setUp() throws IOException {
        server = new MockWebServer();
        server.start();
        baseUrl = server.url("").toString().replaceAll("/$", "");
        
        UmsPayConfig config = UmsPayConfig.builder()
            .appId("test-app-id")
            .appKey("test-app-key")
            .mid("898201612345678")
            .tid("88880001")
            .environment(Environment.TEST)
            .build();
        
        // 创建自定义HttpClient，使用MockWebServer的URL
        HttpClient httpClient = new TestHttpClient(config, baseUrl);
        client = new UmsPayClient(config, httpClient);
    }
    
    @AfterEach
    void tearDown() throws IOException {
        server.shutdown();
    }
    
    @Test
    void testH5PaySuccess() throws Exception {
        String mockResponse = "{\"errCode\":\"SUCCESS\",\"payUrl\":\"https://pay.example.com\",\"merOrderId\":\"TEST123\"}";
        server.enqueue(new MockResponse()
            .setBody(mockResponse)
            .setResponseCode(200));
        
        H5PayRequest request = H5PayRequest.builder()
            .merOrderId("TEST123")
            .totalAmount(100L)
            .build();
        
        H5PayResponse response = client.h5Pay(request);
        
        assertTrue(response.isSuccess());
        assertEquals("https://pay.example.com", response.getPayUrl());
        assertEquals("TEST123", response.getMerOrderId());
    }
    
    @Test
    void testH5PayBusinessError() throws Exception {
        String mockResponse = "{\"errCode\":\"BIZ_ERROR\",\"errMsg\":\"业务错误\"}";
        server.enqueue(new MockResponse()
            .setBody(mockResponse)
            .setResponseCode(200));
        
        H5PayRequest request = H5PayRequest.builder()
            .merOrderId("TEST123")
            .totalAmount(100L)
            .build();
        
        assertThrows(UmsPayBusinessException.class, () -> {
            client.h5Pay(request);
        });
    }
    
    @Test
    void testH5PayNetworkError() throws Exception {
        server.enqueue(new MockResponse()
            .setResponseCode(500));
        
        H5PayRequest request = H5PayRequest.builder()
            .merOrderId("TEST123")
            .totalAmount(100L)
            .build();
        
        assertThrows(UmsPayNetworkException.class, () -> {
            client.h5Pay(request);
        });
    }
    
    @Test
    void testQuerySuccess() throws Exception {
        String mockResponse = "{\"errCode\":\"SUCCESS\",\"status\":\"TRADE_SUCCESS\",\"totalAmount\":100}";
        server.enqueue(new MockResponse()
            .setBody(mockResponse)
            .setResponseCode(200));
        
        QueryRequest request = new QueryRequest("TEST123");
        QueryResponse response = client.query(request);
        
        assertTrue(response.isSuccess());
        assertEquals("TRADE_SUCCESS", response.getStatus());
        assertEquals(100L, response.getTotalAmount());
    }
    
    @Test
    void testQueryBusinessError() throws Exception {
        String mockResponse = "{\"errCode\":\"ORDER_NOT_FOUND\",\"errMsg\":\"订单不存在\"}";
        server.enqueue(new MockResponse()
            .setBody(mockResponse)
            .setResponseCode(200));
        
        QueryRequest request = new QueryRequest("TEST123");
        
        assertThrows(UmsPayBusinessException.class, () -> {
            client.query(request);
        });
    }
    
    @Test
    void testRefundSuccess() throws Exception {
        String mockResponse = "{\"errCode\":\"SUCCESS\",\"status\":\"TRADE_SUCCESS\",\"refundAmount\":50}";
        server.enqueue(new MockResponse()
            .setBody(mockResponse)
            .setResponseCode(200));
        
        RefundRequest request = RefundRequest.builder()
            .merOrderId("TEST123")
            .refundAmount(50L)
            .build();
        
        RefundResponse response = client.refund(request);
        
        assertTrue(response.isSuccess());
        assertEquals("TRADE_SUCCESS", response.getStatus());
        assertEquals(50L, response.getRefundAmount());
    }
    
    @Test
    void testRefundBusinessError() throws Exception {
        String mockResponse = "{\"errCode\":\"REFUND_FAILED\",\"errMsg\":\"退款失败\"}";
        server.enqueue(new MockResponse()
            .setBody(mockResponse)
            .setResponseCode(200));
        
        RefundRequest request = RefundRequest.builder()
            .merOrderId("TEST123")
            .refundAmount(50L)
            .build();
        
        assertThrows(UmsPayBusinessException.class, () -> {
            client.refund(request);
        });
    }
    
    @Test
    void testRefundQuerySuccess() throws Exception {
        String mockResponse = "{\"errCode\":\"SUCCESS\",\"refundStatus\":\"SUCCESS\",\"totalAmount\":100}";
        server.enqueue(new MockResponse()
            .setBody(mockResponse)
            .setResponseCode(200));
        
        RefundQueryRequest request = new RefundQueryRequest("REFUND123");
        RefundQueryResponse response = client.refundQuery(request);
        
        assertTrue(response.isSuccess());
        assertEquals("SUCCESS", response.getRefundStatus());
        assertEquals(100L, response.getTotalAmount());
    }
    
    @Test
    void testRefundQueryBusinessError() throws Exception {
        String mockResponse = "{\"errCode\":\"REFUND_NOT_FOUND\",\"errMsg\":\"退款订单不存在\"}";
        server.enqueue(new MockResponse()
            .setBody(mockResponse)
            .setResponseCode(200));
        
        RefundQueryRequest request = new RefundQueryRequest("REFUND123");
        
        assertThrows(UmsPayBusinessException.class, () -> {
            client.refundQuery(request);
        });
    }
    
    @Test
    void testH5PayRequestValidation() {
        assertThrows(IllegalArgumentException.class, () -> {
            H5PayRequest.builder()
                .totalAmount(100L)
                .build();
        });
        
        assertThrows(IllegalArgumentException.class, () -> {
            H5PayRequest.builder()
                .merOrderId("TEST123")
                .totalAmount(0L)
                .build();
        });
        
        assertThrows(IllegalArgumentException.class, () -> {
            H5PayRequest.builder()
                .merOrderId("TEST123")
                .totalAmount(-1L)
                .build();
        });
    }
    
    @Test
    void testRefundRequestValidation() {
        assertThrows(IllegalArgumentException.class, () -> {
            RefundRequest.builder()
                .refundAmount(50L)
                .build();
        });
        
        assertThrows(IllegalArgumentException.class, () -> {
            RefundRequest.builder()
                .merOrderId("TEST123")
                .refundAmount(0L)
                .build();
        });
        
        assertThrows(IllegalArgumentException.class, () -> {
            RefundRequest.builder()
                .merOrderId("TEST123")
                .refundAmount(-1L)
                .build();
        });
    }
    
    @Test
    void testQueryRequestValidation() {
        assertThrows(IllegalArgumentException.class, () -> {
            new QueryRequest(null);
        });
        
        assertThrows(IllegalArgumentException.class, () -> {
            new QueryRequest("");
        });
    }
    
    @Test
    void testRefundQueryRequestValidation() {
        assertThrows(IllegalArgumentException.class, () -> {
            new RefundQueryRequest(null);
        });
        
        assertThrows(IllegalArgumentException.class, () -> {
            new RefundQueryRequest("");
        });
    }
    
    /**
     * 测试用HttpClient实现
     */
    private static class TestHttpClient implements HttpClient {
        
        private final UmsPayConfig config;
        private final String baseUrl;
        private final SignatureUtil signatureUtil;
        
        TestHttpClient(UmsPayConfig config, String baseUrl) {
            this.config = config;
            this.baseUrl = baseUrl;
            this.signatureUtil = new SignatureUtil(config.getAppId(), config.getAppKey());
        }
        
        @Override
        public HttpResponse get(String url, String content, String signature) throws UmsPayException {
            // 替换URL中的baseUrl
            String actualUrl = url.replace(config.getEnvironment().getBaseUrl(), baseUrl);
            
            SignatureUtil.SignatureResult signResult;
            try {
                signResult = signatureUtil.sign(content);
            } catch (Exception e) {
                throw new UmsPayNetworkException("签名失败", e);
            }
            
            // 使用OkHttp发送请求
            okhttp3.OkHttpClient client = new okhttp3.OkHttpClient();
            okhttp3.HttpUrl httpUrl = okhttp3.HttpUrl.parse(actualUrl).newBuilder()
                .addQueryParameter("authorization", "OPEN-FORM-PARAM")
                .addQueryParameter("appId", config.getAppId())
                .addQueryParameter("timestamp", signResult.getTimestamp())
                .addQueryParameter("nonce", signResult.getNonce())
                .addQueryParameter("content", content)
                .addQueryParameter("signature", signResult.getSignature())
                .build();
            
            okhttp3.Request request = new okhttp3.Request.Builder()
                .url(httpUrl)
                .get()
                .build();
            
            try {
                okhttp3.Response response = client.newCall(request).execute();
                String body = response.body() != null ? response.body().string() : "";
                return new HttpResponse(response.code(), body);
            } catch (IOException e) {
                throw new UmsPayNetworkException("HTTP请求失败", e);
            }
        }
        
        @Override
        public HttpResponse post(String url, String content, String signature) throws UmsPayException {
            // 替换URL中的baseUrl
            String actualUrl = url.replace(config.getEnvironment().getBaseUrl(), baseUrl);
            
            SignatureUtil.SignatureResult signResult;
            try {
                signResult = signatureUtil.sign(content);
            } catch (Exception e) {
                throw new UmsPayNetworkException("签名失败", e);
            }
            
            String authorization = String.format(
                "OPEN-BODY-SIG AppId=\"%s\",Timestamp=\"%s\",Nonce=\"%s\",Signature=\"%s\"",
                config.getAppId(), signResult.getTimestamp(), signResult.getNonce(), signResult.getSignature()
            );
            
            // 使用OkHttp发送请求
            okhttp3.OkHttpClient client = new okhttp3.OkHttpClient();
            okhttp3.MediaType mediaType = okhttp3.MediaType.parse("application/json; charset=utf-8");
            okhttp3.RequestBody body = okhttp3.RequestBody.create(content, mediaType);
            okhttp3.Request request = new okhttp3.Request.Builder()
                .url(actualUrl)
                .post(body)
                .header("Authorization", authorization)
                .header("Content-Type", "application/json")
                .build();
            
            try {
                okhttp3.Response response = client.newCall(request).execute();
                String responseBody = response.body() != null ? response.body().string() : "";
                return new HttpResponse(response.code(), responseBody);
            } catch (IOException e) {
                throw new UmsPayNetworkException("HTTP请求失败", e);
            }
        }
    }
}
