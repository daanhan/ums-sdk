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
 * H5支付请求
 * 
 * 用于发起微信H5支付请求，包含订单信息、商品信息等。
 * 
 * 使用示例：
 * <pre>{@code
 * H5PayRequest request = H5PayRequest.builder()
 *     .merOrderId("ORDER123456")
 *     .totalAmount(100L)
 *     .notifyUrl("https://example.com/notify")
 *     .build();
 * }</pre>
 * 
 * @author ChinaUMS SDK Team
 * @version 1.0.0
 * @since 1.0.0
 */
public class H5PayRequest extends BaseRequest {
    
    @JsonProperty("merOrderId")
    private String merOrderId;
    
    @JsonProperty("totalAmount")
    private Long totalAmount;
    
    @JsonProperty("goods")
    private List<Goods> goods;
    
    @JsonProperty("notifyUrl")
    private String notifyUrl;
    
    @JsonProperty("returnUrl")
    private String returnUrl;
    
    @JsonProperty("expireTime")
    private String expireTime;
    
    @JsonProperty("sceneType")
    private String sceneType;
    
    @JsonProperty("merAppName")
    private String merAppName;
    
    @JsonProperty("merAppId")
    private String merAppId;
    
    @JsonProperty("limitCreditCard")
    private Boolean limitCreditCard;
    
    @JsonProperty("divisionFlag")
    private Boolean divisionFlag;
    
    @JsonProperty("platformAmount")
    private Long platformAmount;
    
    @JsonProperty("subOrders")
    private List<SubOrder> subOrders;
    
    /**
     * 私有构造函数，通过Builder创建实例
     * 
     * @param builder Builder对象
     */
    private H5PayRequest(Builder builder) {
        this.merOrderId = builder.merOrderId;
        this.totalAmount = builder.totalAmount;
        this.goods = builder.goods;
        this.notifyUrl = builder.notifyUrl;
        this.returnUrl = builder.returnUrl;
        this.expireTime = builder.expireTime;
        this.sceneType = builder.sceneType;
        this.merAppName = builder.merAppName;
        this.merAppId = builder.merAppId;
        this.limitCreditCard = builder.limitCreditCard;
        this.divisionFlag = builder.divisionFlag;
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
     * 获取商户订单号
     * 
     * @return 商户订单号
     */
    public String getMerOrderId() {
        return merOrderId;
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
     * 获取商品列表
     * 
     * @return 商品列表
     */
    public List<Goods> getGoods() {
        return goods;
    }
    
    /**
     * 获取异步通知URL
     * 
     * @return 异步通知URL
     */
    public String getNotifyUrl() {
        return notifyUrl;
    }
    
    /**
     * 获取同步返回URL
     * 
     * @return 同步返回URL
     */
    public String getReturnUrl() {
        return returnUrl;
    }
    
    /**
     * 获取订单过期时间
     * 
     * @return 订单过期时间
     */
    public String getExpireTime() {
        return expireTime;
    }
    
    /**
     * 获取场景类型
     * 
     * @return 场景类型
     */
    public String getSceneType() {
        return sceneType;
    }
    
    /**
     * 获取商户应用名称
     * 
     * @return 商户应用名称
     */
    public String getMerAppName() {
        return merAppName;
    }
    
    /**
     * 获取商户应用ID
     * 
     * @return 商户应用ID
     */
    public String getMerAppId() {
        return merAppId;
    }
    
    /**
     * 获取是否限制信用卡
     * 
     * @return true表示限制，false表示不限制
     */
    public Boolean getLimitCreditCard() {
        return limitCreditCard;
    }
    
    /**
     * 获取是否分账
     * 
     * @return true表示分账，false表示不分账
     */
    public Boolean getDivisionFlag() {
        return divisionFlag;
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
        private Long totalAmount;
        private List<Goods> goods;
        private String notifyUrl;
        private String returnUrl;
        private String expireTime;
        private String sceneType;
        private String merAppName;
        private String merAppId;
        private Boolean limitCreditCard;
        private Boolean divisionFlag;
        private Long platformAmount;
        private List<SubOrder> subOrders;
        
        /**
         * 设置商户订单号
         * 
         * @param merOrderId 商户订单号
         * @return Builder对象
         */
        public Builder merOrderId(String merOrderId) {
            this.merOrderId = merOrderId;
            return this;
        }
        
        /**
         * 设置订单金额
         * 
         * @param totalAmount 订单金额（分）
         * @return Builder对象
         */
        public Builder totalAmount(Long totalAmount) {
            this.totalAmount = totalAmount;
            return this;
        }
        
        /**
         * 设置商品列表
         * 
         * @param goods 商品列表
         * @return Builder对象
         */
        public Builder goods(List<Goods> goods) {
            this.goods = goods;
            return this;
        }
        
        /**
         * 设置异步通知URL
         * 
         * @param notifyUrl 异步通知URL
         * @return Builder对象
         */
        public Builder notifyUrl(String notifyUrl) {
            this.notifyUrl = notifyUrl;
            return this;
        }
        
        /**
         * 设置同步返回URL
         * 
         * @param returnUrl 同步返回URL
         * @return Builder对象
         */
        public Builder returnUrl(String returnUrl) {
            this.returnUrl = returnUrl;
            return this;
        }
        
        /**
         * 设置订单过期时间
         * 
         * @param expireTime 订单过期时间
         * @return Builder对象
         */
        public Builder expireTime(String expireTime) {
            this.expireTime = expireTime;
            return this;
        }
        
        /**
         * 设置场景类型
         * 
         * @param sceneType 场景类型
         * @return Builder对象
         */
        public Builder sceneType(String sceneType) {
            this.sceneType = sceneType;
            return this;
        }
        
        /**
         * 设置商户应用名称
         * 
         * @param merAppName 商户应用名称
         * @return Builder对象
         */
        public Builder merAppName(String merAppName) {
            this.merAppName = merAppName;
            return this;
        }
        
        /**
         * 设置商户应用ID
         * 
         * @param merAppId 商户应用ID
         * @return Builder对象
         */
        public Builder merAppId(String merAppId) {
            this.merAppId = merAppId;
            return this;
        }
        
        /**
         * 设置是否限制信用卡
         * 
         * @param limitCreditCard true表示限制，false表示不限制
         * @return Builder对象
         */
        public Builder limitCreditCard(Boolean limitCreditCard) {
            this.limitCreditCard = limitCreditCard;
            return this;
        }
        
        /**
         * 设置是否分账
         * 
         * @param divisionFlag true表示分账，false表示不分账
         * @return Builder对象
         */
        public Builder divisionFlag(Boolean divisionFlag) {
            this.divisionFlag = divisionFlag;
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
         * 构建H5PayRequest对象
         * 
         * @return H5PayRequest对象
         * @throws IllegalArgumentException 如果必填参数为空或无效
         */
        public H5PayRequest build() {
            validate();
            return new H5PayRequest(this);
        }
        
        /**
         * 验证必填参数
         * 
         * @throws IllegalArgumentException 如果必填参数为空或无效
         */
        private void validate() {
            if (merOrderId == null || merOrderId.isEmpty()) {
                throw new IllegalArgumentException("商户订单号不能为空");
            }
            if (totalAmount == null || totalAmount <= 0) {
                throw new IllegalArgumentException("支付金额必须大于0");
            }
        }
    }
}
