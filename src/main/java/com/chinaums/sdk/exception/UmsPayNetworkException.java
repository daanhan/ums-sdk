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

/**
 * 银联商务支付网络异常
 * 
 * 当网络请求失败（如连接超时、网络不可达、HTTP错误等）时抛出此异常。
 * 
 * @author ChinaUMS SDK Team
 * @version 1.0.0
 * @since 1.0.0
 */
public class UmsPayNetworkException extends UmsPayException {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 构造函数
     * 
     * @param message 错误消息
     */
    public UmsPayNetworkException(String message) {
        super("NETWORK_ERROR", message);
    }
    
    /**
     * 构造函数（带原因异常）
     * 
     * @param message 错误消息
     * @param cause 原因异常
     */
    public UmsPayNetworkException(String message, Throwable cause) {
        super("NETWORK_ERROR", message, cause);
    }
}
