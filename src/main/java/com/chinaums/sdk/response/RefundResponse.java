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
 * 退款响应
 * 
 * 包含退款状态和详细信息。
 * 
 * @author ChinaUMS SDK Team
 * @version 1.0.0
 * @since 1.0.0
 */
public class RefundResponse extends BaseResponse {
    
    @JsonProperty("status")
    private String status;
    
    @JsonProperty("totalAmount")
    private Long totalAmount;
    
    @JsonProperty("refundAmount")
    private Long refundAmount;
    
    @JsonProperty("merOrderId")
    private String merOrderId;
    
    @JsonProperty("refundOrderId")
    private String refundOrderId;
    
    @JsonProperty("refundTargetOrderId")
    private String refundTargetOrderId;
    
    @JsonProperty("seqId")
    private String seqId;
    
    @JsonProperty("targetSys")
    private String targetSys;
    
    @JsonProperty("targetMid")
    private String targetMid;
    
    /**
     * 获取退款状态
     * 
     * @return 退款状态
     */
    public String getStatus() {
        return status;
    }
    
    /**
     * 设置退款状态
     * 
     * @param status 退款状态
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
     * 获取退款金额
     * 
     * @return 退款金额（分）
     */
    public Long getRefundAmount() {
        return refundAmount;
    }
    
    /**
     * 设置退款金额
     * 
     * @param refundAmount 退款金额（分）
     */
    public void setRefundAmount(Long refundAmount) {
        this.refundAmount = refundAmount;
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
     * 设置原商户订单号
     * 
     * @param merOrderId 原商户订单号
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
     * 获取目标商户号
     * 
     * @return 目标商户号
     */
    public String getTargetMid() {
        return targetMid;
    }
    
    /**
     * 设置目标商户号
     * 
     * @param targetMid 目标商户号
     */
    public void setTargetMid(String targetMid) {
        this.targetMid = targetMid;
    }
}
