package com.fh.flashman.netty.demo.pojo;

import lombok.Data;

@Data
public class QuitGroupResponsePacket extends Packet {

    private boolean success;

    private String groupId;

    private String reason;

    @Override
    public Byte getCommand() {
        return Command.QUIT_GROUP_RESPONSE;
    }
}
