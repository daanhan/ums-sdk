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

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 请求基类
 * 
 * 所有请求对象的基类，包含公共字段。
 * 
 * @author ChinaUMS SDK Team
 * @version 1.0.0
 * @since 1.0.0
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public abstract class BaseRequest {
    
    @JsonProperty("requestTimestamp")
    private String requestTimestamp;
    
    @JsonProperty("mid")
    private String mid;
    
    @JsonProperty("tid")
    private String tid;
    
    @JsonProperty("instMid")
    private String instMid;
    
    @JsonProperty("msgId")
    private String msgId;
    
    @JsonProperty("srcReserve")
    private String srcReserve;
    
    /**
     * 获取请求时间戳
     * 
     * @return 请求时间戳
     */
    public String getRequestTimestamp() {
        return requestTimestamp;
    }
    
    /**
     * 设置请求时间戳
     * 
     * @param requestTimestamp 请求时间戳
     */
    public void setRequestTimestamp(String requestTimestamp) {
        this.requestTimestamp = requestTimestamp;
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
     * 获取终端号
     * 
     * @return 终端号
     */
    public String getTid() {
        return tid;
    }
    
    /**
     * 设置终端号
     * 
     * @param tid 终端号
     */
    public void setTid(String tid) {
        this.tid = tid;
    }
    
    /**
     * 获取机构商户号
     * 
     * @return 机构商户号
     */
    public String getInstMid() {
        return instMid;
    }
    
    /**
     * 设置机构商户号
     * 
     * @param instMid 机构商户号
     */
    public void setInstMid(String instMid) {
        this.instMid = instMid;
    }
    
    /**
     * 获取消息ID
     * 
     * @return 消息ID
     */
    public String getMsgId() {
        return msgId;
    }
    
    /**
     * 设置消息ID
     * 
     * @param msgId 消息ID
     */
    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }
    
    /**
     * 获取源预留字段
     * 
     * @return 源预留字段
     */
    public String getSrcReserve() {
        return srcReserve;
    }
    
    /**
     * 设置源预留字段
     * 
     * @param srcReserve 源预留字段
     */
    public void setSrcReserve(String srcReserve) {
        this.srcReserve = srcReserve;
    }
}
