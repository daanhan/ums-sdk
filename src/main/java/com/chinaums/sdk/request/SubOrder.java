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
import com.fasterxml.jackson.annotation.JsonInclude;
/**
 * 子订单信息
 * 
 * 用于分账支付场景下的子订单信息。
 * 
 * @author ChinaUMS SDK Team
 * @version 1.0.0
 * @since 1.0.0
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SubOrder {
    
    @JsonProperty("mid")
    private String mid;
    
    @JsonProperty("merOrderId")
    private String merOrderId;
    
    @JsonProperty("totalAmount")
    private Long totalAmount;
    
    /**
     * 默认构造函数
     */
    public SubOrder() {
    }
    
    /**
     * 构造函数
     * 
     * @param mid 商户号
     * @param merOrderId 商户订单号
     * @param totalAmount 订单金额（分）
     */
    public SubOrder(String mid, String merOrderId, Long totalAmount) {
        this.mid = mid;
        this.merOrderId = merOrderId;
        this.totalAmount = totalAmount;
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
     * 设置商户号
     * 
     * @param mid 商户号
     */
    public void setMid(String mid) {
        this.mid = mid;
    }
    
    /**
     * 获取商户订单号
     * 
     * @return 商户订单号
     */
    public String getMerOrderId() {
        return merOrderId;
    }
    
    /**
     * 设置商户订单号
     * 
     * @param merOrderId 商户订单号
     */
    public void setMerOrderId(String merOrderId) {
        this.merOrderId = merOrderId;
    }
    
    /**
     * 获取订单金额
     * 
     * @return 订单金额（分）
     */
    public Long getTotalAmount() {
        return totalAmount;
    }
    
    /**
     * 设置订单金额
     * 
     * @param totalAmount 订单金额（分）
     */
    public void setTotalAmount(Long totalAmount) {
        this.totalAmount = totalAmount;
    }
}
