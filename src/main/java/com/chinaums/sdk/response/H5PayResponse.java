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
 * H5支付响应
 * 
 * 包含支付URL和订单信息。
 * 
 * @author ChinaUMS SDK Team
 * @version 1.0.0
 * @since 1.0.0
 */
public class H5PayResponse extends BaseResponse {
    
    @JsonProperty("payUrl")
    private String payUrl;
    
    @JsonProperty("merOrderId")
    private String merOrderId;
    
    @JsonProperty("seqId")
    private String seqId;
    
    @JsonProperty("targetOrderId")
    private String targetOrderId;
    
    @JsonProperty("targetSys")
    private String targetSys;
    
    /**
     * 获取支付URL
     * 
     * @return 支付URL
     */
    public String getPayUrl() {
        return payUrl;
    }
    
    /**
     * 设置支付URL
     * 
     * @param payUrl 支付URL
     */
    public void setPayUrl(String payUrl) {
        this.payUrl = payUrl;
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
}
