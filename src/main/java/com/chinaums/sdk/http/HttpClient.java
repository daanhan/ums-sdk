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

import com.chinaums.sdk.exception.UmsPayException;

/**
 * HTTP客户端接口
 * 
 * 定义HTTP请求的标准方法，支持GET和POST请求。
 * 
 * @author ChinaUMS SDK Team
 * @version 1.0.0
 * @since 1.0.0
 */
public interface HttpClient {
    
    /**
     * 发送GET请求
     * 
     * @param url 请求URL
     * @param content 请求内容
     * @param signature 签名（可选）
     * @return HTTP响应
     * @throws UmsPayException 请求失败时抛出
     */
    HttpResponse get(String url, String content, String signature) throws UmsPayException;
    
    /**
     * 发送POST请求
     * 
     * @param url 请求URL
     * @param content 请求内容
     * @param signature 签名（可选）
     * @return HTTP响应
     * @throws UmsPayException 请求失败时抛出
     */
    HttpResponse post(String url, String content, String signature) throws UmsPayException;
}
