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

import java.util.UUID;

/**
 * 银联商务支付基础异常类
 * 
 * 所有支付相关的异常都继承自此类，提供统一的异常处理机制。
 * 包含错误码、请求ID和时间戳等信息，便于问题追踪和调试。
 * 
 * @author ChinaUMS SDK Team
 * @version 1.0.0
 * @since 1.0.0
 */
public class UmsPayException extends Exception {
    
    private static final long serialVersionUID = 1L;
    
    private final String errorCode;
    private final String requestId;
    private final long timestamp;
    
    /**
     * 构造函数
     * 
     * @param errorCode 错误码
     * @param message 错误消息
     */
    public UmsPayException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
        this.requestId = UUID.randomUUID().toString();
        this.timestamp = System.currentTimeMillis();
    }
    
    /**
     * 构造函数（带原因异常）
     * 
     * @param errorCode 错误码
     * @param message 错误消息
     * @param cause 原因异常
     */
    public UmsPayException(String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.requestId = UUID.randomUUID().toString();
        this.timestamp = System.currentTimeMillis();
    }
    
    /**
     * 获取错误码
     * 
     * @return 错误码
     */
    public String getErrorCode() {
        return errorCode;
    }
    
    /**
     * 获取请求ID
     * 
     * @return 请求ID
     */
    public String getRequestId() {
        return requestId;
    }
    
    /**
     * 获取时间戳
     * 
     * @return 时间戳（毫秒）
     */
    public long getTimestamp() {
        return timestamp;
    }
    
    @Override
    public String toString() {
        return "UmsPayException{" +
                "errorCode='" + errorCode + '\'' +
                ", requestId='" + requestId + '\'' +
                ", timestamp=" + timestamp +
                ", message='" + getMessage() + '\'' +
                '}';
    }
}
