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

import com.chinaums.sdk.config.UmsPayConfig;
import com.chinaums.sdk.exception.UmsPayAuthException;
import com.chinaums.sdk.exception.UmsPayNetworkException;
import com.chinaums.sdk.security.SignatureUtil;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * OkHttp执行器
 * 
 * 基于OkHttp实现的HTTP客户端，支持连接池、超时配置和自动重试。
 * 
 * @author ChinaUMS SDK Team
 * @version 1.0.0
 * @since 1.0.0
 */
public class OkHttpExecutor implements HttpClient {
    
    private static final Logger logger = LoggerFactory.getLogger(OkHttpExecutor.class);
    private static final MediaType JSON_MEDIA_TYPE = MediaType.parse("application/json; charset=utf-8");
    
    private final OkHttpClient httpClient;
    private final UmsPayConfig config;
    private final String appId;
    
    /**
     * 构造函数
     * 
     * @param config 配置对象
     */
    public OkHttpExecutor(UmsPayConfig config) {
        this.config = config;
        this.appId = config.getAppId();
        this.httpClient = buildHttpClient();
    }
    
    /**
     * 构建OkHttp客户端
     * 
     * @return OkHttpClient实例
     */
    private OkHttpClient buildHttpClient() {
        return new OkHttpClient.Builder()
            .connectTimeout(config.getConnectTimeout(), TimeUnit.MILLISECONDS)
            .readTimeout(config.getReadTimeout(), TimeUnit.MILLISECONDS)
            .writeTimeout(config.getReadTimeout(), TimeUnit.MILLISECONDS)
            .retryOnConnectionFailure(true)
            .build();
    }
    
    @Override
    public HttpResponse get(String url, String content, String signature) throws UmsPayNetworkException {
        try {
            SignatureUtil signatureUtil = new SignatureUtil(appId, config.getAppKey());
            SignatureUtil.SignatureResult signResult = signatureUtil.sign(content);
            
            HttpUrl httpUrl = HttpUrl.parse(url).newBuilder()
                .addQueryParameter("authorization", "OPEN-FORM-PARAM")
                .addQueryParameter("appId", appId)
                .addQueryParameter("timestamp", signResult.getTimestamp())
                .addQueryParameter("nonce", signResult.getNonce())
                .addQueryParameter("content", content)
                .addQueryParameter("signature", signResult.getSignature())
                .build();
            
            Request request = new Request.Builder()
                .url(httpUrl)
                .get()
                .build();
            
            return executeRequest(request);
        } catch (UmsPayAuthException e) {
            throw new UmsPayNetworkException("签名失败: " + e.getMessage(), e);
        }
    }
    
    @Override
    public HttpResponse post(String url, String content, String signature) throws UmsPayNetworkException {
        try {
            SignatureUtil signatureUtil = new SignatureUtil(appId, config.getAppKey());
            SignatureUtil.SignatureResult signResult = signatureUtil.sign(content);
            
            String authorization = String.format(
                "OPEN-BODY-SIG AppId=\"%s\",Timestamp=\"%s\",Nonce=\"%s\",Signature=\"%s\"",
                appId, signResult.getTimestamp(), signResult.getNonce(), signResult.getSignature()
            );
            
            RequestBody body = RequestBody.create(content, JSON_MEDIA_TYPE);
            Request request = new Request.Builder()
                .url(url)
                .post(body)
                .header("Authorization", authorization)
                .header("Content-Type", "application/json")
                .build();
            
            return executeRequest(request);
        } catch (UmsPayAuthException e) {
            throw new UmsPayNetworkException("签名失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 执行HTTP请求
     * 
     * @param request OkHttp请求对象
     * @return HTTP响应
     * @throws UmsPayNetworkException 请求失败时抛出
     */
    private HttpResponse executeRequest(Request request) throws UmsPayNetworkException {
        try {
            logger.debug("发送HTTP请求: {} {}", request.method(), request.url());
            
            Response response = httpClient.newCall(request).execute();
            ResponseBody responseBody = response.body();
            String bodyString = responseBody != null ? responseBody.string() : "";
            
            Map<String, String> headers = new HashMap<>();
            for (String name : response.headers().names()) {
                headers.put(name, response.header(name));
            }
            
            logger.debug("收到HTTP响应: {} {}", response.code(), bodyString);
            
            return new HttpResponse(response.code(), bodyString, headers);
        } catch (IOException e) {
            logger.error("HTTP请求失败: {}", e.getMessage(), e);
            throw new UmsPayNetworkException("HTTP请求失败: " + e.getMessage(), e);
        }
    }
}
