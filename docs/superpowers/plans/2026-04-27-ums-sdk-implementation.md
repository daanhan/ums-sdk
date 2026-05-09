# 银联商务支付SDK实施计划

> **对于代理工作者：** 必需：使用 superpowers:subagent-driven-development（如果子代理可用）或 superpowers:executing-plans 来执行此计划。步骤使用复选框（`- [ ]`）语法进行跟踪。

**目标：** 构建一个简洁易用的Java SDK，为开发者提供银联商务支付接口的便捷调用能力。

**架构：** 采用简洁实用型架构，单一入口类设计，Builder模式配置，清晰的分层结构。核心模块包括配置管理、请求/响应对象、HTTP通信、安全签名和异常处理。

**技术栈：** Java 1.8+ / Maven / OkHttp 4.12.0 / Jackson 2.15.2 / SLF4J + Logback / JUnit 5

---

## 文件结构规划

### 项目根目录
```
ums-sdk/
├── pom.xml
├── README.md
├── README_CN.md
├── LICENSE
├── .gitignore
└── src/
    ├── main/
    │   ├── java/com/chinaums/sdk/
    │   │   ├── UmsPayClient.java
    │   │   ├── config/
    │   │   ├── request/
    │   │   ├── response/
    │   │   ├── exception/
    │   │   ├── http/
    │   │   ├── security/
    │   │   └── util/
    │   └── resources/
    │       └── logback.xml
    └── test/
        └── java/com/chinaums/sdk/
```

### 文件职责说明
- **pom.xml**: Maven项目配置，依赖管理
- **UmsPayClient.java**: 主入口类，提供所有API方法
- **config/UmsPayConfig.java**: 配置类，Builder模式
- **config/Environment.java**: 环境枚举
- **request/**: 请求对象包，包含各种请求类型
- **response/**: 响应对象包，包含各种响应类型
- **exception/**: 异常类包，自定义异常体系
- **http/**: HTTP通信包，OkHttp实现
- **security/**: 安全相关包，签名和证书管理
- **util/**: 工具类包，JSON和日期处理

---

## 任务分解

### 任务1：项目初始化与基础配置

**文件：**
- 创建: `pom.xml`
- 创建: `.gitignore`
- 创建: `LICENSE`

- [ ] **步骤1：创建Maven项目配置文件**

创建 `pom.xml` 文件：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
         http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.chinaums</groupId>
    <artifactId>ums-sdk</artifactId>
    <version>1.0.0-SNAPSHOT</version>
    <packaging>jar</packaging>

    <name>ChinaUMS Payment SDK</name>
    <description>银联商务支付接口客户端SDK</description>
    <url>https://github.com/daanhan/ums-sdk</url>

    <properties>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        <maven.compiler.source>1.8</maven.compiler.source>
        <maven.compiler.target>1.8</maven.compiler.target>
        <okhttp.version>4.12.0</okhttp.version>
        <jackson.version>2.15.2</jackson.version>
        <slf4j.version>1.7.36</slf4j.version>
        <logback.version>1.2.12</logback.version>
        <junit.version>5.9.3</junit.version>
        <mockito.version>5.4.0</mockito.version>
    </properties>

    <dependencies>
        <!-- OkHttp -->
        <dependency>
            <groupId>com.squareup.okhttp3</groupId>
            <artifactId>okhttp</artifactId>
            <version>${okhttp.version}</version>
        </dependency>

        <!-- Jackson -->
        <dependency>
            <groupId>com.fasterxml.jackson.core</groupId>
            <artifactId>jackson-databind</artifactId>
            <version>${jackson.version}</version>
        </dependency>
        <dependency>
            <groupId>com.fasterxml.jackson.core</groupId>
            <artifactId>jackson-core</artifactId>
            <version>${jackson.version}</version>
        </dependency>
        <dependency>
            <groupId>com.fasterxml.jackson.core</groupId>
            <artifactId>jackson-annotations</artifactId>
            <version>${jackson.version}</version>
        </dependency>

        <!-- SLF4J -->
        <dependency>
            <groupId>org.slf4j</groupId>
            <artifactId>slf4j-api</artifactId>
            <version>${slf4j.version}</version>
        </dependency>

        <!-- Logback (for testing) -->
        <dependency>
            <groupId>ch.qos.logback</groupId>
            <artifactId>logback-classic</artifactId>
            <version>${logback.version}</version>
            <scope>test</scope>
        </dependency>

        <!-- Commons Lang3 -->
        <dependency>
            <groupId>org.apache.commons</groupId>
            <artifactId>commons-lang3</artifactId>
            <version>3.12.0</version>
        </dependency>

        <!-- JUnit 5 -->
        <dependency>
            <groupId>org.junit.jupiter</groupId>
            <artifactId>junit-jupiter</artifactId>
            <version>${junit.version}</version>
            <scope>test</scope>
        </dependency>

        <!-- Mockito -->
        <dependency>
            <groupId>org.mockito</groupId>
            <artifactId>mockito-core</artifactId>
            <version>${mockito.version}</version>
            <scope>test</scope>
        </dependency>

        <!-- MockWebServer -->
        <dependency>
            <groupId>com.squareup.okhttp3</groupId>
            <artifactId>mockwebserver</artifactId>
            <version>${okhttp.version}</version>
            <scope>test</scope>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <version>3.11.0</version>
                <configuration>
                    <source>1.8</source>
                    <target>1.8</target>
                    <encoding>UTF-8</encoding>
                </configuration>
            </plugin>

            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-surefire-plugin</artifactId>
                <version>3.1.2</version>
            </plugin>

            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-source-plugin</artifactId>
                <version>3.3.0</version>
                <executions>
                    <execution>
                        <id>attach-sources</id>
                        <goals>
                            <goal>jar</goal>
                        </goals>
                    </execution>
                </executions>
            </plugin>

            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-javadoc-plugin</artifactId>
                <version>3.5.0</version>
                <configuration>
                    <encoding>UTF-8</encoding>
                    <charset>UTF-8</charset>
                    <docencoding>UTF-8</docencoding>
                </configuration>
            </plugin>
        </plugins>
    </build>
</project>
```

- [ ] **步骤2：创建.gitignore文件**

创建 `.gitignore` 文件：

```
# Maven
target/
pom.xml.tag
pom.xml.releaseBackup
pom.xml.versionsBackup
pom.xml.next
release.properties
dependency-reduced-pom.xml
buildNumber.properties
.mvn/timing.properties
.mvn/wrapper/maven-wrapper.jar

# IDE
.idea/
*.iml
*.iws
*.ipr
.vscode/
.settings/
.classpath
.project

# OS
.DS_Store
Thumbs.db

# Logs
logs/
*.log
```

- [ ] **步骤3：创建LICENSE文件**

创建 `LICENSE` 文件：

```
MIT License

Copyright (c) 2026 ChinaUMS

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```

- [ ] **步骤4：验证Maven项目结构**

运行命令验证项目结构：

```bash
mvn validate
```

预期输出：`BUILD SUCCESS`

- [ ] **步骤5：提交基础配置**

```bash
git add pom.xml .gitignore LICENSE
git commit -m "feat: 初始化Maven项目配置"
```

---

### 任务2：创建工具类

**文件：**
- 创建: `src/main/java/com/chinaums/sdk/util/DateUtil.java`
- 创建: `src/main/java/com/chinaums/sdk/util/JsonUtil.java`
- 创建: `src/test/java/com/chinaums/sdk/util/DateUtilTest.java`
- 创建: `src/test/java/com/chinaums/sdk/util/JsonUtilTest.java`

- [ ] **步骤1：编写DateUtil工具类**

创建 `src/main/java/com/chinaums/sdk/util/DateUtil.java`：

```java
package com.chinaums.sdk.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public final class DateUtil {
    
    private static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    private static final String TIMESTAMP_FORMAT = "yyyyMMddHHmmss";
    
    private DateUtil() {
    }
    
    public static String formatDateTime(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_TIME_FORMAT);
        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
        return sdf.format(date);
    }
    
    public static String formatTimestamp(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat(TIMESTAMP_FORMAT);
        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
        return sdf.format(date);
    }
    
    public static Date parseDateTime(String dateStr) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_TIME_FORMAT);
        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
        return sdf.parse(dateStr);
    }
    
    public static Date parseTimestamp(String timestampStr) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat(TIMESTAMP_FORMAT);
        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
        return sdf.parse(timestampStr);
    }
}
```

- [ ] **步骤2：编写DateUtil测试**

创建 `src/test/java/com/chinaums/sdk/util/DateUtilTest.java`：

```java
package com.chinaums.sdk.util;

import org.junit.jupiter.api.Test;

import java.text.ParseException;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class DateUtilTest {
    
    @Test
    void testFormatDateTime() {
        Date date = new Date(1649881503000L);
        String result = DateUtil.formatDateTime(date);
        assertNotNull(result);
        assertTrue(result.matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}"));
    }
    
    @Test
    void testFormatTimestamp() {
        Date date = new Date(1649881503000L);
        String result = DateUtil.formatTimestamp(date);
        assertNotNull(result);
        assertTrue(result.matches("\\d{14}"));
    }
    
    @Test
    void testParseDateTime() throws ParseException {
        String dateStr = "2022-04-19 10:25:03";
        Date result = DateUtil.parseDateTime(dateStr);
        assertNotNull(result);
    }
    
    @Test
    void testParseTimestamp() throws ParseException {
        String timestampStr = "20220419102503";
        Date result = DateUtil.parseTimestamp(timestampStr);
        assertNotNull(result);
    }
}
```

- [ ] **步骤3：运行DateUtil测试**

运行命令：

```bash
mvn test -Dtest=DateUtilTest
```

预期输出：所有测试通过

- [ ] **步骤4：编写JsonUtil工具类**

创建 `src/main/java/com/chinaums/sdk/util/JsonUtil.java`：

```java
package com.chinaums.sdk.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.IOException;

public final class JsonUtil {
    
    private static final ObjectMapper objectMapper = new ObjectMapper();
    
    static {
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        objectMapper.setTimeZone(java.util.TimeZone.getTimeZone("Asia/Shanghai"));
    }
    
    private JsonUtil() {
    }
    
    public static String toJson(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("JSON序列化失败", e);
        }
    }
    
    public static <T> T fromJson(String json, Class<T> clazz) {
        try {
            return objectMapper.readValue(json, clazz);
        } catch (IOException e) {
            throw new RuntimeException("JSON反序列化失败", e);
        }
    }
}
```

- [ ] **步骤5：编写JsonUtil测试**

创建 `src/test/java/com/chinaums/sdk/util/JsonUtilTest.java`：

```java
package com.chinaums.sdk.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JsonUtilTest {
    
    @Test
    void testToJson() {
        TestObject obj = new TestObject("test", 123);
        String json = JsonUtil.toJson(obj);
        assertNotNull(json);
        assertTrue(json.contains("\"name\":\"test\""));
        assertTrue(json.contains("\"value\":123"));
    }
    
    @Test
    void testFromJson() {
        String json = "{\"name\":\"test\",\"value\":123}";
        TestObject obj = JsonUtil.fromJson(json, TestObject.class);
        assertNotNull(obj);
        assertEquals("test", obj.getName());
        assertEquals(123, obj.getValue());
    }
    
    static class TestObject {
        private String name;
        private int value;
        
        public TestObject() {
        }
        
        public TestObject(String name, int value) {
            this.name = name;
            this.value = value;
        }
        
        public String getName() {
            return name;
        }
        
        public void setName(String name) {
            this.name = name;
        }
        
        public int getValue() {
            return value;
        }
        
        public void setValue(int value) {
            this.value = value;
        }
    }
}
```

- [ ] **步骤6：运行JsonUtil测试**

运行命令：

```bash
mvn test -Dtest=JsonUtilTest
```

预期输出：所有测试通过

- [ ] **步骤7：提交工具类**

```bash
git add src/main/java/com/chinaums/sdk/util/ src/test/java/com/chinaums/sdk/util/
git commit -m "feat: 添加DateUtil和JsonUtil工具类"
```

---

### 任务3：创建异常类

**文件：**
- 创建: `src/main/java/com/chinaums/sdk/exception/UmsPayException.java`
- 创建: `src/main/java/com/chinaums/sdk/exception/UmsPayAuthException.java`
- 创建: `src/main/java/com/chinaums/sdk/exception/UmsPayNetworkException.java`
- 创建: `src/main/java/com/chinaums/sdk/exception/UmsPayBusinessException.java`
- 创建: `src/test/java/com/chinaums/sdk/exception/UmsPayExceptionTest.java`

- [ ] **步骤1：编写基础异常类**

创建 `src/main/java/com/chinaums/sdk/exception/UmsPayException.java`：

```java
package com.chinaums.sdk.exception;

import java.util.UUID;

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
    
    public UmsPayException(String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.requestId = UUID.randomUUID().toString();
        this.timestamp = System.currentTimeMillis();
    }
    
    public String getErrorCode() {
        return errorCode;
    }
    
    public String getRequestId() {
        return requestId;
    }
    
    public long getTimestamp() {
        return timestamp;
    }
}
```

- [ ] **步骤2：编写认证异常类**

创建 `src/main/java/com/chinaums/sdk/exception/UmsPayAuthException.java`：

```java
package com.chinaums.sdk.exception;

public class UmsPayAuthException extends UmsPayException {
    
    public UmsPayAuthException(String message) {
        super("AUTH_ERROR", message);
    }
    
    public UmsPayAuthException(String message, Throwable cause) {
        super("AUTH_ERROR", message, cause);
    }
}
```

- [ ] **步骤3：编写网络异常类**

创建 `src/main/java/com/chinaums/sdk/exception/UmsPayNetworkException.java`：

```java
package com.chinaums.sdk.exception;

public class UmsPayNetworkException extends UmsPayException {
    
    public UmsPayNetworkException(String message) {
        super("NETWORK_ERROR", message);
    }
    
    public UmsPayNetworkException(String message, Throwable cause) {
        super("NETWORK_ERROR", message, cause);
    }
}
```

- [ ] **步骤4：编写业务异常类**

创建 `src/main/java/com/chinaums/sdk/exception/UmsPayBusinessException.java`：

```java
package com.chinaums.sdk.exception;

public class UmsPayBusinessException extends UmsPayException {
    
    public UmsPayBusinessException(String errorCode, String message) {
        super(errorCode, message);
    }
    
    public UmsPayBusinessException(String errorCode, String message, Throwable cause) {
        super(errorCode, message, cause);
    }
}
```

- [ ] **步骤5：编写异常测试**

创建 `src/test/java/com/chinaums/sdk/exception/UmsPayExceptionTest.java`：

```java
package com.chinaums.sdk.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UmsPayExceptionTest {
    
    @Test
    void testUmsPayException() {
        UmsPayException ex = new UmsPayException("TEST_ERROR", "测试错误");
        assertEquals("TEST_ERROR", ex.getErrorCode());
        assertEquals("测试错误", ex.getMessage());
        assertNotNull(ex.getRequestId());
        assertTrue(ex.getTimestamp() > 0);
    }
    
    @Test
    void testUmsPayAuthException() {
        UmsPayAuthException ex = new UmsPayAuthException("认证失败");
        assertEquals("AUTH_ERROR", ex.getErrorCode());
        assertEquals("认证失败", ex.getMessage());
    }
    
    @Test
    void testUmsPayNetworkException() {
        UmsPayNetworkException ex = new UmsPayNetworkException("网络错误");
        assertEquals("NETWORK_ERROR", ex.getErrorCode());
        assertEquals("网络错误", ex.getMessage());
    }
    
    @Test
    void testUmsPayBusinessException() {
        UmsPayBusinessException ex = new UmsPayBusinessException("BIZ_ERROR", "业务错误");
        assertEquals("BIZ_ERROR", ex.getErrorCode());
        assertEquals("业务错误", ex.getMessage());
    }
}
```

- [ ] **步骤6：运行异常测试**

运行命令：

```bash
mvn test -Dtest=UmsPayExceptionTest
```

预期输出：所有测试通过

- [ ] **步骤7：提交异常类**

```bash
git add src/main/java/com/chinaums/sdk/exception/ src/test/java/com/chinaums/sdk/exception/
git commit -m "feat: 添加自定义异常体系"
```

---

### 任务4：创建配置类

**文件：**
- 创建: `src/main/java/com/chinaums/sdk/config/Environment.java`
- 创建: `src/main/java/com/chinaums/sdk/config/UmsPayConfig.java`
- 创建: `src/test/java/com/chinaums/sdk/config/UmsPayConfigTest.java`

- [ ] **步骤1：编写Environment枚举**

创建 `src/main/java/com/chinaums/sdk/config/Environment.java`：

```java
package com.chinaums.sdk.config;

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

- [ ] **步骤2：编写UmsPayConfig配置类**

创建 `src/main/java/com/chinaums/sdk/config/UmsPayConfig.java`：

```java
package com.chinaums.sdk.config;

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
    
    public String getAppId() {
        return appId;
    }
    
    public String getAppKey() {
        return appKey;
    }
    
    public String getMid() {
        return mid;
    }
    
    public String getTid() {
        return tid;
    }
    
    public String getInstMid() {
        return instMid;
    }
    
    public Environment getEnvironment() {
        return environment;
    }
    
    public int getConnectTimeout() {
        return connectTimeout;
    }
    
    public int getReadTimeout() {
        return readTimeout;
    }
    
    public int getMaxRetries() {
        return maxRetries;
    }
    
    public boolean isEnableCertValidation() {
        return enableCertValidation;
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
```

- [ ] **步骤3：编写配置类测试**

创建 `src/test/java/com/chinaums/sdk/config/UmsPayConfigTest.java`：

```java
package com.chinaums.sdk.config;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UmsPayConfigTest {
    
    @Test
    void testBuildSuccess() {
        UmsPayConfig config = UmsPayConfig.builder()
            .appId("test-app-id")
            .appKey("test-app-key")
            .mid("898201612345678")
            .tid("88880001")
            .build();
        
        assertEquals("test-app-id", config.getAppId());
        assertEquals("test-app-key", config.getAppKey());
        assertEquals("898201612345678", config.getMid());
        assertEquals("88880001", config.getTid());
        assertEquals("H5DEFAULT", config.getInstMid());
        assertEquals(Environment.PRODUCTION, config.getEnvironment());
        assertEquals(10000, config.getConnectTimeout());
        assertEquals(30000, config.getReadTimeout());
        assertEquals(3, config.getMaxRetries());
        assertTrue(config.isEnableCertValidation());
    }
    
    @Test
    void testBuildWithCustomValues() {
        UmsPayConfig config = UmsPayConfig.builder()
            .appId("test-app-id")
            .appKey("test-app-key")
            .mid("898201612345678")
            .tid("88880001")
            .instMid("CUSTOM")
            .environment(Environment.TEST)
            .connectTimeout(5000)
            .readTimeout(20000)
            .maxRetries(5)
            .enableCertValidation(false)
            .build();
        
        assertEquals("CUSTOM", config.getInstMid());
        assertEquals(Environment.TEST, config.getEnvironment());
        assertEquals(5000, config.getConnectTimeout());
        assertEquals(20000, config.getReadTimeout());
        assertEquals(5, config.getMaxRetries());
        assertFalse(config.isEnableCertValidation());
    }
    
    @Test
    void testBuildWithNullAppId() {
        assertThrows(IllegalArgumentException.class, () -> {
            UmsPayConfig.builder()
                .appKey("test-app-key")
                .mid("898201612345678")
                .tid("88880001")
                .build();
        });
    }
    
    @Test
    void testBuildWithNullAppKey() {
        assertThrows(IllegalArgumentException.class, () -> {
            UmsPayConfig.builder()
                .appId("test-app-id")
                .mid("898201612345678")
                .tid("88880001")
                .build();
        });
    }
    
    @Test
    void testEnvironmentBaseUrl() {
        assertEquals("https://api-mop.chinaums.com", Environment.PRODUCTION.getBaseUrl());
        assertEquals("https://test-api-open.chinaums.com", Environment.TEST.getBaseUrl());
    }
}
```

- [ ] **步骤4：运行配置类测试**

运行命令：

```bash
mvn test -Dtest=UmsPayConfigTest
```

预期输出：所有测试通过

- [ ] **步骤5：提交配置类**

```bash
git add src/main/java/com/chinaums/sdk/config/ src/test/java/com/chinaums/sdk/config/
git commit -m "feat: 添加配置类和环境枚举"
```

---

### 任务5：创建安全模块

**文件：**
- 创建: `src/main/java/com/chinaums/sdk/security/SignatureUtil.java`
- 创建: `src/test/java/com/chinaums/sdk/security/SignatureUtilTest.java`

- [ ] **步骤1：编写签名工具类**

创建 `src/main/java/com/chinaums/sdk/security/SignatureUtil.java`：

```java
package com.chinaums.sdk.security;

import com.chinaums.sdk.exception.UmsPayAuthException;
import com.chinaums.sdk.util.DateUtil;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;

public class SignatureUtil {
    
    private final String appId;
    private final String appKey;
    
    public SignatureUtil(String appId, String appKey) {
        this.appId = appId;
        this.appKey = appKey;
    }
    
    public SignatureResult sign(String content) {
        String timestamp = DateUtil.formatTimestamp(new Date());
        String nonce = UUID.randomUUID().toString().replace("-", "");
        
        String contentSha256 = sha256Hex(content);
        String signStr = appId + timestamp + nonce + contentSha256;
        String signature = hmacSha256Base64(signStr, appKey);
        
        return new SignatureResult(timestamp, nonce, signature);
    }
    
    private String sha256Hex(String content) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(content.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new UmsPayAuthException("SHA-256算法不可用", e);
        }
    }
    
    private String hmacSha256Base64(String data, String key) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKeySpec = new SecretKeySpec(
                key.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            mac.init(secretKeySpec);
            byte[] hash = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (Exception e) {
            throw new UmsPayAuthException("HmacSHA256签名失败", e);
        }
    }
    
    private String bytesToHex(byte[] bytes) {
        StringBuilder result = new StringBuilder();
        for (byte b : bytes) {
            result.append(String.format("%02x", b));
        }
        return result.toString();
    }
    
    public static class SignatureResult {
        private final String timestamp;
        private final String nonce;
        private final String signature;
        
        public SignatureResult(String timestamp, String nonce, String signature) {
            this.timestamp = timestamp;
            this.nonce = nonce;
            this.signature = signature;
        }
        
        public String getTimestamp() {
            return timestamp;
        }
        
        public String getNonce() {
            return nonce;
        }
        
        public String getSignature() {
            return signature;
        }
    }
}
```

- [ ] **步骤2：编写签名工具测试**

创建 `src/test/java/com/chinaums/sdk/security/SignatureUtilTest.java`：

```java
package com.chinaums.sdk.security;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SignatureUtilTest {
    
    @Test
    void testSign() {
        SignatureUtil util = new SignatureUtil("test-app-id", "test-app-key");
        String content = "{\"test\":\"data\"}";
        
        SignatureUtil.SignatureResult result = util.sign(content);
        
        assertNotNull(result);
        assertNotNull(result.getTimestamp());
        assertNotNull(result.getNonce());
        assertNotNull(result.getSignature());
        assertEquals(14, result.getTimestamp().length());
        assertEquals(32, result.getNonce().length());
    }
    
    @Test
    void testSignWithEmptyContent() {
        SignatureUtil util = new SignatureUtil("test-app-id", "test-app-key");
        String content = "";
        
        SignatureUtil.SignatureResult result = util.sign(content);
        
        assertNotNull(result);
        assertNotNull(result.getSignature());
    }
    
    @Test
    void testSignDeterministic() {
        SignatureUtil util = new SignatureUtil("test-app-id", "test-app-key");
        String content = "{\"test\":\"data\"}";
        
        SignatureUtil.SignatureResult result1 = util.sign(content);
        SignatureUtil.SignatureResult result2 = util.sign(content);
        
        assertNotEquals(result1.getTimestamp(), result2.getTimestamp());
        assertNotEquals(result1.getNonce(), result2.getNonce());
    }
}
```

- [ ] **步骤3：运行签名工具测试**

运行命令：

```bash
mvn test -Dtest=SignatureUtilTest
```

预期输出：所有测试通过

- [ ] **步骤4：提交安全模块**

```bash
git add src/main/java/com/chinaums/sdk/security/ src/test/java/com/chinaums/sdk/security/
git commit -m "feat: 添加签名工具类"
```

---

### 任务6：创建HTTP模块

**文件：**
- 创建: `src/main/java/com/chinaums/sdk/http/HttpResponse.java`
- 创建: `src/main/java/com/chinaums/sdk/http/HttpClient.java`
- 创建: `src/main/java/com/chinaums/sdk/http/OkHttpExecutor.java`
- 创建: `src/test/java/com/chinaums/sdk/http/OkHttpExecutorTest.java`

- [ ] **步骤1：编写HttpResponse封装类**

创建 `src/main/java/com/chinaums/sdk/http/HttpResponse.java`：

```java
package com.chinaums.sdk.http;

public class HttpResponse {
    
    private final int code;
    private final String body;
    
    public HttpResponse(int code, String body) {
        this.code = code;
        this.body = body;
    }
    
    public int getCode() {
        return code;
    }
    
    public String getBody() {
        return body;
    }
    
    public boolean isSuccess() {
        return code >= 200 && code < 300;
    }
}
```

- [ ] **步骤2：编写HttpClient接口**

创建 `src/main/java/com/chinaums/sdk/http/HttpClient.java`：

```java
package com.chinaums.sdk.http;

import com.chinaums.sdk.exception.UmsPayException;

public interface HttpClient {
    
    HttpResponse get(String url, String content, String signature) throws UmsPayException;
    
    HttpResponse post(String url, String content, String signature) throws UmsPayException;
}
```

- [ ] **步骤3：编写OkHttpExecutor实现类**

创建 `src/main/java/com/chinaums/sdk/http/OkHttpExecutor.java`：

```java
package com.chinaums.sdk.http;

import com.chinaums.sdk.config.UmsPayConfig;
import com.chinaums.sdk.exception.UmsPayNetworkException;
import com.chinaums.sdk.security.SignatureUtil;
import com.chinaums.sdk.util.DateUtil;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class OkHttpExecutor implements HttpClient {
    
    private static final Logger logger = LoggerFactory.getLogger(OkHttpExecutor.class);
    private static final MediaType JSON_MEDIA_TYPE = MediaType.parse("application/json; charset=utf-8");
    
    private final OkHttpClient httpClient;
    private final UmsPayConfig config;
    private final String appId;
    
    public OkHttpExecutor(UmsPayConfig config) {
        this.config = config;
        this.appId = config.getAppId();
        this.httpClient = buildHttpClient();
    }
    
    private OkHttpClient buildHttpClient() {
        return new OkHttpClient.Builder()
            .connectTimeout(config.getConnectTimeout(), TimeUnit.MILLISECONDS)
            .readTimeout(config.getReadTimeout(), TimeUnit.MILLISECONDS)
            .writeTimeout(config.getReadTimeout(), TimeUnit.MILLISECONDS)
            .retryOnConnectionFailure(true)
            .build();
    }
    
    @Override
    public HttpResponse get(String url, String content, String signature) throws UmsPayNetworkException {
        SignatureUtil signatureUtil = new SignatureUtil(appId, config.getAppKey());
        SignatureUtil.SignatureResult signResult = signatureUtil.sign(content);
        
        HttpUrl httpUrl = HttpUrl.parse(url).newBuilder()
            .addQueryParameter("authorization", "OPEN-FORM-PARAM")
            .addQueryParameter("appId", appId)
            .addQueryParameter("timestamp", signResult.getTimestamp())
            .addQueryParameter("nonce", signResult.getNonce())
            .addQueryParameter("content", content)
            .addQueryParameter("signature", signResult.getSignature())
            .build();
        
        Request request = new Request.Builder()
            .url(httpUrl)
            .get()
            .build();
        
        return executeRequest(request);
    }
    
    @Override
    public HttpResponse post(String url, String content, String signature) throws UmsPayNetworkException {
        SignatureUtil signatureUtil = new SignatureUtil(appId, config.getAppKey());
        SignatureUtil.SignatureResult signResult = signatureUtil.sign(content);
        
        String authorization = String.format(
            "OPEN-BODY-SIG AppId=\"%s\",Timestamp=\"%s\",Nonce=\"%s\",Signature=\"%s\"",
            appId, signResult.getTimestamp(), signResult.getNonce(), signResult.getSignature()
        );
        
        RequestBody body = RequestBody.create(content, JSON_MEDIA_TYPE);
        Request request = new Request.Builder()
            .url(url)
            .post(body)
            .header("Authorization", authorization)
            .header("Content-Type", "application/json")
            .build();
        
        return executeRequest(request);
    }
    
    private HttpResponse executeRequest(Request request) throws UmsPayNetworkException {
        try {
            logger.debug("发送HTTP请求: {} {}", request.method(), request.url());
            
            Response response = httpClient.newCall(request).execute();
            ResponseBody responseBody = response.body();
            String bodyString = responseBody != null ? responseBody.string() : "";
            
            logger.debug("收到HTTP响应: {} {}", response.code(), bodyString);
            
            return new HttpResponse(response.code(), bodyString);
        } catch (IOException e) {
            logger.error("HTTP请求失败: {}", e.getMessage(), e);
            throw new UmsPayNetworkException("HTTP请求失败: " + e.getMessage(), e);
        }
    }
}
```

- [ ] **步骤4：编写HTTP模块测试**

创建 `src/test/java/com/chinaums/sdk/http/OkHttpExecutorTest.java`：

```java
package com.chinaums.sdk.http;

import com.chinaums.sdk.config.Environment;
import com.chinaums.sdk.config.UmsPayConfig;
import com.chinaums.sdk.exception.UmsPayNetworkException;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class OkHttpExecutorTest {
    
    private MockWebServer server;
    private OkHttpExecutor httpClient;
    
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
        
        httpClient = new OkHttpExecutor(config);
    }
    
    @AfterEach
    void tearDown() throws IOException {
        server.shutdown();
    }
    
    @Test
    void testPostSuccess() throws UmsPayNetworkException {
        String mockResponse = "{\"errCode\":\"SUCCESS\",\"message\":\"成功\"}";
        server.enqueue(new MockResponse()
            .setBody(mockResponse)
            .setResponseCode(200));
        
        String url = server.url("/test").toString();
        String content = "{\"test\":\"data\"}";
        
        HttpResponse response = httpClient.post(url, content, "test-signature");
        
        assertTrue(response.isSuccess());
        assertEquals(200, response.getCode());
        assertEquals(mockResponse, response.getBody());
    }
    
    @Test
    void testPostFailure() throws UmsPayNetworkException {
        String mockResponse = "{\"errCode\":\"ERROR\",\"message\":\"失败\"}";
        server.enqueue(new MockResponse()
            .setBody(mockResponse)
            .setResponseCode(400));
        
        String url = server.url("/test").toString();
        String content = "{\"test\":\"data\"}";
        
        HttpResponse response = httpClient.post(url, content, "test-signature");
        
        assertFalse(response.isSuccess());
        assertEquals(400, response.getCode());
        assertEquals(mockResponse, response.getBody());
    }
}
```

- [ ] **步骤5：运行HTTP模块测试**

运行命令：

```bash
mvn test -Dtest=OkHttpExecutorTest
```

预期输出：所有测试通过

- [ ] **步骤6：提交HTTP模块**

```bash
git add src/main/java/com/chinaums/sdk/http/ src/test/java/com/chinaums/sdk/http/
git commit -m "feat: 添加HTTP客户端模块"
```

---

### 任务7：创建请求和响应对象

**文件：**
- 创建: `src/main/java/com/chinaums/sdk/request/BaseRequest.java`
- 创建: `src/main/java/com/chinaums/sdk/request/H5PayRequest.java`
- 创建: `src/main/java/com/chinaums/sdk/request/QueryRequest.java`
- 创建: `src/main/java/com/chinaums/sdk/request/RefundRequest.java`
- 创建: `src/main/java/com/chinaums/sdk/request/RefundQueryRequest.java`
- 创建: `src/main/java/com/chinaums/sdk/response/BaseResponse.java`
- 创建: `src/main/java/com/chinaums/sdk/response/H5PayResponse.java`
- 创建: `src/main/java/com/chinaums/sdk/response/QueryResponse.java`
- 创建: `src/main/java/com/chinaums/sdk/response/RefundResponse.java`
- 创建: `src/main/java/com/chinaums/sdk/response/RefundQueryResponse.java`

由于代码量较大，这里只展示核心类的实现。完整实现请参考设计文档。

- [ ] **步骤1：编写BaseRequest基类**

创建 `src/main/java/com/chinaums/sdk/request/BaseRequest.java`：

```java
package com.chinaums.sdk.request;

import com.fasterxml.jackson.annotation.JsonProperty;

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
    
    public String getRequestTimestamp() {
        return requestTimestamp;
    }
    
    public void setRequestTimestamp(String requestTimestamp) {
        this.requestTimestamp = requestTimestamp;
    }
    
    public String getMid() {
        return mid;
    }
    
    public void setMid(String mid) {
        this.mid = mid;
    }
    
    public String getTid() {
        return tid;
    }
    
    public void setTid(String tid) {
        this.tid = tid;
    }
    
    public String getInstMid() {
        return instMid;
    }
    
    public void setInstMid(String instMid) {
        this.instMid = instMid;
    }
    
    public String getMsgId() {
        return msgId;
    }
    
    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }
    
    public String getSrcReserve() {
        return srcReserve;
    }
    
    public void setSrcReserve(String srcReserve) {
        this.srcReserve = srcReserve;
    }
}
```

- [ ] **步骤2：编写H5PayRequest请求类**

创建 `src/main/java/com/chinaums/sdk/request/H5PayRequest.java`：

```java
package com.chinaums.sdk.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class H5PayRequest extends BaseRequest {
    
    @JsonProperty("merOrderId")
    private String merOrderId;
    
    @JsonProperty("totalAmount")
    private Long totalAmount;
    
    @JsonProperty("goods")
    private List<Goods> goods;
    
    @JsonProperty("notifyUrl")
    private String notifyUrl;
    
    @JsonProperty("returnUrl")
    private String returnUrl;
    
    @JsonProperty("expireTime")
    private String expireTime;
    
    @JsonProperty("sceneType")
    private String sceneType;
    
    @JsonProperty("merAppName")
    private String merAppName;
    
    @JsonProperty("merAppId")
    private String merAppId;
    
    @JsonProperty("limitCreditCard")
    private Boolean limitCreditCard;
    
    @JsonProperty("divisionFlag")
    private Boolean divisionFlag;
    
    @JsonProperty("platformAmount")
    private Long platformAmount;
    
    @JsonProperty("subOrders")
    private List<SubOrder> subOrders;
    
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
    
    public static Builder builder() {
        return new Builder();
    }
    
    public String getMerOrderId() {
        return merOrderId;
    }
    
    public Long getTotalAmount() {
        return totalAmount;
    }
    
    public List<Goods> getGoods() {
        return goods;
    }
    
    public String getNotifyUrl() {
        return notifyUrl;
    }
    
    public String getReturnUrl() {
        return returnUrl;
    }
    
    public String getExpireTime() {
        return expireTime;
    }
    
    public String getSceneType() {
        return sceneType;
    }
    
    public String getMerAppName() {
        return merAppName;
    }
    
    public String getMerAppId() {
        return merAppId;
    }
    
    public Boolean getLimitCreditCard() {
        return limitCreditCard;
    }
    
    public Boolean getDivisionFlag() {
        return divisionFlag;
    }
    
    public Long getPlatformAmount() {
        return platformAmount;
    }
    
    public List<SubOrder> getSubOrders() {
        return subOrders;
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
}
```

- [ ] **步骤3：编写Goods和SubOrder辅助类**

创建 `src/main/java/com/chinaums/sdk/request/Goods.java`：

```java
package com.chinaums.sdk.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Goods {
    
    @JsonProperty("goodsId")
    private String goodsId;
    
    @JsonProperty("goodsName")
    private String goodsName;
    
    @JsonProperty("quantity")
    private String quantity;
    
    @JsonProperty("price")
    private String price;
    
    @JsonProperty("goodsCategory")
    private String goodsCategory;
    
    @JsonProperty("body")
    private String body;
    
    @JsonProperty("subMerchantId")
    private String subMerchantId;
    
    @JsonProperty("merOrderId")
    private String merOrderId;
    
    @JsonProperty("subOrderAmount")
    private Long subOrderAmount;
    
    public Goods() {
    }
    
    public String getGoodsId() {
        return goodsId;
    }
    
    public void setGoodsId(String goodsId) {
        this.goodsId = goodsId;
    }
    
    public String getGoodsName() {
        return goodsName;
    }
    
    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }
    
    public String getQuantity() {
        return quantity;
    }
    
    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }
    
    public String getPrice() {
        return price;
    }
    
    public void setPrice(String price) {
        this.price = price;
    }
    
    public String getGoodsCategory() {
        return goodsCategory;
    }
    
    public void setGoodsCategory(String goodsCategory) {
        this.goodsCategory = goodsCategory;
    }
    
    public String getBody() {
        return body;
    }
    
    public void setBody(String body) {
        this.body = body;
    }
    
    public String getSubMerchantId() {
        return subMerchantId;
    }
    
    public void setSubMerchantId(String subMerchantId) {
        this.subMerchantId = subMerchantId;
    }
    
    public String getMerOrderId() {
        return merOrderId;
    }
    
    public void setMerOrderId(String merOrderId) {
        this.merOrderId = merOrderId;
    }
    
    public Long getSubOrderAmount() {
        return subOrderAmount;
    }
    
    public void setSubOrderAmount(Long subOrderAmount) {
        this.subOrderAmount = subOrderAmount;
    }
}
```

创建 `src/main/java/com/chinaums/sdk/request/SubOrder.java`：

```java
package com.chinaums.sdk.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SubOrder {
    
    @JsonProperty("mid")
    private String mid;
    
    @JsonProperty("merOrderId")
    private String merOrderId;
    
    @JsonProperty("totalAmount")
    private Long totalAmount;
    
    public SubOrder() {
    }
    
    public SubOrder(String mid, String merOrderId, Long totalAmount) {
        this.mid = mid;
        this.merOrderId = merOrderId;
        this.totalAmount = totalAmount;
    }
    
    public String getMid() {
        return mid;
    }
    
    public void setMid(String mid) {
        this.mid = mid;
    }
    
    public String getMerOrderId() {
        return merOrderId;
    }
    
    public void setMerOrderId(String merOrderId) {
        this.merOrderId = merOrderId;
    }
    
    public Long getTotalAmount() {
        return totalAmount;
    }
    
    public void setTotalAmount(Long totalAmount) {
        this.totalAmount = totalAmount;
    }
}
```

- [ ] **步骤4：编写其他请求类**

创建 `src/main/java/com/chinaums/sdk/request/QueryRequest.java`：

```java
package com.chinaums.sdk.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public class QueryRequest extends BaseRequest {
    
    @JsonProperty("merOrderId")
    private String merOrderId;
    
    public QueryRequest(String merOrderId) {
        if (merOrderId == null || merOrderId.isEmpty()) {
            throw new IllegalArgumentException("商户订单号不能为空");
        }
        this.merOrderId = merOrderId;
    }
    
    public String getMerOrderId() {
        return merOrderId;
    }
}
```

创建 `src/main/java/com/chinaums/sdk/request/RefundRequest.java`：

```java
package com.chinaums.sdk.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class RefundRequest extends BaseRequest {
    
    @JsonProperty("merOrderId")
    private String merOrderId;
    
    @JsonProperty("refundOrderId")
    private String refundOrderId;
    
    @JsonProperty("refundAmount")
    private Long refundAmount;
    
    @JsonProperty("refundDesc")
    private String refundDesc;
    
    @JsonProperty("platformAmount")
    private Long platformAmount;
    
    @JsonProperty("subOrders")
    private List<SubOrder> subOrders;
    
    private RefundRequest(Builder builder) {
        this.merOrderId = builder.merOrderId;
        this.refundOrderId = builder.refundOrderId;
        this.refundAmount = builder.refundAmount;
        this.refundDesc = builder.refundDesc;
        this.platformAmount = builder.platformAmount;
        this.subOrders = builder.subOrders;
    }
    
    public static Builder builder() {
        return new Builder();
    }
    
    public String getMerOrderId() {
        return merOrderId;
    }
    
    public String getRefundOrderId() {
        return refundOrderId;
    }
    
    public Long getRefundAmount() {
        return refundAmount;
    }
    
    public String getRefundDesc() {
        return refundDesc;
    }
    
    public Long getPlatformAmount() {
        return platformAmount;
    }
    
    public List<SubOrder> getSubOrders() {
        return subOrders;
    }
    
    public static class Builder {
        private String merOrderId;
        private String refundOrderId;
        private Long refundAmount;
        private String refundDesc;
        private Long platformAmount;
        private List<SubOrder> subOrders;
        
        public Builder merOrderId(String merOrderId) {
            this.merOrderId = merOrderId;
            return this;
        }
        
        public Builder refundOrderId(String refundOrderId) {
            this.refundOrderId = refundOrderId;
            return this;
        }
        
        public Builder refundAmount(Long refundAmount) {
            this.refundAmount = refundAmount;
            return this;
        }
        
        public Builder refundDesc(String refundDesc) {
            this.refundDesc = refundDesc;
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
        
        public RefundRequest build() {
            validate();
            return new RefundRequest(this);
        }
        
        private void validate() {
            if (merOrderId == null || merOrderId.isEmpty()) {
                throw new IllegalArgumentException("原商户订单号不能为空");
            }
            if (refundAmount == null || refundAmount <= 0) {
                throw new IllegalArgumentException("退款金额必须大于0");
            }
        }
    }
}
```

创建 `src/main/java/com/chinaums/sdk/request/RefundQueryRequest.java`：

```java
package com.chinaums.sdk.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RefundQueryRequest extends BaseRequest {
    
    @JsonProperty("merOrderId")
    private String merOrderId;
    
    public RefundQueryRequest(String merOrderId) {
        if (merOrderId == null || merOrderId.isEmpty()) {
            throw new IllegalArgumentException("退款订单号不能为空");
        }
        this.merOrderId = merOrderId;
    }
    
    public String getMerOrderId() {
        return merOrderId;
    }
}
```

- [ ] **步骤5：编写BaseResponse基类**

创建 `src/main/java/com/chinaums/sdk/response/BaseResponse.java`：

```java
package com.chinaums.sdk.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public abstract class BaseResponse {
    
    @JsonProperty("errCode")
    private String errCode;
    
    @JsonProperty("errMsg")
    private String errMsg;
    
    @JsonProperty("responseTimeStamp")
    private String responseTimeStamp;
    
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
    
    public boolean isSuccess() {
        return "SUCCESS".equals(errCode);
    }
    
    public String getErrCode() {
        return errCode;
    }
    
    public void setErrCode(String errCode) {
        this.errCode = errCode;
    }
    
    public String getErrMsg() {
        return errMsg;
    }
    
    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
    }
    
    public String getResponseTimeStamp() {
        return responseTimeStamp;
    }
    
    public void setResponseTimeStamp(String responseTimeStamp) {
        this.responseTimeStamp = responseTimeStamp;
    }
    
    public String getMid() {
        return mid;
    }
    
    public void setMid(String mid) {
        this.mid = mid;
    }
    
    public String getTid() {
        return tid;
    }
    
    public void setTid(String tid) {
        this.tid = tid;
    }
    
    public String getInstMid() {
        return instMid;
    }
    
    public void setInstMid(String instMid) {
        this.instMid = instMid;
    }
    
    public String getMsgId() {
        return msgId;
    }
    
    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }
    
    public String getSrcReserve() {
        return srcReserve;
    }
    
    public void setSrcReserve(String srcReserve) {
        this.srcReserve = srcReserve;
    }
}
```

- [ ] **步骤6：编写各个响应类**

创建 `src/main/java/com/chinaums/sdk/response/H5PayResponse.java`：

```java
package com.chinaums.sdk.response;

import com.fasterxml.jackson.annotation.JsonProperty;

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
    
    public String getPayUrl() {
        return payUrl;
    }
    
    public void setPayUrl(String payUrl) {
        this.payUrl = payUrl;
    }
    
    public String getMerOrderId() {
        return merOrderId;
    }
    
    public void setMerOrderId(String merOrderId) {
        this.merOrderId = merOrderId;
    }
    
    public String getSeqId() {
        return seqId;
    }
    
    public void setSeqId(String seqId) {
        this.seqId = seqId;
    }
    
    public String getTargetOrderId() {
        return targetOrderId;
    }
    
    public void setTargetOrderId(String targetOrderId) {
        this.targetOrderId = targetOrderId;
    }
    
    public String getTargetSys() {
        return targetSys;
    }
    
    public void setTargetSys(String targetSys) {
        this.targetSys = targetSys;
    }
}
```

创建 `src/main/java/com/chinaums/sdk/response/QueryResponse.java`：

```java
package com.chinaums.sdk.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public class QueryResponse extends BaseResponse {
    
    @JsonProperty("status")
    private String status;
    
    @JsonProperty("totalAmount")
    private Long totalAmount;
    
    @JsonProperty("merOrderId")
    private String merOrderId;
    
    @JsonProperty("payTime")
    private String payTime;
    
    @JsonProperty("settleDate")
    private String settleDate;
    
    @JsonProperty("buyerId")
    private String buyerId;
    
    @JsonProperty("targetOrderId")
    private String targetOrderId;
    
    @JsonProperty("targetSys")
    private String targetSys;
    
    @JsonProperty("buyerPayAmount")
    private Long buyerPayAmount;
    
    @JsonProperty("couponAmount")
    private Long couponAmount;
    
    @JsonProperty("invoiceAmount")
    private Long invoiceAmount;
    
    @JsonProperty("receiptAmount")
    private Long receiptAmount;
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public Long getTotalAmount() {
        return totalAmount;
    }
    
    public void setTotalAmount(Long totalAmount) {
        this.totalAmount = totalAmount;
    }
    
    public String getMerOrderId() {
        return merOrderId;
    }
    
    public void setMerOrderId(String merOrderId) {
        this.merOrderId = merOrderId;
    }
    
    public String getPayTime() {
        return payTime;
    }
    
    public void setPayTime(String payTime) {
        this.payTime = payTime;
    }
    
    public String getSettleDate() {
        return settleDate;
    }
    
    public void setSettleDate(String settleDate) {
        this.settleDate = settleDate;
    }
    
    public String getBuyerId() {
        return buyerId;
    }
    
    public void setBuyerId(String buyerId) {
        this.buyerId = buyerId;
    }
    
    public String getTargetOrderId() {
        return targetOrderId;
    }
    
    public void setTargetOrderId(String targetOrderId) {
        this.targetOrderId = targetOrderId;
    }
    
    public String getTargetSys() {
        return targetSys;
    }
    
    public void setTargetSys(String targetSys) {
        this.targetSys = targetSys;
    }
    
    public Long getBuyerPayAmount() {
        return buyerPayAmount;
    }
    
    public void setBuyerPayAmount(Long buyerPayAmount) {
        this.buyerPayAmount = buyerPayAmount;
    }
    
    public Long getCouponAmount() {
        return couponAmount;
    }
    
    public void setCouponAmount(Long couponAmount) {
        this.couponAmount = couponAmount;
    }
    
    public Long getInvoiceAmount() {
        return invoiceAmount;
    }
    
    public void setInvoiceAmount(Long invoiceAmount) {
        this.invoiceAmount = invoiceAmount;
    }
    
    public Long getReceiptAmount() {
        return receiptAmount;
    }
    
    public void setReceiptAmount(Long receiptAmount) {
        this.receiptAmount = receiptAmount;
    }
}
```

创建 `src/main/java/com/chinaums/sdk/response/RefundResponse.java`：

```java
package com.chinaums.sdk.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RefundResponse extends BaseResponse {
    
    @JsonProperty("status")
    private String status;
    
    @JsonProperty("totalAmount")
    private Long totalAmount;
    
    @JsonProperty("refundAmount")
    private Long refundAmount;
    
    @JsonProperty("merOrderId")
    private String merOrderId;
    
    @JsonProperty("refundOrderId")
    private String refundOrderId;
    
    @JsonProperty("refundTargetOrderId")
    private String refundTargetOrderId;
    
    @JsonProperty("seqId")
    private String seqId;
    
    @JsonProperty("targetSys")
    private String targetSys;
    
    @JsonProperty("targetMid")
    private String targetMid;
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public Long getTotalAmount() {
        return totalAmount;
    }
    
    public void setTotalAmount(Long totalAmount) {
        this.totalAmount = totalAmount;
    }
    
    public Long getRefundAmount() {
        return refundAmount;
    }
    
    public void setRefundAmount(Long refundAmount) {
        this.refundAmount = refundAmount;
    }
    
    public String getMerOrderId() {
        return merOrderId;
    }
    
    public void setMerOrderId(String merOrderId) {
        this.merOrderId = merOrderId;
    }
    
    public String getRefundOrderId() {
        return refundOrderId;
    }
    
    public void setRefundOrderId(String refundOrderId) {
        this.refundOrderId = refundOrderId;
    }
    
    public String getRefundTargetOrderId() {
        return refundTargetOrderId;
    }
    
    public void setRefundTargetOrderId(String refundTargetOrderId) {
        this.refundTargetOrderId = refundTargetOrderId;
    }
    
    public String getSeqId() {
        return seqId;
    }
    
    public void setSeqId(String seqId) {
        this.seqId = seqId;
    }
    
    public String getTargetSys() {
        return targetSys;
    }
    
    public void setTargetSys(String targetSys) {
        this.targetSys = targetSys;
    }
    
    public String getTargetMid() {
        return targetMid;
    }
    
    public void setTargetMid(String targetMid) {
        this.targetMid = targetMid;
    }
}
```

创建 `src/main/java/com/chinaums/sdk/response/RefundQueryResponse.java`：

```java
package com.chinaums.sdk.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RefundQueryResponse extends BaseResponse {
    
    @JsonProperty("refundStatus")
    private String refundStatus;
    
    @JsonProperty("status")
    private String status;
    
    @JsonProperty("totalAmount")
    private Long totalAmount;
    
    @JsonProperty("merOrderId")
    private String merOrderId;
    
    @JsonProperty("refundOrderId")
    private String refundOrderId;
    
    @JsonProperty("refundTargetOrderId")
    private String refundTargetOrderId;
    
    @JsonProperty("seqId")
    private String seqId;
    
    @JsonProperty("payTime")
    private String payTime;
    
    @JsonProperty("settleDate")
    private String settleDate;
    
    public String getRefundStatus() {
        return refundStatus;
    }
    
    public void setRefundStatus(String refundStatus) {
        this.refundStatus = refundStatus;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public Long getTotalAmount() {
        return totalAmount;
    }
    
    public void setTotalAmount(Long totalAmount) {
        this.totalAmount = totalAmount;
    }
    
    public String getMerOrderId() {
        return merOrderId;
    }
    
    public void setMerOrderId(String merOrderId) {
        this.merOrderId = merOrderId;
    }
    
    public String getRefundOrderId() {
        return refundOrderId;
    }
    
    public void setRefundOrderId(String refundOrderId) {
        this.refundOrderId = refundOrderId;
    }
    
    public String getRefundTargetOrderId() {
        return refundTargetOrderId;
    }
    
    public void setRefundTargetOrderId(String refundTargetOrderId) {
        this.refundTargetOrderId = refundTargetOrderId;
    }
    
    public String getSeqId() {
        return seqId;
    }
    
    public void setSeqId(String seqId) {
        this.seqId = seqId;
    }
    
    public String getPayTime() {
        return payTime;
    }
    
    public void setPayTime(String payTime) {
        this.payTime = payTime;
    }
    
    public String getSettleDate() {
        return settleDate;
    }
    
    public void setSettleDate(String settleDate) {
        this.settleDate = settleDate;
    }
}
```

- [ ] **步骤7：提交请求和响应对象**

```bash
git add src/main/java/com/chinaums/sdk/request/ src/main/java/com/chinaums/sdk/response/
git commit -m "feat: 添加请求和响应对象"
```

---

### 任务8：创建主入口类UmsPayClient

**文件：**
- 创建: `src/main/java/com/chinaums/sdk/UmsPayClient.java`
- 创建: `src/test/java/com/chinaums/sdk/UmsPayClientTest.java`

- [ ] **步骤1：编写UmsPayClient主类**

创建 `src/main/java/com/chinaums/sdk/UmsPayClient.java`：

```java
package com.chinaums.sdk;

import com.chinaums.sdk.config.UmsPayConfig;
import com.chinaums.sdk.exception.UmsPayBusinessException;
import com.chinaums.sdk.exception.UmsPayException;
import com.chinaums.sdk.exception.UmsPayNetworkException;
import com.chinaums.sdk.http.HttpClient;
import com.chinaums.sdk.http.HttpResponse;
import com.chinaums.sdk.http.OkHttpExecutor;
import com.chinaums.sdk.request.*;
import com.chinaums.sdk.response.*;
import com.chinaums.sdk.util.DateUtil;
import com.chinaums.sdk.util.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

/**
 * 银联商务支付客户端
 * 
 * 提供统一的支付接口调用入口，支持H5支付、订单查询、退款、退款查询等功能。
 * 
 * 快速开始：
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
    
    private static final Logger logger = LoggerFactory.getLogger(UmsPayClient.class);
    
    private final UmsPayConfig config;
    private final HttpClient httpClient;
    
    /**
     * 构造函数
     * 
     * @param config 配置对象
     */
    public UmsPayClient(UmsPayConfig config) {
        this.config = config;
        this.httpClient = new OkHttpExecutor(config);
    }
    
    /**
     * H5支付下单
     * 
     * 发起微信H5支付请求，返回支付URL供商户引导用户完成支付。
     * 
     * @param request H5支付请求对象，包含订单信息
     * @return H5支付响应对象，包含支付URL
     * @throws UmsPayException 支付异常
     */
    public H5PayResponse h5Pay(H5PayRequest request) throws UmsPayException {
        String url = config.getEnvironment().getBaseUrl() + "/v1/netpay/wxpay/h5-pay";
        String requestBody = buildRequestBody(request);
        
        logger.info("发起H5支付请求: {}", request.getMerOrderId());
        
        HttpResponse response = httpClient.get(url, requestBody, null);
        return parseResponse(response, H5PayResponse.class);
    }
    
    /**
     * 订单查询
     * 
     * 查询订单交易状态和详细信息。
     * 
     * @param request 订单查询请求对象
     * @return 订单查询响应对象
     * @throws UmsPayException 查询异常
     */
    public QueryResponse query(QueryRequest request) throws UmsPayException {
        String url = config.getEnvironment().getBaseUrl() + "/v1/netpay/query";
        String requestBody = buildRequestBody(request);
        
        logger.info("发起订单查询请求: {}", request.getMerOrderId());
        
        HttpResponse response = httpClient.post(url, requestBody, null);
        return parseResponse(response, QueryResponse.class);
    }
    
    /**
     * 退款
     * 
     * 发起退款请求，支持全额退款和部分退款。
     * 
     * @param request 退款请求对象
     * @return 退款响应对象
     * @throws UmsPayException 退款异常
     */
    public RefundResponse refund(RefundRequest request) throws UmsPayException {
        String url = config.getEnvironment().getBaseUrl() + "/v1/netpay/refund";
        String requestBody = buildRequestBody(request);
        
        logger.info("发起退款请求: {}", request.getMerOrderId());
        
        HttpResponse response = httpClient.post(url, requestBody, null);
        return parseResponse(response, RefundResponse.class);
    }
    
    /**
     * 退款查询
     * 
     * 查询退款订单状态和详细信息。
     * 
     * @param request 退款查询请求对象
     * @return 退款查询响应对象
     * @throws UmsPayException 查询异常
     */
    public RefundQueryResponse refundQuery(RefundQueryRequest request) throws UmsPayException {
        String url = config.getEnvironment().getBaseUrl() + "/v1/netpay/refund-query";
        String requestBody = buildRequestBody(request);
        
        logger.info("发起退款查询请求: {}", request.getMerOrderId());
        
        HttpResponse response = httpClient.post(url, requestBody, null);
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

- [ ] **步骤2：编写UmsPayClient测试**

创建 `src/test/java/com/chinaums/sdk/UmsPayClientTest.java`：

```java
package com.chinaums.sdk;

import com.chinaums.sdk.config.Environment;
import com.chinaums.sdk.config.UmsPayConfig;
import com.chinaums.sdk.exception.UmsPayBusinessException;
import com.chinaums.sdk.exception.UmsPayNetworkException;
import com.chinaums.sdk.request.H5PayRequest;
import com.chinaums.sdk.request.QueryRequest;
import com.chinaums.sdk.request.RefundQueryRequest;
import com.chinaums.sdk.request.RefundRequest;
import com.chinaums.sdk.response.H5PayResponse;
import com.chinaums.sdk.response.QueryResponse;
import com.chinaums.sdk.response.RefundQueryResponse;
import com.chinaums.sdk.response.RefundResponse;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class UmsPayClientTest {
    
    private MockWebServer server;
    private UmsPayClient client;
    
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
    
    @AfterEach
    void tearDown() throws IOException {
        server.shutdown();
    }
    
    @Test
    void testH5PaySuccess() throws Exception {
        String mockResponse = "{\"errCode\":\"SUCCESS\",\"payUrl\":\"https://pay.example.com\",\"merOrderId\":\"TEST123\"}";
        server.enqueue(new MockResponse()
            .setBody(mockResponse)
            .setResponseCode(200));
        
        H5PayRequest request = H5PayRequest.builder()
            .merOrderId("TEST123")
            .totalAmount(100L)
            .build();
        
        H5PayResponse response = client.h5Pay(request);
        
        assertTrue(response.isSuccess());
        assertEquals("https://pay.example.com", response.getPayUrl());
        assertEquals("TEST123", response.getMerOrderId());
    }
    
    @Test
    void testH5PayBusinessError() throws Exception {
        String mockResponse = "{\"errCode\":\"BIZ_ERROR\",\"errMsg\":\"业务错误\"}";
        server.enqueue(new MockResponse()
            .setBody(mockResponse)
            .setResponseCode(200));
        
        H5PayRequest request = H5PayRequest.builder()
            .merOrderId("TEST123")
            .totalAmount(100L)
            .build();
        
        assertThrows(UmsPayBusinessException.class, () -> {
            client.h5Pay(request);
        });
    }
    
    @Test
    void testQuerySuccess() throws Exception {
        String mockResponse = "{\"errCode\":\"SUCCESS\",\"status\":\"TRADE_SUCCESS\",\"totalAmount\":100}";
        server.enqueue(new MockResponse()
            .setBody(mockResponse)
            .setResponseCode(200));
        
        QueryRequest request = new QueryRequest("TEST123");
        QueryResponse response = client.query(request);
        
        assertTrue(response.isSuccess());
        assertEquals("TRADE_SUCCESS", response.getStatus());
        assertEquals(100L, response.getTotalAmount());
    }
    
    @Test
    void testRefundSuccess() throws Exception {
        String mockResponse = "{\"errCode\":\"SUCCESS\",\"status\":\"TRADE_SUCCESS\",\"refundAmount\":50}";
        server.enqueue(new MockResponse()
            .setBody(mockResponse)
            .setResponseCode(200));
        
        RefundRequest request = RefundRequest.builder()
            .merOrderId("TEST123")
            .refundAmount(50L)
            .build();
        
        RefundResponse response = client.refund(request);
        
        assertTrue(response.isSuccess());
        assertEquals("TRADE_SUCCESS", response.getStatus());
        assertEquals(50L, response.getRefundAmount());
    }
    
    @Test
    void testRefundQuerySuccess() throws Exception {
        String mockResponse = "{\"errCode\":\"SUCCESS\",\"refundStatus\":\"SUCCESS\",\"totalAmount\":100}";
        server.enqueue(new MockResponse()
            .setBody(mockResponse)
            .setResponseCode(200));
        
        RefundQueryRequest request = new RefundQueryRequest("REFUND123");
        RefundQueryResponse response = client.refundQuery(request);
        
        assertTrue(response.isSuccess());
        assertEquals("SUCCESS", response.getRefundStatus());
        assertEquals(100L, response.getTotalAmount());
    }
}
```

- [ ] **步骤3：运行UmsPayClient测试**

运行命令：

```bash
mvn test -Dtest=UmsPayClientTest
```

预期输出：所有测试通过

- [ ] **步骤4：提交主入口类**

```bash
git add src/main/java/com/chinaums/sdk/UmsPayClient.java src/test/java/com/chinaums/sdk/UmsPayClientTest.java
git commit -m "feat: 添加UmsPayClient主入口类"
```

---

### 任务9：创建日志配置

**文件：**
- 创建: `src/main/resources/logback.xml`

- [ ] **步骤1：编写日志配置文件**

创建 `src/main/resources/logback.xml`：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<configuration>
    <appender name="STDOUT" class="ch.qos.logback.core.ConsoleAppender">
        <encoder>
            <pattern>%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n</pattern>
        </encoder>
    </appender>
    
    <logger name="com.chinaums.sdk" level="DEBUG"/>
    
    <root level="INFO">
        <appender-ref ref="STDOUT"/>
    </root>
</configuration>
```

- [ ] **步骤2：提交日志配置**

```bash
git add src/main/resources/logback.xml
git commit -m "feat: 添加日志配置"
```

---

### 任务10：运行完整测试

- [ ] **步骤1：运行所有测试**

运行命令：

```bash
mvn test
```

预期输出：所有测试通过，测试覆盖率 > 80%

- [ ] **步骤2：生成测试报告**

运行命令：

```bash
mvn surefire-report:report
```

预期输出：生成测试报告

---

### 任务11：创建README文档

**文件：**
- 创建: `README.md`
- 创建: `README_CN.md`

由于README文档较长，这里只展示结构。完整内容请参考设计文档。

- [ ] **步骤1：编写README.md（英文版）**

创建 `README.md`，包含以下内容：
- 项目介绍
- 功能特性
- 环境要求
- 快速开始
- API文档
- 示例代码
- 常见问题
- 贡献指南
- 许可证

- [ ] **步骤2：编写README_CN.md（中文版）**

创建 `README_CN.md`，包含以下内容：
- 项目介绍
- 功能特性
- 环境要求
- 快速开始
- API文档
- 示例代码
- 常见问题
- 贡献指南
- 许可证

- [ ] **步骤3：提交README文档**

```bash
git add README.md README_CN.md
git commit -m "docs: 添加README文档"
```

---

### 任务12：最终验证和打包

- [ ] **步骤1：编译项目**

运行命令：

```bash
mvn clean compile
```

预期输出：编译成功

- [ ] **步骤2：运行所有测试**

运行命令：

```bash
mvn test
```

预期输出：所有测试通过

- [ ] **步骤3：打包项目**

运行命令：

```bash
mvn package
```

预期输出：生成jar包

- [ ] **步骤4：生成JavaDoc**

运行命令：

```bash
mvn javadoc:javadoc
```

预期输出：生成JavaDoc文档

- [ ] **步骤5：最终提交**

```bash
git add .
git commit -m "feat: 完成银联商务支付SDK开发"
```

---

## 完成标准

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

**计划版本**: 1.0  
**创建日期**: 2026-04-27  
**最后更新**: 2026-04-27  
**作者**: AI Assistant  
**状态**: 待执行
