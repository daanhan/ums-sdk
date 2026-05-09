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
import com.chinaums.sdk.request.MiniProgramPayRequest;
import com.chinaums.sdk.request.QueryRequest;
import com.chinaums.sdk.response.MiniPayRequest;
import com.chinaums.sdk.response.MiniProgramPayResponse;
import com.chinaums.sdk.response.QueryResponse;
import com.chinaums.sdk.security.SignatureUtil;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 微信小程序支付功能测试类
 */
class MiniProgramPayTest {

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
            .notifyKey("test-notify-key")
            .environment(Environment.TEST)
            .build();

        HttpClient httpClient = new TestHttpClient(config, baseUrl);
        client = new UmsPayClient(config, httpClient);
    }

    @AfterEach
    void tearDown() throws IOException {
        server.shutdown();
    }

    @Test
    void testMiniProgramPaySuccess() throws Exception {
        String mockResponse = "{"
            + "\"errCode\":\"SUCCESS\","
            + "\"errMsg\":\"微信下单\","
            + "\"merOrderId\":\"327M20220419111222\","
            + "\"seqId\":\"20230719162959\","
            + "\"settleRefId\":\"20230719162959\","
            + "\"status\":\"WAIT_BUYER_PAY\","
            + "\"totalAmount\":500,"
            + "\"targetOrderId\":\"64342023071922595938541723006782\","
            + "\"targetSys\":\"WXPay\","
            + "\"targetStatus\":\"SUCCESS\","
            + "\"miniPayRequest\":{"
            + "\"appId\":\"wx1234567890abcdef\","
            + "\"timeStamp\":\"1687285800\","
            + "\"nonceStr\":\"abc123\","
            + "\"package\":\"prepay_id=wx201410272009395522657a690389285100\","
            + "\"signType\":\"RSA\","
            + "\"paySign\":\"sign123456\""
            + "},"
            + "\"targetMid\":\"\","
            + "\"merName\":\"测试商户\""
            + "}";

        server.enqueue(new MockResponse()
            .setBody(mockResponse)
            .setResponseCode(200));

        MiniProgramPayRequest request = MiniProgramPayRequest.builder()
            .merOrderId("327M20220419111222")
            .totalAmount(500L)
            .subAppId("wx1234567890abcdef")
            .subOpenId("o1234567890abcdef")
            .notifyUrl("https://example.com/notify")
            .build();

        MiniProgramPayResponse response = client.miniProgramPay(request);

        assertTrue(response.isSuccess());
        assertEquals("327M20220419111222", response.getMerOrderId());
        assertEquals("WAIT_BUYER_PAY", response.getStatus());
        assertEquals(500L, response.getTotalAmount());
        assertEquals("64342023071922595938541723006782", response.getTargetOrderId());
        assertEquals("WXPay", response.getTargetSys());
        assertEquals("SUCCESS", response.getTargetStatus());
        assertEquals("20230719162959", response.getSeqId());
        assertEquals("20230719162959", response.getSettleRefId());
        assertEquals("测试商户", response.getMerName());

        assertNotNull(response.getMiniPayRequest());
        MiniPayRequest payRequest = response.getMiniPayRequest();
        assertEquals("wx1234567890abcdef", payRequest.getAppId());
        assertEquals("1687285800", payRequest.getTimeStamp());
        assertEquals("abc123", payRequest.getNonceStr());
        assertEquals("prepay_id=wx201410272009395522657a690389285100", payRequest.getPackageValue());
        assertEquals("RSA", payRequest.getSignType());
        assertEquals("sign123456", payRequest.getPaySign());
    }

    @Test
    void testMiniProgramPaySuccessWithYxlmAmount() throws Exception {
        String mockResponse = "{"
            + "\"errCode\":\"SUCCESS\","
            + "\"errMsg\":\"微信下单\","
            + "\"merOrderId\":\"327M20220419111222\","
            + "\"status\":\"WAIT_BUYER_PAY\","
            + "\"totalAmount\":500,"
            + "\"yxlmAmount\":100,"
            + "\"miniPayRequest\":{"
            + "\"appId\":\"wx1234567890abcdef\","
            + "\"timeStamp\":\"1687285800\","
            + "\"nonceStr\":\"abc123\","
            + "\"package\":\"prepay_id=wx201410272009395522657a690389285100\","
            + "\"signType\":\"RSA\","
            + "\"paySign\":\"sign123456\""
            + "}"
            + "}";

        server.enqueue(new MockResponse()
            .setBody(mockResponse)
            .setResponseCode(200));

        MiniProgramPayRequest request = MiniProgramPayRequest.builder()
            .merOrderId("327M20220419111222")
            .totalAmount(500L)
            .subAppId("wx1234567890abcdef")
            .subOpenId("o1234567890abcdef")
            .build();

        MiniProgramPayResponse response = client.miniProgramPay(request);

        assertTrue(response.isSuccess());
        assertEquals(100L, response.getYxlmAmount());
    }

    @Test
    void testMiniProgramPayBusinessError() throws Exception {
        String mockResponse = "{\"errCode\":\"FAIL\",\"errMsg\":\"下单失败\"}";
        server.enqueue(new MockResponse()
            .setBody(mockResponse)
            .setResponseCode(200));

        MiniProgramPayRequest request = MiniProgramPayRequest.builder()
            .merOrderId("327M20220419111222")
            .totalAmount(500L)
            .subAppId("wx1234567890abcdef")
            .subOpenId("o1234567890abcdef")
            .build();

        assertThrows(UmsPayBusinessException.class, () -> {
            client.miniProgramPay(request);
        });
    }

    @Test
    void testMiniProgramPayNetworkError() throws Exception {
        server.enqueue(new MockResponse()
            .setResponseCode(500));

        MiniProgramPayRequest request = MiniProgramPayRequest.builder()
            .merOrderId("327M20220419111222")
            .totalAmount(500L)
            .subAppId("wx1234567890abcdef")
            .subOpenId("o1234567890abcdef")
            .build();

        assertThrows(UmsPayNetworkException.class, () -> {
            client.miniProgramPay(request);
        });
    }

    @Test
    void testMiniProgramPayRequestValidationMissingMerOrderId() {
        assertThrows(IllegalArgumentException.class, () -> {
            MiniProgramPayRequest.builder()
                .totalAmount(500L)
                .subAppId("wx1234567890abcdef")
                .subOpenId("o1234567890abcdef")
                .build();
        });
    }

    @Test
    void testMiniProgramPayRequestValidationInvalidTotalAmount() {
        assertThrows(IllegalArgumentException.class, () -> {
            MiniProgramPayRequest.builder()
                .merOrderId("327M20220419111222")
                .totalAmount(0L)
                .subAppId("wx1234567890abcdef")
                .subOpenId("o1234567890abcdef")
                .build();
        });

        assertThrows(IllegalArgumentException.class, () -> {
            MiniProgramPayRequest.builder()
                .merOrderId("327M20220419111222")
                .totalAmount(-1L)
                .subAppId("wx1234567890abcdef")
                .subOpenId("o1234567890abcdef")
                .build();
        });
    }

    @Test
    void testMiniProgramPayRequestValidationMissingSubAppId() {
        assertThrows(IllegalArgumentException.class, () -> {
            MiniProgramPayRequest.builder()
                .merOrderId("327M20220419111222")
                .totalAmount(500L)
                .subOpenId("o1234567890abcdef")
                .build();
        });
    }

    @Test
    void testMiniProgramPayRequestValidationMissingSubOpenId() {
        assertThrows(IllegalArgumentException.class, () -> {
            MiniProgramPayRequest.builder()
                .merOrderId("327M20220419111222")
                .totalAmount(500L)
                .subAppId("wx1234567890abcdef")
                .build();
        });
    }

    @Test
    void testMiniProgramPayRequestTradeTypeIsMini() {
        MiniProgramPayRequest request = MiniProgramPayRequest.builder()
            .merOrderId("327M20220419111222")
            .totalAmount(500L)
            .subAppId("wx1234567890abcdef")
            .subOpenId("o1234567890abcdef")
            .build();

        assertEquals("MINI", request.getTradeType());
    }

    @Test
    void testMiniProgramPayRequestDefaultInstMid() {
        MiniProgramPayRequest request = MiniProgramPayRequest.builder()
            .merOrderId("327M20220419111222")
            .totalAmount(500L)
            .subAppId("wx1234567890abcdef")
            .subOpenId("o1234567890abcdef")
            .build();

        assertEquals("MINIDEFAULT", request.getInstMid());
    }

    @Test
    void testMiniProgramPayWithOptionalFields() throws Exception {
        String mockResponse = "{"
            + "\"errCode\":\"SUCCESS\","
            + "\"errMsg\":\"微信下单\","
            + "\"merOrderId\":\"327M20220419111222\","
            + "\"status\":\"WAIT_BUYER_PAY\","
            + "\"totalAmount\":500,"
            + "\"miniPayRequest\":{"
            + "\"appId\":\"wx1234567890abcdef\","
            + "\"timeStamp\":\"1687285800\","
            + "\"nonceStr\":\"abc123\","
            + "\"package\":\"prepay_id=wx201410272009395522657a690389285100\","
            + "\"signType\":\"RSA\","
            + "\"paySign\":\"sign123456\""
            + "}"
            + "}";

        server.enqueue(new MockResponse()
            .setBody(mockResponse)
            .setResponseCode(200));

        MiniProgramPayRequest request = MiniProgramPayRequest.builder()
            .merOrderId("327M20220419111222")
            .totalAmount(500L)
            .subAppId("wx1234567890abcdef")
            .subOpenId("o1234567890abcdef")
            .notifyUrl("https://example.com/notify")
            .expireTime("2022-04-22 10:10:13")
            .orderDesc("测试订单")
            .originalAmount(1000L)
            .limitCreditCard(false)
            .clientIp("1.1.1.1")
            .build();

        MiniProgramPayResponse response = client.miniProgramPay(request);

        assertTrue(response.isSuccess());
    }

    @Test
    void testPaymentStatusQuery() throws Exception {
        String mockResponse = "{"
            + "\"errCode\":\"SUCCESS\","
            + "\"status\":\"TRADE_SUCCESS\","
            + "\"totalAmount\":500,"
            + "\"merOrderId\":\"327M20220419111222\","
            + "\"targetOrderId\":\"64342023071922595938541723006782\","
            + "\"targetSys\":\"WXPay\""
            + "}";

        server.enqueue(new MockResponse()
            .setBody(mockResponse)
            .setResponseCode(200));

        QueryRequest request = new QueryRequest("327M20220419111222");
        QueryResponse response = client.query(request);

        assertTrue(response.isSuccess());
        assertEquals("TRADE_SUCCESS", response.getStatus());
        assertEquals(500L, response.getTotalAmount());
        assertEquals("327M20220419111222", response.getMerOrderId());
    }

    @Test
    void testPaymentNotificationVerification() throws Exception {
        Map<String, String> params = new HashMap<>();
        params.put("status", "TRADE_SUCCESS");
        params.put("merOrderId", "327M20220419111222");
        params.put("totalAmount", "500");
        params.put("targetOrderId", "64342023071922595938541723006782");
        params.put("signType", "SHA256");

        String signStr = "merOrderId=327M20220419111222"
            + "&status=TRADE_SUCCESS"
            + "&targetOrderId=64342023071922595938541723006782"
            + "&totalAmount=500"
            + "test-notify-key";

        java.security.MessageDigest digest = java.security.MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(signStr.getBytes(java.nio.charset.StandardCharsets.UTF_8));
        StringBuilder hexString = new StringBuilder();
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        params.put("sign", hexString.toString());

        boolean verified = client.verifyNotification(params);
        assertTrue(verified);
    }

    @Test
    void testPaymentNotificationVerificationFailed() throws Exception {
        Map<String, String> params = new HashMap<>();
        params.put("status", "TRADE_SUCCESS");
        params.put("merOrderId", "327M20220419111222");
        params.put("totalAmount", "500");
        params.put("sign", "invalid-sign");
        params.put("signType", "SHA256");

        boolean verified = client.verifyNotification(params);
        assertFalse(verified);
    }

    @Test
    void testMiniPayRequestDeserialization() throws Exception {
        String mockResponse = "{"
            + "\"errCode\":\"SUCCESS\","
            + "\"errMsg\":\"微信下单\","
            + "\"merOrderId\":\"327M20220419111222\","
            + "\"status\":\"WAIT_BUYER_PAY\","
            + "\"totalAmount\":1,"
            + "\"miniPayRequest\":{"
            + "\"appId\":\"wx8888888888888888\","
            + "\"timeStamp\":\"1650430213\","
            + "\"nonceStr\":\"5K8264ILTKCH16CQ2502SI8ZNMTM67VS\","
            + "\"package\":\"prepay_id=wx021410272009395522657a690389285100\","
            + "\"signType\":\"RSA\","
            + "\"paySign\":\"C3804EC3BD4E6FEB2C9B6C0F9D3E8F1A2B3C4D5E6F7A8B9C0D1E2F3A4B5C6D7\""
            + "}"
            + "}";

        server.enqueue(new MockResponse()
            .setBody(mockResponse)
            .setResponseCode(200));

        MiniProgramPayRequest request = MiniProgramPayRequest.builder()
            .merOrderId("327M20220419111222")
            .totalAmount(1L)
            .subAppId("wx8888888888888888")
            .subOpenId("oABC123456789")
            .build();

        MiniProgramPayResponse response = client.miniProgramPay(request);

        MiniPayRequest payRequest = response.getMiniPayRequest();
        assertNotNull(payRequest);
        assertEquals("wx8888888888888888", payRequest.getAppId());
        assertEquals("1650430213", payRequest.getTimeStamp());
        assertEquals("5K8264ILTKCH16CQ2502SI8ZNMTM67VS", payRequest.getNonceStr());
        assertEquals("prepay_id=wx021410272009395522657a690389285100", payRequest.getPackageValue());
        assertEquals("RSA", payRequest.getSignType());
        assertEquals("C3804EC3BD4E6FEB2C9B6C0F9D3E8F1A2B3C4D5E6F7A8B9C0D1E2F3A4B5C6D7", payRequest.getPaySign());
    }

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
            String actualUrl = url.replace(config.getEnvironment().getBaseUrl(), baseUrl);

            SignatureUtil.SignatureResult signResult;
            try {
                signResult = signatureUtil.sign(content);
            } catch (Exception e) {
                throw new UmsPayNetworkException("签名失败", e);
            }

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
