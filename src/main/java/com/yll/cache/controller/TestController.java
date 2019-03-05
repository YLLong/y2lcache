package com.yll.cache.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yll.cache.utils.RedisUtils;
import io.lettuce.core.RedisCommandExecutionException;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.data.annotation.Id;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.*;

/**
 * @className: TestController
 * @description: //测试请求类
 * @author: yys1778
 * @date: Created in 2019/2/28 14:27
 * @modify by: yys1778
 * @version: V1.0
 */
@RestController
@RequestMapping("/redis")
public class TestController {

    @Autowired
    RedisUtils redisUtils;
    @Autowired
    RestTemplate restTemplate;

    @GetMapping("/demo")
    public String demo() {
        System.out.println("==========演示字符串==========");
        redisUtils.set("demo1", "demo1value");
        System.out.println(redisUtils.get("demo1"));
        System.out.println("==========演示对象==========");
        User user = new User();
        user.setId(123);
        user.setName("张三");
        user.setAge(24);
        redisUtils.set("demo2", user);
        System.out.println(redisUtils.get("demo2"));
        System.out.println("==========演示map==========");
        Map map = new HashMap();
        map.put("key1", "value1");
        map.put("key2", "value2");
        map.put("key3", "value3");
        redisUtils.hmset("demo3", map);
        System.out.println(redisUtils.hmget("demo3"));
        System.out.println("==========演示list==========");
        List list = new ArrayList();
        list.add("list1");
        list.add("list2");
        list.add("list3");
        redisUtils.lSet("demo4", list);
        System.out.println(redisUtils.lGet("demo4", 0, -1));
        return "演示成功";
    }

    /**
     * @param jsonObject
     * @title: setCache
     * @description: //添加缓存
     * @author: yys1778
     * @date: Created in 2019/3/1 13:51
     * @throws:
     * @return: boolean
     */
    @PostMapping("/cache/add")
    public boolean setCache(@RequestBody JSONObject jsonObject) {
        String key = jsonObject.getString("key");
        Object value = jsonObject.get("value");
        String item = jsonObject.getString("item");
        Long time = jsonObject.getLong("time");
        boolean setFlag = false;
        if (StringUtils.isEmpty(item)) {
            if (value instanceof Map) {
                // map 格式数据缓存
                Map map = (Map) value;
                if (StringUtils.isEmpty(time)) {
                    setFlag = redisUtils.hmset(key, map);
                } else {
                    setFlag = redisUtils.hmset(key, map, time);
                }
            } else if (value instanceof List) {
                // list 格式数据缓存
                List list = (List) value;
                if (StringUtils.isEmpty(time)) {
                    setFlag = redisUtils.lSet(key, list);
                } else {
                    setFlag = redisUtils.lSet(key, list, time);
                }
            } else if (value instanceof Set) {
                // set 格式数据缓存
                Set set = (Set) value;
                if (StringUtils.isEmpty(time)) {
                    redisUtils.sSet(key, set);
                } else {
                    redisUtils.sSetAndTime(key, time, set);
                }
            } else {
                if (StringUtils.isEmpty(time)) {
                    setFlag = redisUtils.set(key, value);
                } else {
                    setFlag = redisUtils.set(key, value, time);
                }
            }
        } else {
            if (StringUtils.isEmpty(time)) {
                setFlag = redisUtils.hset(key, item, value);
            } else {
                setFlag = redisUtils.hset(key, item, value, time);
            }
        }
        return setFlag;
    }

    /**
     * @param key
     * @param item
     * @title: getCache
     * @description: //根据key key/item 获取缓存
     * @author: yys1778
     * @date: Created in 2019/3/1 13:59
     * @throws:
     * @return: java.lang.Object
     */
    @GetMapping("/cache/get")
    public String getCache(String key, @RequestParam(required = false) String type, @RequestParam(required = false) String item) {
        Object object = null;
        if (StringUtils.isEmpty(item)) {
            if (StringUtils.isEmpty(type)) {
                object = redisUtils.get(key);
                return JSONObject.toJSONString(object);
            }
            if (type.contains("Map")) {
                object = redisUtils.hmget(key);
            }
            if (type.contains("List")) {
                object = redisUtils.lGet(key, 0, -1);
            }
            if (type.contains("Set")) {
                object = redisUtils.sGet(key);
            }
        } else {
            object = redisUtils.hget(key, item);
        }
        return JSONObject.toJSONString(object);
    }

    /**
     * @param key
     * @param item
     * @title: deleteCache
     * @description: //删除缓存
     * @author: yys1778
     * @date: Created in 2019/3/1 14:10
     * @throws:
     * @return: void
     */
    @DeleteMapping("/cache/delete")
    public void deleteCache(String key, @RequestParam(required = false) String item) {
        if (StringUtils.isEmpty(item)) {
            redisUtils.del(key);
        } else {
            redisUtils.hdel(key, item);
        }
    }

    @GetMapping("/setCache2")
    public String setCache2() {
        JSONObject jSONObject = new JSONObject();
        jSONObject.put("key", "测试key");
        User user = new User();
        user.setId(123);
        user.setName("张三");
        user.setAge(24);
        Map map = new HashMap();
        map.put("key1", "value1");
        map.put("key2", "value2");
        map.put("key3", "value3");
        List list = new ArrayList();
        list.add("list1");
        list.add("list2");
        list.add("list3");
        jSONObject.put("value", list);
        jSONObject.put("item", "测试项");
        jSONObject.put("time", 1000);
        ResponseEntity<Boolean> booleanResponseEntity = restTemplate.postForEntity("http://127.0.0.1:8011/redis/setCache", jSONObject, Boolean.class);
        if (booleanResponseEntity.getBody()) {
            System.out.println("调用成功！");
            return "远程调用成功";
        }
        return "远程调用失败";
    }

    @GetMapping("/getCache")
    public Object getCache2() {
        Map<String, Object> map = new HashMap<>();
        map.put("key", "demo4");
        //map.put("type", "");
        //ResponseEntity<String> entity = restTemplate.getForEntity("http://127.0.0.1:8011/redis/cache/get?key={key}&type={type}", String.class, map);
        restTemplate.delete("http://127.0.0.1:8011/redis/cache/delete?key={key}", map);
        //return entity.getBody();
        return "删除成功";
    }

    @Data
    static class User {
        private Integer id;
        private String name;
        private Integer age;

        public User() {
        }
    }

    @Bean
    private RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder.build();
    }

}
