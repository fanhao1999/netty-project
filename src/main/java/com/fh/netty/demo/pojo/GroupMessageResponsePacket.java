package com.fh.netty.demo.pojo;

import lombok.Data;

@Data
public class GroupMessageResponsePacket extends Packet {

    private String message;

    private Session fromUser;

    private String fromGroupId;

    @Override
    public Byte getCommand() {
        return Command.GROUP_MESSAGE_RESPONSE;
    }
}
