package com.fh.flashman.netty.demo.pojo;

import lombok.Getter;

@Getter
public enum CommandEnum {

    LOGIN_REQUEST(Command.LOGIN_REQUEST, LoginRequestPacket.class),
    LOGIN_RESPONSE(Command.LOGIN_RESPONSE, LoginResponsePacket.class),
    MESSAGE_REQUEST(Command.MESSAGE_REQUEST, MessageRequestPacket.class),
    MESSAGE_RESPONSE(Command.MESSAGE_RESPONSE, MessageResponsePacket.class),
    CREATE_GROUP_REQUEST(Command.CREATE_GROUP_REQUEST, CreateGroupRequestPacket.class),
    CREATE_GROUP_RESPONSE(Command.CREATE_GROUP_RESPONSE, CreateGroupResponsePacket.class),
    JOIN_GROUP_REQUEST(Command.JOIN_GROUP_REQUEST, JoinGroupRequestPacket.class),
    JOIN_GROUP_RESPONSE(Command.JOIN_GROUP_RESPONSE, JoinGroupResponsePacket.class),
    QUIT_GROUP_REQUEST(Command.QUIT_GROUP_REQUEST, QuitGroupRequestPacket.class),
    QUIT_GROUP_RESPONSE(Command.QUIT_GROUP_RESPONSE, QuitGroupResponsePacket.class),
    LIST_GROUP_MEMBERS_REQUEST(Command.LIST_GROUP_MEMBERS_REQUEST, ListGroupMembersRequestPacket.class),
    LIST_GROUP_MEMBERS_RESPONSE(Command.LIST_GROUP_MEMBERS_RESPONSE, ListGroupMembersResponsePacket.class),
    GROUP_MESSAGE_REQUEST(Command.GROUP_MESSAGE_REQUEST, GroupMessageRequestPacket.class),
    GROUP_MESSAGE_RESPONSE(Command.GROUP_MESSAGE_RESPONSE, GroupMessageResponsePacket.class),
    HEARTBEAT_REQUEST(Command.HEARTBEAT_REQUEST, HeartBeatRequestPacket.class),
    HEARTBEAT_RESPONSE(Command.HEARTBEAT_RESPONSE, HeartBeatResponsePacket.class);

    private byte commond;

    private Class<? extends Packet> requestType;

    CommandEnum(byte commond, Class<? extends Packet> requestType) {
        this.commond = commond;
        this.requestType = requestType;
    }
}
