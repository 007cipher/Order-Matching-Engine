package com.cipher.matching.engine.config;

import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.api.RLiveObjectService;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;

@Slf4j
public class RedisConfig {

    private static final String REDIS_ADDRESS = "redis://127.0.0.1:6379";

    private static RedissonClient REDIS_CLIENT = null;

    static {
        try {
            Config config = new Config();
            config.useSingleServer().setAddress(REDIS_ADDRESS);
            REDIS_CLIENT = Redisson.create(config);
            log.info("Successfully connected with Redis server");
        } catch (Exception e) {
            log.error("Redis server connection error: {}", e.getLocalizedMessage());
            e.printStackTrace();
            System.exit(100);
        }
    }

    public static RedissonClient getRedissonClient() {
        return REDIS_CLIENT;
    }

    public static RLiveObjectService getLiveObjectService() {
        return REDIS_CLIENT.getLiveObjectService();
    }
}
