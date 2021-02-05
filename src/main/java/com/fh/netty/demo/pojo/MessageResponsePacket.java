package com.fh.netty.demo.pojo;

import lombok.Data;

@Data
public class MessageResponsePacket extends Packet {

    private String message;

    private String fromUserId;

    private String fromUsername;

    @Override
    public Byte getCommand() {
        return Command.MESSAGE_RESPONSE;
    }
}
