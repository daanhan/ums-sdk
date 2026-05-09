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

import com.chinaums.sdk.config.UmsPayConfig;
import com.chinaums.sdk.exception.UmsPayAuthException;
import com.chinaums.sdk.exception.UmsPayBusinessException;
import com.chinaums.sdk.exception.UmsPayException;
import com.chinaums.sdk.exception.UmsPayNetworkException;
import com.chinaums.sdk.http.HttpClient;
import com.chinaums.sdk.http.HttpResponse;
import com.chinaums.sdk.http.OkHttpExecutor;
import com.chinaums.sdk.request.*;
import com.chinaums.sdk.response.*;
import com.chinaums.sdk.security.NotificationVerifier;
import com.chinaums.sdk.security.SignatureUtil;
import com.chinaums.sdk.util.DateUtil;
import com.chinaums.sdk.util.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.Map;

/**
 * 银联商务支付客户端
 * 
 * 提供统一的支付接口调用入口，支持H5支付、微信小程序支付、订单查询、退款、退款查询等功能。
 * 
 * 快速开始：
 * <pre>{@code
 * UmsPayConfig config = UmsPayConfig.builder()
 *     .appId("your-app-id")
 *     .appKey("your-app-key")
 *     .mid("898201612345678")
 *     .tid("88880001")
 *     .build();
 * 
 * UmsPayClient client = new UmsPayClient(config);
 * }</pre>
 * 
 * @author ChinaUMS SDK Team
 * @version 1.0.0
 * @since 1.0.0
 */
public class UmsPayClient {
    
    private static final Logger logger = LoggerFactory.getLogger(UmsPayClient.class);
    
    private final UmsPayConfig config;
    private final HttpClient httpClient;
    
    /**
     * 构造函数
     * 
     * @param config 配置对象
     */
    public UmsPayClient(UmsPayConfig config) {
        this.config = config;
        this.httpClient = new OkHttpExecutor(config);
    }
    
    /**
     * 构造函数（用于测试）
     * 
     * @param config 配置对象
     * @param httpClient HTTP客户端
     */
    UmsPayClient(UmsPayConfig config, HttpClient httpClient) {
        this.config = config;
        this.httpClient = httpClient;
    }
    
    /**
     * H5支付下单
     * 
     * 发起微信H5支付请求，返回支付URL供商户引导用户完成支付。
     * 
     * @param request H5支付请求对象，包含订单信息
     * @return H5支付响应对象，包含支付URL
     * @throws UmsPayException 支付异常
     */
    public H5PayResponse h5Pay(H5PayRequest request) throws UmsPayException {
        String url = config.getEnvironment().getBaseUrl() + "/v1/netpay/wxpay/h5-pay";
        String requestBody = buildRequestBody(request);
        
        logger.info("发起H5支付请求: {}", request.getMerOrderId());
        
        HttpResponse response = httpClient.get(url, requestBody, null);
        return parseResponse(response, H5PayResponse.class);
    }
    
    /**
     * 微信小程序支付下单
     *
     * 发起微信小程序支付请求，返回包含miniPayRequest的响应，
     * 商户需将miniPayRequest中的参数传递给前端调用wx.requestPayment完成支付。
     *
     * @param request 小程序支付请求对象，包含订单信息和用户标识
     * @return 小程序支付响应对象，包含支付调起参数
     * @throws UmsPayException 支付异常
     */
    public MiniProgramPayResponse miniProgramPay(MiniProgramPayRequest request) throws UmsPayException {
        String url = config.getEnvironment().getBaseUrl() + "/v1/netpay/wx/unified-order";
        String requestBody = buildRequestBody(request);
        
        logger.info("发起微信小程序支付请求: {}", request.getMerOrderId());
        
        HttpResponse response = httpClient.post(url, requestBody, null);
        return parseResponse(response, MiniProgramPayResponse.class);
    }
    
    /**
     * 订单查询
     * 
     * 查询订单交易状态和详细信息。
     * 
     * @param request 订单查询请求对象
     * @return 订单查询响应对象
     * @throws UmsPayException 查询异常
     */
    public QueryResponse query(QueryRequest request) throws UmsPayException {
        String url = config.getEnvironment().getBaseUrl() + "/v1/netpay/query";
        String requestBody = buildRequestBody(request);
        
        logger.info("发起订单查询请求: {}", request.getMerOrderId());
        
        HttpResponse response = httpClient.post(url, requestBody, null);
        return parseResponse(response, QueryResponse.class);
    }
    
    /**
     * 退款
     * 
     * 发起退款请求，支持全额退款和部分退款。
     * 
     * @param request 退款请求对象
     * @return 退款响应对象
     * @throws UmsPayException 退款异常
     */
    public RefundResponse refund(RefundRequest request) throws UmsPayException {
        String url = config.getEnvironment().getBaseUrl() + "/v1/netpay/refund";
        String requestBody = buildRequestBody(request);
        
        logger.info("发起退款请求: {}", request.getMerOrderId());
        
        HttpResponse response = httpClient.post(url, requestBody, null);
        return parseResponse(response, RefundResponse.class);
    }
    
    /**
     * 退款查询
     * 
     * 查询退款订单状态和详细信息。
     * 
     * @param request 退款查询请求对象
     * @return 退款查询响应对象
     * @throws UmsPayException 查询异常
     */
    public RefundQueryResponse refundQuery(RefundQueryRequest request) throws UmsPayException {
        String url = config.getEnvironment().getBaseUrl() + "/v1/netpay/refund-query";
        String requestBody = buildRequestBody(request);
        
        logger.info("发起退款查询请求: {}", request.getMerOrderId());
        
        HttpResponse response = httpClient.post(url, requestBody, null);
        return parseResponse(response, RefundQueryResponse.class);
    }
    
    /**
     * 构建请求体
     * 
     * @param request 请求对象
     * @return JSON格式的请求体
     */
    private String buildRequestBody(BaseRequest request) {
        request.setMid(config.getMid());
        request.setTid(config.getTid());
        request.setInstMid(config.getInstMid());
        request.setRequestTimestamp(DateUtil.formatDateTime(new Date()));
        return JsonUtil.toJson(request);
    }
    
    /**
     * 解析响应
     * 
     * @param response HTTP响应
     * @param responseClass 响应类
     * @return 响应对象
     * @throws UmsPayException 解析异常
     */
    private <T extends BaseResponse> T parseResponse(HttpResponse response, Class<T> responseClass) 
            throws UmsPayException {
        if (!response.isSuccess()) {
            throw new UmsPayNetworkException("HTTP请求失败: " + response.getCode());
        }
        
        verifyResponseSignature(response);
        
        T result = JsonUtil.fromJson(response.getBody(), responseClass);
        if (!result.isSuccess()) {
            throw new UmsPayBusinessException(result.getErrCode(), result.getErrMsg());
        }
        
        return result;
    }
    
    /**
     * 验证服务器应答签名
     * 
     * @param response HTTP响应
     * @throws UmsPayAuthException 签名验证失败时抛出
     */
    private void verifyResponseSignature(HttpResponse response) throws UmsPayAuthException {
        String authorization = response.getHeader("Authorization");
        if (authorization == null || authorization.isEmpty()) {
            return;
        }
        
        boolean verified = SignatureUtil.verifyResponseSignature(
            authorization, response.getBody(), config.getAppKey());
        
        if (!verified) {
            throw new UmsPayAuthException("应答签名验证失败");
        }
        
        logger.debug("应答签名验证通过");
    }
    
    /**
     * 验证支付通知签名
     * 
     * @param params 通知参数Map
     * @return 验证通过返回true，否则返回false
     * @throws UmsPayException 验证异常
     */
    public boolean verifyNotification(Map<String, String> params) throws UmsPayException {
        String notifyKey = config.getNotifyKey();
        if (notifyKey == null || notifyKey.isEmpty()) {
            throw new UmsPayAuthException("通讯密钥(notifyKey)未配置");
        }
        return NotificationVerifier.verify(params, notifyKey);
    }
}
