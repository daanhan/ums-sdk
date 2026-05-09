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
