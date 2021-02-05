package com.fh.netty.demo.handler2;

import com.fh.netty.demo.pojo.GroupMessageRequestPacket;
import com.fh.netty.demo.pojo.GroupMessageResponsePacket;
import com.fh.netty.demo.util.SessionUtil;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;

@ChannelHandler.Sharable
public class GroupMessageRequestHandler extends SimpleChannelInboundHandler<GroupMessageRequestPacket> {

    public static final GroupMessageRequestHandler INSTANCE = new GroupMessageRequestHandler();

    protected GroupMessageRequestHandler() {
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, GroupMessageRequestPacket groupMessageRequestPacket) throws Exception {
        // 1.拿到 groupId 构造群聊消息的响应
        String fromGroupId = groupMessageRequestPacket.getToGroupId();
        GroupMessageResponsePacket groupMessageResponsePacket = new GroupMessageResponsePacket();
        groupMessageResponsePacket.setFromGroupId(fromGroupId);
        groupMessageResponsePacket.setMessage(groupMessageRequestPacket.getMessage());
        groupMessageResponsePacket.setFromUser(SessionUtil.getSession(channelHandlerContext.channel()));
        // 2. 拿到群聊对应的 channelGroup，写到每个客户端
        ChannelGroup channelGroup = SessionUtil.getChannelGroup(fromGroupId);
        channelGroup.writeAndFlush(groupMessageResponsePacket);
    }
}
