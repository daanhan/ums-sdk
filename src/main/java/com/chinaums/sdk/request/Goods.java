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
 * 商品信息
 * 
 * 用于H5支付请求中的商品详细信息。
 * 
 * @author ChinaUMS SDK Team
 * @version 1.0.0
 * @since 1.0.0
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Goods {
    
    @JsonProperty("goodsId")
    private String goodsId;
    
    @JsonProperty("goodsName")
    private String goodsName;
    
    @JsonProperty("quantity")
    private String quantity;
    
    @JsonProperty("price")
    private String price;
    
    @JsonProperty("goodsCategory")
    private String goodsCategory;
    
    @JsonProperty("body")
    private String body;
    
    @JsonProperty("subMerchantId")
    private String subMerchantId;
    
    @JsonProperty("merOrderId")
    private String merOrderId;
    
    @JsonProperty("subOrderAmount")
    private Long subOrderAmount;
    
    /**
     * 默认构造函数
     */
    public Goods() {
    }
    
    /**
     * 获取商品ID
     * 
     * @return 商品ID
     */
    public String getGoodsId() {
        return goodsId;
    }
    
    /**
     * 设置商品ID
     * 
     * @param goodsId 商品ID
     */
    public void setGoodsId(String goodsId) {
        this.goodsId = goodsId;
    }
    
    /**
     * 获取商品名称
     * 
     * @return 商品名称
     */
    public String getGoodsName() {
        return goodsName;
    }
    
    /**
     * 设置商品名称
     * 
     * @param goodsName 商品名称
     */
    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }
    
    /**
     * 获取商品数量
     * 
     * @return 商品数量
     */
    public String getQuantity() {
        return quantity;
    }
    
    /**
     * 设置商品数量
     * 
     * @param quantity 商品数量
     */
    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }
    
    /**
     * 获取商品价格
     * 
     * @return 商品价格
     */
    public String getPrice() {
        return price;
    }
    
    /**
     * 设置商品价格
     * 
     * @param price 商品价格
     */
    public void setPrice(String price) {
        this.price = price;
    }
    
    /**
     * 获取商品类别
     * 
     * @return 商品类别
     */
    public String getGoodsCategory() {
        return goodsCategory;
    }
    
    /**
     * 设置商品类别
     * 
     * @param goodsCategory 商品类别
     */
    public void setGoodsCategory(String goodsCategory) {
        this.goodsCategory = goodsCategory;
    }
    
    /**
     * 获取商品描述
     * 
     * @return 商品描述
     */
    public String getBody() {
        return body;
    }
    
    /**
     * 设置商品描述
     * 
     * @param body 商品描述
     */
    public void setBody(String body) {
        this.body = body;
    }
    
    /**
     * 获取子商户ID
     * 
     * @return 子商户ID
     */
    public String getSubMerchantId() {
        return subMerchantId;
    }
    
    /**
     * 设置子商户ID
     * 
     * @param subMerchantId 子商户ID
     */
    public void setSubMerchantId(String subMerchantId) {
        this.subMerchantId = subMerchantId;
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
     * 获取子订单金额
     * 
     * @return 子订单金额（分）
     */
    public Long getSubOrderAmount() {
        return subOrderAmount;
    }
    
    /**
     * 设置子订单金额
     * 
     * @param subOrderAmount 子订单金额（分）
     */
    public void setSubOrderAmount(Long subOrderAmount) {
        this.subOrderAmount = subOrderAmount;
    }
}
