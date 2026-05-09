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


package com.chinaums.sdk.request;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 订单查询请求
 * 
 * 用于查询订单交易状态和详细信息。
 * 
 * @author ChinaUMS SDK Team
 * @version 1.0.0
 * @since 1.0.0
 */
public class QueryRequest extends BaseRequest {
    
    @JsonProperty("merOrderId")
    private String merOrderId;
    
    /**
     * 构造函数
     * 
     * @param merOrderId 商户订单号
     * @throws IllegalArgumentException 如果商户订单号为空
     */
    public QueryRequest(String merOrderId) {
        if (merOrderId == null || merOrderId.isEmpty()) {
            throw new IllegalArgumentException("商户订单号不能为空");
        }
        this.merOrderId = merOrderId;
    }
    
    /**
     * 获取商户订单号
     * 
     * @return 商户订单号
     */
    public String getMerOrderId() {
        return merOrderId;
    }
}
