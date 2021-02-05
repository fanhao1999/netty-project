package com.fh.flashman.netty.demo.handler2;

import com.fh.flashman.netty.demo.pojo.MessageResponsePacket;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class MessageResponseHandler extends SimpleChannelInboundHandler<MessageResponsePacket> {

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, MessageResponsePacket messageResponsePacket) throws Exception {
        String fromUserId = messageResponsePacket.getFromUserId();
        String fromUsername = messageResponsePacket.getFromUsername();
        String message = messageResponsePacket.getMessage();
        System.out.println(fromUserId + ":" + fromUsername + " -> " + message);
    }
}
