package org.webbuilder.utils.storage.instance.redis;

import org.webbuilder.utils.base.StringUtil;
import org.webbuilder.utils.storage.event.Finder;
import redis.clients.jedis.ShardedJedis;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by 浩 on 2015-09-10 0010.
 */
public class RedisQueueStorage extends RedisStorage {

    public RedisQueueStorage() {
    }

    public RedisQueueStorage(String name, Class type) {
        setName(name);
        setType(type);
    }

    public RedisQueueStorage(String name) {
        setName(name);
    }

    public String getKey() {
        return this.getName().concat("$queue");
    }

    @Override
    public Object get(Object key) {
        try (ShardedJedis jedis = getResource()) {
            if (StringUtil.isInt(key)) {
                int index = StringUtil.toInt(key);
                List<byte[]> bytes = jedis.lrange(getKey().getBytes(), index, index);
                List<Object> datas = new LinkedList<>();
                for (byte[] aByte : bytes) {
                    datas.add(getParser().deserialize(String.valueOf(key), aByte));
                }
                return datas;
            }
        }
        return null;
    }

    @Override
    public boolean put(Object key, Object val) {
        try (ShardedJedis jedis = getResource()) {
            jedis.rpush(getKey().getBytes(), getParser().serialize(String.valueOf(key), val));
        }
        return true;
    }

    @Override
    public boolean remove(Object key) {
        //not support
        return false;
    }

    @Override
    public int clear() {
        try (ShardedJedis jedis = getResource()) {
            Long len = jedis.del(getKey());
            return len.intValue();
        }
    }

    @Override
    public int size() {
        try (ShardedJedis jedis = getResource()) {
            Long len = jedis.llen(getKey());
            return len.intValue();
        }
    }

    private int range = 50;

    @Override
    public List find(Finder finder) {
        try (ShardedJedis jedis = getResource()) {
            Long len = jedis.llen(getKey());
            List<Object> datas = new LinkedList<>();
            int flag = 0;
            int count = 0;
            //循环队列长度
            for (int i = 0, lent = len.intValue(); i < lent + range; i += range) {
                if (finder.isOver()) break;//已结束
                //每次获取range个
                List<byte[]> pdata = jedis.lrange(getKey().getBytes(), flag, flag + range);
                List<Object> data = byte2Data(pdata, i);//反序列化
                //迭代本次获取
                for (int i1 = 0; i1 < data.size(); i1++) {
                    if (finder.isOver()) break;
                    Object o = data.get(i1);
                    if (finder.each(++count, String.valueOf(count), o)) {
                        datas.add(o);
                        jedis.ltrim(getKey(), 1, -1);//如果回掉返回true才执行队列删除
                    } else {
                        flag++;
                    }
                }
            }
            return datas;
        }
    }

    private List byte2Data(List<byte[]> pdata, Object key) {
        List<Object> datas = new LinkedList<>();
        for (byte[] bytes : pdata) {
            Object obj = getParser().deserialize(String.valueOf(key), bytes);
            datas.add(obj);
        }
        return datas;
    }

    public int getRange() {
        return range;
    }

    public void setRange(int range) {
        this.range = range;
    }
}
