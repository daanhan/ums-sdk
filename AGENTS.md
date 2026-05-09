# 项目概述

**项目名称**: ChinaUMS Payment SDK (银联商务支付接口客户端SDK)

**项目背景**: 本项目是银联商务(ChinaUMS)支付平台的 Java 客户端 SDK，封装了银联商务支付 API 的签名、请求、响应解析等通用逻辑，帮助商户快速接入银联商务支付能力。支持 H5 支付、微信小程序支付、订单查询、退款、退款查询、支付通知验签等核心支付场景。

**技术栈**:
- 语言: Java 8+（编译目标 1.8）
- 构建工具: Maven
- HTTP 客户端: OkHttp 4.12.0
- JSON 序列化: Jackson 2.15.2
- 日志: SLF4J 1.7.36 + Logback 1.2.12（测试范围）
- 工具库: Apache Commons Lang3 3.12.0
- 测试: JUnit 5.9.3 + Mockito 5.4.0 + OkHttp MockWebServer 4.12.0
- 覆盖率: JaCoCo 0.8.10
- 许可证: MIT License

**版本**: 1.0.0-SNAPSHOT

**GroupId**: `com.chinaums` | **ArtifactId**: `ums-sdk`

# 编码规范

## 命名惯例

| 类别 | 规范 | 示例 |
|------|------|------|
| 包名 | 全小写，按功能分层 | `com.chinaums.sdk.config` |
| 类名 | PascalCase，以功能/领域词结尾 | `UmsPayClient`, `H5PayRequest`, `SignatureUtil` |
| 请求类 | `{业务}Request` | `H5PayRequest`, `MiniProgramPayRequest` |
| 响应类 | `{业务}Response` | `H5PayResponse`, `QueryResponse` |
| 异常类 | `UmsPay{类型}Exception` | `UmsPayAuthException`, `UmsPayNetworkException` |
| 工具类 | `{功能}Util`，final 类 + 私有构造 | `JsonUtil`, `DateUtil` |
| 枚举 | PascalCase | `Environment` |
| 常量 | UPPER_SNAKE_CASE | `DEFAULT_TRADE_TYPE`, `JSON_MEDIA_TYPE` |
| 变量/方法 | camelCase | `merOrderId`, `buildRequestBody()` |
| Builder 内部类 | 统一使用 `Builder` 内部类 | `UmsPayConfig.Builder` |

## 目录结构惯例

```
com.chinaums.sdk/
├── config/          # 配置相关（UmsPayConfig, Environment）
├── exception/       # 异常体系（UmsPayException 及其子类）
├── http/            # HTTP 通信层（HttpClient 接口, OkHttpExecutor 实现）
├── request/         # 请求对象（BaseRequest 及其子类, Goods, SubOrder）
├── response/        # 响应对象（BaseResponse 及其子类, MiniPayRequest）
├── security/        # 安全/签名（SignatureUtil, NotificationVerifier）
├── util/            # 工具类（JsonUtil, DateUtil）
└── UmsPayClient.java  # 核心入口类
```

## API 设计惯例

- **配置对象**: 使用 Builder 模式构建，必填参数在 `build()` 中校验并抛出 `IllegalArgumentException`
- **请求对象**: 复杂请求使用 Builder 模式（`H5PayRequest`, `MiniProgramPayRequest`, `RefundRequest`），简单请求使用构造函数（`QueryRequest`, `RefundQueryRequest`）
- **响应对象**: 使用 setter 注入（Jackson 反序列化），提供 getter 访问
- **JSON 字段映射**: 统一使用 `@JsonProperty` 注解显式指定 JSON 字段名
- **空值处理**: 请求基类使用 `@JsonInclude(JsonInclude.Include.NON_NULL)` 忽略 null 字段
- **异常体系**: 继承自 `UmsPayException`，按错误类型分为 `UmsPayAuthException`（认证/签名）、`UmsPayBusinessException`（业务逻辑）、`UmsPayNetworkException`（网络通信）
- **HTTP 接口**: `HttpClient` 接口抽象，`OkHttpExecutor` 为唯一实现，支持 GET/POST
- **签名机制**: 请求签名（SHA-256 + HmacSHA256）和通知验签（SHA256/MD5）分离
- **日志规范**: 使用 SLF4J，关键操作记录 INFO 日志，调试信息使用 DEBUG

## 代码风格

- 每个源文件头部包含 MIT License 版权声明
- 公共 API 必须编写 Javadoc 注释，包含 `@author`、`@version`、`@since` 标签
- Javadoc 中提供使用示例（`<pre>{@code ...}</pre>` 格式）
- 类字段使用 `private final`，通过构造函数/Builder 初始化
- 工具类使用 `final` 修饰 + 私有构造函数

# 常用命令

```bash
# 编译项目
mvn clean compile

# 运行全部测试
mvn clean test

# 运行测试并生成覆盖率报告（输出到 target/site/jacoco/）
mvn clean test jacoco:report

# 打包（跳过测试）
mvn clean package -DskipTests

# 打包（含测试）
mvn clean package

# 安装到本地仓库
mvn clean install

# 生成 Javadoc
mvn javadoc:javadoc

# 生成源码包
mvn source:jar
```

**CI 环境要求**: 项目在 Java 8/11/17 三个版本上运行 CI，确保兼容性。

# 架构说明

## 整体架构

```
┌─────────────────────────────────────────────────┐
│                  UmsPayClient                    │  ← 统一入口
│  h5Pay() / miniProgramPay() / query() / ...     │
└──────────┬──────────────────────┬────────────────┘
           │                      │
           ▼                      ▼
┌──────────────────┐   ┌──────────────────────┐
│   security/      │   │      http/            │
│  SignatureUtil   │   │  HttpClient (接口)    │
│  Notification-   │   │  OkHttpExecutor (实现)│
│  Verifier        │   │  HttpResponse         │
└──────────────────┘   └──────────┬───────────┘
                                │
           ▼                      ▼
┌──────────────────┐   ┌──────────────────────┐
│   request/       │   │    response/          │
│  BaseRequest     │   │  BaseResponse         │
│  H5PayRequest    │   │  H5PayResponse        │
│  MiniProgram-    │   │  MiniProgramPay-      │
│  PayRequest      │   │  Response             │
│  QueryRequest    │   │  QueryResponse        │
│  RefundRequest   │   │  RefundResponse       │
│  RefundQuery-    │   │  RefundQueryResponse  │
│  Request         │   │                      │
│  Goods / SubOrder│   │  MiniPayRequest       │
└──────────────────┘   └──────────────────────┘
           │                      │
           ▼                      ▼
┌──────────────────┐   ┌──────────────────────┐
│   config/        │   │    util/              │
│  UmsPayConfig    │   │  JsonUtil             │
│  Environment     │   │  DateUtil             │
└──────────────────┘   └──────────────────────┘
```

## 模块功能说明

| 包 | 职责 | 核心类 |
|---|---|---|
| `config` | SDK 配置管理，环境切换 | `UmsPayConfig`（Builder 模式）, `Environment`（枚举：PRODUCTION/TEST） |
| `request` | 封装各 API 请求参数 | `BaseRequest`（公共字段：mid/tid/instMid/requestTimestamp/msgId/srcReserve）, `H5PayRequest`, `MiniProgramPayRequest`, `QueryRequest`, `RefundRequest`, `RefundQueryRequest`, `Goods`, `SubOrder` |
| `response` | 封装各 API 响应数据 | `BaseResponse`（公共字段：errCode/errMsg/mid/tid/instMid/msgId/srcReserve + `isSuccess()` 判断）, `H5PayResponse`, `MiniProgramPayResponse`, `QueryResponse`, `RefundResponse`, `RefundQueryResponse`, `MiniPayRequest` |
| `http` | HTTP 通信抽象与实现 | `HttpClient`（接口：get/post）, `OkHttpExecutor`（OkHttp 实现，含签名、超时、重试）, `HttpResponse`（响应封装） |
| `security` | 签名与验签 | `SignatureUtil`（请求签名：SHA-256 + HmacSHA256 + Base64；应答验签）, `NotificationVerifier`（通知验签：SHA256/MD5） |
| `exception` | 异常体系 | `UmsPayException`（基类，含 errorCode/requestId/timestamp）, `UmsPayAuthException`（AUTH_ERROR）, `UmsPayBusinessException`（业务错误码）, `UmsPayNetworkException`（NETWORK_ERROR） |
| `util` | 通用工具 | `JsonUtil`（Jackson 封装，忽略未知属性，上海时区）, `DateUtil`（日期格式化，上海时区） |

## API 端点映射

| SDK 方法 | HTTP 方法 | API 路径 | 说明 |
|---------|----------|---------|------|
| `h5Pay()` | GET | `/v1/netpay/wxpay/h5-pay` | H5 支付下单 |
| `miniProgramPay()` | POST | `/v1/netpay/wx/unified-order` | 微信小程序支付下单 |
| `query()` | POST | `/v1/netpay/query` | 订单查询 |
| `refund()` | POST | `/v1/netpay/refund` | 退款 |
| `refundQuery()` | POST | `/v1/netpay/refund-query` | 退款查询 |
| `verifyNotification()` | — | — | 支付通知验签（本地验证） |

## 请求处理流程

1. `UmsPayClient` 方法接收 Request 对象
2. `buildRequestBody()` 自动填充 mid/tid/instMid/requestTimestamp
3. `JsonUtil.toJson()` 序列化为 JSON
4. `OkHttpExecutor` 对请求内容签名（SHA-256 + HmacSHA256）
5. GET 请求：签名参数放入 URL query；POST 请求：签名放入 Authorization header
6. 发送 HTTP 请求并获取响应
7. `parseResponse()` 验证 HTTP 状态码 → 验证应答签名 → 反序列化 → 检查业务错误码

# 注意事项

## 安全相关

- **`appKey` 是核心密钥**，用于请求签名和应答验签。绝不能在日志中输出或硬编码到代码中。`OkHttpExecutor` 中 DEBUG 级别日志可能包含敏感信息，生产环境应调整日志级别。
- **`notifyKey` 用于通知验签**，`UmsPayClient.verifyNotification()` 要求必须配置 notifyKey，否则抛出 `UmsPayAuthException`。
- **签名算法不可随意修改**：`SignatureUtil` 中的签名流程（SHA-256 → 拼接 → HmacSHA256 → Base64）和 `NotificationVerifier` 中的验签流程（字典序排序 → 拼接 → 追加 key → 哈希）必须与银联商务 API 规范严格一致，任何改动都可能导致签名失败。
- **`Environment.PRODUCTION` 为默认环境**，生产 URL 为 `https://api-mop.chinaums.com`，测试 URL 为 `https://test-api-open.chinaums.com`。

## 核心文件修改风险

- **`UmsPayClient`**: SDK 主入口，所有 API 调用都经过此类。修改 `buildRequestBody()` 或 `parseResponse()` 会影响全部 API 的行为。
- **`OkHttpExecutor`**: HTTP 通信核心，修改签名逻辑或请求构建方式会影响所有 API 调用。
- **`SignatureUtil` / `NotificationVerifier`**: 签名/验签核心，任何算法变更都会导致与银联商务服务端不兼容。
- **`BaseRequest` / `BaseResponse`**: 请求/响应基类，修改公共字段会影响所有子类。
- **`JsonUtil`**: 全局 JSON 序列化/反序列化配置，修改 ObjectMapper 配置会影响所有 JSON 处理。

## 兼容性注意

- **Java 8 兼容性**: 项目编译目标为 Java 8，不能使用 Java 9+ 的 API（如 `List.of()`, `Map.of()`, `var` 等）。
- **Jackson 版本**: 使用 2.15.2，需注意 `@JsonProperty` 注解的用法一致性。
- **OkHttp 版本**: 使用 4.12.0，API 与 3.x 不兼容，不要混用。
- **金额单位**: 所有金额字段（`totalAmount`, `refundAmount`, `platformAmount` 等）统一使用 **Long 类型，单位为分**，不要误用为元。
- **时区**: `JsonUtil` 和 `DateUtil` 均硬编码为 `Asia/Shanghai` 时区，与银联商务 API 要求一致。

## 测试注意

- **集成测试**: `IntegrationTest` 需要真实的银联商务测试环境凭证，不应在 CI 中默认运行。
- **单元测试覆盖率要求**: 项目目标覆盖率 80%+，使用 JaCoCo 生成报告。
- **MockWebServer**: 用于模拟 HTTP 响应，测试 `OkHttpExecutor` 的行为。
- **测试包结构**: 测试类的包路径与源码一致（`com.chinaums.sdk.*`）。

## 扩展指南

- 新增支付方式：创建新的 `XxxRequest extends BaseRequest` 和 `XxxResponse extends BaseResponse`，在 `UmsPayClient` 中添加对应的公共方法。
- 新增 HTTP 实现：实现 `HttpClient` 接口，通过 `UmsPayClient(config, httpClient)` 构造函数注入。
- 新增异常类型：继承 `UmsPayException`，定义固定 errorCode 常量。
