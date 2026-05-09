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
 * 小程序支付调起参数
 *
 * 对应微信小程序支付响应中的miniPayRequest字段，
 * 包含调用wx.requestPayment所需的全部参数。
 *
 * 使用示例：
 * <pre>{@code
 * MiniProgramPayResponse response = client.miniProgramPay(request);
 * MiniPayRequest payRequest = response.getMiniPayRequest();
 * // 将payRequest的字段传递给前端调用wx.requestPayment
 * }</pre>
 *
 * @author ChinaUMS SDK Team
 * @version 1.0.0
 * @since 1.0.0
 */
public class MiniPayRequest {

    @JsonProperty("appId")
    private String appId;

    @JsonProperty("timeStamp")
    private String timeStamp;

    @JsonProperty("nonceStr")
    private String nonceStr;

    @JsonProperty("package")
    private String packageValue;

    @JsonProperty("signType")
    private String signType;

    @JsonProperty("paySign")
    private String paySign;

    public MiniPayRequest() {
    }

    /**
     * 获取微信appId
     *
     * @return 微信appId
     */
    public String getAppId() {
        return appId;
    }

    /**
     * 设置微信appId
     *
     * @param appId 微信appId
     */
    public void setAppId(String appId) {
        this.appId = appId;
    }

    /**
     * 获取时间戳
     *
     * @return 时间戳
     */
    public String getTimeStamp() {
        return timeStamp;
    }

    /**
     * 设置时间戳
     *
     * @param timeStamp 时间戳
     */
    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    /**
     * 获取随机字符串
     *
     * @return 随机字符串
     */
    public String getNonceStr() {
        return nonceStr;
    }

    /**
     * 设置随机字符串
     *
     * @param nonceStr 随机字符串
     */
    public void setNonceStr(String nonceStr) {
        this.nonceStr = nonceStr;
    }

    /**
     * 获取预支付交易会话标识
     *
     * @return 预支付交易会话标识，格式如 prepay_id=xxx
     */
    public String getPackageValue() {
        return packageValue;
    }

    /**
     * 设置预支付交易会话标识
     *
     * @param packageValue 预支付交易会话标识
     */
    public void setPackageValue(String packageValue) {
        this.packageValue = packageValue;
    }

    /**
     * 获取签名方式
     *
     * @return 签名方式，如RSA
     */
    public String getSignType() {
        return signType;
    }

    /**
     * 设置签名方式
     *
     * @param signType 签名方式
     */
    public void setSignType(String signType) {
        this.signType = signType;
    }

    /**
     * 获取签名
     *
     * @return 签名
     */
    public String getPaySign() {
        return paySign;
    }

    /**
     * 设置签名
     *
     * @param paySign 签名
     */
    public void setPaySign(String paySign) {
        this.paySign = paySign;
    }
}
