package com.flance.framework.modules.ai.utils;

import java.time.Duration;

public class DurationUtil {

    /**
     * 数据库 long(毫秒) → Duration
     */
    public static Duration toDuration(Long millis) {
        if (millis == null || millis <= 0) {
            return Duration.ofSeconds(60); // 默认60秒
        }
        return Duration.ofMillis(millis);
    }

}
