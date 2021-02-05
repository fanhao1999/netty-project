package com.fh.flashman.netty.demo.pojo;

import com.alibaba.fastjson.JSON;

public class JSONSerializer implements Serializer {

    public interface SerializerAlgorithm {
        /**
         * json 序列化标识
         */
        byte JSON = 1;
    }

    @Override
    public byte getSerializerAlgorithm() {
        return SerializerAlgorithm.JSON;
    }

    @Override
    public byte[] serialize(Object object) {
        return JSON.toJSONBytes(object);
    }

    @Override
    public <T> T deserialize(Class<T> clazz, byte[] bytes) {
        return JSON.parseObject(bytes, clazz);
    }
}
