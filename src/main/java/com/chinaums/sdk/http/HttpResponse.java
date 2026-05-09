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


package com.chinaums.sdk.http;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * HTTP响应封装类
 *
 * 封装HTTP响应的状态码、响应体和响应头。
 *
 * @author ChinaUMS SDK Team
 * @version 1.0.0
 * @since 1.0.0
 */
public class HttpResponse {

    private final int code;
    private final String body;
    private final Map<String, String> headers;

    /**
     * 构造函数
     *
     * @param code HTTP状态码
     * @param body 响应体
     */
    public HttpResponse(int code, String body) {
        this(code, body, Collections.<String, String>emptyMap());
    }

    /**
     * 构造函数
     *
     * @param code HTTP状态码
     * @param body 响应体
     * @param headers 响应头
     */
    public HttpResponse(int code, String body, Map<String, String> headers) {
        this.code = code;
        this.body = body;
        this.headers = headers != null ? new HashMap<>(headers) : new HashMap<String, String>();
    }

    /**
     * 获取HTTP状态码
     *
     * @return HTTP状态码
     */
    public int getCode() {
        return code;
    }

    /**
     * 获取响应体
     *
     * @return 响应体
     */
    public String getBody() {
        return body;
    }

    /**
     * 获取所有响应头
     *
     * @return 响应头Map
     */
    public Map<String, String> getHeaders() {
        return Collections.unmodifiableMap(headers);
    }

    /**
     * 获取指定名称的响应头
     *
     * @param name 响应头名称
     * @return 响应头值，如果不存在返回null
     */
    public String getHeader(String name) {
        if (name == null) {
            return null;
        }
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            if (entry.getKey().equalsIgnoreCase(name)) {
                return entry.getValue();
            }
        }
        return null;
    }

    /**
     * 判断响应是否成功
     *
     * @return true表示成功（状态码200-299），false表示失败
     */
    public boolean isSuccess() {
        return code >= 200 && code < 300;
    }
}
