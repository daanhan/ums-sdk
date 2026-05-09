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
 * 微信小程序支付请求
 *
 * 用于发起微信小程序支付请求，包含订单信息、用户标识等。
 *
 * 使用示例：
 * <pre>{@code
 * MiniProgramPayRequest request = MiniProgramPayRequest.builder()
 *     .merOrderId("ORDER123456")
 *     .totalAmount(100L)
 *     .subAppId("wx1234567890abcdef")
 *     .subOpenId("o1234567890abcdef")
 *     .notifyUrl("https://example.com/notify")
 *     .build();
 * }</pre>
 *
 * @author ChinaUMS SDK Team
 * @version 1.0.0
 * @since 1.0.0
 */
public class MiniProgramPayRequest extends BaseRequest {

    private static final String DEFAULT_TRADE_TYPE = "MINI";
    private static final String DEFAULT_INST_MID = "MINIDEFAULT";

    @JsonProperty("merOrderId")
    private String merOrderId;

    @JsonProperty("totalAmount")
    private Long totalAmount;

    @JsonProperty("subAppId")
    private String subAppId;

    @JsonProperty("tradeType")
    private String tradeType;

    @JsonProperty("subOpenId")
    private String subOpenId;

    @JsonProperty("notifyUrl")
    private String notifyUrl;

    @JsonProperty("expireTime")
    private String expireTime;

    @JsonProperty("goods")
    private List<Goods> goods;

    @JsonProperty("orderDesc")
    private String orderDesc;

    @JsonProperty("originalAmount")
    private Long originalAmount;

    @JsonProperty("productId")
    private String productId;

    @JsonProperty("attachedData")
    private String attachedData;

    @JsonProperty("goodsTag")
    private String goodsTag;

    @JsonProperty("divisionFlag")
    private Boolean divisionFlag;

    @JsonProperty("asynDivisionFlag")
    private Boolean asynDivisionFlag;

    @JsonProperty("platformAmount")
    private Long platformAmount;

    @JsonProperty("subOrders")
    private List<SubOrder> subOrders;

    @JsonProperty("secureTransaction")
    private Boolean secureTransaction;

    @JsonProperty("limitCreditCard")
    private Boolean limitCreditCard;

    @JsonProperty("installmentNumber")
    private Integer installmentNumber;

    @JsonProperty("name")
    private String name;

    @JsonProperty("mobile")
    private String mobile;

    @JsonProperty("certType")
    private String certType;

    @JsonProperty("certNo")
    private String certNo;

    @JsonProperty("fixBuyer")
    private Boolean fixBuyer;

    @JsonProperty("clientIp")
    private String clientIp;

    @JsonProperty("userId")
    private String userId;

    @JsonProperty("feeRatio")
    private String feeRatio;

    @JsonProperty("costSubsidy")
    private Boolean costSubsidy;

    @JsonProperty("preauthTransaction")
    private Boolean preauthTransaction;

    private MiniProgramPayRequest(Builder builder) {
        this.merOrderId = builder.merOrderId;
        this.totalAmount = builder.totalAmount;
        this.subAppId = builder.subAppId;
        this.tradeType = DEFAULT_TRADE_TYPE;
        this.subOpenId = builder.subOpenId;
        this.notifyUrl = builder.notifyUrl;
        this.expireTime = builder.expireTime;
        this.goods = builder.goods;
        this.orderDesc = builder.orderDesc;
        this.originalAmount = builder.originalAmount;
        this.productId = builder.productId;
        this.attachedData = builder.attachedData;
        this.goodsTag = builder.goodsTag;
        this.divisionFlag = builder.divisionFlag;
        this.asynDivisionFlag = builder.asynDivisionFlag;
        this.platformAmount = builder.platformAmount;
        this.subOrders = builder.subOrders;
        this.secureTransaction = builder.secureTransaction;
        this.limitCreditCard = builder.limitCreditCard;
        this.installmentNumber = builder.installmentNumber;
        this.name = builder.name;
        this.mobile = builder.mobile;
        this.certType = builder.certType;
        this.certNo = builder.certNo;
        this.fixBuyer = builder.fixBuyer;
        this.clientIp = builder.clientIp;
        this.userId = builder.userId;
        this.feeRatio = builder.feeRatio;
        this.costSubsidy = builder.costSubsidy;
        this.preauthTransaction = builder.preauthTransaction;
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
     * 获取支付金额
     *
     * @return 支付金额（分）
     */
    public Long getTotalAmount() {
        return totalAmount;
    }

    /**
     * 获取微信子商户appId
     *
     * @return 微信子商户appId
     */
    public String getSubAppId() {
        return subAppId;
    }

    /**
     * 获取交易类型
     *
     * @return 交易类型，固定为MINI
     */
    public String getTradeType() {
        return tradeType;
    }

    /**
     * 获取用户子标识
     *
     * @return 用户子标识（subOpenId）
     */
    public String getSubOpenId() {
        return subOpenId;
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
     * 获取订单过期时间
     *
     * @return 订单过期时间
     */
    public String getExpireTime() {
        return expireTime;
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
     * 获取账单描述
     *
     * @return 账单描述
     */
    public String getOrderDesc() {
        return orderDesc;
    }

    /**
     * 获取订单原始金额
     *
     * @return 订单原始金额（分）
     */
    public Long getOriginalAmount() {
        return originalAmount;
    }

    /**
     * 获取商品ID
     *
     * @return 商品ID
     */
    public String getProductId() {
        return productId;
    }

    /**
     * 获取商户附加数据
     *
     * @return 商户附加数据
     */
    public String getAttachedData() {
        return attachedData;
    }

    /**
     * 获取商品标记
     *
     * @return 商品标记
     */
    public String getGoodsTag() {
        return goodsTag;
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
     * 获取是否异步分账
     *
     * @return true表示异步分账，false表示同步分账
     */
    public Boolean getAsynDivisionFlag() {
        return asynDivisionFlag;
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
     * 获取是否担保交易
     *
     * @return true表示担保交易，false表示非担保交易
     */
    public Boolean getSecureTransaction() {
        return secureTransaction;
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
     * 获取花呗分期数
     *
     * @return 花呗分期数
     */
    public Integer getInstallmentNumber() {
        return installmentNumber;
    }

    /**
     * 获取实名认证姓名
     *
     * @return 实名认证姓名（Base64编码）
     */
    public String getName() {
        return name;
    }

    /**
     * 获取实名认证手机号
     *
     * @return 实名认证手机号（Base64编码）
     */
    public String getMobile() {
        return mobile;
    }

    /**
     * 获取实名认证证件类型
     *
     * @return 实名认证证件类型
     */
    public String getCertType() {
        return certType;
    }

    /**
     * 获取实名认证证件号
     *
     * @return 实名认证证件号（Base64编码）
     */
    public String getCertNo() {
        return certNo;
    }

    /**
     * 获取是否需要实名认证
     *
     * @return true表示需要实名认证
     */
    public Boolean getFixBuyer() {
        return fixBuyer;
    }

    /**
     * 获取客户端IP
     *
     * @return 客户端IP
     */
    public String getClientIp() {
        return clientIp;
    }

    /**
     * 获取用户子标识（支付宝）
     *
     * @return 用户子标识
     */
    public String getUserId() {
        return userId;
    }

    /**
     * 获取手续费比例
     *
     * @return 手续费比例
     */
    public String getFeeRatio() {
        return feeRatio;
    }

    /**
     * 获取是否成本补贴
     *
     * @return true表示不判断手续费比例，false表示按正常判断
     */
    public Boolean getCostSubsidy() {
        return costSubsidy;
    }

    /**
     * 获取是否预授权交易
     *
     * @return true表示预授权交易，false表示普通交易
     */
    public Boolean getPreauthTransaction() {
        return preauthTransaction;
    }

    /**
     * 覆盖基类instMid，小程序支付默认使用MINIDEFAULT
     *
     * @return 机构商户号
     */
    @Override
    @JsonProperty("instMid")
    public String getInstMid() {
        String instMid = super.getInstMid();
        return (instMid == null || instMid.isEmpty()) ? DEFAULT_INST_MID : instMid;
    }

    /**
     * Builder类
     */
    public static class Builder {
        private String merOrderId;
        private Long totalAmount;
        private String subAppId;
        private String subOpenId;
        private String notifyUrl;
        private String expireTime;
        private List<Goods> goods;
        private String orderDesc;
        private Long originalAmount;
        private String productId;
        private String attachedData;
        private String goodsTag;
        private Boolean divisionFlag;
        private Boolean asynDivisionFlag;
        private Long platformAmount;
        private List<SubOrder> subOrders;
        private Boolean secureTransaction;
        private Boolean limitCreditCard;
        private Integer installmentNumber;
        private String name;
        private String mobile;
        private String certType;
        private String certNo;
        private Boolean fixBuyer;
        private String clientIp;
        private String userId;
        private String feeRatio;
        private Boolean costSubsidy;
        private Boolean preauthTransaction;

        public Builder merOrderId(String merOrderId) {
            this.merOrderId = merOrderId;
            return this;
        }

        public Builder totalAmount(Long totalAmount) {
            this.totalAmount = totalAmount;
            return this;
        }

        public Builder subAppId(String subAppId) {
            this.subAppId = subAppId;
            return this;
        }

        public Builder subOpenId(String subOpenId) {
            this.subOpenId = subOpenId;
            return this;
        }

        public Builder notifyUrl(String notifyUrl) {
            this.notifyUrl = notifyUrl;
            return this;
        }

        public Builder expireTime(String expireTime) {
            this.expireTime = expireTime;
            return this;
        }

        public Builder goods(List<Goods> goods) {
            this.goods = goods;
            return this;
        }

        public Builder orderDesc(String orderDesc) {
            this.orderDesc = orderDesc;
            return this;
        }

        public Builder originalAmount(Long originalAmount) {
            this.originalAmount = originalAmount;
            return this;
        }

        public Builder productId(String productId) {
            this.productId = productId;
            return this;
        }

        public Builder attachedData(String attachedData) {
            this.attachedData = attachedData;
            return this;
        }

        public Builder goodsTag(String goodsTag) {
            this.goodsTag = goodsTag;
            return this;
        }

        public Builder divisionFlag(Boolean divisionFlag) {
            this.divisionFlag = divisionFlag;
            return this;
        }

        public Builder asynDivisionFlag(Boolean asynDivisionFlag) {
            this.asynDivisionFlag = asynDivisionFlag;
            return this;
        }

        public Builder platformAmount(Long platformAmount) {
            this.platformAmount = platformAmount;
            return this;
        }

        public Builder subOrders(List<SubOrder> subOrders) {
            this.subOrders = subOrders;
            return this;
        }

        public Builder secureTransaction(Boolean secureTransaction) {
            this.secureTransaction = secureTransaction;
            return this;
        }

        public Builder limitCreditCard(Boolean limitCreditCard) {
            this.limitCreditCard = limitCreditCard;
            return this;
        }

        public Builder installmentNumber(Integer installmentNumber) {
            this.installmentNumber = installmentNumber;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder mobile(String mobile) {
            this.mobile = mobile;
            return this;
        }

        public Builder certType(String certType) {
            this.certType = certType;
            return this;
        }

        public Builder certNo(String certNo) {
            this.certNo = certNo;
            return this;
        }

        public Builder fixBuyer(Boolean fixBuyer) {
            this.fixBuyer = fixBuyer;
            return this;
        }

        public Builder clientIp(String clientIp) {
            this.clientIp = clientIp;
            return this;
        }

        public Builder userId(String userId) {
            this.userId = userId;
            return this;
        }

        public Builder feeRatio(String feeRatio) {
            this.feeRatio = feeRatio;
            return this;
        }

        public Builder costSubsidy(Boolean costSubsidy) {
            this.costSubsidy = costSubsidy;
            return this;
        }

        public Builder preauthTransaction(Boolean preauthTransaction) {
            this.preauthTransaction = preauthTransaction;
            return this;
        }

        /**
         * 构建MiniProgramPayRequest对象
         *
         * @return MiniProgramPayRequest对象
         * @throws IllegalArgumentException 如果必填参数为空或无效
         */
        public MiniProgramPayRequest build() {
            validate();
            return new MiniProgramPayRequest(this);
        }

        private void validate() {
            if (merOrderId == null || merOrderId.isEmpty()) {
                throw new IllegalArgumentException("商户订单号不能为空");
            }
            if (totalAmount == null || totalAmount <= 0) {
                throw new IllegalArgumentException("支付金额必须大于0");
            }
            if (subAppId == null || subAppId.isEmpty()) {
                throw new IllegalArgumentException("微信子商户appId(subAppId)不能为空");
            }
            if (subOpenId == null || subOpenId.isEmpty()) {
                throw new IllegalArgumentException("用户子标识(subOpenId)不能为空");
            }
        }
    }
}
