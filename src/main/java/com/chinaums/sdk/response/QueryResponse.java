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


package com.chinaums.sdk.response;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 订单查询响应
 * 
 * 包含订单交易状态和详细信息。
 * 
 * @author ChinaUMS SDK Team
 * @version 1.0.0
 * @since 1.0.0
 */
public class QueryResponse extends BaseResponse {
    
    @JsonProperty("status")
    private String status;
    
    @JsonProperty("totalAmount")
    private Long totalAmount;
    
    @JsonProperty("merOrderId")
    private String merOrderId;
    
    @JsonProperty("payTime")
    private String payTime;
    
    @JsonProperty("settleDate")
    private String settleDate;
    
    @JsonProperty("buyerId")
    private String buyerId;
    
    @JsonProperty("targetOrderId")
    private String targetOrderId;
    
    @JsonProperty("targetSys")
    private String targetSys;
    
    @JsonProperty("buyerPayAmount")
    private Long buyerPayAmount;
    
    @JsonProperty("couponAmount")
    private Long couponAmount;
    
    @JsonProperty("invoiceAmount")
    private Long invoiceAmount;
    
    @JsonProperty("receiptAmount")
    private Long receiptAmount;
    
    /**
     * 获取订单状态
     * 
     * @return 订单状态
     */
    public String getStatus() {
        return status;
    }
    
    /**
     * 设置订单状态
     * 
     * @param status 订单状态
     */
    public void setStatus(String status) {
        this.status = status;
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
     * 获取支付时间
     * 
     * @return 支付时间
     */
    public String getPayTime() {
        return payTime;
    }
    
    /**
     * 设置支付时间
     * 
     * @param payTime 支付时间
     */
    public void setPayTime(String payTime) {
        this.payTime = payTime;
    }
    
    /**
     * 获取结算日期
     * 
     * @return 结算日期
     */
    public String getSettleDate() {
        return settleDate;
    }
    
    /**
     * 设置结算日期
     * 
     * @param settleDate 结算日期
     */
    public void setSettleDate(String settleDate) {
        this.settleDate = settleDate;
    }
    
    /**
     * 获取买家ID
     * 
     * @return 买家ID
     */
    public String getBuyerId() {
        return buyerId;
    }
    
    /**
     * 设置买家ID
     * 
     * @param buyerId 买家ID
     */
    public void setBuyerId(String buyerId) {
        this.buyerId = buyerId;
    }
    
    /**
     * 获取目标订单号
     * 
     * @return 目标订单号
     */
    public String getTargetOrderId() {
        return targetOrderId;
    }
    
    /**
     * 设置目标订单号
     * 
     * @param targetOrderId 目标订单号
     */
    public void setTargetOrderId(String targetOrderId) {
        this.targetOrderId = targetOrderId;
    }
    
    /**
     * 获取目标系统
     * 
     * @return 目标系统
     */
    public String getTargetSys() {
        return targetSys;
    }
    
    /**
     * 设置目标系统
     * 
     * @param targetSys 目标系统
     */
    public void setTargetSys(String targetSys) {
        this.targetSys = targetSys;
    }
    
    /**
     * 获取买家支付金额
     * 
     * @return 买家支付金额（分）
     */
    public Long getBuyerPayAmount() {
        return buyerPayAmount;
    }
    
    /**
     * 设置买家支付金额
     * 
     * @param buyerPayAmount 买家支付金额（分）
     */
    public void setBuyerPayAmount(Long buyerPayAmount) {
        this.buyerPayAmount = buyerPayAmount;
    }
    
    /**
     * 获取优惠券金额
     * 
     * @return 优惠券金额（分）
     */
    public Long getCouponAmount() {
        return couponAmount;
    }
    
    /**
     * 设置优惠券金额
     * 
     * @param couponAmount 优惠券金额（分）
     */
    public void setCouponAmount(Long couponAmount) {
        this.couponAmount = couponAmount;
    }
    
    /**
     * 获取发票金额
     * 
     * @return 发票金额（分）
     */
    public Long getInvoiceAmount() {
        return invoiceAmount;
    }
    
    /**
     * 设置发票金额
     * 
     * @param invoiceAmount 发票金额（分）
     */
    public void setInvoiceAmount(Long invoiceAmount) {
        this.invoiceAmount = invoiceAmount;
    }
    
    /**
     * 获取实收金额
     * 
     * @return 实收金额（分）
     */
    public Long getReceiptAmount() {
        return receiptAmount;
    }
    
    /**
     * 设置实收金额
     * 
     * @param receiptAmount 实收金额（分）
     */
    public void setReceiptAmount(Long receiptAmount) {
        this.receiptAmount = receiptAmount;
    }
}
