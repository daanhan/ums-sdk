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
 * 退款查询响应
 * 
 * 包含退款订单状态和详细信息。
 * 
 * @author ChinaUMS SDK Team
 * @version 1.0.0
 * @since 1.0.0
 */
public class RefundQueryResponse extends BaseResponse {
    
    @JsonProperty("refundStatus")
    private String refundStatus;
    
    @JsonProperty("status")
    private String status;
    
    @JsonProperty("totalAmount")
    private Long totalAmount;
    
    @JsonProperty("merOrderId")
    private String merOrderId;
    
    @JsonProperty("refundOrderId")
    private String refundOrderId;
    
    @JsonProperty("refundTargetOrderId")
    private String refundTargetOrderId;
    
    @JsonProperty("seqId")
    private String seqId;
    
    @JsonProperty("payTime")
    private String payTime;
    
    @JsonProperty("settleDate")
    private String settleDate;
    
    /**
     * 获取退款状态
     * 
     * @return 退款状态
     */
    public String getRefundStatus() {
        return refundStatus;
    }
    
    /**
     * 设置退款状态
     * 
     * @param refundStatus 退款状态
     */
    public void setRefundStatus(String refundStatus) {
        this.refundStatus = refundStatus;
    }
    
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
     * 获取原订单金额
     * 
     * @return 原订单金额（分）
     */
    public Long getTotalAmount() {
        return totalAmount;
    }
    
    /**
     * 设置原订单金额
     * 
     * @param totalAmount 原订单金额（分）
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
     * 获取退款订单号
     * 
     * @return 退款订单号
     */
    public String getRefundOrderId() {
        return refundOrderId;
    }
    
    /**
     * 设置退款订单号
     * 
     * @param refundOrderId 退款订单号
     */
    public void setRefundOrderId(String refundOrderId) {
        this.refundOrderId = refundOrderId;
    }
    
    /**
     * 获取退款目标订单号
     * 
     * @return 退款目标订单号
     */
    public String getRefundTargetOrderId() {
        return refundTargetOrderId;
    }
    
    /**
     * 设置退款目标订单号
     * 
     * @param refundTargetOrderId 退款目标订单号
     */
    public void setRefundTargetOrderId(String refundTargetOrderId) {
        this.refundTargetOrderId = refundTargetOrderId;
    }
    
    /**
     * 获取流水号
     * 
     * @return 流水号
     */
    public String getSeqId() {
        return seqId;
    }
    
    /**
     * 设置流水号
     * 
     * @param seqId 流水号
     */
    public void setSeqId(String seqId) {
        this.seqId = seqId;
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
}
