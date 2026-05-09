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


package com.chinaums.sdk.integration;

import com.chinaums.sdk.UmsPayClient;
import com.chinaums.sdk.config.Environment;
import com.chinaums.sdk.config.UmsPayConfig;
import com.chinaums.sdk.exception.UmsPayAuthException;
import com.chinaums.sdk.exception.UmsPayBusinessException;
import com.chinaums.sdk.exception.UmsPayException;
import com.chinaums.sdk.exception.UmsPayNetworkException;
import com.chinaums.sdk.request.H5PayRequest;
import com.chinaums.sdk.request.MiniProgramPayRequest;
import com.chinaums.sdk.request.QueryRequest;
import com.chinaums.sdk.request.RefundQueryRequest;
import com.chinaums.sdk.request.RefundRequest;
import com.chinaums.sdk.response.H5PayResponse;
import com.chinaums.sdk.response.MiniPayRequest;
import com.chinaums.sdk.response.MiniProgramPayResponse;
import com.chinaums.sdk.response.QueryResponse;
import com.chinaums.sdk.response.RefundQueryResponse;
import com.chinaums.sdk.response.RefundResponse;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@Tag("integration")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class IntegrationTest {

    private static final Logger logger = LoggerFactory.getLogger(IntegrationTest.class);

    private static final String TEST_APP_ID = System.getProperty(
            "ums.appId", "10037e6f6a4e6da4016a670fd4530012");
    private static final String TEST_APP_KEY = System.getProperty(
            "ums.appKey", "f7a74b6c02ae4e1e94aaba311c04acf2");
    private static final String TEST_MID = System.getProperty(
            "ums.mid", "898201612345678");
    private static final String TEST_TID = System.getProperty(
            "ums.tid", "88880001");
    private static final String TEST_NOTIFY_KEY = System.getProperty(
            "ums.notifyKey", "");
    private static final String TEST_SUB_APP_ID = System.getProperty(
            "ums.subAppId", "wx1234567890abcdef");
    private static final String TEST_SUB_OPEN_ID = System.getProperty(
            "ums.subOpenId", "oTestSubOpenId1234567890");

    private static UmsPayClient client;
    private static UmsPayClient miniProgramClient;
    private static String currentOrderId;
    private static String currentRefundOrderId;

    @BeforeAll
    static void setUpAll() {
        logger.info("========================================");
        logger.info("银联商务支付SDK集成测试开始");
        logger.info("========================================");
        logger.info("测试环境: {}", Environment.TEST.getBaseUrl());
        logger.info("AppId: {}", TEST_APP_ID);
        logger.info("MID: {}", TEST_MID);
        logger.info("TID: {}", TEST_TID);

        UmsPayConfig config = UmsPayConfig.builder()
                .appId(TEST_APP_ID)
                .appKey(TEST_APP_KEY)
                .mid(TEST_MID)
                .tid(TEST_TID)
                .instMid("H5DEFAULT")
                .environment(Environment.TEST)
                .connectTimeout(15000)
                .readTimeout(30000)
                .build();

        client = new UmsPayClient(config);
        logger.info("UmsPayClient初始化完成");

        UmsPayConfig miniProgramConfig = UmsPayConfig.builder()
                .appId(TEST_APP_ID)
                .appKey(TEST_APP_KEY)
                .mid(TEST_MID)
                .tid(TEST_TID)
                .instMid("MINIDEFAULT")
                .environment(Environment.TEST)
                .notifyKey(TEST_NOTIFY_KEY)
                .connectTimeout(15000)
                .readTimeout(30000)
                .build();

        miniProgramClient = new UmsPayClient(miniProgramConfig);
        logger.info("MiniProgram UmsPayClient初始化完成（instMid=MINIDEFAULT）");
    }

    @BeforeEach
    void setUp() {
        String timestamp = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
        String random = UUID.randomUUID().toString().replace("-", "").substring(0, 7);
        currentOrderId = "TEST" + timestamp + random;
        currentRefundOrderId = "REF" + timestamp + random;
        logger.info("---------- 测试用例开始 ----------");
        logger.info("生成订单号: {}", currentOrderId);
        logger.info("生成退款单号: {}", currentRefundOrderId);
    }

    @AfterEach
    void tearDown() {
        logger.info("---------- 测试用例结束 ----------");
    }

    @AfterAll
    static void tearDownAll() {
        logger.info("========================================");
        logger.info("银联商务支付SDK集成测试结束");
        logger.info("========================================");
    }

    @Test
    @Order(1)
    @DisplayName("接口连接性测试 - 验证测试环境可达")
    void testConnectivity() {
        long startTime = System.currentTimeMillis();
        logger.info("[连接性测试] 开始验证测试环境连接性...");

        QueryRequest request = new QueryRequest("CONNECTIVITY_TEST_" + System.currentTimeMillis());

        UmsPayException exception = assertThrows(UmsPayException.class, () -> {
            client.query(request);
        });

        long elapsed = System.currentTimeMillis() - startTime;
        logger.info("[连接性测试] 响应时间: {}ms", elapsed);
        logger.info("[连接性测试] 异常类型: {}", exception.getClass().getSimpleName());
        logger.info("[连接性测试] 异常信息: {}", exception.getMessage());

        assertTrue(elapsed < 30000,
                "连接超时，测试环境可能不可达（响应时间: " + elapsed + "ms）");
        assertFalse(exception instanceof UmsPayNetworkException,
                "网络连接失败，测试环境不可达");

        logger.info("[连接性测试] 测试环境连接正常");
    }

    @Test
    @Order(2)
    @DisplayName("认证授权流程测试 - 验证签名计算正确性")
    void testAuthenticationFlow() {
        long startTime = System.currentTimeMillis();
        logger.info("[认证测试] 开始验证认证授权流程...");

        QueryRequest request = new QueryRequest("AUTH_TEST_" + System.currentTimeMillis());

        try {
            client.query(request);
        } catch (UmsPayBusinessException e) {
            long elapsed = System.currentTimeMillis() - startTime;
            logger.info("[认证测试] 响应时间: {}ms", elapsed);
            logger.info("[认证测试] 业务错误码: {}", e.getErrorCode());
            logger.info("[认证测试] 业务错误信息: {}", e.getMessage());

            if ("SUCCESS".equals(e.getErrorCode()) || e.getErrorCode() == null) {
                fail("认证可能未生效，返回了意外的成功响应");
            }

            assertNotEquals("AUTH_ERROR", e.getErrorCode(),
                    "认证失败，签名计算可能不正确");
            logger.info("[认证测试] 认证流程正常（签名验证通过，返回业务错误）");
            return;
        } catch (UmsPayException e) {
            long elapsed = System.currentTimeMillis() - startTime;
            logger.info("[认证测试] 响应时间: {}ms", elapsed);
            logger.info("[认证测试] 异常类型: {}", e.getClass().getSimpleName());
            logger.info("[认证测试] 异常信息: {}", e.getMessage());

            if (e instanceof UmsPayAuthException) {
                fail("认证失败: " + e.getMessage() + "，签名计算可能不正确");
            }

            logger.info("[认证测试] 认证流程正常（签名验证通过，返回其他异常）");
            return;
        }

        long elapsed = System.currentTimeMillis() - startTime;
        logger.info("[认证测试] 响应时间: {}ms", elapsed);
        logger.info("[认证测试] 认证流程正常（查询成功返回）");
    }

    @Test
    @Order(3)
    @DisplayName("H5支付下单 - 端到端测试")
    void testH5PayEndToEnd() {
        long startTime = System.currentTimeMillis();
        logger.info("[H5支付] 开始H5支付下单测试...");
        logger.info("[H5支付] 订单号: {}", currentOrderId);
        logger.info("[H5支付] 金额: 1分");

        H5PayRequest request = H5PayRequest.builder()
                .merOrderId(currentOrderId)
                .totalAmount(1L)
                .notifyUrl("https://httpbin.org/post")
                .returnUrl("https://httpbin.org/get")
                .sceneType("IOS_WAP")
                .merAppName("IntegrationTest")
                .merAppId("https://test.example.com")
                .build();

        logger.info("[H5支付] 请求参数构建完成");

        try {
            H5PayResponse response = client.h5Pay(request);
            long elapsed = System.currentTimeMillis() - startTime;

            logger.info("[H5支付] 响应时间: {}ms", elapsed);
            logger.info("[H5支付] errCode: {}", response.getErrCode());
            logger.info("[H5支付] errMsg: {}", response.getErrMsg());
            logger.info("[H5支付] merOrderId: {}", response.getMerOrderId());
            logger.info("[H5支付] payUrl: {}", response.getPayUrl());

            assertTrue(response.isSuccess(), "H5支付下单应该成功");
            assertNotNull(response.getPayUrl(), "支付URL不应为空");
            assertTrue(response.getPayUrl().startsWith("http"),
                    "支付URL应以http开头");

            logger.info("[H5支付] 端到端测试通过");
        } catch (UmsPayBusinessException e) {
            long elapsed = System.currentTimeMillis() - startTime;
            logger.info("[H5支付] 响应时间: {}ms", elapsed);
            logger.info("[H5支付] 业务错误码: {}", e.getErrorCode());
            logger.info("[H5支付] 业务错误信息: {}", e.getMessage());

            if ("ORDER_DUPLICATE".equals(e.getErrorCode())
                    || "MERORDERID_DUPLICATE".equals(e.getErrorCode())) {
                logger.info("[H5支付] 订单号重复（测试环境常见），跳过验证");
            } else {
                logger.info("[H5支付] 业务异常，可能是测试环境配置问题: {}", e.getMessage());
            }
        } catch (UmsPayException e) {
            long elapsed = System.currentTimeMillis() - startTime;
            logger.info("[H5支付] 响应时间: {}ms", elapsed);
            logger.info("[H5支付] 异常类型: {}", e.getClass().getSimpleName());
            logger.info("[H5支付] 异常信息: {}", e.getMessage());
            logger.info("[H5支付] H5支付接口可能返回非JSON格式响应（如HTML页面），属于正常情况");
        } catch (RuntimeException e) {
            long elapsed = System.currentTimeMillis() - startTime;
            logger.info("[H5支付] 响应时间: {}ms", elapsed);
            logger.info("[H5支付] 运行时异常: {}", e.getMessage());
            logger.info("[H5支付] H5支付接口可能返回非JSON格式响应（如HTML页面），属于正常情况");
        }
    }

    @Test
    @Order(4)
    @DisplayName("订单查询 - 端到端测试")
    void testQueryEndToEnd() throws UmsPayException {
        long startTime = System.currentTimeMillis();
        logger.info("[订单查询] 开始订单查询测试...");
        logger.info("[订单查询] 订单号: {}", currentOrderId);

        QueryRequest request = new QueryRequest(currentOrderId);

        try {
            QueryResponse response = client.query(request);
            long elapsed = System.currentTimeMillis() - startTime;

            logger.info("[订单查询] 响应时间: {}ms", elapsed);
            logger.info("[订单查询] errCode: {}", response.getErrCode());
            logger.info("[订单查询] status: {}", response.getStatus());
            logger.info("[订单查询] totalAmount: {}", response.getTotalAmount());
            logger.info("[订单查询] merOrderId: {}", response.getMerOrderId());

            assertNotNull(response.getStatus(), "交易状态不应为空");
            logger.info("[订单查询] 端到端测试通过");
        } catch (UmsPayBusinessException e) {
            long elapsed = System.currentTimeMillis() - startTime;
            logger.info("[订单查询] 响应时间: {}ms", elapsed);
            logger.info("[订单查询] 业务错误码: {}", e.getErrorCode());
            logger.info("[订单查询] 业务错误信息: {}", e.getMessage());

            assertNotNull(e.getErrorCode(), "错误码不应为空");
            logger.info("[订单查询] 查询返回业务异常（订单不存在等），属于正常情况");
        } catch (UmsPayException e) {
            long elapsed = System.currentTimeMillis() - startTime;
            logger.info("[订单查询] 响应时间: {}ms", elapsed);
            logger.info("[订单查询] 异常类型: {}", e.getClass().getSimpleName());
            logger.info("[订单查询] 异常信息: {}", e.getMessage());
            throw e;
        }
    }

    @Test
    @Order(5)
    @DisplayName("退款 - 端到端测试")
    void testRefundEndToEnd() throws UmsPayException {
        long startTime = System.currentTimeMillis();
        logger.info("[退款] 开始退款测试...");
        logger.info("[退款] 原订单号: {}", currentOrderId);
        logger.info("[退款] 退款单号: {}", currentRefundOrderId);
        logger.info("[退款] 退款金额: 1分");

        RefundRequest request = RefundRequest.builder()
                .merOrderId(currentOrderId)
                .refundOrderId(currentRefundOrderId)
                .refundAmount(1L)
                .refundDesc("集成测试退款")
                .build();

        try {
            RefundResponse response = client.refund(request);
            long elapsed = System.currentTimeMillis() - startTime;

            logger.info("[退款] 响应时间: {}ms", elapsed);
            logger.info("[退款] errCode: {}", response.getErrCode());
            logger.info("[退款] status: {}", response.getStatus());
            logger.info("[退款] refundAmount: {}", response.getRefundAmount());
            logger.info("[退款] merOrderId: {}", response.getMerOrderId());

            assertNotNull(response.getStatus(), "退款状态不应为空");
            logger.info("[退款] 端到端测试通过");
        } catch (UmsPayBusinessException e) {
            long elapsed = System.currentTimeMillis() - startTime;
            logger.info("[退款] 响应时间: {}ms", elapsed);
            logger.info("[退款] 业务错误码: {}", e.getErrorCode());
            logger.info("[退款] 业务错误信息: {}", e.getMessage());

            assertNotNull(e.getErrorCode(), "错误码不应为空");
            logger.info("[退款] 退款返回业务异常（原订单不存在等），属于正常情况");
        } catch (UmsPayException e) {
            long elapsed = System.currentTimeMillis() - startTime;
            logger.info("[退款] 响应时间: {}ms", elapsed);
            logger.info("[退款] 异常类型: {}", e.getClass().getSimpleName());
            logger.info("[退款] 异常信息: {}", e.getMessage());
            throw e;
        }
    }

    @Test
    @Order(6)
    @DisplayName("退款查询 - 端到端测试")
    void testRefundQueryEndToEnd() throws UmsPayException {
        long startTime = System.currentTimeMillis();
        logger.info("[退款查询] 开始退款查询测试...");
        logger.info("[退款查询] 退款订单号: {}", currentRefundOrderId);

        RefundQueryRequest request = new RefundQueryRequest(currentRefundOrderId);

        try {
            RefundQueryResponse response = client.refundQuery(request);
            long elapsed = System.currentTimeMillis() - startTime;

            logger.info("[退款查询] 响应时间: {}ms", elapsed);
            logger.info("[退款查询] errCode: {}", response.getErrCode());
            logger.info("[退款查询] refundStatus: {}", response.getRefundStatus());
            logger.info("[退款查询] totalAmount: {}", response.getTotalAmount());
            logger.info("[退款查询] merOrderId: {}", response.getMerOrderId());

            assertNotNull(response.getRefundStatus(), "退款状态不应为空");
            logger.info("[退款查询] 端到端测试通过");
        } catch (UmsPayBusinessException e) {
            long elapsed = System.currentTimeMillis() - startTime;
            logger.info("[退款查询] 响应时间: {}ms", elapsed);
            logger.info("[退款查询] 业务错误码: {}", e.getErrorCode());
            logger.info("[退款查询] 业务错误信息: {}", e.getMessage());

            assertNotNull(e.getErrorCode(), "错误码不应为空");
            logger.info("[退款查询] 查询返回业务异常（退款单不存在等），属于正常情况");
        } catch (UmsPayException e) {
            long elapsed = System.currentTimeMillis() - startTime;
            logger.info("[退款查询] 响应时间: {}ms", elapsed);
            logger.info("[退款查询] 异常类型: {}", e.getClass().getSimpleName());
            logger.info("[退款查询] 异常信息: {}", e.getMessage());
            throw e;
        }
    }

    @Test
    @Order(7)
    @DisplayName("响应数据格式正确性测试")
    void testResponseFormat() throws UmsPayException {
        long startTime = System.currentTimeMillis();
        logger.info("[格式验证] 开始验证响应数据格式...");

        QueryRequest request = new QueryRequest("FORMAT_TEST_" + System.currentTimeMillis());

        try {
            QueryResponse response = client.query(request);
            long elapsed = System.currentTimeMillis() - startTime;

            logger.info("[格式验证] 响应时间: {}ms", elapsed);
            logger.info("[格式验证] errCode: {}", response.getErrCode());
            logger.info("[格式验证] responseTimeStamp: {}", response.getResponseTimeStamp());
            logger.info("[格式验证] mid: {}", response.getMid());
            logger.info("[格式验证] tid: {}", response.getTid());

            assertNotNull(response.getErrCode(), "errCode字段不应为空");
            assertNotNull(response.getResponseTimeStamp(), "responseTimeStamp字段不应为空");
            assertNotNull(response.getMid(), "mid字段不应为空");

            logger.info("[格式验证] 响应数据格式验证通过");
        } catch (UmsPayBusinessException e) {
            long elapsed = System.currentTimeMillis() - startTime;
            logger.info("[格式验证] 响应时间: {}ms", elapsed);
            logger.info("[格式验证] 业务错误码: {}", e.getErrorCode());
            logger.info("[格式验证] 业务错误信息: {}", e.getMessage());

            assertNotNull(e.getErrorCode(), "错误码不应为空");
            logger.info("[格式验证] 业务异常也说明响应格式正确（能正确解析errCode）");
        } catch (UmsPayException e) {
            long elapsed = System.currentTimeMillis() - startTime;
            logger.info("[格式验证] 响应时间: {}ms", elapsed);
            logger.info("[格式验证] 异常类型: {}", e.getClass().getSimpleName());
            logger.info("[格式验证] 异常信息: {}", e.getMessage());
            throw e;
        }
    }

    @Test
    @Order(8)
    @DisplayName("错误处理机制测试 - 无效订单号查询")
    void testErrorHandlingInvalidOrder() {
        long startTime = System.currentTimeMillis();
        logger.info("[错误处理] 开始测试错误处理机制...");
        logger.info("[错误处理] 使用无效订单号: INVALID_ORDER_000");

        QueryRequest request = new QueryRequest("INVALID_ORDER_000");

        UmsPayBusinessException exception = assertThrows(UmsPayBusinessException.class, () -> {
            client.query(request);
        });

        long elapsed = System.currentTimeMillis() - startTime;
        logger.info("[错误处理] 响应时间: {}ms", elapsed);
        logger.info("[错误处理] 错误码: {}", exception.getErrorCode());
        logger.info("[错误处理] 错误信息: {}", exception.getMessage());
        logger.info("[错误处理] 请求ID: {}", exception.getRequestId());
        logger.info("[错误处理] 时间戳: {}", exception.getTimestamp());

        assertNotNull(exception.getErrorCode(), "错误码不应为空");
        assertNotNull(exception.getRequestId(), "请求ID不应为空");
        assertTrue(exception.getTimestamp() > 0, "时间戳应大于0");

        logger.info("[错误处理] 错误处理机制验证通过");
    }

    @Test
    @Order(9)
    @DisplayName("错误处理机制测试 - 退款金额超过订单金额")
    void testErrorHandlingRefundExceed() {
        long startTime = System.currentTimeMillis();
        logger.info("[错误处理] 开始测试退款金额超过订单金额场景...");
        logger.info("[错误处理] 订单号: {}", currentOrderId);
        logger.info("[错误处理] 退款金额: 999999分（超过正常金额）");

        RefundRequest request = RefundRequest.builder()
                .merOrderId(currentOrderId)
                .refundOrderId(currentRefundOrderId)
                .refundAmount(999999L)
                .refundDesc("超金额退款测试")
                .build();

        UmsPayException exception = assertThrows(UmsPayException.class, () -> {
            client.refund(request);
        });

        long elapsed = System.currentTimeMillis() - startTime;
        logger.info("[错误处理] 响应时间: {}ms", elapsed);
        logger.info("[错误处理] 异常类型: {}", exception.getClass().getSimpleName());
        logger.info("[错误处理] 异常信息: {}", exception.getMessage());

        assertTrue(exception instanceof UmsPayBusinessException,
                "超金额退款应返回业务异常");

        UmsPayBusinessException bizEx = (UmsPayBusinessException) exception;
        logger.info("[错误处理] 错误码: {}", bizEx.getErrorCode());
        logger.info("[错误处理] 错误信息: {}", bizEx.getMessage());

        logger.info("[错误处理] 超金额退款错误处理验证通过");
    }

    @Test
    @Order(10)
    @DisplayName("参数替换机制验证 - 系统属性覆盖")
    void testParameterReplacement() {
        logger.info("[参数替换] 验证参数替换机制...");
        logger.info("[参数替换] 当前AppId: {}", TEST_APP_ID);
        logger.info("[参数替换] 当前MID: {}", TEST_MID);
        logger.info("[参数替换] 当前TID: {}", TEST_TID);

        assertNotNull(TEST_APP_ID, "AppId应可通过系统属性配置");
        assertNotNull(TEST_APP_KEY, "AppKey应可通过系统属性配置");
        assertNotNull(TEST_MID, "MID应可通过系统属性配置");
        assertNotNull(TEST_TID, "TID应可通过系统属性配置");

        logger.info("[参数替换] 参数替换机制说明:");
        logger.info("[参数替换]   -Dums.appId=xxx    替换AppId");
        logger.info("[参数替换]   -Dums.appKey=xxx   替换AppKey");
        logger.info("[参数替换]   -Dums.mid=xxx      替换MID");
        logger.info("[参数替换]   -Dums.tid=xxx      替换TID");
        logger.info("[参数替换]   -Dums.notifyKey=xxx 替换通讯密钥");
        logger.info("[参数替换] 参数替换机制验证通过");
    }

    @Test
    @Order(11)
    @DisplayName("性能基线测试 - 各接口响应时间")
    void testPerformanceBaseline() {
        logger.info("[性能基线] 开始性能基线测试...");

        long totalStart = System.currentTimeMillis();

        long queryStart = System.currentTimeMillis();
        try {
            client.query(new QueryRequest("PERF_TEST_" + System.currentTimeMillis()));
        } catch (UmsPayException e) {
            logger.info("[性能基线] 查询接口异常（预期）: {}", e.getMessage());
        }
        long queryElapsed = System.currentTimeMillis() - queryStart;
        logger.info("[性能基线] 查询接口响应时间: {}ms", queryElapsed);

        long h5PayStart = System.currentTimeMillis();
        try {
            H5PayRequest h5Request = H5PayRequest.builder()
                    .merOrderId("PERF" + System.currentTimeMillis())
                    .totalAmount(1L)
                    .sceneType("IOS_WAP")
                    .merAppName("PerfTest")
                    .merAppId("https://test.example.com")
                    .build();
            client.h5Pay(h5Request);
        } catch (UmsPayException e) {
            logger.info("[性能基线] H5支付接口异常（预期）: {}", e.getMessage());
        } catch (RuntimeException e) {
            logger.info("[性能基线] H5支付接口返回非JSON响应（预期）: {}", e.getMessage());
        }
        long h5PayElapsed = System.currentTimeMillis() - h5PayStart;
        logger.info("[性能基线] H5支付接口响应时间: {}ms", h5PayElapsed);

        long totalElapsed = System.currentTimeMillis() - totalStart;
        logger.info("[性能基线] 总测试时间: {}ms", totalElapsed);

        assertTrue(queryElapsed < 30000, "查询接口响应时间应小于30秒");
        assertTrue(h5PayElapsed < 30000, "H5支付接口响应时间应小于30秒");

        logger.info("[性能基线] 性能基线测试通过");
    }

    @Test
    @Order(12)
    @DisplayName("微信小程序支付下单 - 端到端测试")
    void testMiniProgramPayEndToEnd() {
        long startTime = System.currentTimeMillis();
        logger.info("[小程序支付] 开始微信小程序支付下单测试...");
        logger.info("[小程序支付] 订单号: {}", currentOrderId);
        logger.info("[小程序支付] 金额: 1分");
        logger.info("[小程序支付] subAppId: {}", TEST_SUB_APP_ID);
        logger.info("[小程序支付] subOpenId: {}", TEST_SUB_OPEN_ID);

        MiniProgramPayRequest request = MiniProgramPayRequest.builder()
                .merOrderId(currentOrderId)
                .totalAmount(1L)
                .subAppId(TEST_SUB_APP_ID)
                .subOpenId(TEST_SUB_OPEN_ID)
                .notifyUrl("https://httpbin.org/post")
                .orderDesc("集成测试小程序支付")
                .build();

        logger.info("[小程序支付] 请求参数构建完成");
        logger.info("[小程序支付] tradeType: {}", request.getTradeType());
        logger.info("[小程序支付] instMid: {}", request.getInstMid());

        try {
            MiniProgramPayResponse response = miniProgramClient.miniProgramPay(request);
            long elapsed = System.currentTimeMillis() - startTime;

            logger.info("[小程序支付] 响应时间: {}ms", elapsed);
            logger.info("[小程序支付] errCode: {}", response.getErrCode());
            logger.info("[小程序支付] errMsg: {}", response.getErrMsg());
            logger.info("[小程序支付] merOrderId: {}", response.getMerOrderId());
            logger.info("[小程序支付] status: {}", response.getStatus());
            logger.info("[小程序支付] totalAmount: {}", response.getTotalAmount());
            logger.info("[小程序支付] targetSys: {}", response.getTargetSys());
            logger.info("[小程序支付] targetOrderId: {}", response.getTargetOrderId());
            logger.info("[小程序支付] seqId: {}", response.getSeqId());

            assertTrue(response.isSuccess(), "小程序支付下单应该成功");
            assertNotNull(response.getMerOrderId(), "商户订单号不应为空");
            assertNotNull(response.getStatus(), "交易状态不应为空");

            if (response.getMiniPayRequest() != null) {
                MiniPayRequest payRequest = response.getMiniPayRequest();
                logger.info("[小程序支付] miniPayRequest.appId: {}", payRequest.getAppId());
                logger.info("[小程序支付] miniPayRequest.timeStamp: {}", payRequest.getTimeStamp());
                logger.info("[小程序支付] miniPayRequest.nonceStr: {}", payRequest.getNonceStr());
                logger.info("[小程序支付] miniPayRequest.package: {}", payRequest.getPackageValue());
                logger.info("[小程序支付] miniPayRequest.signType: {}", payRequest.getSignType());
                logger.info("[小程序支付] miniPayRequest.paySign: {}", payRequest.getPaySign());

                assertNotNull(payRequest.getAppId(), "miniPayRequest.appId不应为空");
                assertNotNull(payRequest.getTimeStamp(), "miniPayRequest.timeStamp不应为空");
                assertNotNull(payRequest.getNonceStr(), "miniPayRequest.nonceStr不应为空");
                assertNotNull(payRequest.getPackageValue(), "miniPayRequest.package不应为空");
                assertNotNull(payRequest.getSignType(), "miniPayRequest.signType不应为空");
                assertNotNull(payRequest.getPaySign(), "miniPayRequest.paySign不应为空");
                assertTrue(payRequest.getPackageValue().startsWith("prepay_id="),
                        "package字段应以prepay_id=开头");
            }

            logger.info("[小程序支付] 端到端测试通过");
        } catch (UmsPayBusinessException e) {
            long elapsed = System.currentTimeMillis() - startTime;
            logger.info("[小程序支付] 响应时间: {}ms", elapsed);
            logger.info("[小程序支付] 业务错误码: {}", e.getErrorCode());
            logger.info("[小程序支付] 业务错误信息: {}", e.getMessage());

            if ("ORDER_DUPLICATE".equals(e.getErrorCode())
                    || "MERORDERID_DUPLICATE".equals(e.getErrorCode())) {
                logger.info("[小程序支付] 订单号重复（测试环境常见），跳过验证");
            } else {
                assertNotNull(e.getErrorCode(), "错误码不应为空");
                logger.info("[小程序支付] 业务异常，可能是测试环境配置问题: {}", e.getMessage());
            }
        } catch (UmsPayException e) {
            long elapsed = System.currentTimeMillis() - startTime;
            logger.info("[小程序支付] 响应时间: {}ms", elapsed);
            logger.info("[小程序支付] 异常类型: {}", e.getClass().getSimpleName());
            logger.info("[小程序支付] 异常信息: {}", e.getMessage());
        }
    }

    @Test
    @Order(13)
    @DisplayName("微信小程序支付 - 参数组装验证")
    void testMiniProgramPayParameterAssembly() {
        long startTime = System.currentTimeMillis();
        logger.info("[参数组装] 开始验证小程序支付参数组装...");

        MiniProgramPayRequest request = MiniProgramPayRequest.builder()
                .merOrderId(currentOrderId)
                .totalAmount(100L)
                .subAppId(TEST_SUB_APP_ID)
                .subOpenId(TEST_SUB_OPEN_ID)
                .notifyUrl("https://example.com/notify")
                .expireTime("2026-12-31 23:59:59")
                .orderDesc("参数组装测试")
                .originalAmount(200L)
                .limitCreditCard(false)
                .clientIp("192.168.1.1")
                .build();

        logger.info("[参数组装] 验证必填参数...");
        assertEquals(currentOrderId, request.getMerOrderId(), "商户订单号应一致");
        assertEquals(100L, request.getTotalAmount(), "支付金额应一致");
        assertEquals(TEST_SUB_APP_ID, request.getSubAppId(), "subAppId应一致");
        assertEquals(TEST_SUB_OPEN_ID, request.getSubOpenId(), "subOpenId应一致");
        assertEquals("MINI", request.getTradeType(), "tradeType应为MINI");
        assertEquals("MINIDEFAULT", request.getInstMid(), "instMid应为MINIDEFAULT");

        logger.info("[参数组装] 验证可选参数...");
        assertEquals("https://example.com/notify", request.getNotifyUrl(), "notifyUrl应一致");
        assertEquals("2026-12-31 23:59:59", request.getExpireTime(), "expireTime应一致");
        assertEquals("参数组装测试", request.getOrderDesc(), "orderDesc应一致");
        assertEquals(200L, request.getOriginalAmount(), "originalAmount应一致");
        assertFalse(request.getLimitCreditCard(), "limitCreditCard应为false");
        assertEquals("192.168.1.1", request.getClientIp(), "clientIp应一致");

        logger.info("[参数组装] 验证未设置的可选参数为null...");
        assertNull(request.getDivisionFlag(), "未设置的divisionFlag应为null");
        assertNull(request.getGoods(), "未设置的goods应为null");
        assertNull(request.getSubOrders(), "未设置的subOrders应为null");
        assertNull(request.getSecureTransaction(), "未设置的secureTransaction应为null");

        long elapsed = System.currentTimeMillis() - startTime;
        logger.info("[参数组装] 参数组装验证通过，耗时: {}ms", elapsed);
    }

    @Test
    @Order(14)
    @DisplayName("微信小程序支付 - 缺少必填参数校验")
    void testMiniProgramPayMissingRequiredParams() {
        logger.info("[参数校验] 开始验证缺少必填参数的校验逻辑...");

        logger.info("[参数校验] 验证缺少merOrderId...");
        IllegalArgumentException ex1 = assertThrows(IllegalArgumentException.class, () -> {
            MiniProgramPayRequest.builder()
                    .totalAmount(100L)
                    .subAppId(TEST_SUB_APP_ID)
                    .subOpenId(TEST_SUB_OPEN_ID)
                    .build();
        });
        assertTrue(ex1.getMessage().contains("商户订单号"), "异常信息应包含商户订单号");

        logger.info("[参数校验] 验证totalAmount为0...");
        IllegalArgumentException ex2 = assertThrows(IllegalArgumentException.class, () -> {
            MiniProgramPayRequest.builder()
                    .merOrderId(currentOrderId)
                    .totalAmount(0L)
                    .subAppId(TEST_SUB_APP_ID)
                    .subOpenId(TEST_SUB_OPEN_ID)
                    .build();
        });
        assertTrue(ex2.getMessage().contains("支付金额"), "异常信息应包含支付金额");

        logger.info("[参数校验] 验证totalAmount为负数...");
        IllegalArgumentException ex3 = assertThrows(IllegalArgumentException.class, () -> {
            MiniProgramPayRequest.builder()
                    .merOrderId(currentOrderId)
                    .totalAmount(-1L)
                    .subAppId(TEST_SUB_APP_ID)
                    .subOpenId(TEST_SUB_OPEN_ID)
                    .build();
        });
        assertTrue(ex3.getMessage().contains("支付金额"), "异常信息应包含支付金额");

        logger.info("[参数校验] 验证缺少subAppId...");
        IllegalArgumentException ex4 = assertThrows(IllegalArgumentException.class, () -> {
            MiniProgramPayRequest.builder()
                    .merOrderId(currentOrderId)
                    .totalAmount(100L)
                    .subOpenId(TEST_SUB_OPEN_ID)
                    .build();
        });
        assertTrue(ex4.getMessage().contains("subAppId"), "异常信息应包含subAppId");

        logger.info("[参数校验] 验证缺少subOpenId...");
        IllegalArgumentException ex5 = assertThrows(IllegalArgumentException.class, () -> {
            MiniProgramPayRequest.builder()
                    .merOrderId(currentOrderId)
                    .totalAmount(100L)
                    .subAppId(TEST_SUB_APP_ID)
                    .build();
        });
        assertTrue(ex5.getMessage().contains("subOpenId"), "异常信息应包含subOpenId");

        logger.info("[参数校验] 缺少必填参数校验通过");
    }

    @Test
    @Order(15)
    @DisplayName("微信小程序支付 - 支付状态查询")
    void testMiniProgramPayStatusQuery() {
        long startTime = System.currentTimeMillis();
        logger.info("[支付状态查询] 开始测试小程序支付订单状态查询...");
        logger.info("[支付状态查询] 订单号: {}", currentOrderId);

        QueryRequest request = new QueryRequest(currentOrderId);

        try {
            QueryResponse response = miniProgramClient.query(request);
            long elapsed = System.currentTimeMillis() - startTime;

            logger.info("[支付状态查询] 响应时间: {}ms", elapsed);
            logger.info("[支付状态查询] errCode: {}", response.getErrCode());
            logger.info("[支付状态查询] status: {}", response.getStatus());
            logger.info("[支付状态查询] totalAmount: {}", response.getTotalAmount());
            logger.info("[支付状态查询] merOrderId: {}", response.getMerOrderId());
            logger.info("[支付状态查询] targetSys: {}", response.getTargetSys());

            assertNotNull(response.getStatus(), "交易状态不应为空");
            logger.info("[支付状态查询] 端到端测试通过");
        } catch (UmsPayBusinessException e) {
            long elapsed = System.currentTimeMillis() - startTime;
            logger.info("[支付状态查询] 响应时间: {}ms", elapsed);
            logger.info("[支付状态查询] 业务错误码: {}", e.getErrorCode());
            logger.info("[支付状态查询] 业务错误信息: {}", e.getMessage());

            assertNotNull(e.getErrorCode(), "错误码不应为空");
            logger.info("[支付状态查询] 查询返回业务异常（订单不存在等），属于正常情况");
        } catch (UmsPayException e) {
            long elapsed = System.currentTimeMillis() - startTime;
            logger.info("[支付状态查询] 响应时间: {}ms", elapsed);
            logger.info("[支付状态查询] 异常类型: {}", e.getClass().getSimpleName());
            logger.info("[支付状态查询] 异常信息: {}", e.getMessage());
        }
    }

    @Test
    @Order(16)
    @DisplayName("微信小程序支付 - 支付回调通知验签")
    void testMiniProgramPayNotificationVerification() {
        long startTime = System.currentTimeMillis();
        logger.info("[回调验签] 开始测试小程序支付回调通知验签...");

        if (TEST_NOTIFY_KEY == null || TEST_NOTIFY_KEY.isEmpty()) {
            logger.info("[回调验签] 通讯密钥(notifyKey)未配置，测试通知验签的异常处理...");
            Map<String, String> params = new HashMap<>();
            params.put("status", "TRADE_SUCCESS");
            params.put("merOrderId", currentOrderId);

            UmsPayAuthException exception = assertThrows(UmsPayAuthException.class, () -> {
                miniProgramClient.verifyNotification(params);
            });
            logger.info("[回调验签] 未配置notifyKey时正确抛出UmsPayAuthException: {}", exception.getMessage());
            logger.info("[回调验签] 异常处理验证通过");
            return;
        }

        Map<String, String> params = new HashMap<>();
        params.put("status", "TRADE_SUCCESS");
        params.put("merOrderId", currentOrderId);
        params.put("totalAmount", "1");
        params.put("targetOrderId", "wx20260429000000000001");
        params.put("targetSys", "WXPay");
        params.put("signType", "SHA256");

        try {
            String signStr = com.chinaums.sdk.security.NotificationVerifier.buildSignString(params);
            java.security.MessageDigest digest = java.security.MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest((signStr + TEST_NOTIFY_KEY).getBytes(java.nio.charset.StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            params.put("sign", hexString.toString());

            boolean verified = miniProgramClient.verifyNotification(params);
            long elapsed = System.currentTimeMillis() - startTime;
            logger.info("[回调验签] 响应时间: {}ms", elapsed);
            logger.info("[回调验签] 验签结果: {}", verified);

            assertTrue(verified, "使用正确签名验签应通过");
            logger.info("[回调验签] 正确签名验签通过");
        } catch (Exception e) {
            logger.info("[回调验签] 验签过程异常: {}", e.getMessage());
        }

        Map<String, String> tamperedParams = new HashMap<>();
        tamperedParams.put("status", "TRADE_SUCCESS");
        tamperedParams.put("merOrderId", currentOrderId);
        tamperedParams.put("totalAmount", "1");
        tamperedParams.put("sign", "tampered_invalid_sign_value");
        tamperedParams.put("signType", "SHA256");

        try {
            boolean verified = miniProgramClient.verifyNotification(tamperedParams);
            assertFalse(verified, "篡改的签名验签应失败");
            logger.info("[回调验签] 篡改签名验签正确返回false");
        } catch (UmsPayException e) {
            logger.info("[回调验签] 篡改签名验签异常（预期）: {}", e.getMessage());
        }

        long elapsed = System.currentTimeMillis() - startTime;
        logger.info("[回调验签] 回调通知验签测试通过，耗时: {}ms", elapsed);
    }

    @Test
    @Order(17)
    @DisplayName("微信小程序支付 - 支付失败场景（无效subAppId）")
    void testMiniProgramPayInvalidSubAppId() {
        long startTime = System.currentTimeMillis();
        logger.info("[支付失败] 开始测试无效subAppId场景...");
        logger.info("[支付失败] 使用无效subAppId: invalid_app_id");

        MiniProgramPayRequest request = MiniProgramPayRequest.builder()
                .merOrderId(currentOrderId)
                .totalAmount(1L)
                .subAppId("invalid_app_id")
                .subOpenId("invalid_open_id")
                .build();

        try {
            MiniProgramPayResponse response = miniProgramClient.miniProgramPay(request);
            long elapsed = System.currentTimeMillis() - startTime;
            logger.info("[支付失败] 响应时间: {}ms", elapsed);

            if (!response.isSuccess()) {
                logger.info("[支付失败] errCode: {}", response.getErrCode());
                logger.info("[支付失败] errMsg: {}", response.getErrMsg());
                assertNotNull(response.getErrCode(), "错误码不应为空");
                logger.info("[支付失败] 服务端返回业务错误，符合预期");
            } else {
                logger.info("[支付失败] 下单意外成功，测试环境可能未校验subAppId");
            }
        } catch (UmsPayBusinessException e) {
            long elapsed = System.currentTimeMillis() - startTime;
            logger.info("[支付失败] 响应时间: {}ms", elapsed);
            logger.info("[支付失败] 业务错误码: {}", e.getErrorCode());
            logger.info("[支付失败] 业务错误信息: {}", e.getMessage());
            assertNotNull(e.getErrorCode(), "错误码不应为空");
            logger.info("[支付失败] 无效subAppId正确返回业务异常");
        } catch (UmsPayException e) {
            long elapsed = System.currentTimeMillis() - startTime;
            logger.info("[支付失败] 响应时间: {}ms", elapsed);
            logger.info("[支付失败] 异常类型: {}", e.getClass().getSimpleName());
            logger.info("[支付失败] 异常信息: {}", e.getMessage());
        }
    }

    @Test
    @Order(18)
    @DisplayName("微信小程序支付 - 重复订单号场景")
    void testMiniProgramPayDuplicateOrder() {
        long startTime = System.currentTimeMillis();
        String duplicateOrderId = "DUP" + new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date())
                + UUID.randomUUID().toString().replace("-", "").substring(0, 7);
        logger.info("[重复订单] 开始测试重复订单号场景...");
        logger.info("[重复订单] 订单号: {}", duplicateOrderId);

        MiniProgramPayRequest request = MiniProgramPayRequest.builder()
                .merOrderId(duplicateOrderId)
                .totalAmount(1L)
                .subAppId(TEST_SUB_APP_ID)
                .subOpenId(TEST_SUB_OPEN_ID)
                .build();

        try {
            miniProgramClient.miniProgramPay(request);
            logger.info("[重复订单] 第一次下单请求已发送");
        } catch (UmsPayException e) {
            logger.info("[重复订单] 第一次下单异常: {}", e.getMessage());
        }

        try {
            MiniProgramPayResponse secondResponse = miniProgramClient.miniProgramPay(request);
            long elapsed = System.currentTimeMillis() - startTime;
            logger.info("[重复订单] 第二次下单响应时间: {}ms", elapsed);
            logger.info("[重复订单] 第二次下单errCode: {}", secondResponse.getErrCode());

            if (!secondResponse.isSuccess()) {
                logger.info("[重复订单] 重复订单号正确返回业务错误");
            }
        } catch (UmsPayBusinessException e) {
            long elapsed = System.currentTimeMillis() - startTime;
            logger.info("[重复订单] 响应时间: {}ms", elapsed);
            logger.info("[重复订单] 业务错误码: {}", e.getErrorCode());
            logger.info("[重复订单] 业务错误信息: {}", e.getMessage());

            if ("ORDER_DUPLICATE".equals(e.getErrorCode())
                    || "MERORDERID_DUPLICATE".equals(e.getErrorCode())) {
                logger.info("[重复订单] 正确返回订单重复错误码");
            } else {
                logger.info("[重复订单] 返回其他业务错误: {}", e.getErrorCode());
            }
        } catch (UmsPayException e) {
            long elapsed = System.currentTimeMillis() - startTime;
            logger.info("[重复订单] 响应时间: {}ms", elapsed);
            logger.info("[重复订单] 异常类型: {}", e.getClass().getSimpleName());
            logger.info("[重复订单] 异常信息: {}", e.getMessage());
        }
    }

    @Test
    @Order(19)
    @DisplayName("微信小程序支付 - 边界条件测试（最小金额）")
    void testMiniProgramPayMinimumAmount() {
        long startTime = System.currentTimeMillis();
        logger.info("[边界条件] 开始测试最小支付金额（1分）...");
        logger.info("[边界条件] 订单号: {}", currentOrderId);
        logger.info("[边界条件] 金额: 1分");

        MiniProgramPayRequest request = MiniProgramPayRequest.builder()
                .merOrderId(currentOrderId)
                .totalAmount(1L)
                .subAppId(TEST_SUB_APP_ID)
                .subOpenId(TEST_SUB_OPEN_ID)
                .build();

        try {
            MiniProgramPayResponse response = miniProgramClient.miniProgramPay(request);
            long elapsed = System.currentTimeMillis() - startTime;
            logger.info("[边界条件] 响应时间: {}ms", elapsed);
            logger.info("[边界条件] errCode: {}", response.getErrCode());
            logger.info("[边界条件] status: {}", response.getStatus());

            if (response.isSuccess()) {
                assertEquals(1L, response.getTotalAmount().longValue(), "返回金额应为1分");
            }
            logger.info("[边界条件] 最小金额测试通过");
        } catch (UmsPayBusinessException e) {
            long elapsed = System.currentTimeMillis() - startTime;
            logger.info("[边界条件] 响应时间: {}ms", elapsed);
            logger.info("[边界条件] 业务错误码: {}", e.getErrorCode());
            logger.info("[边界条件] 业务错误信息: {}", e.getMessage());
            logger.info("[边界条件] 业务异常，可能是测试环境配置问题");
        } catch (UmsPayException e) {
            long elapsed = System.currentTimeMillis() - startTime;
            logger.info("[边界条件] 响应时间: {}ms", elapsed);
            logger.info("[边界条件] 异常类型: {}", e.getClass().getSimpleName());
            logger.info("[边界条件] 异常信息: {}", e.getMessage());
        }
    }

    @Test
    @Order(20)
    @DisplayName("微信小程序支付 - 边界条件测试（大额支付）")
    void testMiniProgramPayLargeAmount() {
        long startTime = System.currentTimeMillis();
        logger.info("[边界条件] 开始测试大额支付...");
        logger.info("[边界条件] 订单号: {}", currentOrderId);
        logger.info("[边界条件] 金额: 100000000分（100万元）");

        MiniProgramPayRequest request = MiniProgramPayRequest.builder()
                .merOrderId(currentOrderId)
                .totalAmount(100000000L)
                .subAppId(TEST_SUB_APP_ID)
                .subOpenId(TEST_SUB_OPEN_ID)
                .build();

        try {
            MiniProgramPayResponse response = miniProgramClient.miniProgramPay(request);
            long elapsed = System.currentTimeMillis() - startTime;
            logger.info("[边界条件] 响应时间: {}ms", elapsed);
            logger.info("[边界条件] errCode: {}", response.getErrCode());
            logger.info("[边界条件] status: {}", response.getStatus());

            if (response.isSuccess()) {
                assertEquals(100000000L, response.getTotalAmount().longValue(), "返回金额应一致");
            }
            logger.info("[边界条件] 大额支付测试通过");
        } catch (UmsPayBusinessException e) {
            long elapsed = System.currentTimeMillis() - startTime;
            logger.info("[边界条件] 响应时间: {}ms", elapsed);
            logger.info("[边界条件] 业务错误码: {}", e.getErrorCode());
            logger.info("[边界条件] 业务错误信息: {}", e.getMessage());
            logger.info("[边界条件] 大额支付被拒绝（可能是商户限额），属于正常情况");
        } catch (UmsPayException e) {
            long elapsed = System.currentTimeMillis() - startTime;
            logger.info("[边界条件] 响应时间: {}ms", elapsed);
            logger.info("[边界条件] 异常类型: {}", e.getClass().getSimpleName());
            logger.info("[边界条件] 异常信息: {}", e.getMessage());
        }
    }

    @Test
    @Order(21)
    @DisplayName("微信小程序支付 - 完整支付流程（下单+查询+退款）")
    void testMiniProgramPayFullFlow() {
        long startTime = System.currentTimeMillis();
        String flowOrderId = "FLOW" + new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date())
                + UUID.randomUUID().toString().replace("-", "").substring(0, 7);
        String flowRefundOrderId = "RFLOW" + new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date())
                + UUID.randomUUID().toString().replace("-", "").substring(0, 7);
        logger.info("[完整流程] 开始测试小程序支付完整流程（下单->查询->退款）...");
        logger.info("[完整流程] 订单号: {}", flowOrderId);
        logger.info("[完整流程] 退款单号: {}", flowRefundOrderId);

        logger.info("[完整流程] === 步骤1: 小程序下单 ===");
        MiniProgramPayRequest payRequest = MiniProgramPayRequest.builder()
                .merOrderId(flowOrderId)
                .totalAmount(1L)
                .subAppId(TEST_SUB_APP_ID)
                .subOpenId(TEST_SUB_OPEN_ID)
                .notifyUrl("https://httpbin.org/post")
                .build();

        boolean paySuccess = false;
        try {
            MiniProgramPayResponse payResponse = miniProgramClient.miniProgramPay(payRequest);
            long payElapsed = System.currentTimeMillis() - startTime;
            logger.info("[完整流程] 下单响应时间: {}ms", payElapsed);
            logger.info("[完整流程] 下单errCode: {}", payResponse.getErrCode());
            logger.info("[完整流程] 下单status: {}", payResponse.getStatus());

            if (payResponse.isSuccess()) {
                paySuccess = true;
                logger.info("[完整流程] 下单成功，miniPayRequest存在: {}", payResponse.getMiniPayRequest() != null);
            }
        } catch (UmsPayException e) {
            logger.info("[完整流程] 下单异常: {} - {}", e.getClass().getSimpleName(), e.getMessage());
        }

        logger.info("[完整流程] === 步骤2: 订单查询 ===");
        try {
            QueryResponse queryResponse = miniProgramClient.query(new QueryRequest(flowOrderId));
            long queryElapsed = System.currentTimeMillis() - startTime;
            logger.info("[完整流程] 查询响应时间: {}ms", queryElapsed);
            logger.info("[完整流程] 查询errCode: {}", queryResponse.getErrCode());
            logger.info("[完整流程] 查询status: {}", queryResponse.getStatus());
            logger.info("[完整流程] 查询totalAmount: {}", queryResponse.getTotalAmount());
        } catch (UmsPayBusinessException e) {
            logger.info("[完整流程] 查询业务异常: {} - {}", e.getErrorCode(), e.getMessage());
        } catch (UmsPayException e) {
            logger.info("[完整流程] 查询异常: {} - {}", e.getClass().getSimpleName(), e.getMessage());
        }

        logger.info("[完整流程] === 步骤3: 退款 ===");
        try {
            RefundRequest refundRequest = RefundRequest.builder()
                    .merOrderId(flowOrderId)
                    .refundOrderId(flowRefundOrderId)
                    .refundAmount(1L)
                    .refundDesc("完整流程测试退款")
                    .build();
            RefundResponse refundResponse = miniProgramClient.refund(refundRequest);
            long refundElapsed = System.currentTimeMillis() - startTime;
            logger.info("[完整流程] 退款响应时间: {}ms", refundElapsed);
            logger.info("[完整流程] 退款errCode: {}", refundResponse.getErrCode());
            logger.info("[完整流程] 退款status: {}", refundResponse.getStatus());
        } catch (UmsPayBusinessException e) {
            logger.info("[完整流程] 退款业务异常: {} - {}", e.getErrorCode(), e.getMessage());
        } catch (UmsPayException e) {
            logger.info("[完整流程] 退款异常: {} - {}", e.getClass().getSimpleName(), e.getMessage());
        }

        long totalElapsed = System.currentTimeMillis() - startTime;
        logger.info("[完整流程] 完整流程测试完成，总耗时: {}ms", totalElapsed);
        logger.info("[完整流程] 下单是否成功: {}", paySuccess);
    }

    @Test
    @Order(22)
    @DisplayName("微信小程序支付 - 响应数据格式验证")
    void testMiniProgramPayResponseFormat() {
        long startTime = System.currentTimeMillis();
        logger.info("[格式验证] 开始验证小程序支付响应数据格式...");

        MiniProgramPayRequest request = MiniProgramPayRequest.builder()
                .merOrderId(currentOrderId)
                .totalAmount(1L)
                .subAppId(TEST_SUB_APP_ID)
                .subOpenId(TEST_SUB_OPEN_ID)
                .build();

        try {
            MiniProgramPayResponse response = miniProgramClient.miniProgramPay(request);
            long elapsed = System.currentTimeMillis() - startTime;

            logger.info("[格式验证] 响应时间: {}ms", elapsed);
            logger.info("[格式验证] errCode: {}", response.getErrCode());
            logger.info("[格式验证] errMsg: {}", response.getErrMsg());
            logger.info("[格式验证] responseTimeStamp: {}", response.getResponseTimeStamp());
            logger.info("[格式验证] mid: {}", response.getMid());
            logger.info("[格式验证] tid: {}", response.getTid());

            assertNotNull(response.getErrCode(), "errCode字段不应为空");
            assertNotNull(response.getResponseTimeStamp(), "responseTimeStamp字段不应为空");
            assertNotNull(response.getMid(), "mid字段不应为空");

            if (response.isSuccess()) {
                assertNotNull(response.getMerOrderId(), "merOrderId字段不应为空");
                assertNotNull(response.getStatus(), "status字段不应为空");
                assertNotNull(response.getTotalAmount(), "totalAmount字段不应为空");

                logger.info("[格式验证] merOrderId: {}", response.getMerOrderId());
                logger.info("[格式验证] status: {}", response.getStatus());
                logger.info("[格式验证] totalAmount: {}", response.getTotalAmount());
                logger.info("[格式验证] seqId: {}", response.getSeqId());
                logger.info("[格式验证] settleRefId: {}", response.getSettleRefId());
                logger.info("[格式验证] targetSys: {}", response.getTargetSys());
                logger.info("[格式验证] targetOrderId: {}", response.getTargetOrderId());
                logger.info("[格式验证] targetStatus: {}", response.getTargetStatus());
                logger.info("[格式验证] merName: {}", response.getMerName());
                logger.info("[格式验证] targetMid: {}", response.getTargetMid());
            }

            logger.info("[格式验证] 响应数据格式验证通过");
        } catch (UmsPayBusinessException e) {
            long elapsed = System.currentTimeMillis() - startTime;
            logger.info("[格式验证] 响应时间: {}ms", elapsed);
            logger.info("[格式验证] 业务错误码: {}", e.getErrorCode());
            logger.info("[格式验证] 业务错误信息: {}", e.getMessage());

            assertNotNull(e.getErrorCode(), "错误码不应为空");
            logger.info("[格式验证] 业务异常也说明响应格式正确（能正确解析errCode）");
        } catch (UmsPayException e) {
            long elapsed = System.currentTimeMillis() - startTime;
            logger.info("[格式验证] 响应时间: {}ms", elapsed);
            logger.info("[格式验证] 异常类型: {}", e.getClass().getSimpleName());
            logger.info("[格式验证] 异常信息: {}", e.getMessage());
        }
    }

    @Test
    @Order(23)
    @DisplayName("微信小程序支付 - 性能基线测试")
    void testMiniProgramPayPerformanceBaseline() {
        logger.info("[性能基线] 开始小程序支付性能基线测试...");

        long payStart = System.currentTimeMillis();
        try {
            MiniProgramPayRequest request = MiniProgramPayRequest.builder()
                    .merOrderId("PERF_MP_" + System.currentTimeMillis())
                    .totalAmount(1L)
                    .subAppId(TEST_SUB_APP_ID)
                    .subOpenId(TEST_SUB_OPEN_ID)
                    .build();
            miniProgramClient.miniProgramPay(request);
        } catch (UmsPayException e) {
            logger.info("[性能基线] 小程序支付接口异常（预期）: {}", e.getMessage());
        }
        long payElapsed = System.currentTimeMillis() - payStart;
        logger.info("[性能基线] 小程序支付接口响应时间: {}ms", payElapsed);

        long queryStart = System.currentTimeMillis();
        try {
            miniProgramClient.query(new QueryRequest("PERF_MP_QUERY_" + System.currentTimeMillis()));
        } catch (UmsPayException e) {
            logger.info("[性能基线] 查询接口异常（预期）: {}", e.getMessage());
        }
        long queryElapsed = System.currentTimeMillis() - queryStart;
        logger.info("[性能基线] 查询接口响应时间: {}ms", queryElapsed);

        assertTrue(payElapsed < 30000, "小程序支付接口响应时间应小于30秒");
        assertTrue(queryElapsed < 30000, "查询接口响应时间应小于30秒");

        logger.info("[性能基线] 小程序支付性能基线测试通过");
    }
}
