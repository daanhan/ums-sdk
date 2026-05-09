# 银联商务支付SDK设计方案

## 项目概述

### 项目名称
银联商务支付接口客户端SDK (ChinaUMS Payment SDK)

### 项目定位
开源Java SDK，为开发者提供便捷的银联商务支付接口调用能力，支持微信H5支付、订单查询、退款、退款查询等核心功能。

### 目标用户
- 需要接入银联商务支付系统的开发者
- 希望快速集成支付功能的中小型项目
- 需要统一支付接口封装的企业级应用

### 核心价值
- **简单易用**：5分钟即可完成支付功能集成
- **安全可靠**：完善的签名验证和证书管理机制
- **功能完整**：覆盖支付全流程的核心接口
- **文档齐全**：中英文双语文档，丰富的示例代码

---

## 需求分析

### 功能需求

#### 1. 微信H5支付下单
- 支持微信H5支付场景
- 自动处理签名和参数编码
- 支持商品信息、分账等高级功能
- 返回支付URL供商户引导用户支付

#### 2. 订单交易查询
- 支持按商户订单号查询
- 支持按平台流水号查询
- 返回详细的交易状态和金额信息
- 支持查询分账订单信息

#### 3. 退款
- 支持全额退款和部分退款
- 支持多次退款（需使用不同退款订单号）
- 支持分账订单退款
- 自动处理退款金额校验

#### 4. 退款查询
- 查询退款订单状态
- 返回退款详细信息
- 支持查询退款渠道和金额

### 非功能需求

#### 性能要求
- 单次API调用响应时间 < 3秒
- 支持连接池和请求复用
- 支持超时配置和重试机制

#### 安全要求
- 自动签名验证，防止篡改
- 支持HTTPS证书验证
- 敏感信息自动脱敏和编码
- 日志中不暴露敏感数据

#### 可用性要求
- Java 1.8及以上版本支持
- 零外部依赖冲突
- 清晰的错误提示和异常处理
- 完善的文档和示例代码

#### 可维护性要求
- 代码结构清晰，职责分明
- 单元测试覆盖率 > 80%
- 遵循Java编码规范
- 详细的JavaDoc注释

---

## 架构设计

### 整体架构

采用简洁实用型架构，单一入口类设计，降低学习成本。

```
┌─────────────────────────────────────────────────────────┐
│                    UmsPayClient                         │
│                   (统一入口类)                           │
└─────────────────────────────────────────────────────────┘
                          │
        ┌─────────────────┼─────────────────┐
        │                 │                 │
        ▼                 ▼                 ▼
┌──────────────┐  ┌──────────────┐  ┌──────────────┐
│  Config层    │  │  Request层   │  │ Response层   │
│  配置管理    │  │  请求封装    │  │  响应处理    │
└──────────────┘  └──────────────┘  └──────────────┘
        │                 │                 │
        └─────────────────┼─────────────────┘
                          │
                          ▼
                ┌──────────────────┐
                │   HTTP层         │
                │  OkHttp执行器    │
                └──────────────────┘
                          │
        ┌─────────────────┼─────────────────┐
        │                 │                 │
        ▼                 ▼                 ▼
┌──────────────┐  ┌──────────────┐  ┌──────────────┐
│ Security层   │  │  Util层      │  │ Exception层  │
│ 签名/证书    │  │  工具类      │  │  异常处理    │
└──────────────┘  └──────────────┘  └──────────────┘
```

### 模块划分

#### 1. 核心模块 (core)
- **UmsPayClient**: 主入口类，提供所有API方法
- **UmsPayConfig**: 配置类，使用Builder模式

#### 2. 请求模块
- **BaseRequest**: 请求基类
- **H5PayRequest**: H5支付请求
- **QueryRequest**: 订单查询请求
- **RefundRequest**: 退款请求
- **RefundQueryRequest**: 退款查询请求

#### 3. 响应模块
- **BaseResponse**: 响应基类
- **H5PayResponse**: H5支付响应
- **QueryResponse**: 订单查询响应
- **RefundResponse**: 退款响应
- **RefundQueryResponse**: 退款查询响应

#### 4. HTTP模块
- **HttpClient**: HTTP客户端接口
- **OkHttpExecutor**: OkHttp实现类

#### 5. 安全模块
- **SignatureUtil**: 签名工具类
- **CertManager**: 证书管理类

#### 6. 工具模块
- **JsonUtil**: JSON序列化/反序列化
- **DateUtil**: 日期处理工具

#### 7. 异常模块
- **UmsPayException**: 基础异常
- **UmsPayAuthException**: 认证异常
- **UmsPayNetworkException**: 网络异常
- **UmsPayBusinessException**: 业务异常

---

## 详细设计

### 1. 配置类设计

```java
public class UmsPayConfig {
    private final String appId;
    private final String appKey;
    private final String mid;
    private final String tid;
    private final String instMid;
    private final Environment environment;
    private final int connectTimeout;
    private final int readTimeout;
    private final int maxRetries;
    private final boolean enableCertValidation;
    
    private UmsPayConfig(Builder builder) {
        this.appId = builder.appId;
        this.appKey = builder.appKey;
        this.mid = builder.mid;
        this.tid = builder.tid;
        this.instMid = builder.instMid;
        this.environment = builder.environment;
        this.connectTimeout = builder.connectTimeout;
        this.readTimeout = builder.readTimeout;
        this.maxRetries = builder.maxRetries;
        this.enableCertValidation = builder.enableCertValidation;
    }
    
    public static Builder builder() {
        return new Builder();
    }
    
    public static class Builder {
        private String appId;
        private String appKey;
        private String mid;
        private String tid;
        private String instMid = "H5DEFAULT";
        private Environment environment = Environment.PRODUCTION;
        private int connectTimeout = 10000;
        private int readTimeout = 30000;
        private int maxRetries = 3;
        private boolean enableCertValidation = true;
        
        public Builder appId(String appId) {
            this.appId = appId;
            return this;
        }
        
        public Builder appKey(String appKey) {
            this.appKey = appKey;
            return this;
        }
        
        public Builder mid(String mid) {
            this.mid = mid;
            return this;
        }
        
        public Builder tid(String tid) {
            this.tid = tid;
            return this;
        }
        
        public Builder instMid(String instMid) {
            this.instMid = instMid;
            return this;
        }
        
        public Builder environment(Environment environment) {
            this.environment = environment;
            return this;
        }
        
        public Builder connectTimeout(int connectTimeout) {
            this.connectTimeout = connectTimeout;
            return this;
        }
        
        public Builder readTimeout(int readTimeout) {
            this.readTimeout = readTimeout;
            return this;
        }
        
        public Builder maxRetries(int maxRetries) {
            this.maxRetries = maxRetries;
            return this;
        }
        
        public Builder enableCertValidation(boolean enableCertValidation) {
            this.enableCertValidation = enableCertValidation;
            return this;
        }
        
        public UmsPayConfig build() {
            validate();
            return new UmsPayConfig(this);
        }
        
        private void validate() {
            if (appId == null || appId.isEmpty()) {
                throw new IllegalArgumentException("appId不能为空");
            }
            if (appKey == null || appKey.isEmpty()) {
                throw new IllegalArgumentException("appKey不能为空");
            }
            if (mid == null || mid.isEmpty()) {
                throw new IllegalArgumentException("mid不能为空");
            }
            if (tid == null || tid.isEmpty()) {
                throw new IllegalArgumentException("tid不能为空");
            }
        }
    }
}

public enum Environment {
    PRODUCTION("https://api-mop.chinaums.com"),
    TEST("https://test-api-open.chinaums.com");
    
    private final String baseUrl;
    
    Environment(String baseUrl) {
        this.baseUrl = baseUrl;
    }
    
    public String getBaseUrl() {
        return baseUrl;
    }
}
```

### 2. 主入口类设计

```java
public class UmsPayClient {
    private final UmsPayConfig config;
    private final HttpClient httpClient;
    private final SignatureUtil signatureUtil;
    
    public UmsPayClient(UmsPayConfig config) {
        this.config = config;
        this.httpClient = new OkHttpExecutor(config);
        this.signatureUtil = new SignatureUtil(config.getAppId(), config.getAppKey());
    }
    
    /**
     * H5支付下单
     */
    public H5PayResponse h5Pay(H5PayRequest request) throws UmsPayException {
        String url = config.getEnvironment().getBaseUrl() + "/v1/netpay/wxpay/h5-pay";
        String requestBody = buildRequestBody(request);
        String signature = signatureUtil.sign(requestBody);
        
        HttpResponse response = httpClient.get(url, requestBody, signature);
        return parseResponse(response, H5PayResponse.class);
    }
    
    /**
     * 订单查询
     */
    public QueryResponse query(QueryRequest request) throws UmsPayException {
        String url = config.getEnvironment().getBaseUrl() + "/v1/netpay/query";
        String requestBody = buildRequestBody(request);
        String signature = signatureUtil.sign(requestBody);
        
        HttpResponse response = httpClient.post(url, requestBody, signature);
        return parseResponse(response, QueryResponse.class);
    }
    
    /**
     * 退款
     */
    public RefundResponse refund(RefundRequest request) throws UmsPayException {
        String url = config.getEnvironment().getBaseUrl() + "/v1/netpay/refund";
        String requestBody = buildRequestBody(request);
        String signature = signatureUtil.sign(requestBody);
        
        HttpResponse response = httpClient.post(url, requestBody, signature);
        return parseResponse(response, RefundResponse.class);
    }
    
    /**
     * 退款查询
     */
    public RefundQueryResponse refundQuery(RefundQueryRequest request) throws UmsPayException {
        String url = config.getEnvironment().getBaseUrl() + "/v1/netpay/refund-query";
        String requestBody = buildRequestBody(request);
        String signature = signatureUtil.sign(requestBody);
        
        HttpResponse response = httpClient.post(url, requestBody, signature);
        return parseResponse(response, RefundQueryResponse.class);
    }
    
    private String buildRequestBody(BaseRequest request) {
        request.setMid(config.getMid());
        request.setTid(config.getTid());
        request.setInstMid(config.getInstMid());
        request.setRequestTimestamp(DateUtil.formatDateTime(new Date()));
        return JsonUtil.toJson(request);
    }
    
    private <T extends BaseResponse> T parseResponse(HttpResponse response, Class<T> responseClass) 
            throws UmsPayException {
        if (!response.isSuccess()) {
            throw new UmsPayNetworkException("HTTP请求失败: " + response.getCode());
        }
        
        T result = JsonUtil.fromJson(response.getBody(), responseClass);
        if (!result.isSuccess()) {
            throw new UmsPayBusinessException(result.getErrCode(), result.getErrMsg());
        }
        
        return result;
    }
}
```

### 3. 请求对象设计

```java
public abstract class BaseRequest {
    protected String requestTimestamp;
    protected String mid;
    protected String tid;
    protected String instMid;
    protected String msgId;
    protected String srcReserve;
    
    // getters and setters
}

public class H5PayRequest extends BaseRequest {
    private String merOrderId;
    private Long totalAmount;
    private List<Goods> goods;
    private String notifyUrl;
    private String returnUrl;
    private String expireTime;
    private String sceneType;
    private String merAppName;
    private String merAppId;
    private Boolean limitCreditCard;
    private Boolean divisionFlag;
    private Long platformAmount;
    private List<SubOrder> subOrders;
    
    public static Builder builder() {
        return new Builder();
    }
    
    public static class Builder {
        private String merOrderId;
        private Long totalAmount;
        private List<Goods> goods;
        private String notifyUrl;
        private String returnUrl;
        private String expireTime;
        private String sceneType;
        private String merAppName;
        private String merAppId;
        private Boolean limitCreditCard;
        private Boolean divisionFlag;
        private Long platformAmount;
        private List<SubOrder> subOrders;
        
        public Builder merOrderId(String merOrderId) {
            this.merOrderId = merOrderId;
            return this;
        }
        
        public Builder totalAmount(Long totalAmount) {
            this.totalAmount = totalAmount;
            return this;
        }
        
        public Builder goods(List<Goods> goods) {
            this.goods = goods;
            return this;
        }
        
        public Builder notifyUrl(String notifyUrl) {
            this.notifyUrl = notifyUrl;
            return this;
        }
        
        public Builder returnUrl(String returnUrl) {
            this.returnUrl = returnUrl;
            return this;
        }
        
        public Builder expireTime(String expireTime) {
            this.expireTime = expireTime;
            return this;
        }
        
        public Builder sceneType(String sceneType) {
            this.sceneType = sceneType;
            return this;
        }
        
        public Builder merAppName(String merAppName) {
            this.merAppName = merAppName;
            return this;
        }
        
        public Builder merAppId(String merAppId) {
            this.merAppId = merAppId;
            return this;
        }
        
        public Builder limitCreditCard(Boolean limitCreditCard) {
            this.limitCreditCard = limitCreditCard;
            return this;
        }
        
        public Builder divisionFlag(Boolean divisionFlag) {
            this.divisionFlag = divisionFlag;
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
        
        public H5PayRequest build() {
            validate();
            return new H5PayRequest(this);
        }
        
        private void validate() {
            if (merOrderId == null || merOrderId.isEmpty()) {
                throw new IllegalArgumentException("商户订单号不能为空");
            }
            if (totalAmount == null || totalAmount <= 0) {
                throw new IllegalArgumentException("支付金额必须大于0");
            }
        }
    }
    
    private H5PayRequest(Builder builder) {
        this.merOrderId = builder.merOrderId;
        this.totalAmount = builder.totalAmount;
        this.goods = builder.goods;
        this.notifyUrl = builder.notifyUrl;
        this.returnUrl = builder.returnUrl;
        this.expireTime = builder.expireTime;
        this.sceneType = builder.sceneType;
        this.merAppName = builder.merAppName;
        this.merAppId = builder.merAppId;
        this.limitCreditCard = builder.limitCreditCard;
        this.divisionFlag = builder.divisionFlag;
        this.platformAmount = builder.platformAmount;
        this.subOrders = builder.subOrders;
    }
    
    // getters
}
```

### 4. 响应对象设计

```java
public abstract class BaseResponse {
    private String errCode;
    private String errMsg;
    private String responseTimeStamp;
    private String mid;
    private String tid;
    private String instMid;
    private String msgId;
    private String srcReserve;
    
    public boolean isSuccess() {
        return "SUCCESS".equals(errCode);
    }
    
    // getters
}

public class H5PayResponse extends BaseResponse {
    private String payUrl;
    private String merOrderId;
    private String seqId;
    private String targetOrderId;
    private String targetSys;
    
    // getters
}

public class QueryResponse extends BaseResponse {
    private String status;
    private Long totalAmount;
    private String merOrderId;
    private String payTime;
    private String settleDate;
    private String buyerId;
    private String targetOrderId;
    private String targetSys;
    private Long buyerPayAmount;
    private Long couponAmount;
    private Long invoiceAmount;
    private Long receiptAmount;
    
    // getters
}

public class RefundResponse extends BaseResponse {
    private String status;
    private Long totalAmount;
    private Long refundAmount;
    private String merOrderId;
    private String refundOrderId;
    private String refundTargetOrderId;
    private String seqId;
    private String targetSys;
    private String targetMid;
    
    // getters
}

public class RefundQueryResponse extends BaseResponse {
    private String refundStatus;
    private String status;
    private Long totalAmount;
    private String merOrderId;
    private String refundOrderId;
    private String refundTargetOrderId;
    private String seqId;
    private String payTime;
    private String settleDate;
    
    // getters
}
```

### 5. 异常处理设计

```java
public class UmsPayException extends Exception {
    private final String errorCode;
    private final String requestId;
    private final long timestamp;
    
    public UmsPayException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
        this.requestId = UUID.randomUUID().toString();
        this.timestamp = System.currentTimeMillis();
    }
    
    // getters
}

public class UmsPayAuthException extends UmsPayException {
    public UmsPayAuthException(String message) {
        super("AUTH_ERROR", message);
    }
}

public class UmsPayNetworkException extends UmsPayException {
    public UmsPayNetworkException(String message) {
        super("NETWORK_ERROR", message);
    }
}

public class UmsPayBusinessException extends UmsPayException {
    public UmsPayBusinessException(String errorCode, String message) {
        super(errorCode, message);
    }
}
```

### 6. 签名工具类设计

```java
public class SignatureUtil {
    private final String appId;
    private final String appKey;
    
    public SignatureUtil(String appId, String appKey) {
        this.appId = appId;
        this.appKey = appKey;
    }
    
    public String sign(String content) {
        String timestamp = DateUtil.formatTimestamp(new Date());
        String nonce = UUID.randomUUID().toString().replace("-", "");
        
        String contentSha256 = sha256Hex(content);
        String signStr = appId + timestamp + nonce + contentSha256;
        String signature = hmacSha256(signStr, appKey);
        
        return Base64.getEncoder().encodeToString(signature.getBytes(StandardCharsets.UTF_8));
    }
    
    private String sha256Hex(String content) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(content.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new UmsPayAuthException("SHA-256算法不可用");
        }
    }
    
    private String hmacSha256(String data, String key) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            mac.init(secretKeySpec);
            byte[] hash = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(hash);
        } catch (Exception e) {
            throw new UmsPayAuthException("HmacSHA256签名失败");
        }
    }
    
    private String bytesToHex(byte[] bytes) {
        StringBuilder result = new StringBuilder();
        for (byte b : bytes) {
            result.append(String.format("%02x", b));
        }
        return result.toString();
    }
}
```

---

## 技术选型

### 核心技术栈

| 技术 | 版本 | 用途 | 选择理由 |
|------|------|------|----------|
| Java | 1.8+ | 开发语言 | 兼容性好，企业级应用主流 |
| Maven | 3.6+ | 构建工具 | 依赖管理规范，适合开源项目 |
| OkHttp | 4.12.0 | HTTP客户端 | 性能优秀，API友好，广泛使用 |
| Jackson | 2.15.2 | JSON处理 | 功能强大，Spring默认使用 |
| SLF4J | 1.7.36 | 日志门面 | 统一日志接口，兼容性好 |
| Logback | 1.2.12 | 日志实现 | 性能优秀，配置灵活 |
| JUnit 5 | 5.9.3 | 测试框架 | 现代测试框架，功能全面 |
| Mockito | 5.4.0 | Mock框架 | 测试隔离，单元测试必备 |

### 依赖管理策略

1. **最小化依赖**：只引入必要的依赖，避免依赖冲突
2. **版本管理**：使用Maven属性统一管理版本号
3. **作用域控制**：测试依赖使用test scope，日志实现使用runtime scope
4. **兼容性保证**：所有依赖都支持Java 1.8

---

## 项目结构

```
ums-sdk/
├── pom.xml                          # Maven配置文件
├── README.md                        # 项目说明文档（英文）
├── README_CN.md                     # 项目说明文档（中文）
├── LICENSE                          # 开源协议
├── .gitignore                       # Git忽略文件
├── docs/                            # 文档目录
│   └── superpowers/
│       └── specs/
│           └── 2026-04-27-ums-sdk-design.md  # 设计文档
└── src/
    ├── main/
    │   ├── java/
    │   │   └── com/
    │   │       └── chinaums/
    │   │           └── sdk/
    │   │               ├── UmsPayClient.java           # 主入口类
    │   │               ├── config/
    │   │               │   ├── UmsPayConfig.java       # 配置类
    │   │               │   └── Environment.java        # 环境枚举
    │   │               ├── request/
    │   │               │   ├── BaseRequest.java        # 请求基类
    │   │               │   ├── H5PayRequest.java       # H5支付请求
    │   │               │   ├── QueryRequest.java       # 订单查询请求
    │   │               │   ├── RefundRequest.java      # 退款请求
    │   │               │   ├── RefundQueryRequest.java # 退款查询请求
    │   │               │   ├── Goods.java              # 商品信息
    │   │               │   └── SubOrder.java           # 子订单信息
    │   │               ├── response/
    │   │               │   ├── BaseResponse.java       # 响应基类
    │   │               │   ├── H5PayResponse.java      # H5支付响应
    │   │               │   ├── QueryResponse.java      # 订单查询响应
    │   │               │   ├── RefundResponse.java     # 退款响应
    │   │               │   └── RefundQueryResponse.java# 退款查询响应
    │   │               ├── exception/
    │   │               │   ├── UmsPayException.java    # 基础异常
    │   │               │   ├── UmsPayAuthException.java# 认证异常
    │   │               │   ├── UmsPayNetworkException.java # 网络异常
    │   │               │   └── UmsPayBusinessException.java # 业务异常
    │   │               ├── http/
    │   │               │   ├── HttpClient.java         # HTTP客户端接口
    │   │               │   ├── HttpResponse.java       # HTTP响应封装
    │   │               │   └── OkHttpExecutor.java     # OkHttp实现
    │   │               ├── security/
    │   │               │   ├── SignatureUtil.java      # 签名工具类
    │   │               │   └── CertManager.java        # 证书管理
    │   │               └── util/
    │   │                   ├── JsonUtil.java           # JSON工具类
    │   │                   └── DateUtil.java           # 日期工具类
    │   └── resources/
    │       └── logback.xml                            # 日志配置
    └── test/
        └── java/
            └── com/
                └── chinaums/
                    └── sdk/
                        ├── UmsPayClientTest.java       # 客户端测试
                        ├── H5PayTest.java              # H5支付测试示例
                        ├── QueryTest.java              # 查询测试示例
                        ├── RefundTest.java             # 退款测试示例
                        └── RefundQueryTest.java        # 退款查询测试示例
```

---

## 测试策略

### 测试分层

#### 1. 单元测试
- 测试每个类的独立功能
- 使用Mockito隔离外部依赖
- 覆盖正常流程和异常流程
- 目标覆盖率：80%以上

#### 2. 集成测试
- 使用MockWebServer模拟银联API
- 测试完整的请求响应流程
- 验证签名和参数编码
- 测试重试和超时机制

#### 3. 功能测试
- 在测试环境真实调用银联API
- 验证各接口功能正确性
- 测试边界条件和异常场景

### 测试示例

```java
class H5PayTest {
    private UmsPayClient client;
    private MockWebServer server;
    
    @BeforeEach
    void setUp() throws IOException {
        server = new MockWebServer();
        server.start();
        
        UmsPayConfig config = UmsPayConfig.builder()
            .appId("test-app-id")
            .appKey("test-app-key")
            .mid("898201612345678")
            .tid("88880001")
            .environment(Environment.TEST)
            .build();
        
        client = new UmsPayClient(config);
    }
    
    @Test
    void testH5Pay_Success() throws Exception {
        String mockResponse = "{\"errCode\":\"SUCCESS\",\"payUrl\":\"https://pay.example.com\"}";
        server.enqueue(new MockResponse()
            .setBody(mockResponse)
            .setResponseCode(200));
        
        H5PayRequest request = H5PayRequest.builder()
            .merOrderId("TEST123456")
            .totalAmount(100L)
            .build();
        
        H5PayResponse response = client.h5Pay(request);
        
        assertTrue(response.isSuccess());
        assertNotNull(response.getPayUrl());
    }
    
    @Test
    void testH5Pay_InvalidAmount() {
        assertThrows(IllegalArgumentException.class, () -> {
            H5PayRequest.builder()
                .merOrderId("TEST123456")
                .totalAmount(-1L)
                .build();
        });
    }
    
    @AfterEach
    void tearDown() throws IOException {
        server.shutdown();
    }
}
```

---

## 文档规划

### README结构

#### README.md (英文版)
```
# ChinaUMS Payment SDK

## Overview
- Introduction
- Features
- Requirements

## Quick Start
- Prerequisites
- Maven Dependency
- Basic Configuration
- First Example

## Detailed Documentation
- Configuration Guide
- API Reference
- Error Handling
- Best Practices

## Examples
- H5 Payment Example
- Query Example
- Refund Example
- Refund Query Example

## FAQ
- Frequently Asked Questions

## Contributing
- How to Contribute
- Code Style Guide

## License
- Open Source License
```

#### README_CN.md (中文版)
```
# 银联商务支付SDK

## 简介
- 项目介绍
- 功能特性
- 环境要求

## 快速开始
- 环境准备
- Maven依赖
- 基本配置
- 第一个示例

## 详细文档
- 配置说明
- API文档
- 异常处理
- 最佳实践

## 示例代码
- H5支付示例
- 订单查询示例
- 退款示例
- 退款查询示例

## 常见问题
- FAQ

## 贡献指南
- 如何贡献代码
- 代码规范

## 许可证
- 开源协议
```

### JavaDoc规范

所有public类和方法都必须有JavaDoc注释，格式如下：

```java
/**
 * 银联商务支付客户端
 * 
 * <p>提供统一的支付接口调用入口，支持H5支付、订单查询、退款、退款查询等功能。</p>
 * 
 * <h2>快速开始</h2>
 * <pre>{@code
 * UmsPayConfig config = UmsPayConfig.builder()
 *     .appId("your-app-id")
 *     .appKey("your-app-key")
 *     .mid("898201612345678")
 *     .tid("88880001")
 *     .build();
 * 
 * UmsPayClient client = new UmsPayClient(config);
 * }</pre>
 * 
 * @author ChinaUMS SDK Team
 * @version 1.0.0
 * @since 1.0.0
 */
public class UmsPayClient {
    /**
     * H5支付下单
     * 
     * <p>发起微信H5支付请求，返回支付URL供商户引导用户完成支付。</p>
     * 
     * @param request H5支付请求对象，包含订单信息
     * @return H5支付响应对象，包含支付URL
     * @throws UmsPayAuthException 认证失败
     * @throws UmsPayNetworkException 网络异常
     * @throws UmsPayBusinessException 业务异常
     */
    public H5PayResponse h5Pay(H5PayRequest request) throws UmsPayException {
        // implementation
    }
}
```

---

## 开发计划

### 阶段划分

#### 第一阶段：基础框架搭建（3-5天）
- [ ] 创建Maven项目结构
- [ ] 配置依赖管理
- [ ] 实现基础工具类（JsonUtil、DateUtil）
- [ ] 实现配置类（UmsPayConfig）
- [ ] 搭建HTTP客户端框架
- [ ] 实现签名工具类

#### 第二阶段：核心功能实现（5-7天）
- [ ] 实现H5支付下单接口
- [ ] 实现订单查询接口
- [ ] 实现退款接口
- [ ] 实现退款查询接口
- [ ] 实现异常处理体系
- [ ] 实现证书验证功能

#### 第三阶段：测试与优化（3-5天）
- [ ] 编写单元测试
- [ ] 编写集成测试
- [ ] 性能测试和优化
- [ ] 代码审查和重构
- [ ] 测试覆盖率验证

#### 第四阶段：文档与发布（2-3天）
- [ ] 编写README文档（中英文）
- [ ] 编写JavaDoc注释
- [ ] 编写示例代码
- [ ] 准备发布包
- [ ] 项目总结

### 里程碑

| 里程碑 | 完成标准 | 预计时间 |
|--------|----------|----------|
| M1: 框架完成 | 基础框架可运行，能发送HTTP请求 | 第5天 |
| M2: 核心功能完成 | 四个核心接口全部实现并测试通过 | 第12天 |
| M3: 测试完成 | 测试覆盖率达到80%，所有测试通过 | 第17天 |
| M4: 文档完成 | README、JavaDoc、示例代码全部完成 | 第20天 |

---

## 风险评估与应对

### 技术风险

#### 风险1：银联API文档不完整或有变更
- **影响**：可能导致接口实现不正确或需要频繁修改
- **概率**：中
- **应对措施**：
  - 及时查看官方文档更新
  - 预留扩展接口，便于适配变更
  - 建立测试环境验证机制

#### 风险2：Java 1.8兼容性问题
- **影响**：某些新特性或依赖可能不支持Java 1.8
- **概率**：低
- **应对措施**：
  - 所有依赖都验证Java 1.8兼容性
  - 使用Java 1.8语法特性
  - 在Java 1.8环境下进行完整测试

### 时间风险

#### 风险3：某些功能实现比预期复杂
- **影响**：项目延期
- **概率**：中
- **应对措施**：
  - 优先实现核心功能
  - 可选功能可后续迭代
  - 预留缓冲时间

### 质量风险

#### 风险4：测试覆盖不足，存在隐藏bug
- **影响**：SDK稳定性差，用户体验不好
- **概率**：中
- **应对措施**：
  - 严格执行测试策略
  - 代码审查机制
  - 建立issue反馈机制

---

## 成功标准

### 功能完整性
- ✅ 支持微信H5支付下单
- ✅ 支持订单交易查询
- ✅ 支持退款
- ✅ 支持退款查询

### 质量标准
- ✅ 单元测试覆盖率 ≥ 80%
- ✅ 所有测试用例通过
- ✅ 无严重bug
- ✅ 代码符合Java编码规范

### 文档完整性
- ✅ README文档（中英文双语）
- ✅ JavaDoc注释完整
- ✅ 示例代码齐全
- ✅ 常见问题FAQ

### 易用性标准
- ✅ 5分钟内完成基本配置
- ✅ API设计简洁易懂
- ✅ 错误提示清晰明确
- ✅ 支持Java 1.8及以上版本

---

## 后续规划

### 短期规划（1-3个月）
- 收集用户反馈
- 修复bug和优化性能
- 完善文档和示例
- 发布稳定版本

### 中期规划（3-6个月）
- 支持更多支付渠道（支付宝、云闪付等）
- 提供异步回调处理工具
- 增加更多高级功能
- 发布到Maven中央仓库

### 长期规划（6-12个月）
- 开发其他语言版本（Python、Go等）
- 提供可视化配置工具
- 建立开发者社区
- 持续维护和更新

---

## 附录

### 参考资料
- [银联商务开放平台文档](https://open.chinaums.com/saas-doc/openplate/netpay/h5/transaction/7YX0bM2x.html)
- [OkHttp官方文档](https://square.github.io/okhttp/)
- [Jackson官方文档](https://github.com/FasterXML/jackson)
- [JUnit 5用户指南](https://junit.org/junit5/docs/current/user-guide/)

### 术语表
- **SDK**: Software Development Kit，软件开发工具包
- **H5**: HTML5，移动端网页技术
- **MID**: Merchant ID，商户号
- **TID**: Terminal ID，终端号
- **AppID**: 应用ID，银联商务平台分配
- **AppKey**: 应用密钥，银联商务平台分配

---

**文档版本**: 1.0  
**创建日期**: 2026-04-27  
**最后更新**: 2026-04-27  
**作者**: AI Assistant  
**状态**: 待审阅
