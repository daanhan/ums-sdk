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

/**
 * 银联商务支付配置类
 * 
 * 使用Builder模式构建配置对象，支持链式调用。
 * 包含应用ID、密钥、商户号、终端号等必要配置信息。
 * 
 * 快速开始：
 * <pre>{@code
 * UmsPayConfig config = UmsPayConfig.builder()
 *     .appId("your-app-id")
 *     .appKey("your-app-key")
 *     .mid("898201612345678")
 *     .tid("88880001")
 *     .build();
 * }</pre>
 * 
 * @author ChinaUMS SDK Team
 * @version 1.0.0
 * @since 1.0.0
 */
public class UmsPayConfig {
    
    private final String appId;
    private final String appKey;
    private final String mid;
    private final String tid;
    private final String instMid;
    private final Environment environment;
    private final int connectTimeout;
    private final int readTimeout;
    private final int maxRetries;
    private final boolean enableCertValidation;
    private final String notifyKey;
    
    /**
     * 私有构造函数，通过Builder创建实例
     * 
     * @param builder Builder对象
     */
    private UmsPayConfig(Builder builder) {
        this.appId = builder.appId;
        this.appKey = builder.appKey;
        this.mid = builder.mid;
        this.tid = builder.tid;
        this.instMid = builder.instMid;
        this.environment = builder.environment;
        this.connectTimeout = builder.connectTimeout;
        this.readTimeout = builder.readTimeout;
        this.maxRetries = builder.maxRetries;
        this.enableCertValidation = builder.enableCertValidation;
        this.notifyKey = builder.notifyKey;
    }
    
    /**
     * 创建Builder实例
     * 
     * @return Builder对象
     */
    public static Builder builder() {
        return new Builder();
    }
    
    /**
     * 获取应用ID
     * 
     * @return 应用ID
     */
    public String getAppId() {
        return appId;
    }
    
    /**
     * 获取应用密钥
     * 
     * @return 应用密钥
     */
    public String getAppKey() {
        return appKey;
    }
    
    /**
     * 获取商户号
     * 
     * @return 商户号
     */
    public String getMid() {
        return mid;
    }
    
    /**
     * 获取终端号
     * 
     * @return 终端号
     */
    public String getTid() {
        return tid;
    }
    
    /**
     * 获取机构商户号
     * 
     * @return 机构商户号
     */
    public String getInstMid() {
        return instMid;
    }
    
    /**
     * 获取环境配置
     * 
     * @return 环境枚举
     */
    public Environment getEnvironment() {
        return environment;
    }
    
    /**
     * 获取连接超时时间（毫秒）
     * 
     * @return 连接超时时间
     */
    public int getConnectTimeout() {
        return connectTimeout;
    }
    
    /**
     * 获取读取超时时间（毫秒）
     * 
     * @return 读取超时时间
     */
    public int getReadTimeout() {
        return readTimeout;
    }
    
    /**
     * 获取最大重试次数
     * 
     * @return 最大重试次数
     */
    public int getMaxRetries() {
        return maxRetries;
    }
    
    /**
     * 是否启用证书验证
     * 
     * @return true表示启用，false表示不启用
     */
    public boolean isEnableCertValidation() {
        return enableCertValidation;
    }
    
    /**
     * 获取通讯密钥（用于通知验签）
     * 
     * @return 通讯密钥，可能为null
     */
    public String getNotifyKey() {
        return notifyKey;
    }
    
    /**
     * Builder类
     */
    public static class Builder {
        private String appId;
        private String appKey;
        private String mid;
        private String tid;
        private String instMid = "H5DEFAULT";
        private Environment environment = Environment.PRODUCTION;
        private int connectTimeout = 10000;
        private int readTimeout = 30000;
        private int maxRetries = 3;
        private boolean enableCertValidation = true;
        private String notifyKey;
        
        /**
         * 设置应用ID
         * 
         * @param appId 应用ID
         * @return Builder对象
         */
        public Builder appId(String appId) {
            this.appId = appId;
            return this;
        }
        
        /**
         * 设置应用密钥
         * 
         * @param appKey 应用密钥
         * @return Builder对象
         */
        public Builder appKey(String appKey) {
            this.appKey = appKey;
            return this;
        }
        
        /**
         * 设置商户号
         * 
         * @param mid 商户号
         * @return Builder对象
         */
        public Builder mid(String mid) {
            this.mid = mid;
            return this;
        }
        
        /**
         * 设置终端号
         * 
         * @param tid 终端号
         * @return Builder对象
         */
        public Builder tid(String tid) {
            this.tid = tid;
            return this;
        }
        
        /**
         * 设置机构商户号
         * 
         * @param instMid 机构商户号
         * @return Builder对象
         */
        public Builder instMid(String instMid) {
            this.instMid = instMid;
            return this;
        }
        
        /**
         * 设置环境
         * 
         * @param environment 环境枚举
         * @return Builder对象
         */
        public Builder environment(Environment environment) {
            this.environment = environment;
            return this;
        }
        
        /**
         * 设置连接超时时间
         * 
         * @param connectTimeout 连接超时时间（毫秒）
         * @return Builder对象
         */
        public Builder connectTimeout(int connectTimeout) {
            this.connectTimeout = connectTimeout;
            return this;
        }
        
        /**
         * 设置读取超时时间
         * 
         * @param readTimeout 读取超时时间（毫秒）
         * @return Builder对象
         */
        public Builder readTimeout(int readTimeout) {
            this.readTimeout = readTimeout;
            return this;
        }
        
        /**
         * 设置最大重试次数
         * 
         * @param maxRetries 最大重试次数
         * @return Builder对象
         */
        public Builder maxRetries(int maxRetries) {
            this.maxRetries = maxRetries;
            return this;
        }
        
        /**
         * 设置是否启用证书验证
         * 
         * @param enableCertValidation true表示启用，false表示不启用
         * @return Builder对象
         */
        public Builder enableCertValidation(boolean enableCertValidation) {
            this.enableCertValidation = enableCertValidation;
            return this;
        }
        
        /**
         * 设置通讯密钥（用于通知验签）
         * 
         * @param notifyKey 通讯密钥
         * @return Builder对象
         */
        public Builder notifyKey(String notifyKey) {
            this.notifyKey = notifyKey;
            return this;
        }
        
        /**
         * 构建配置对象
         * 
         * @return 配置对象
         * @throws IllegalArgumentException 如果必填参数为空
         */
        public UmsPayConfig build() {
            validate();
            return new UmsPayConfig(this);
        }
        
        /**
         * 验证必填参数
         * 
         * @throws IllegalArgumentException 如果必填参数为空
         */
        private void validate() {
            if (appId == null || appId.isEmpty()) {
                throw new IllegalArgumentException("appId不能为空");
            }
            if (appKey == null || appKey.isEmpty()) {
                throw new IllegalArgumentException("appKey不能为空");
            }
            if (mid == null || mid.isEmpty()) {
                throw new IllegalArgumentException("mid不能为空");
            }
            if (tid == null || tid.isEmpty()) {
                throw new IllegalArgumentException("tid不能为空");
            }
        }
    }
}
