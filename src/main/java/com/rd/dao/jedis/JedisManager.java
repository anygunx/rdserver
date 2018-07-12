package com.rd.dao.jedis;

import com.rd.define.GameDefine;
import org.apache.log4j.Logger;
import redis.clients.jedis.*;

import java.util.*;

/**
 * RedisCluster管理器
 *
 * @author U-Demon Created on 2017年3月2日 下午6:15:41
 * @version 1.0.0
 */
public class JedisManager {

    private static Logger logger = Logger.getLogger(JedisManager.class);

    //单例
    private static JedisManager mgr = new JedisManager();

    public static JedisManager gi() {
        return mgr;
    }

    private ShardedJedisPool jedisPool = null;

    private JedisCluster jc = null;

    private JedisManager() {
        //Redis配置
        String[] redisInfos = GameDefine.REDIS_CLUSTER.split(",");
        String host = redisInfos[0];
        //集群配置
        if (redisInfos.length > 2) {
            Set<HostAndPort> nodes = new HashSet<>();
            for (int i = 1; i < redisInfos.length; i++) {
                HostAndPort node = new HostAndPort(host, Integer.valueOf(redisInfos[i]));
                nodes.add(node);
            }
            jc = new JedisCluster(nodes);
        }
        //单节点
        else {
            int port = Integer.valueOf(redisInfos[1]);

            JedisPoolConfig config = new JedisPoolConfig();
            config.setMaxTotal(300);
            config.setMaxIdle(60000);
            config.setMaxWaitMillis(-1);
            config.setTestOnBorrow(false);

            List<JedisShardInfo> shards = new ArrayList<>();
            JedisShardInfo shardA = new JedisShardInfo(host, port);
//      	infoA.setPassword("redis.360buy");
            shards.add(shardA);

            //连接池
            jedisPool = new ShardedJedisPool(config, shards);
//			jedisPool = new JedisPool(config, host, port, 2000);
        }
    }

    private synchronized ShardedJedis getJedis() {
        ShardedJedis jedis = null;
        try {
            if (jedisPool != null) {
                jedis = jedisPool.getResource();
            }
        } catch (Exception e) {
            logger.error("Get jedis error : ", e);
        } finally {
            close(jedis);
        }
        return jedis;
    }

    /**
     * 释放jedis资源
     *
     * @param jedis
     */
    private void close(final ShardedJedis jedis) {
        if (jedis != null && jedisPool != null) {
            jedis.close();
        }
    }

    public void hset(String key, String field, String value) {
        if (jc != null)
            jc.hset(key, field, value);
        else
            getJedis().hset(key, field, value);
    }

    public void set(String key, String value) {
        if (jc != null)
            jc.set(key, value);
        else
            getJedis().set(key, value);
    }

    public Map<String, String> hgetAll(String key) {
        if (jc != null)
            return jc.hgetAll(key);
        else
            return getJedis().hgetAll(key);
    }

    public String hget(String key, String field) {
        if (jc != null)
            return jc.hget(key, field);
        else
            return getJedis().hget(key, field);
    }

    public String get(String key) {
        if (jc != null)
            return jc.get(key);
        else
            return getJedis().get(key);
    }

}
