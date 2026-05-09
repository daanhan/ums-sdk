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

import java.util.List;

/**
 * 退款请求
 * 
 * 用于发起退款请求，支持全额退款和部分退款。
 * 
 * 使用示例：
 * <pre>{@code
 * RefundRequest request = RefundRequest.builder()
 *     .merOrderId("ORDER123456")
 *     .refundOrderId("REFUND123456")
 *     .refundAmount(50L)
 *     .build();
 * }</pre>
 * 
 * @author ChinaUMS SDK Team
 * @version 1.0.0
 * @since 1.0.0
 */
public class RefundRequest extends BaseRequest {
    
    @JsonProperty("merOrderId")
    private String merOrderId;
    
    @JsonProperty("refundOrderId")
    private String refundOrderId;
    
    @JsonProperty("refundAmount")
    private Long refundAmount;
    
    @JsonProperty("refundDesc")
    private String refundDesc;
    
    @JsonProperty("platformAmount")
    private Long platformAmount;
    
    @JsonProperty("subOrders")
    private List<SubOrder> subOrders;
    
    /**
     * 私有构造函数，通过Builder创建实例
     * 
     * @param builder Builder对象
     */
    private RefundRequest(Builder builder) {
        this.merOrderId = builder.merOrderId;
        this.refundOrderId = builder.refundOrderId;
        this.refundAmount = builder.refundAmount;
        this.refundDesc = builder.refundDesc;
        this.platformAmount = builder.platformAmount;
        this.subOrders = builder.subOrders;
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
     * 获取原商户订单号
     * 
     * @return 原商户订单号
     */
    public String getMerOrderId() {
        return merOrderId;
    }
    
    /**
     * 获取退款订单号
     * 
     * @return 退款订单号
     */
    public String getRefundOrderId() {
        return refundOrderId;
    }
    
    /**
     * 获取退款金额
     * 
     * @return 退款金额（分）
     */
    public Long getRefundAmount() {
        return refundAmount;
    }
    
    /**
     * 获取退款描述
     * 
     * @return 退款描述
     */
    public String getRefundDesc() {
        return refundDesc;
    }
    
    /**
     * 获取平台金额
     * 
     * @return 平台金额（分）
     */
    public Long getPlatformAmount() {
        return platformAmount;
    }
    
    /**
     * 获取子订单列表
     * 
     * @return 子订单列表
     */
    public List<SubOrder> getSubOrders() {
        return subOrders;
    }
    
    /**
     * Builder类
     */
    public static class Builder {
        private String merOrderId;
        private String refundOrderId;
        private Long refundAmount;
        private String refundDesc;
        private Long platformAmount;
        private List<SubOrder> subOrders;
        
        /**
         * 设置原商户订单号
         * 
         * @param merOrderId 原商户订单号
         * @return Builder对象
         */
        public Builder merOrderId(String merOrderId) {
            this.merOrderId = merOrderId;
            return this;
        }
        
        /**
         * 设置退款订单号
         * 
         * @param refundOrderId 退款订单号
         * @return Builder对象
         */
        public Builder refundOrderId(String refundOrderId) {
            this.refundOrderId = refundOrderId;
            return this;
        }
        
        /**
         * 设置退款金额
         * 
         * @param refundAmount 退款金额（分）
         * @return Builder对象
         */
        public Builder refundAmount(Long refundAmount) {
            this.refundAmount = refundAmount;
            return this;
        }
        
        /**
         * 设置退款描述
         * 
         * @param refundDesc 退款描述
         * @return Builder对象
         */
        public Builder refundDesc(String refundDesc) {
            this.refundDesc = refundDesc;
            return this;
        }
        
        /**
         * 设置平台金额
         * 
         * @param platformAmount 平台金额（分）
         * @return Builder对象
         */
        public Builder platformAmount(Long platformAmount) {
            this.platformAmount = platformAmount;
            return this;
        }
        
        /**
         * 设置子订单列表
         * 
         * @param subOrders 子订单列表
         * @return Builder对象
         */
        public Builder subOrders(List<SubOrder> subOrders) {
            this.subOrders = subOrders;
            return this;
        }
        
        /**
         * 构建RefundRequest对象
         * 
         * @return RefundRequest对象
         * @throws IllegalArgumentException 如果必填参数为空或无效
         */
        public RefundRequest build() {
            validate();
            return new RefundRequest(this);
        }
        
        /**
         * 验证必填参数
         * 
         * @throws IllegalArgumentException 如果必填参数为空或无效
         */
        private void validate() {
            if (merOrderId == null || merOrderId.isEmpty()) {
                throw new IllegalArgumentException("原商户订单号不能为空");
            }
            if (refundAmount == null || refundAmount <= 0) {
                throw new IllegalArgumentException("退款金额必须大于0");
            }
        }
    }
}
