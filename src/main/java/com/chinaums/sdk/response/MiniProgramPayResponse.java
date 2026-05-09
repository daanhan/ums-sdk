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
 * 微信小程序支付响应
 *
 * 包含小程序支付调起参数和订单信息。
 *
 * @author ChinaUMS SDK Team
 * @version 1.0.0
 * @since 1.0.0
 */
public class MiniProgramPayResponse extends BaseResponse {

    @JsonProperty("merOrderId")
    private String merOrderId;

    @JsonProperty("seqId")
    private String seqId;

    @JsonProperty("settleRefId")
    private String settleRefId;

    @JsonProperty("status")
    private String status;

    @JsonProperty("totalAmount")
    private Long totalAmount;

    @JsonProperty("targetOrderId")
    private String targetOrderId;

    @JsonProperty("targetSys")
    private String targetSys;

    @JsonProperty("targetStatus")
    private String targetStatus;

    @JsonProperty("miniPayRequest")
    private MiniPayRequest miniPayRequest;

    @JsonProperty("targetMid")
    private String targetMid;

    @JsonProperty("merName")
    private String merName;

    @JsonProperty("yxlmAmount")
    private Long yxlmAmount;

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
     * 获取平台流水号
     *
     * @return 平台流水号
     */
    public String getSeqId() {
        return seqId;
    }

    /**
     * 设置平台流水号
     *
     * @param seqId 平台流水号
     */
    public void setSeqId(String seqId) {
        this.seqId = seqId;
    }

    /**
     * 获取清分ID
     *
     * @return 清分ID
     */
    public String getSettleRefId() {
        return settleRefId;
    }

    /**
     * 设置清分ID
     *
     * @param settleRefId 清分ID
     */
    public void setSettleRefId(String settleRefId) {
        this.settleRefId = settleRefId;
    }

    /**
     * 获取交易状态
     *
     * @return 交易状态，如WAIT_BUYER_PAY、TRADE_SUCCESS等
     */
    public String getStatus() {
        return status;
    }

    /**
     * 设置交易状态
     *
     * @param status 交易状态
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * 获取支付总金额
     *
     * @return 支付总金额（分）
     */
    public Long getTotalAmount() {
        return totalAmount;
    }

    /**
     * 设置支付总金额
     *
     * @param totalAmount 支付总金额（分）
     */
    public void setTotalAmount(Long totalAmount) {
        this.totalAmount = totalAmount;
    }

    /**
     * 获取第三方订单号
     *
     * @return 第三方订单号
     */
    public String getTargetOrderId() {
        return targetOrderId;
    }

    /**
     * 设置第三方订单号
     *
     * @param targetOrderId 第三方订单号
     */
    public void setTargetOrderId(String targetOrderId) {
        this.targetOrderId = targetOrderId;
    }

    /**
     * 获取目标平台代码
     *
     * @return 目标平台代码，如WXPay
     */
    public String getTargetSys() {
        return targetSys;
    }

    /**
     * 设置目标平台代码
     *
     * @param targetSys 目标平台代码
     */
    public void setTargetSys(String targetSys) {
        this.targetSys = targetSys;
    }

    /**
     * 获取目标平台状态
     *
     * @return 目标平台状态
     */
    public String getTargetStatus() {
        return targetStatus;
    }

    /**
     * 设置目标平台状态
     *
     * @param targetStatus 目标平台状态
     */
    public void setTargetStatus(String targetStatus) {
        this.targetStatus = targetStatus;
    }

    /**
     * 获取小程序支付调起参数
     *
     * @return 小程序支付调起参数，用于调用wx.requestPayment
     */
    public MiniPayRequest getMiniPayRequest() {
        return miniPayRequest;
    }

    /**
     * 设置小程序支付调起参数
     *
     * @param miniPayRequest 小程序支付调起参数
     */
    public void setMiniPayRequest(MiniPayRequest miniPayRequest) {
        this.miniPayRequest = miniPayRequest;
    }

    /**
     * 获取支付渠道商户号
     *
     * @return 支付渠道商户号
     */
    public String getTargetMid() {
        return targetMid;
    }

    /**
     * 设置支付渠道商户号
     *
     * @param targetMid 支付渠道商户号
     */
    public void setTargetMid(String targetMid) {
        this.targetMid = targetMid;
    }

    /**
     * 获取商户名称
     *
     * @return 商户名称
     */
    public String getMerName() {
        return merName;
    }

    /**
     * 设置商户名称
     *
     * @param merName 商户名称
     */
    public void setMerName(String merName) {
        this.merName = merName;
    }

    /**
     * 获取营销联盟优惠金额
     *
     * @return 营销联盟优惠金额（分）
     */
    public Long getYxlmAmount() {
        return yxlmAmount;
    }

    /**
     * 设置营销联盟优惠金额
     *
     * @param yxlmAmount 营销联盟优惠金额（分）
     */
    public void setYxlmAmount(Long yxlmAmount) {
        this.yxlmAmount = yxlmAmount;
    }
}
