# SKILL.md — 银联商务支付 SDK 使用技能指南

> 本文档为 AI 工具用户和开发者提供 ums-sdk 的完整使用指南，涵盖技能定义、参数规范、输出格式、使用示例、认证要求、错误处理及最佳实践。

---

## 目录

- [技能总览](#技能总览)
- [前置准备](#前置准备)
- [Skill 1: SDK 初始化与配置](#skill-1-sdk-初始化与配置)
- [Skill 2: 微信 H5 支付](#skill-2-微信-h5-支付)
- [Skill 3: 微信小程序支付](#skill-3-微信小程序支付)
- [Skill 4: 订单查询](#skill-4-订单查询)
- [Skill 5: 退款](#skill-5-退款)
- [Skill 6: 退款查询](#skill-6-退款查询)
- [Skill 7: 支付回调通知验签](#skill-7-支付回调通知验签)
- [认证与安全规范](#认证与安全规范)
- [频率限制与超时配置](#频率限制与超时配置)
- [错误处理手册](#错误处理手册)
- [最佳实践](#最佳实践)
- [AI 工具集成提示词模板](#ai-工具集成提示词模板)

---

## 技能总览

ums-sdk 提供 7 大核心技能，覆盖支付全生命周期：

```
┌─────────────────────────────────────────────────────────┐
│                   ums-sdk 技能图谱                       │
├──────────────┬──────────────────────────────────────────┤
│ Skill 1      │ SDK 初始化与配置                          │
│ Skill 2      │ 微信 H5 支付                             │
│ Skill 3      │ 微信小程序支付                            │
│ Skill 4      │ 订单查询                                 │
│ Skill 5      │ 退款                                     │
│ Skill 6      │ 退款查询                                 │
│ Skill 7      │ 支付回调通知验签                          │
└──────────────┴──────────────────────────────────────────┘
```

**技能依赖关系：**

```
Skill 1 (初始化) ──→ Skill 2/3 (支付) ──→ Skill 7 (回调验签)
                   ──→ Skill 4 (查询)
                   ──→ Skill 5 (退款) ──→ Skill 6 (退款查询)
```

> **重要：** 所有技能均依赖 Skill 1 完成初始化后方可使用。

---

## 前置准备

### 环境要求

| 项目 | 要求 |
|---|---|
| Java 版本 | 1.8 及以上 |
| 构建工具 | Maven 3.6+ |
| 依赖管理 | 通过 pom.xml 自动管理 |

### 添加依赖

在项目的 `pom.xml` 中添加：

```xml
<dependency>
    <groupId>com.chinaums</groupId>
    <artifactId>ums-sdk</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>
```

### 获取凭证

在银联商务开放平台（https://open.chinaums.com）注册并创建应用后，获取以下凭证：

| 凭证 | 说明 | 获取方式 |
|---|---|---|
| appId | 应用 ID | 开放平台应用详情页 |
| appKey | 应用密钥 | 开放平台应用详情页 |
| mid | 商户号 | 开通商户后分配 |
| tid | 终端号 | 开通商户后分配 |
| notifyKey | 通讯密钥 | 开通支付回调后分配 |

---

## Skill 1: SDK 初始化与配置

### 技能定义

创建并配置 SDK 客户端实例，为后续所有支付操作提供基础环境。

### 输入参数

| 参数 | 类型 | 必填 | 默认值 | 说明 |
|---|---|---|---|---|
| appId | String | ✅ | — | 银联商务平台应用 ID |
| appKey | String | ✅ | — | 银联商务平台应用密钥 |
| mid | String | ✅ | — | 商户号 |
| tid | String | ✅ | — | 终端号 |
| instMid | String | ❌ | H5DEFAULT | 业务类型：H5 支付用 `H5DEFAULT`，小程序支付用 `MINIDEFAULT` |
| notifyKey | String | ❌ | null | 通讯密钥，回调验签时必填 |
| environment | Enum | ❌ | PRODUCTION | 环境：`PRODUCTION` 或 `TEST` |
| connectTimeout | int | ❌ | 10000 | 连接超时（毫秒） |
| readTimeout | int | ❌ | 30000 | 读取超时（毫秒） |
| maxRetries | int | ❌ | 3 | 最大重试次数 |
| enableCertValidation | boolean | ❌ | true | 是否启用证书验证 |

### 输出格式

返回 `UmsPayClient` 实例对象，可通过该实例调用所有支付技能。

### 使用示例

#### 基础配置（H5 支付场景）

```java
import com.chinaums.sdk.UmsPayClient;
import com.chinaums.sdk.config.UmsPayConfig;
import com.chinaums.sdk.config.Environment;

UmsPayConfig config = UmsPayConfig.builder()
    .appId("your-app-id")
    .appKey("your-app-key")
    .mid("898201612345678")
    .tid("88880001")
    .environment(Environment.PRODUCTION)
    .build();

UmsPayClient client = new UmsPayClient(config);
```

#### 小程序支付配置

```java
UmsPayConfig miniConfig = UmsPayConfig.builder()
    .appId("your-app-id")
    .appKey("your-app-key")
    .mid("898201612345678")
    .tid("88880001")
    .instMid("MINIDEFAULT")
    .notifyKey("your-notify-key")
    .environment(Environment.PRODUCTION)
    .build();

UmsPayClient miniClient = new UmsPayClient(miniConfig);
```

#### 测试环境配置

```java
UmsPayConfig testConfig = UmsPayConfig.builder()
    .appId("test-app-id")
    .appKey("test-app-key")
    .mid("898201612345678")
    .tid("88880001")
    .environment(Environment.TEST)
    .connectTimeout(5000)
    .readTimeout(15000)
    .maxRetries(5)
    .build();

UmsPayClient testClient = new UmsPayClient(testConfig);
```

### 注意事项

- **H5 支付和小程序支付应使用独立的 UmsPayClient 实例**，避免 instMid 配置冲突
- UmsPayClient 建议使用**单例模式**，避免重复初始化
- 切勿在源代码中硬编码敏感凭证，推荐使用环境变量

---

## Skill 2: 微信 H5 支付

### 技能定义

发起微信 H5 支付请求，获取支付跳转 URL，引导用户在微信浏览器中完成支付。

### 输入参数

| 参数 | 类型 | 必填 | 说明 |
|---|---|---|---|
| merOrderId | String | ✅ | 商户订单号，需全局唯一 |
| totalAmount | Long | ✅ | 订单金额，单位：分（1 元 = 100 分） |
| notifyUrl | String | ❌ | 支付结果异步通知 URL |
| returnUrl | String | ❌ | 支付完成后同步跳转 URL |
| goods | List\<Goods\> | ❌ | 商品信息列表 |
| expireTime | String | ❌ | 订单过期时间 |
| limitCreditCard | Boolean | ❌ | 是否限制信用卡支付 |
| divisionFlag | Boolean | ❌ | 是否分账 |
| subOrders | List\<SubOrder\> | ❌ | 子订单列表（分账场景） |

### 输出格式

| 字段 | 类型 | 说明 |
|---|---|---|
| errCode | String | 错误码，`SUCCESS` 表示成功 |
| payUrl | String | 支付跳转 URL |
| merOrderId | String | 商户订单号 |
| targetOrderId | String | 银联商务订单号 |
| targetSys | String | 目标支付系统 |

### 使用示例

```java
import com.chinaums.sdk.request.H5PayRequest;
import com.chinaums.sdk.response.H5PayResponse;

H5PayRequest request = H5PayRequest.builder()
    .merOrderId("ORDER" + System.currentTimeMillis())
    .totalAmount(100L)
    .notifyUrl("https://your-domain.com/api/notify")
    .returnUrl("https://your-domain.com/return")
    .build();

try {
    H5PayResponse response = client.h5Pay(request);
    if (response.isSuccess()) {
        String payUrl = response.getPayUrl();
        // 将用户重定向到 payUrl 完成支付
        response.sendRedirect(payUrl);
    }
} catch (UmsPayBusinessException e) {
    logger.error("业务错误: {} - {}", e.getErrorCode(), e.getMessage());
} catch (UmsPayNetworkException e) {
    logger.error("网络错误: {}", e.getMessage());
}
```

### AI 提示词示例

```
请帮我使用 ums-sdk 发起一笔微信 H5 支付，订单号为 ORDER20260509001，
金额为 50 元，回调地址为 https://example.com/notify，
支付完成后跳转到 https://example.com/return。
```

---

## Skill 3: 微信小程序支付

### 技能定义

发起微信小程序支付请求，获取 `miniPayRequest` 参数，传递给前端小程序调用 `wx.requestPayment()` 完成支付。

### 输入参数

| 参数 | 类型 | 必填 | 说明 |
|---|---|---|---|
| merOrderId | String | ✅ | 商户订单号，需全局唯一 |
| totalAmount | Long | ✅ | 支付金额，单位：分 |
| subAppId | String | ✅ | 微信小程序 AppID |
| subOpenId | String | ✅ | 用户 OpenID（通过 wx.login 获取） |
| notifyUrl | String | ❌ | 支付结果异步通知 URL |
| orderDesc | String | ❌ | 订单描述 |
| limitCreditCard | Boolean | ❌ | 是否限制信用卡 |
| goods | List\<Goods\> | ❌ | 商品信息列表 |
| subOrders | List\<SubOrder\> | ❌ | 子订单列表（分账场景） |

### 输出格式

| 字段 | 类型 | 说明 |
|---|---|---|
| errCode | String | 错误码 |
| status | String | 交易状态（WAIT_BUYER_PAY 等） |
| totalAmount | Long | 支付金额（分） |
| targetOrderId | String | 第三方订单号 |
| miniPayRequest | MiniPayRequest | **前端支付调起参数** |

**miniPayRequest 子对象：**

| 字段 | 类型 | 说明 |
|---|---|---|
| appId | String | 微信 AppID |
| timeStamp | String | 时间戳 |
| nonceStr | String | 随机字符串 |
| packageValue | String | 预支付标识（prepay_id=xxx） |
| signType | String | 签名方式（RSA） |
| paySign | String | 签名 |

### 使用示例

#### 后端：生成支付参数

```java
import com.chinaums.sdk.request.MiniProgramPayRequest;
import com.chinaums.sdk.response.MiniProgramPayResponse;
import com.chinaums.sdk.response.MiniPayRequest;

MiniProgramPayRequest request = MiniProgramPayRequest.builder()
    .merOrderId("ORDER" + System.currentTimeMillis())
    .totalAmount(100L)
    .subAppId("wx1234567890abcdef")
    .subOpenId("o1234567890abcdef")
    .notifyUrl("https://your-domain.com/api/notify")
    .orderDesc("商品购买")
    .build();

try {
    MiniProgramPayResponse response = client.miniProgramPay(request);
    if (response.isSuccess()) {
        MiniPayRequest payRequest = response.getMiniPayRequest();
        // 将 payRequest 序列化后返回给前端
        Map<String, String> result = new HashMap<>();
        result.put("appId", payRequest.getAppId());
        result.put("timeStamp", payRequest.getTimeStamp());
        result.put("nonceStr", payRequest.getNonceStr());
        result.put("packageValue", payRequest.getPackageValue());
        result.put("signType", payRequest.getSignType());
        result.put("paySign", payRequest.getPaySign());
        return result;
    }
} catch (UmsPayBusinessException e) {
    logger.error("业务错误: {} - {}", e.getErrorCode(), e.getMessage());
}
```

#### 前端：调起微信支付

```javascript
function requestPayment(miniPayRequest) {
    wx.requestPayment({
        timeStamp:  miniPayRequest.timeStamp,
        nonceStr:   miniPayRequest.nonceStr,
        package:    miniPayRequest.packageValue,
        signType:   miniPayRequest.signType,
        paySign:    miniPayRequest.paySign,
        success: function(res) {
            // 支付成功，查询后端确认订单状态
            queryOrderStatus(miniPayRequest.merOrderId);
        },
        fail: function(res) {
            if (res.errMsg.indexOf('cancel') !== -1) {
                console.log('用户取消支付');
            } else {
                console.error('支付失败:', res.errMsg);
            }
        }
    });
}
```

### 完整流程图

```
后端服务                           微信小程序                      微信服务器
   │                                 │                               │
   │  1. miniProgramPay()            │                               │
   │  ──────────>                    │                               │
   │  miniPayRequest                 │                               │
   │  <──────────                    │                               │
   │                                 │                               │
   │                  2. wx.requestPayment(miniPayRequest)           │
   │                                 │  ──────────────────────────>  │
   │                                 │                               │
   │                  3. 用户确认支付                                  │
   │                                 │  <──────────────────────────  │
   │                                 │                               │
   │  4. 支付回调通知 (notifyUrl)     │                               │
   │  <──────────────────────────────│                               │
   │                                 │                               │
   │  5. 查询订单状态                  │                               │
   │  ──────────>                    │                               │
   │  <──────────                    │                               │
```

### AI 提示词示例

```
请帮我实现微信小程序支付的后端接口：
1. 接收前端传来的 subOpenId
2. 使用 ums-sdk 发起小程序支付
3. 将 miniPayRequest 返回给前端
4. 处理支付回调通知
```

---

## Skill 4: 订单查询

### 技能定义

查询订单的交易状态和详细信息，用于确认支付结果或轮询订单状态。

### 输入参数

| 参数 | 类型 | 必填 | 说明 |
|---|---|---|---|
| merOrderId | String | ✅ | 商户订单号 |

### 输出格式

| 字段 | 类型 | 说明 |
|---|---|---|
| status | String | 订单状态 |
| totalAmount | Long | 订单金额（分） |
| merOrderId | String | 商户订单号 |
| payTime | String | 支付时间 |
| targetOrderId | String | 第三方订单号 |
| buyerPayAmount | Long | 买家实付金额（分） |

**订单状态值：**

| 状态 | 说明 |
|---|---|
| WAIT_BUYER_PAY | 等待支付 |
| TRADE_SUCCESS | 支付成功 |
| TRADE_CLOSED | 交易关闭（超时/取消） |
| TRADE_FAIL | 支付失败 |

### 使用示例

```java
import com.chinaums.sdk.request.QueryRequest;
import com.chinaums.sdk.response.QueryResponse;

QueryRequest request = new QueryRequest("ORDER123456");

try {
    QueryResponse response = client.query(request);
    if (response.isSuccess()) {
        String status = response.getStatus();
        if ("TRADE_SUCCESS".equals(status)) {
            // 订单已支付
            orderService.confirmPayment(
                response.getMerOrderId(),
                response.getTargetOrderId(),
                response.getTotalAmount()
            );
        }
    }
} catch (UmsPayBusinessException e) {
    logger.error("查询失败: {} - {}", e.getErrorCode(), e.getMessage());
}
```

### AI 提示词示例

```
请帮我实现一个订单状态轮询接口，使用 ums-sdk 每隔 3 秒查询一次订单状态，
最多查询 10 次，直到状态变为 TRADE_SUCCESS 或超时。
```

---

## Skill 5: 退款

### 技能定义

发起退款请求，支持全额退款和部分退款。

### 输入参数

| 参数 | 类型 | 必填 | 说明 |
|---|---|---|---|
| merOrderId | String | ✅ | 原商户订单号 |
| refundAmount | Long | ✅ | 退款金额（分），不能超过原订单金额 |
| refundOrderId | String | ❌ | 退款订单号，需唯一 |
| refundDesc | String | ❌ | 退款原因描述 |
| platformAmount | Long | ❌ | 平台金额（分） |
| subOrders | List\<SubOrder\> | ❌ | 子订单列表（分账退款场景） |

### 输出格式

| 字段 | 类型 | 说明 |
|---|---|---|
| status | String | 退款状态 |
| totalAmount | Long | 原订单金额（分） |
| refundAmount | Long | 退款金额（分） |
| merOrderId | String | 原商户订单号 |
| refundOrderId | String | 退款订单号 |

**退款状态值：**

| 状态 | 说明 |
|---|---|
| SUCCESS | 退款成功 |
| PROCESSING | 退款处理中 |

### 使用示例

```java
import com.chinaums.sdk.request.RefundRequest;
import com.chinaums.sdk.response.RefundResponse;

RefundRequest request = RefundRequest.builder()
    .merOrderId("ORDER123456")
    .refundOrderId("REFUND" + System.currentTimeMillis())
    .refundAmount(50L)
    .refundDesc("商品退货")
    .build();

try {
    RefundResponse response = client.refund(request);
    if (response.isSuccess()) {
        String refundStatus = response.getStatus();
        if ("PROCESSING".equals(refundStatus)) {
            // 退款处理中，需轮询查询
            scheduleRefundQuery(response.getRefundOrderId());
        }
    }
} catch (UmsPayBusinessException e) {
    logger.error("退款失败: {} - {}", e.getErrorCode(), e.getMessage());
}
```

### AI 提示词示例

```
请帮我实现退款功能：对订单 ORDER123456 退款 30 元，
退款原因为"用户申请退款"，退款单号自动生成。
如果退款状态为 PROCESSING，需要定时查询退款结果。
```

---

## Skill 6: 退款查询

### 技能定义

查询退款订单的处理状态和详细信息。

### 输入参数

| 参数 | 类型 | 必填 | 说明 |
|---|---|---|---|
| merOrderId | String | ✅ | 退款订单号（即退款时传入的 refundOrderId） |

### 输出格式

| 字段 | 类型 | 说明 |
|---|---|---|
| refundStatus | String | 退款状态 |
| status | String | 订单状态 |
| totalAmount | Long | 原订单金额（分） |
| merOrderId | String | 商户订单号 |
| refundOrderId | String | 退款订单号 |

**退款状态值：**

| 状态 | 说明 |
|---|---|
| SUCCESS | 退款成功 |
| PROCESSING | 退款处理中 |
| FAIL | 退款失败 |

### 使用示例

```java
import com.chinaums.sdk.request.RefundQueryRequest;
import com.chinaums.sdk.response.RefundQueryResponse;

RefundQueryRequest request = new RefundQueryRequest("REFUND123456");

try {
    RefundQueryResponse response = client.refundQuery(request);
    if (response.isSuccess()) {
        String refundStatus = response.getRefundStatus();
        switch (refundStatus) {
            case "SUCCESS":
                logger.info("退款成功");
                break;
            case "PROCESSING":
                logger.info("退款处理中，请稍后查询");
                break;
            case "FAIL":
                logger.warn("退款失败");
                break;
        }
    }
} catch (UmsPayBusinessException e) {
    logger.error("退款查询失败: {} - {}", e.getErrorCode(), e.getMessage());
}
```

---

## Skill 7: 支付回调通知验签

### 技能定义

验证银联商务支付回调通知的签名真实性，防止伪造通知攻击。**必须在处理回调业务逻辑前调用。**

### 输入参数

| 参数 | 类型 | 必填 | 说明 |
|---|---|---|---|
| params | Map\<String, String\> | ✅ | 回调通知的全部参数（包括 sign、signType） |
| notifyKey | String | ✅ | 通讯密钥（通过 UmsPayConfig 配置） |

### 输出格式

| 返回值 | 类型 | 说明 |
|---|---|---|
| verified | boolean | `true` = 签名验证通过，`false` = 签名无效 |

### 使用示例

```java
import com.chinaums.sdk.UmsPayClient;
import java.util.Map;

@PostMapping("/api/notify")
public String handlePaymentNotify(HttpServletRequest request) {
    Map<String, String> params = extractParams(request);

    // 第一步：验证签名（必须！）
    boolean verified = client.verifyNotification(params);
    if (!verified) {
        logger.warn("回调签名验证失败: {}", params);
        return "FAIL";
    }

    // 第二步：处理业务逻辑
    String status = params.get("status");
    String merOrderId = params.get("merOrderId");
    String totalAmount = params.get("totalAmount");
    String targetOrderId = params.get("targetOrderId");

    if ("TRADE_SUCCESS".equals(status)) {
        orderService.paySuccess(merOrderId, targetOrderId);
    } else if ("TRADE_CLOSED".equals(status)) {
        orderService.payClosed(merOrderId);
    }

    // 第三步：返回成功响应
    return "SUCCESS";
}
```

### 回调通知关键字段

| 字段 | 说明 |
|---|---|
| status | 交易状态（TRADE_SUCCESS / TRADE_CLOSED） |
| merOrderId | 商户订单号 |
| totalAmount | 订单金额（分） |
| targetOrderId | 第三方订单号 |
| sign | 签名值 |
| signType | 签名类型（SHA256 / MD5） |

### 注意事项

- **必须先验签再处理业务**，否则可能遭受伪造通知攻击
- 回调接口必须实现**幂等性**，微信可能会多次发送同一通知
- 返回 `"SUCCESS"` 告知微信服务器已收到回调，否则会持续重发
- 如果长时间未收到回调，建议使用 Skill 4 主动查询订单状态作为兜底

### AI 提示词示例

```
请帮我实现银联商务支付回调通知接口：
1. 接收 POST 请求
2. 使用 ums-sdk 验证签名
3. 根据支付状态更新订单
4. 实现幂等性处理
5. 返回 SUCCESS/FAIL
```

---

## 认证与安全规范

### 认证方式

ums-sdk 使用 **HmacSHA256 签名认证**，每次请求自动完成签名，无需手动处理。

**签名流程：**

```
1. 对请求体进行 SHA-256 哈希 → contentSha256
2. 拼接签名串: appId + timestamp + nonce + contentSha256
3. 使用 appKey 进行 HmacSHA256 签名
4. Base64 编码得到最终签名
```

**GET 请求：** 签名参数通过 URL query string 传递

```
?authorization=OPEN-FORM-PARAM&appId=xxx&timestamp=xxx&nonce=xxx&content=xxx&signature=xxx
```

**POST 请求：** 签名参数通过 Authorization 头传递

```
Authorization: OPEN-BODY-SIG AppId="xxx",Timestamp="xxx",Nonce="xxx",Signature="xxx"
```

### 凭证管理

**⚠️ 安全红线：切勿在源代码中硬编码敏感凭证！**

推荐方式：

```bash
# 方式一：环境变量
export UMS_APP_ID="your-app-id"
export UMS_APP_KEY="your-app-key"
export UMS_MID="898201612345678"
export UMS_TID="88880001"
export UMS_NOTIFY_KEY="your-notify-key"
```

```java
// 方式二：从环境变量读取
UmsPayConfig config = UmsPayConfig.builder()
    .appId(System.getenv("UMS_APP_ID"))
    .appKey(System.getenv("UMS_APP_KEY"))
    .mid(System.getenv("UMS_MID"))
    .tid(System.getenv("UMS_TID"))
    .notifyKey(System.getenv("UMS_NOTIFY_KEY"))
    .build();
```

```bash
# 方式三：JVM 系统属性
java -Dums.appId=your-app-id -Dums.appKey=your-app-key -jar app.jar
```

### 应答验签

SDK 自动验证服务器应答签名（通过响应头 `Authorization` 字段），验签失败会抛出 `UmsPayAuthException`。

---

## 频率限制与超时配置

### API 频率限制

| 限制项 | 说明 |
|---|---|
| 单商户 QPS | 以银联商务开放平台分配为准 |
| 回调重试 | 微信支付回调最多重试 8 次，间隔 15s/15s/30s/3m/10m/20m/30m/30m |

### 超时配置

| 配置项 | 默认值 | 推荐值 | 说明 |
|---|---|---|---|
| connectTimeout | 10000ms | 5000-15000ms | 建立 TCP 连接的超时时间 |
| readTimeout | 30000ms | 15000-60000ms | 等待服务器响应的超时时间 |
| maxRetries | 3 | 2-5 | 网络异常时的最大重试次数 |

### 超时配置示例

```java
UmsPayConfig config = UmsPayConfig.builder()
    .appId("your-app-id")
    .appKey("your-app-key")
    .mid("898201612345678")
    .tid("88880001")
    .connectTimeout(5000)
    .readTimeout(30000)
    .maxRetries(3)
    .build();
```

---

## 错误处理手册

### 异常体系

```
UmsPayException
  ├── UmsPayAuthException      (认证异常)
  ├── UmsPayNetworkException   (网络异常)
  └── UmsPayBusinessException  (业务异常)
```

### 异常分类与处理策略

| 异常类型 | 错误码 | 典型场景 | 处理策略 |
|---|---|---|---|
| UmsPayAuthException | AUTH_ERROR | 签名验证失败、notifyKey 未配置 | 检查 appId/appKey/notifyKey 配置 |
| UmsPayNetworkException | NETWORK_ERROR | 连接超时、网络不可达、HTTP 非 2xx | 可重试，检查网络连接 |
| UmsPayBusinessException | 动态 | 订单号重复、参数错误、余额不足 | 检查请求参数，不可重试 |

### 常见业务错误码

| 错误码 | 说明 | 处理建议 |
|---|---|---|
| INVALID_PARAMETER | 参数错误 | 检查请求参数 |
| ORDER_NOT_EXIST | 订单不存在 | 确认订单号是否正确 |
| ORDER_DUPLICATE | 订单号重复 | 更换唯一订单号 |
| ORDER_EXPIRED | 订单已过期 | 重新下单 |
| REFUND_EXCEED | 退款金额超限 | 检查退款金额不超过原订单金额 |
| BALANCE_NOT_ENOUGH | 余额不足 | 联系银联商务客服 |

### 统一错误处理模板

```java
try {
    H5PayResponse response = client.h5Pay(request);
    if (response.isSuccess()) {
        // 业务成功处理
    }
} catch (UmsPayAuthException e) {
    logger.error("[认证失败] requestId={}, msg={}", e.getRequestId(), e.getMessage());
    // 提示用户检查配置
} catch (UmsPayNetworkException e) {
    logger.error("[网络异常] requestId={}, msg={}", e.getRequestId(), e.getMessage());
    // 可重试或提示用户稍后再试
} catch (UmsPayBusinessException e) {
    logger.error("[业务错误] requestId={}, code={}, msg={}",
        e.getRequestId(), e.getErrorCode(), e.getMessage());
    // 根据错误码进行不同处理
    switch (e.getErrorCode()) {
        case "ORDER_DUPLICATE":
            // 更换订单号重试
            break;
        case "INVALID_PARAMETER":
            // 提示用户检查输入
            break;
        default:
            // 通用错误提示
            break;
    }
}
```

### 异常字段说明

每个异常都包含以下诊断字段：

| 字段 | 类型 | 说明 |
|---|---|---|
| errorCode | String | 错误码 |
| requestId | String | 请求 ID（UUID），用于日志追踪 |
| timestamp | long | 异常发生时间戳（毫秒） |

---

## 最佳实践

### 1. 客户端管理

```java
// ✅ 推荐：单例模式
public class UmsPayClientHolder {
    private static volatile UmsPayClient instance;

    public static UmsPayClient getInstance() {
        if (instance == null) {
            synchronized (UmsPayClientHolder.class) {
                if (instance == null) {
                    UmsPayConfig config = UmsPayConfig.builder()
                        .appId(System.getenv("UMS_APP_ID"))
                        .appKey(System.getenv("UMS_APP_KEY"))
                        .mid(System.getenv("UMS_MID"))
                        .tid(System.getenv("UMS_TID"))
                        .build();
                    instance = new UmsPayClient(config);
                }
            }
        }
        return instance;
    }
}
```

```java
// ✅ 推荐：H5 和小程序使用独立实例
UmsPayClient h5Client = new UmsPayClient(h5Config);      // instMid=H5DEFAULT
UmsPayClient miniClient = new UmsPayClient(miniConfig);   // instMid=MINIDEFAULT
```

### 2. 订单号生成

```java
// ✅ 推荐：使用时间戳 + 随机数确保唯一性
String merOrderId = "ORD" + System.currentTimeMillis()
    + String.format("%04d", new Random().nextInt(10000));
```

### 3. 回调处理

```java
// ✅ 推荐：先验签，再处理，实现幂等
@PostMapping("/api/notify")
public String handleNotify(Map<String, String> params) {
    if (!client.verifyNotification(params)) {
        return "FAIL";
    }

    String merOrderId = params.get("merOrderId");
    // 幂等检查：如果已处理过该订单，直接返回 SUCCESS
    if (orderService.isProcessed(merOrderId)) {
        return "SUCCESS";
    }

    // 处理业务逻辑
    orderService.processPayment(params);
    return "SUCCESS";
}
```

### 4. 轮询兜底

```java
// ✅ 推荐：回调 + 轮询双重保障
@Scheduled(fixedRate = 60000)
public void pollPendingOrders() {
    List<Order> pendingOrders = orderService.findPendingOrders();
    for (Order order : pendingOrders) {
        if (order.getCreateTime().plusMinutes(5).isBefore(LocalDateTime.now())) {
            QueryRequest request = new QueryRequest(order.getMerOrderId());
            QueryResponse response = client.query(request);
            if ("TRADE_SUCCESS".equals(response.getStatus())) {
                orderService.confirmPayment(order.getId());
            }
        }
    }
}
```

### 5. 日志配置

```xml
<!-- logback.xml 推荐配置 -->
<logger name="com.chinaums.sdk" level="INFO" />

<!-- 调试时可临时设为 DEBUG -->
<!-- <logger name="com.chinaums.sdk" level="DEBUG" /> -->
```

### 6. 金额处理

```java
// ✅ 推荐：使用 Long 类型，单位为分，避免浮点精度问题
Long totalAmount = 100L;  // 1 元 = 100 分

// ❌ 避免：使用 double/float 处理金额
double amount = 1.00;  // 可能产生精度问题
```

---

## AI 工具集成提示词模板

以下模板可帮助 AI 工具快速理解和使用 ums-sdk：

### 模板 1：通用支付集成

```
你是一个 Java 支付集成专家。请使用 ums-sdk（银联商务支付 SDK）帮我实现以下功能：

场景：{描述你的业务场景}
支付方式：{H5支付 / 小程序支付}
金额：{金额} 元
回调地址：{回调URL}

要求：
1. 使用环境变量管理敏感凭证
2. 完善的异常处理
3. 支付回调验签
4. 订单状态轮询兜底

SDK 关键类：
- UmsPayClient: 门面入口
- UmsPayConfig: 配置（Builder 模式）
- H5PayRequest / MiniProgramPayRequest: 请求
- H5PayResponse / MiniProgramPayResponse: 响应
- 异常: UmsPayAuthException, UmsPayNetworkException, UmsPayBusinessException
```

### 模板 2：小程序支付完整流程

```
请使用 ums-sdk 实现微信小程序支付的完整流程：

1. 后端接口：接收 subOpenId，调用 miniProgramPay()，返回 miniPayRequest
2. 前端代码：使用 miniPayRequest 调用 wx.requestPayment()
3. 回调接口：验证签名，更新订单状态
4. 查询接口：主动查询订单状态

配置要求：
- instMid 必须为 MINIDEFAULT
- notifyKey 必须配置（回调验签需要）
- 使用独立的 UmsPayClient 实例
```

### 模板 3：问题排查

```
我在使用 ums-sdk 时遇到了以下问题：

错误信息：{粘贴错误信息}
请求代码：{粘贴代码}

请帮我排查可能的原因：
1. 是否为认证问题（UmsPayAuthException）→ 检查 appId/appKey
2. 是否为网络问题（UmsPayNetworkException）→ 检查网络/超时配置
3. 是否为业务问题（UmsPayBusinessException）→ 检查请求参数
4. 是否为签名问题 → 检查系统时间是否准确
```

### 模板 4：代码审查

```
请审查以下 ums-sdk 集成代码，检查是否存在以下问题：

1. 凭证是否硬编码（安全风险）
2. 异常处理是否完善
3. 回调是否验签
4. 是否实现幂等性
5. 金额是否使用 Long 类型（分）
6. UmsPayClient 是否使用单例
7. H5 和小程序是否使用独立实例

代码：{粘贴代码}
```

---

## 技术术语表

| 术语 | 说明 |
|---|---|
| appId | 银联商务开放平台分配的应用标识 |
| appKey | 银联商务开放平台分配的应用密钥，用于签名 |
| mid | Merchant ID，商户号 |
| tid | Terminal ID，终端号 |
| instMid | 机构商户号，标识业务类型（H5DEFAULT / MINIDEFAULT） |
| notifyKey | 通讯密钥，用于支付回调通知验签 |
| merOrderId | 商户订单号，由商户生成，需全局唯一 |
| subAppId | 微信小程序的 AppID |
| subOpenId | 微信用户的 OpenID，通过 wx.login 获取 |
| prepay_id | 微信预支付交易会话标识，用于调起支付 |
| HmacSHA256 | 基于哈希的消息认证码算法，SDK 使用的签名算法 |
| OPEN-BODY-SIG | POST 请求签名方式，签名通过 Authorization 头传递 |
| OPEN-FORM-PARAM | GET 请求签名方式，签名通过 URL 参数传递 |
| TRADE_SUCCESS | 交易成功状态 |
| WAIT_BUYER_PAY | 等待买家支付状态 |
| TRADE_CLOSED | 交易关闭状态 |
| 幂等性 | 同一操作执行多次与执行一次的效果相同 |
