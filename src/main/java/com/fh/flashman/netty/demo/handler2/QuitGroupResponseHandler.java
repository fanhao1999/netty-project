package com.fh.flashman.netty.demo.handler2;

import com.fh.flashman.netty.demo.pojo.QuitGroupResponsePacket;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class QuitGroupResponseHandler extends SimpleChannelInboundHandler<QuitGroupResponsePacket> {

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, QuitGroupResponsePacket quitGroupResponsePacket) throws Exception {
        if (quitGroupResponsePacket.isSuccess()) {
            System.out.println("退出群[" + quitGroupResponsePacket.getGroupId() + "]成功!");
        } else {
            System.err.println("退出群[" + quitGroupResponsePacket.getGroupId() + "]失败，原因为：" + quitGroupResponsePacket.getReason());
        }
    }
}
