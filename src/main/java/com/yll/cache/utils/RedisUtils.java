package com.yll.cache.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @className: RedisUtils
 * @description: //缓存操作工具类
 * @author: yys1778
 * @date: Created in 2019/2/28 11:47
 * @modify by: yys1778
 * @version: V1.0
 */
@Component
public class RedisUtils {

    @Autowired
    RedisTemplate redisTemplate;

    //=============================common============================

    /**
     * @param key  键
     * @param time 时间(秒)
     * @title: expire
     * @description: //指定缓存失效时间
     * @author: yys1778
     * @date: Created in 2019/2/28 12:01
     * @throws:
     * @return: boolean
     */
    public boolean expire(String key, long time) {
        try {
            if (time > 0) {
                redisTemplate.expire(key, time, TimeUnit.SECONDS);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * @param key 键 不能为null 
     * @title: getExpire
     * @description: // 根据key 获取过期时间 
     * @author: yys1778
     * @date: Created in 2019/2/28 12:04
     * @throws:
     * @return: long 时间(秒) 返回0代表为永久有效 
     */
    public long getExpire(String key) {
        return redisTemplate.getExpire(key, TimeUnit.SECONDS);
    }

    /**
     * @param key 键
     * @title: hasKey
     * @description: // 判断key是否存在 
     * @author: yys1778
     * @date: Created in 2019/2/28 13:43
     * @throws:
     * @return: boolean true 存在 false不存在
     */
    public boolean hasKey(String key) {
        try {
            return redisTemplate.hasKey(key);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * @param key 可以传一个 或 多个
     * @title: del
     * @description: // 删除缓存 
     * @author: yys1778
     * @date: Created in 2019/2/28 13:46
     * @throws:
     * @return: void
     */
    public void del(String... key) {
        if (key != null && key.length > 0) {
            if (key.length == 1) {
                redisTemplate.delete(key[0]);
            } else {
                redisTemplate.delete(CollectionUtils.arrayToList(key));
            }
        }
    }
    //============================String=============================

    /**
     * @param key 键
     * @title: get
     * @description: // 普通缓存获取 
     * @author: yys1778
     * @date: Created in 2019/2/28 13:49
     * @throws:
     * @return: java.lang.Object 值
     */
    public Object get(String key) {
        return key == null ? null : redisTemplate.opsForValue().get(key);
    }

    /**
     * @param key   键
     * @param value 值
     * @title: set
     * @description: //普通缓存放入
     * @author: yys1778
     * @date: Created in 2019/2/28 13:54
     * @throws:
     * @return: boolean true成功 false失败
     */
    public boolean set(String key, Object value) {
        try {
            redisTemplate.opsForValue().set(key, value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * @param key   键
     * @param value 值
     * @param time  时间(秒) time要大于0 如果time小于等于0 将设置无限期
     * @title: set
     * @description: //普通缓存放入并设置时间
     * @author: yys1778
     * @date: Created in 2019/2/28 13:56
     * @throws:
     * @return: boolean true成功 false 失败
     */
    public boolean set(String key, Object value, long time) {
        try {
            if (time > 0) {
                redisTemplate.opsForValue().set(key, value, time, TimeUnit.SECONDS);
            } else {
                set(key, value);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * @param key   键
     * @param delta 要增加几(大于0)
     * @title: incr
     * @description: //递增
     * @author: yys1778
     * @date: Created in 2019/2/28 13:57
     * @throws:
     * @return: long
     */
    public long incr(String key, long delta) {
        if (delta < 0) {
            throw new RuntimeException("递增因子必须大于0");
        }
        return redisTemplate.opsForValue().increment(key, delta);
    }

    /**
     * @param key   键
     * @param delta 要减少几(小于0)
     * @title: incr
     * @description: //递减
     * @author: yys1778
     * @date: Created in 2019/2/28 13:57
     * @throws:
     * @return: long
     */
    public long decr(String key, long delta) {
        if (delta < 0) {
            throw new RuntimeException("递减因子必须大于0");
        }
        return redisTemplate.opsForValue().increment(key, -delta);
    }

    //================================Map=================================

    /**
     * @param key  键 不能为null
     * @param item 项 不能为null
     * @title: hget
     * @description: //根据 key item 获取值
     * @author: yys1778
     * @date: Created in 2019/2/28 13:59
     * @throws:
     * @return: java.lang.Object
     */
    public Object hget(String key, String item) {
        return redisTemplate.opsForHash().get(key, item);
    }

    /**
     * @param key 键
     * @title: hmget
     * @description: //获取hashKey对应的所有键值
     * @author: yys1778
     * @date: Created in 2019/2/28 14:00
     * @throws:
     * @return: java.util.Map 对应的多个键值
     */
    public Map<Object, Object> hmget(String key) {
        return redisTemplate.opsForHash().entries(key);
    }

    /**
     * @param key 键
     * @param map 对应多个键值
     * @title: hmset
     * @description: // hash map 缓存数据
     * @author: yys1778
     * @date: Created in 2019/2/28 14:01
     * @throws:
     * @return: boolean true 成功 false 失败
     */
    public boolean hmset(String key, Map<String, Object> map) {
        try {
            redisTemplate.opsForHash().putAll(key, map);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * @param key  键
     * @param map  对应多个键值
     * @param time 时间(秒)
     * @title: hmset
     * @description: //HashSet 并设置时间
     * @author: yys1778
     * @date: Created in 2019/2/28 14:04
     * @throws:
     * @return: boolean true成功 false失败
     */
    public boolean hmset(String key, Map<String, Object> map, long time) {
        try {
            redisTemplate.opsForHash().putAll(key, map);
            if (time > 0) {
                expire(key, time);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * @param key   键
     * @param item  项
     * @param value 值
     * @title: hset
     * @description: //向一张hash表中放入数据,如果不存在将创建
     * @author: yys1778
     * @date: Created in 2019/2/28 14:03
     * @throws:
     * @return: boolean true 成功 false失败
     */
    public boolean hset(String key, String item, Object value) {
        try {
            redisTemplate.opsForHash().put(key, item, value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * @param key   键
     * @param item  项
     * @param value 值
     * @param time  时间(秒)注意:如果已存在的hash表有时间,这里将会替换原有的时间
     * @title: hset
     * @description: //向一张hash表中放入数据,如果不存在将创建
     * @author: yys1778
     * @date: Created in 2019/2/28 14:05
     * @throws:
     * @return: boolean true 成功 false失败
     */
    public boolean hset(String key, String item, Object value, long time) {
        try {
            redisTemplate.opsForHash().put(key, item, value);
            if (time > 0) {
                expire(key, time);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * @param key  键 不能为null
     * @param item 项 可以使多个 不能为null
     * @title: hdel
     * @description: //删除hash表中的值
     * @author: yys1778
     * @date: Created in 2019/2/28 14:06
     * @throws:
     * @return: void
     */
    public void hdel(String key, Object... item) {
        redisTemplate.opsForHash().delete(key, item);
    }

    /**
     * @param key  键 不能为null
     * @param item 项 不能为null
     * @title: hHasKey
     * @description: //判断hash表中是否有该项的值
     * @author: yys1778
     * @date: Created in 2019/2/28 14:07
     * @throws:
     * @return: boolean true 存在 false不存在
     */
    public boolean hHasKey(String key, String item) {
        return redisTemplate.opsForHash().hasKey(key, item);
    }

    /**
     * @param key  键
     * @param item 项
     * @param by   要增加几(大于0)
     * @title: hincr
     * @description: //hash递增 如果不存在,就会创建一个 并把新增后的值返回
     * @author: yys1778
     * @date: Created in 2019/2/28 14:07
     * @throws:
     * @return: double
     */
    public double hincr(String key, String item, double by) {
        return redisTemplate.opsForHash().increment(key, item, by);
    }

    /**
     * @param key  键
     * @param item 项
     * @param by   要减少记(小于0)
     * @title: hdecr
     * @description: //hash递减
     * @author: yys1778
     * @date: Created in 2019/2/28 14:08
     * @throws:
     * @return: double
     */
    public double hdecr(String key, String item, double by) {
        return redisTemplate.opsForHash().increment(key, item, -by);
    }

    //============================set=============================

    /**
     * @param key 键
     * @title: sGet
     * @description: //根据key获取Set中的所有值
     * @author: yys1778
     * @date: Created in 2019/2/28 14:09
     * @throws:
     * @return: java.util.Set<java.lang.Object>
     */
    public Set<Object> sGet(String key) {
        try {
            return redisTemplate.opsForSet().members(key);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * @param key   键
     * @param value 值
     * @title: sHasKey
     * @description: //根据value从一个set中查询,是否存在
     * @author: yys1778
     * @date: Created in 2019/2/28 14:09
     * @throws:
     * @return: boolean true 存在 false不存在
     */
    public boolean sHasKey(String key, Object value) {
        try {
            return redisTemplate.opsForSet().isMember(key, value);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * @param key    键
     * @param values 值 可以是多个
     * @title: sSet
     * @description: //将数据放入set缓存
     * @author: yys1778
     * @date: Created in 2019/2/28 14:10
     * @throws:
     * @return: long 成功个数
     */
    public long sSet(String key, Object... values) {
        try {
            return redisTemplate.opsForSet().add(key, values);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * @param key    键
     * @param time   时间(秒)
     * @param values 值 可以是多个
     * @title: sSetAndTime
     * @description: //将set数据放入缓存
     * @author: yys1778
     * @date: Created in 2019/2/28 14:11
     * @throws:
     * @return: long 成功个数
     */
    public long sSetAndTime(String key, long time, Object... values) {
        try {
            Long count = redisTemplate.opsForSet().add(key, values);
            if (time > 0) {
                expire(key, time);
            }
            return count;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * @param key 键
     * @title: sGetSetSize
     * @description: //获取set缓存的长度
     * @author: yys1778
     * @date: Created in 2019/2/28 14:12
     * @throws:
     * @return: long
     */
    public long sGetSetSize(String key) {
        try {
            return redisTemplate.opsForSet().size(key);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * @param key    键
     * @param values 值 可以是多个
     * @title: setRemove
     * @description: //移除值为value的
     * @author: yys1778
     * @date: Created in 2019/2/28 14:12
     * @throws:
     * @return: long 移除的个数
     */
    public long setRemove(String key, Object... values) {
        try {
            Long count = redisTemplate.opsForSet().remove(key, values);
            return count;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    //===============================list=================================

    /**
     * @param key   键
     * @param start 开始
     * @param end   结束0 到 -1代表所有值
     * @title: lGet
     * @description: //获取list缓存的内容
     * @author: yys1778
     * @date: Created in 2019/2/28 14:13
     * @throws:
     * @return: java.util.List<java.lang.Object>
     */
    public List<Object> lGet(String key, long start, long end) {
        try {
            return redisTemplate.opsForList().range(key, start, end);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * @param key 键
     * @title: lGetListSize
     * @description: //获取list缓存的长度
     * @author: yys1778
     * @date: Created in 2019/2/28 14:13
     * @throws:
     * @return: long
     */
    public long lGetListSize(String key) {
        try {
            return redisTemplate.opsForList().size(key);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * @param key   键
     * @param index 索引index>=0时， 0 表头，1 第二个元素，依次类推；index<0时，-1，表尾，-2倒数第二个元素，依次类推
     * @title: lGetIndex
     * @description: //通过索引 获取list中的值
     * @author: yys1778
     * @date: Created in 2019/2/28 14:14
     * @throws:
     * @return: java.lang.Object
     */
    public Object lGetIndex(String key, long index) {
        try {
            return redisTemplate.opsForList().index(key, index);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * @param key   键
     * @param value 值
     * @title: lSet
     * @description: //将list放入缓存
     * @author: yys1778
     * @date: Created in 2019/2/28 14:14
     * @throws:
     * @return: boolean
     */
    public boolean lSet(String key, Object value) {
        try {
            redisTemplate.opsForList().rightPush(key, value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * @param key   键
     * @param value 值
     * @param time  时间(秒)
     * @title: lSet
     * @description: //将list放入缓存
     * @author: yys1778
     * @date: Created in 2019/2/28 14:15
     * @throws:
     * @return: boolean
     */
    public boolean lSet(String key, Object value, long time) {
        try {
            redisTemplate.opsForList().rightPush(key, value);
            if (time > 0) {
                expire(key, time);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * @param key   键
     * @param value 值
     * @title: lSet
     * @description: //将list放入缓存
     * @author: yys1778
     * @date: Created in 2019/2/28 14:16
     * @throws:
     * @return: boolean
     */
    public boolean lSet(String key, List<Object> value) {
        try {
            redisTemplate.opsForList().rightPushAll(key, value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * @param key   键
     * @param value 值
     * @param time  时间(秒)
     * @title: lSet
     * @description: //将list放入缓存
     * @author: yys1778
     * @date: Created in 2019/2/28 14:16
     * @throws:
     * @return: boolean
     */
    public boolean lSet(String key, List<Object> value, long time) {
        try {
            redisTemplate.opsForList().rightPushAll(key, value);
            if (time > 0) {
                expire(key, time);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * @param key   键
     * @param index 索引
     * @param value 值
     * @title: lUpdateIndex
     * @description: //根据索引修改list中的某条数据
     * @author: yys1778
     * @date: Created in 2019/2/28 14:17
     * @throws:
     * @return: boolean
     */
    public boolean lUpdateIndex(String key, long index, Object value) {
        try {
            redisTemplate.opsForList().set(key, index, value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * @param key   键
     * @param count 移除多少个
     * @param value 值
     * @title: lRemove
     * @description: //移除N个值为value
     * @author: yys1778
     * @date: Created in 2019/2/28 14:17
     * @throws:
     * @return: long 移除的个数
     */
    public long lRemove(String key, long count, Object value) {
        try {
            Long remove = redisTemplate.opsForList().remove(key, count, value);
            return remove;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

}
