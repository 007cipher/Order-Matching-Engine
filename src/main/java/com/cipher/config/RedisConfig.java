package com.cipher.config;

import org.redisson.Redisson;
import org.redisson.api.RLiveObjectService;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;

public class RedisConfig {

    private static final String REDIS_ADDRESS = "redis://127.0.0.1:6379";

    public static RedissonClient getRedissonClient() {
        Config config = new Config();
        config.useSingleServer().setAddress(REDIS_ADDRESS);
        return Redisson.create(config);
    }

    public static RLiveObjectService getLiveObjectService() {
        return getRedissonClient().getLiveObjectService();
    }
}
