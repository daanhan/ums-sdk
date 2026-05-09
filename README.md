# ChinaUMS Payment SDK

[中文文档](README_CN.md)

A simple and easy-to-use Java SDK for ChinaUMS payment integration, supporting WeChat H5 payment, WeChat Mini Program payment, order query, refund, and refund query.

## Features

- ✅ **Simple and Easy to Use**: Get started in 5 minutes with Builder pattern configuration
- ✅ **Complete Functionality**: Covers H5 payment, Mini Program payment, order query, refund, and refund query
- ✅ **Secure and Reliable**: Automatic signature verification and certificate management
- ✅ **Well Documented**: Bilingual documentation (Chinese/English) with rich examples
- ✅ **High Quality**: Unit test coverage > 80%, all tests passing

## Requirements

- Java 1.8 or higher
- Maven 3.6+

## Quick Start

### 1. Add Maven Dependency

```xml
<dependency>
    <groupId>com.chinaums</groupId>
    <artifactId>ums-sdk</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>
```

### 2. Basic Configuration

```java
import com.chinaums.sdk.UmsPayClient;
import com.chinaums.sdk.config.UmsPayConfig;
import com.chinaums.sdk.config.Environment;

UmsPayConfig config = UmsPayConfig.builder()
    .appId("your-app-id")
    .appKey("your-app-key")
    .mid("898201612345678")
    .tid("88880001")
    .environment(Environment.PRODUCTION)  // or Environment.TEST
    .build();

UmsPayClient client = new UmsPayClient(config);
```

### 3. H5 Payment

```java
import com.chinaums.sdk.request.H5PayRequest;
import com.chinaums.sdk.response.H5PayResponse;

H5PayRequest request = H5PayRequest.builder()
    .merOrderId("ORDER123456")
    .totalAmount(100L)  // Unit: cents
    .notifyUrl("https://your-domain.com/notify")
    .returnUrl("https://your-domain.com/return")
    .build();

H5PayResponse response = client.h5Pay(request);
if (response.isSuccess()) {
    String payUrl = response.getPayUrl();
    // Redirect user to payUrl to complete payment
}
```

### 4. Mini Program Payment

WeChat Mini Program payment covers **5 key stages**:

```
Backend Server                          WeChat Mini Program               WeChat Server
     |                                       |                               |
     |  1. miniProgramPay()                   |                               |
     |  ---------------->                    |                               |
     |  miniPayRequest                       |                               |
     |  <---------------------------------   |                               |
     |                                       |                               |
     |                          2. wx.requestPayment(miniPayRequest)         |
     |                                       |  --------------------------> |
     |                                       |                               |
     |                          3. User confirms payment                    |
     |                                       |  <-------------------------- |
     |                                       |                               |
     |  4. Payment callback (notifyUrl)      |                               |
     |  <----------------------------------- |                               |
     |                                       |                               |
     |  5. Query order status                 |                               |
     |  ---------------->                    |                               |
     |  <---------------------------------   |                               |
```

#### Stage 1: Backend - Generate Payment Parameters

```java
import com.chinaums.sdk.UmsPayClient;
import com.chinaums.sdk.config.UmsPayConfig;
import com.chinaums.sdk.config.Environment;
import com.chinaums.sdk.request.MiniProgramPayRequest;
import com.chinaums.sdk.response.MiniProgramPayResponse;
import com.chinaums.sdk.response.MiniPayRequest;

// Configure with Mini Program-specific instMid
UmsPayConfig config = UmsPayConfig.builder()
    .appId("your-app-id")
    .appKey("your-app-key")
    .mid("898201612345678")
    .tid("88880001")
    .instMid("MINIDEFAULT")           // Required for Mini Program
    .notifyKey("your-notify-key")      // Required for callback verification
    .environment(Environment.PRODUCTION)
    .build();

UmsPayClient client = new UmsPayClient(config);

// Build mini program payment request
MiniProgramPayRequest request = MiniProgramPayRequest.builder()
    .merOrderId("ORDER" + System.currentTimeMillis())  // Unique merchant order ID
    .totalAmount(100L)                                  // Amount in cents (1 yuan = 100)
    .subAppId("wx1234567890abcdef")                     // WeChat Mini Program AppID
    .subOpenId("o1234567890abcdef")                     // User's OpenID from WeChat login
    .notifyUrl("https://your-domain.com/api/notify")    // Payment callback URL
    .orderDesc("Order Description")                     // Optional
    .limitCreditCard(false)                              // Optional: restrict credit card
    .build();

try {
    MiniProgramPayResponse response = client.miniProgramPay(request);
    
    if (response.isSuccess()) {
        MiniPayRequest payRequest = response.getMiniPayRequest();
        
        // Return these parameters to the Mini Program frontend
        // The frontend will use them to call wx.requestPayment()
        System.out.println("appId:       " + payRequest.getAppId());
        System.out.println("timeStamp:   " + payRequest.getTimeStamp());
        System.out.println("nonceStr:    " + payRequest.getNonceStr());
        System.out.println("package:     " + payRequest.getPackageValue());  // prepay_id=xxx
        System.out.println("signType:    " + payRequest.getSignType());      // RSA
        System.out.println("paySign:     " + payRequest.getPaySign());
    }
} catch (UmsPayBusinessException e) {
    // Handle business error (invalid parameters, duplicate order, etc.)
    System.err.println("Business error: " + e.getErrorCode() + " - " + e.getMessage());
} catch (UmsPayNetworkException e) {
    // Handle network error (can retry)
    System.err.println("Network error: " + e.getMessage());
}
```

#### Stage 2: Frontend - Call wx.requestPayment()

```javascript
// Mini Program JavaScript - Call after receiving payment params from backend
// The backend API returns the miniPayRequest object
function requestPayment(miniPayRequest) {
    wx.requestPayment({
        timeStamp:  miniPayRequest.timeStamp,
        nonceStr:   miniPayRequest.nonceStr,
        package:    miniPayRequest.packageValue,
        signType:   miniPayRequest.signType,
        paySign:    miniPayRequest.paySign,
        success: function(res) {
            // Payment successful - the backend will receive the callback
            // Call backend API to confirm order status
            wx.request({
                url: 'https://your-domain.com/api/order/status',
                data: { merOrderId: miniPayRequest.merOrderId },
                success: function(orderRes) {
                    console.log('Order status:', orderRes.data.status);
                }
            });
        },
        fail: function(res) {
            // Payment failed or cancelled by user
            if (res.errMsg.indexOf('cancel') !== -1) {
                console.log('User cancelled payment');
            } else {
                console.error('Payment failed:', res.errMsg);
            }
        },
        complete: function(res) {
            console.log('Payment completed:', res);
        }
    });
}
```

#### Stage 3: Backend - Payment Callback Verification

```java
import com.chinaums.sdk.UmsPayClient;
import java.util.Map;
import java.util.HashMap;

// Controller endpoint receiving WeChat payment callback
// POST https://your-domain.com/api/notify
public String handlePaymentNotify(HttpServletRequest request) {
    Map<String, String> params = extractParams(request);
    
    // Verify notification signature
    boolean verified = client.verifyNotification(params);
    if (!verified) {
        return "FAIL";  // Invalid signature, reject callback
    }
    
    String status = params.get("status");
    String merOrderId = params.get("merOrderId");
    String totalAmount = params.get("totalAmount");
    String targetOrderId = params.get("targetOrderId");
    
    logger.info("Payment callback received - Order: {}, Status: {}, Amount: {}",
        merOrderId, status, totalAmount);
    
    if ("TRADE_SUCCESS".equals(status)) {
        // Update order status to paid
        orderService.paySuccess(merOrderId, targetOrderId);
    } else if ("TRADE_CLOSED".equals(status)) {
        // Handle order closure
        orderService.payClosed(merOrderId);
    }
    
    return "SUCCESS";  // Acknowledge receipt to WeChat server
}
```

#### Stage 4: Order Status Query

```java
import com.chinaums.sdk.request.QueryRequest;
import com.chinaums.sdk.response.QueryResponse;

QueryRequest request = new QueryRequest("ORDER123456");
QueryResponse response = client.query(request);

if (response.isSuccess()) {
    // Possible statuses:
    // WAIT_BUYER_PAY  - Waiting for payment
    // TRADE_SUCCESS   - Payment successful
    // TRADE_CLOSED    - Transaction closed (expired/cancelled)
    // TRADE_FAIL      - Payment failed
    String status = response.getStatus();
    Long amount = response.getTotalAmount();
    String targetOrderId = response.getTargetOrderId();
    String payTime = response.getPayTime();
}
```

#### Stage 5: Refund

```java
import com.chinaums.sdk.request.RefundRequest;
import com.chinaums.sdk.response.RefundResponse;

RefundRequest request = RefundRequest.builder()
    .merOrderId("ORDER123456")    // Original order ID
    .refundOrderId("REFUND123456") // Unique refund ID
    .refundAmount(50L)            // Refund amount (must <= original amount)
    .refundDesc("Refund reason")
    .build();

RefundResponse response = client.refund(request);
if (response.isSuccess()) {
    String refundStatus = response.getStatus();
    // SUCCESS: Refund successful
    // PROCESSING: Refund in progress
}
```

#### Expected Response from miniProgramPay()

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

### 5. Order Query

```java
import com.chinaums.sdk.request.QueryRequest;
import com.chinaums.sdk.response.QueryResponse;

QueryRequest request = new QueryRequest("ORDER123456");
QueryResponse response = client.query(request);

if (response.isSuccess()) {
    String status = response.getStatus();
    // WAIT_BUYER_PAY: Waiting for payment
    // TRADE_SUCCESS: Payment successful
    // TRADE_CLOSED: Transaction closed
}
```

### 6. Refund

```java
import com.chinaums.sdk.request.RefundRequest;
import com.chinaums.sdk.response.RefundResponse;

RefundRequest request = RefundRequest.builder()
    .merOrderId("ORDER123456")
    .refundOrderId("REFUND123456")
    .refundAmount(50L)  // Unit: cents
    .refundDesc("Refund reason")
    .build();

RefundResponse response = client.refund(request);
```

### 7. Refund Query

```java
import com.chinaums.sdk.request.RefundQueryRequest;
import com.chinaums.sdk.response.RefundQueryResponse;

RefundQueryRequest request = new RefundQueryRequest("REFUND123456");
RefundQueryResponse response = client.refundQuery(request);

if (response.isSuccess()) {
    String refundStatus = response.getRefundStatus();
    // SUCCESS: Refund successful
    // PROCESSING: Refund processing
    // FAIL: Refund failed
}
```

## Configuration Options

| Option | Type | Required | Default | Description |
|--------|------|----------|---------|-------------|
| appId | String | Yes | - | Application ID assigned by ChinaUMS |
| appKey | String | Yes | - | Application key assigned by ChinaUMS |
| mid | String | Yes | - | Merchant ID |
| tid | String | Yes | - | Terminal ID |
| instMid | String | No | H5DEFAULT | Business type. Use `MINIDEFAULT` for Mini Program |
| notifyKey | String | No | - | Notification key for callback signature verification |
| environment | Environment | No | PRODUCTION | Environment (PRODUCTION/TEST) |
| connectTimeout | int | No | 10000 | Connection timeout (milliseconds) |
| readTimeout | int | No | 30000 | Read timeout (milliseconds) |
| maxRetries | int | No | 3 | Maximum retry count |
| enableCertValidation | boolean | No | true | Enable certificate validation |

## Error Handling

The SDK provides a complete exception handling system:

```java
import com.chinaums.sdk.exception.*;

try {
    H5PayResponse response = client.h5Pay(request);
} catch (UmsPayAuthException e) {
    // Authentication failed, check appId/appKey configuration
    logger.error("Authentication failed: {}", e.getMessage());
} catch (UmsPayNetworkException e) {
    // Network issue, can retry
    logger.error("Network error: {}", e.getMessage());
} catch (UmsPayBusinessException e) {
    // Business error, check request parameters
    logger.error("Business error: {} - {}", e.getErrorCode(), e.getMessage());
}
```

## Best Practices

1. **Singleton Pattern**: Use singleton pattern for UmsPayClient to avoid repeated initialization
2. **Separate Client Instances**: Use separate `UmsPayClient` instances for H5 pay (`instMid=H5DEFAULT`) and Mini Program pay (`instMid=MINIDEFAULT`) to avoid configuration conflicts
3. **Error Handling**: Properly handle various exceptions and provide user-friendly error messages
4. **Logging**: Enable appropriate log levels for debugging and monitoring
5. **Timeout Configuration**: Adjust timeout settings based on your network environment
6. **Security**: Do not hardcode sensitive information like appKey in code. Use environment variables or system properties instead

## Sensitive Information Management

**Never hardcode sensitive credentials in your source code.** Use environment variables or system properties:

```bash
# Set via environment variables
export UMS_APP_ID="your-app-id"
export UMS_APP_KEY="your-app-key"
export UMS_MID="898201612345678"

# Or via JVM system properties
java -Dums.appId=your-app-id -Dums.appKey=your-app-key -jar app.jar
```

```java
// Read configuration from environment variables
UmsPayConfig config = UmsPayConfig.builder()
    .appId(System.getenv("UMS_APP_ID"))
    .appKey(System.getenv("UMS_APP_KEY"))
    .mid(System.getenv("UMS_MID"))
    .tid(System.getenv("UMS_TID"))
    .build();
```

> **Note:** The integration test suite also supports parameter override via system properties: `-Dums.appId=xxx`, `-Dums.appKey=xxx`, `-Dums.mid=xxx`, `-Dums.tid=xxx`, `-Dums.notifyKey=xxx`.

## FAQ

### Q: How to get appId and appKey?
A: You need to register on ChinaUMS Open Platform and create an application to get appId and appKey.

### Q: How to test in test environment?
A: Set `environment(Environment.TEST)` in configuration to use test environment.

### Q: What if signature verification fails?
A: Check if appId and appKey are correct, and ensure system time is accurate.

### Q: How to handle payment timeout?
A: Adjust `readTimeout` configuration, or use asynchronous notification mechanism.

### Q: What is the difference between Mini Program payment and H5 payment?
A: Mini Program payment requires `subAppId` (WeChat Mini Program AppID) and `subOpenId` (user's OpenID), uses `instMid=MINIDEFAULT`, and returns `miniPayRequest` parameters for `wx.requestPayment()`. H5 payment returns a redirect URL instead.

### Q: How to get the user's OpenID (subOpenId)?
A: Call WeChat Mini Program login API `wx.login()` to get a code, then exchange it for session_key and openid via your backend server. See [WeChat Official Documentation](https://developers.weixin.qq.com/miniprogram/dev/framework/open-ability/login.html).

### Q: How to handle payment callback properly?
A: Always verify the notification signature using `client.verifyNotification()` before processing. Return `SUCCESS` to acknowledge receipt. Implement idempotency - a callback may be sent multiple times by WeChat.

### Q: What should I do if the callback hasn't been received?
A: Implement an active order status polling mechanism as a fallback. Use `client.query()` to periodically check the order status. WeChat also provides manual refund trigger API for timeout scenarios.

## Contributing

We welcome contributions! Please follow these steps:

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Support

- 📖 [Documentation](https://open.chinaums.com/saas-doc/openplate/netpay/mini-program/transaction/VxXa3w2o.html)
- 🐛 [Issue Tracker](https://github.com/daanhan/ums-sdk/issues)
- 💬 [Discussions](https://github.com/daanhan/ums-sdk/discussions)

## Changelog

### v1.0.0-SNAPSHOT (2026-04-29)
- Support for WeChat Mini Program payment
- Support for WeChat H5 payment
- Support for order query, refund, refund query
- Mini Program payment callback verification
- Complete exception handling system
- Bilingual documentation with Mini Program payment examples

---

**Made with ❤️ by ChinaUMS SDK Team**

Copyright © 2026 ChinaUMS. Licensed under the [MIT License](LICENSE).
