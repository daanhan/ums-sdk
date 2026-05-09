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
 * 环境枚举
 * 
 * 定义银联商务支付API的环境类型，包括生产环境和测试环境。
 * 
 * @author ChinaUMS SDK Team
 * @version 1.0.0
 * @since 1.0.0
 */
public enum Environment {
    
    /**
     * 生产环境
     */
    PRODUCTION("https://api-mop.chinaums.com"),
    
    /**
     * 测试环境
     */
    TEST("https://test-api-open.chinaums.com");
    
    private final String baseUrl;
    
    /**
     * 构造函数
     * 
     * @param baseUrl API基础URL
     */
    Environment(String baseUrl) {
        this.baseUrl = baseUrl;
    }
    
    /**
     * 获取API基础URL
     * 
     * @return API基础URL
     */
    public String getBaseUrl() {
        return baseUrl;
    }
}
