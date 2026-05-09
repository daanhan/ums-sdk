# 银联商务支付SDK

[English](README.md)

一个简洁易用的银联商务支付接口Java SDK，支持微信H5支付、**微信小程序支付**、订单查询、退款、退款查询等功能。

## 功能特性

- ✅ **简单易用**：Builder模式配置，5分钟即可上手
- ✅ **功能完整**：覆盖H5支付、微信小程序支付、订单查询、退款、退款查询全流程
- ✅ **安全可靠**：自动签名验证和证书管理机制
- ✅ **文档齐全**：中英文双语文档，丰富的示例代码
- ✅ **质量保证**：单元测试覆盖率 > 80%，所有测试通过

## 环境要求

- Java 1.8及以上版本
- Maven 3.6+

## 快速开始

### 1. 添加Maven依赖

```xml
<dependency>
    <groupId>com.chinaums</groupId>
    <artifactId>ums-sdk</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>
```

### 2. 基本配置

```java
import com.chinaums.sdk.UmsPayClient;
import com.chinaums.sdk.config.UmsPayConfig;
import com.chinaums.sdk.config.Environment;

UmsPayConfig config = UmsPayConfig.builder()
    .appId("your-app-id")
    .appKey("your-app-key")
    .mid("898201612345678")
    .tid("88880001")
    .environment(Environment.PRODUCTION)  // 或 Environment.TEST
    .build();

UmsPayClient client = new UmsPayClient(config);
```

### 3. H5支付

```java
import com.chinaums.sdk.request.H5PayRequest;
import com.chinaums.sdk.response.H5PayResponse;

H5PayRequest request = H5PayRequest.builder()
    .merOrderId("ORDER123456")
    .totalAmount(100L)  // 单位：分
    .notifyUrl("https://your-domain.com/notify")
    .returnUrl("https://your-domain.com/return")
    .build();

H5PayResponse response = client.h5Pay(request);
if (response.isSuccess()) {
    String payUrl = response.getPayUrl();
    // 引导用户跳转到payUrl完成支付
}
```

### 4. 微信小程序支付

微信小程序支付覆盖 **5大核心阶段**：

```
后端服务                             微信小程序                      微信服务器
     |                                   |                               |
     |  1. miniProgramPay()               |                               |
     |  ---------------->                |                               |
     |  miniPayRequest                    |                               |
     |  <-------------------------------- |                               |
     |                                   |                               |
     |                    2. wx.requestPayment(miniPayRequest)           |
     |                                   |  --------------------------> |
     |                                   |                               |
     |                    3. 用户确认支付                                 |
     |                                   |  <-------------------------- |
     |                                   |                               |
     |  4. 支付回调通知 (notifyUrl)       |                               |
     |  <------------------------------- |                               |
     |                                   |                               |
     |  5. 查询订单状态                    |                               |
     |  ---------------->                |                               |
     |  <-------------------------------- |                               |
```

#### 阶段1：后端 - 生成支付参数

```java
import com.chinaums.sdk.UmsPayClient;
import com.chinaums.sdk.config.UmsPayConfig;
import com.chinaums.sdk.config.Environment;
import com.chinaums.sdk.request.MiniProgramPayRequest;
import com.chinaums.sdk.response.MiniProgramPayResponse;
import com.chinaums.sdk.response.MiniPayRequest;

// 配置微信小程序专用的instMid
UmsPayConfig config = UmsPayConfig.builder()
    .appId("your-app-id")
    .appKey("your-app-key")
    .mid("898201612345678")
    .tid("88880001")
    .instMid("MINIDEFAULT")            // 小程序支付必填
    .notifyKey("your-notify-key")       // 回调验签必填
    .environment(Environment.PRODUCTION)
    .build();

UmsPayClient client = new UmsPayClient(config);

// 构建小程序支付请求
MiniProgramPayRequest request = MiniProgramPayRequest.builder()
    .merOrderId("ORDER" + System.currentTimeMillis())  // 商户订单号，需唯一
    .totalAmount(100L)                                  // 支付金额（分），1元=100
    .subAppId("wx1234567890abcdef")                     // 微信小程序AppID
    .subOpenId("o1234567890abcdef")                     // 用户OpenID（微信登录获取）
    .notifyUrl("https://your-domain.com/api/notify")    // 支付结果回调地址
    .orderDesc("订单描述")                               // 可选
    .limitCreditCard(false)                              // 可选：是否限制信用卡
    .build();

try {
    MiniProgramPayResponse response = client.miniProgramPay(request);

    if (response.isSuccess()) {
        MiniPayRequest payRequest = response.getMiniPayRequest();

        // 将以下参数返回给前端小程序，用于调起微信支付
        System.out.println("appId:       " + payRequest.getAppId());
        System.out.println("timeStamp:   " + payRequest.getTimeStamp());
        System.out.println("nonceStr:    " + payRequest.getNonceStr());
        System.out.println("package:     " + payRequest.getPackageValue());  // prepay_id=xxx
        System.out.println("signType:    " + payRequest.getSignType());      // RSA
        System.out.println("paySign:     " + payRequest.getPaySign());
    }
} catch (UmsPayBusinessException e) {
    // 业务异常（参数错误、订单号重复等）
    System.err.println("业务错误: " + e.getErrorCode() + " - " + e.getMessage());
} catch (UmsPayNetworkException e) {
    // 网络异常（可重试）
    System.err.println("网络错误: " + e.getMessage());
}
```

#### 阶段2：前端 - 调起微信支付

```javascript
// 小程序端代码 - 收到后端返回的支付参数后调起支付
// 后端API返回的 miniPayRequest 对象
function requestPayment(miniPayRequest) {
    wx.requestPayment({
        timeStamp:  miniPayRequest.timeStamp,
        nonceStr:   miniPayRequest.nonceStr,
        package:    miniPayRequest.packageValue,  // 注意是 packageValue，不是 package
        signType:   miniPayRequest.signType,
        paySign:    miniPayRequest.paySign,
        success: function(res) {
            // 支付成功 - 后端会收到回调通知
            // 调后端API确认订单状态
            wx.request({
                url: 'https://your-domain.com/api/order/status',
                data: { merOrderId: miniPayRequest.merOrderId },
                success: function(orderRes) {
                    console.log('订单状态:', orderRes.data.status);
                }
            });
        },
        fail: function(res) {
            if (res.errMsg.indexOf('cancel') !== -1) {
                console.log('用户取消支付');
            } else {
                console.error('支付失败:', res.errMsg);
            }
        },
        complete: function(res) {
            console.log('支付完成:', res);
        }
    });
}
```

#### 阶段3：后端 - 支付回调通知验签

```java
import com.chinaums.sdk.UmsPayClient;
import java.util.Map;
import java.util.HashMap;

// 接收微信支付回调的接口
// POST https://your-domain.com/api/notify
public String handlePaymentNotify(HttpServletRequest request) {
    Map<String, String> params = extractParams(request);

    // 验证回调签名
    boolean verified = client.verifyNotification(params);
    if (!verified) {
        return "FAIL";  // 签名无效，拒绝回调
    }

    String status = params.get("status");
    String merOrderId = params.get("merOrderId");
    String totalAmount = params.get("totalAmount");
    String targetOrderId = params.get("targetOrderId");

    logger.info("收到支付回调 - 订单号: {}, 状态: {}, 金额: {}",
        merOrderId, status, totalAmount);

    if ("TRADE_SUCCESS".equals(status)) {
        // 更新订单为已支付
        orderService.paySuccess(merOrderId, targetOrderId);
    } else if ("TRADE_CLOSED".equals(status)) {
        // 处理订单关闭
        orderService.payClosed(merOrderId);
    }

    return "SUCCESS";  // 通知微信服务器已收到回调
}
```

#### 阶段4：查询订单状态

```java
import com.chinaums.sdk.request.QueryRequest;
import com.chinaums.sdk.response.QueryResponse;

QueryRequest request = new QueryRequest("ORDER123456");
QueryResponse response = client.query(request);

if (response.isSuccess()) {
    // 可能的交易状态：
    // WAIT_BUYER_PAY  - 等待支付
    // TRADE_SUCCESS   - 支付成功
    // TRADE_CLOSED    - 交易关闭（超时/取消）
    // TRADE_FAIL      - 支付失败
    String status = response.getStatus();
    Long amount = response.getTotalAmount();
    String targetOrderId = response.getTargetOrderId();
    String payTime = response.getPayTime();
}
```

#### 阶段5：退款

```java
import com.chinaums.sdk.request.RefundRequest;
import com.chinaums.sdk.response.RefundResponse;

RefundRequest request = RefundRequest.builder()
    .merOrderId("ORDER123456")    // 原订单号
    .refundOrderId("REFUND123456") // 退款单号（需唯一）
    .refundAmount(50L)            // 退款金额（不能超过原订单金额）
    .refundDesc("退款原因")
    .build();

RefundResponse response = client.refund(request);
if (response.isSuccess()) {
    String refundStatus = response.getStatus();
    // SUCCESS: 退款成功
    // PROCESSING: 退款处理中
}
```

#### 下单返回结果示例

```json
{
    "errCode": "SUCCESS",
    "errMsg": "微信下单",
    "merOrderId": "327M20220419111222",
    "status": "WAIT_BUYER_PAY",
    "totalAmount": 100,
    "seqId": "20230719162959",
    "targetOrderId": "64342023071922595938541723006782",
    "targetSys": "WXPay",
    "miniPayRequest": {
        "appId": "wx1234567890abcdef",
        "timeStamp": "1687285800",
        "nonceStr": "5K8264ILTKCH16CQ2502SI8ZNMTM67VS",
        "package": "prepay_id=wx201410272009395522657a690389285100",
        "signType": "RSA",
        "paySign": "C3804EC3BD4E6FEB2C9B6C0F9D3E8F1A2B3C4D5E6F7A8B9C0D1E2F3A4B5C6D7"
    },
    "merName": "测试商户"
}
```

### 5. 订单查询

```java
import com.chinaums.sdk.request.QueryRequest;
import com.chinaums.sdk.response.QueryResponse;

QueryRequest request = new QueryRequest("ORDER123456");
QueryResponse response = client.query(request);

if (response.isSuccess()) {
    String status = response.getStatus();
    // WAIT_BUYER_PAY: 等待支付
    // TRADE_SUCCESS: 支付成功
    // TRADE_CLOSED: 交易关闭
}
```

### 6. 退款

```java
import com.chinaums.sdk.request.RefundRequest;
import com.chinaums.sdk.response.RefundResponse;

RefundRequest request = RefundRequest.builder()
    .merOrderId("ORDER123456")
    .refundOrderId("REFUND123456")
    .refundAmount(50L)  // 单位：分
    .refundDesc("退款原因")
    .build();

RefundResponse response = client.refund(request);
```

### 7. 退款查询

```java
import com.chinaums.sdk.request.RefundQueryRequest;
import com.chinaums.sdk.response.RefundQueryResponse;

RefundQueryRequest request = new RefundQueryRequest("REFUND123456");
RefundQueryResponse response = client.refundQuery(request);

if (response.isSuccess()) {
    String refundStatus = response.getRefundStatus();
    // SUCCESS: 退款成功
    // PROCESSING: 退款处理中
    // FAIL: 退款失败
}
```

## 配置选项

| 选项 | 类型 | 必填 | 默认值 | 说明 |
|------|------|------|--------|------|
| appId | String | 是 | - | 银联商务平台分配的应用ID |
| appKey | String | 是 | - | 银联商务平台分配的应用密钥 |
| mid | String | 是 | - | 商户号 |
| tid | String | 是 | - | 终端号 |
| instMid | String | 否 | H5DEFAULT | 业务类型。**微信小程序支付请使用 `MINIDEFAULT`** |
| notifyKey | String | 否 | - | 通讯密钥，用于支付回调通知验签 |
| environment | Environment | 否 | PRODUCTION | 环境（PRODUCTION/TEST） |
| connectTimeout | int | 否 | 10000 | 连接超时时间（毫秒） |
| readTimeout | int | 否 | 30000 | 读取超时时间（毫秒） |
| maxRetries | int | 否 | 3 | 最大重试次数 |
| enableCertValidation | boolean | 否 | true | 是否启用证书验证 |

## 异常处理

SDK提供了完整的异常处理体系：

```java
import com.chinaums.sdk.exception.*;

try {
    H5PayResponse response = client.h5Pay(request);
} catch (UmsPayAuthException e) {
    // 认证失败，检查appId/appKey配置
    logger.error("认证失败: {}", e.getMessage());
} catch (UmsPayNetworkException e) {
    // 网络问题，可以重试
    logger.error("网络异常: {}", e.getMessage());
} catch (UmsPayBusinessException e) {
    // 业务错误，检查请求参数
    logger.error("业务错误: {} - {}", e.getErrorCode(), e.getMessage());
}
```

## 最佳实践

1. **单例模式**：UmsPayClient建议使用单例模式，避免重复初始化
2. **独立客户端实例**：微信H5支付（`instMid=H5DEFAULT`）和小程序支付（`instMid=MINIDEFAULT`）使用独立的UmsPayClient实例，避免配置冲突
3. **异常处理**：合理处理各种异常，给用户提供友好的错误提示
4. **日志记录**：开启适当的日志级别，便于调试和监控
5. **超时配置**：根据网络环境调整超时设置
6. **安全性**：不要在代码中硬编码appKey等敏感信息，建议使用环境变量或系统属性

## 敏感信息管理

**切勿在源代码中硬编码敏感凭证**，推荐使用环境变量或JVM系统属性：

```bash
# 通过环境变量设置
export UMS_APP_ID="your-app-id"
export UMS_APP_KEY="your-app-key"
export UMS_MID="898201612345678"

# 或通过JVM系统属性
java -Dums.appId=your-app-id -Dums.appKey=your-app-key -jar app.jar
```

```java
// 从环境变量读取配置
UmsPayConfig config = UmsPayConfig.builder()
    .appId(System.getenv("UMS_APP_ID"))
    .appKey(System.getenv("UMS_APP_KEY"))
    .mid(System.getenv("UMS_MID"))
    .tid(System.getenv("UMS_TID"))
    .build();
```

> **提示：** 集成测试也支持通过系统属性覆盖参数：`-Dums.appId=xxx`、`-Dums.appKey=xxx`、`-Dums.mid=xxx`、`-Dums.tid=xxx`、`-Dums.notifyKey=xxx`。

## 常见问题

### Q: 如何获取appId和appKey？
A: 需要在银联商务开放平台注册并创建应用，即可获得appId和appKey。

### Q: 如何在测试环境测试？
A: 在配置中设置 `environment(Environment.TEST)` 即可使用测试环境。

### Q: 签名验证失败怎么办？
A: 检查appId和appKey是否正确，确保系统时间准确。

### Q: 支付超时如何处理？
A: 调整 `readTimeout` 配置，或使用异步通知机制。

### Q: 微信小程序支付和H5支付有什么区别？
A: 小程序支付需要传入 `subAppId`（微信小程序AppID）和 `subOpenId`（用户的OpenID），使用 `instMid=MINIDEFAULT`，返回 `miniPayRequest` 参数用于 `wx.requestPayment()`。H5支付则返回跳转链接。

### Q: 如何获取用户的OpenID（subOpenId）？
A: 在前端调用微信小程序的 `wx.login()` 获取code，通过后端服务器换取 session_key 和 openid。详见[微信官方文档](https://developers.weixin.qq.com/miniprogram/dev/framework/open-ability/login.html)。

### Q: 如何处理支付回调通知？
A: 务必使用 `client.verifyNotification()` 验证回调签名后再进行业务处理。返回 `SUCCESS` 告知微信服务器已收到回调。注意实现幂等性，微信可能会多次发送回调。

### Q: 一直没有收到支付回调怎么办？
A: 建议实现主动轮询机制作为兜底方案，使用 `client.query()` 定时查询订单状态。超时场景也可通过手动退款处理。

## 贡献指南

我们欢迎所有形式的贡献！请遵循以下步骤：

1. Fork本仓库
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 创建Pull Request

## 许可证

本项目采用MIT许可证 - 详见 [LICENSE](LICENSE) 文件。

## 技术支持

- 📖 [官方文档](https://open.chinaums.com/saas-doc/openplate/netpay/mini-program/transaction/VxXa3w2o.html)
- 🐛 [问题反馈](https://github.com/daanhan/ums-sdk/issues)
- 💬 [讨论区](https://github.com/daanhan/ums-sdk/discussions)

## 更新日志

### v1.0.0-SNAPSHOT (2026-04-29)
- 新增**微信小程序支付**功能
- 支持微信H5支付
- 支持订单查询、退款、退款查询
- 小程序支付回调验签支持
- 完善的异常处理体系
- 中英文双语文档，包含小程序支付完整示例

---

**由银联商务SDK团队用 ❤️ 制作**

版权所有 © 2026 ChinaUMS。基于 [MIT 许可证](LICENSE) 发布。
