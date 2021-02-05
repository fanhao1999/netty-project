package com.fh.netty.demo.pojo;

import lombok.Getter;

@Getter
public enum SerializerEnum {

    JSON_SERIALIZER(JSONSerializer.SerializerAlgorithm.JSON, new JSONSerializer());

    private byte serializeAlgorithm;

    private Serializer serializer;

    SerializerEnum(byte serializeAlgorithm, Serializer serializer) {
        this.serializeAlgorithm = serializeAlgorithm;
        this.serializer = serializer;
    }
}
