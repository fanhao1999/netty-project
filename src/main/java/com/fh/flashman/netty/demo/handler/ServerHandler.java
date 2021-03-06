package com.fh.flashman.netty.demo.handler;

import com.fh.flashman.netty.demo.pojo.*;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.Date;

public class ServerHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf requestByteBuf = (ByteBuf) msg;

        // 解码
        Packet packet = PacketCodeC.INSTANCE.decode(requestByteBuf);
        // 判断是否是登录请求数据包
        if (packet.getClass() == LoginRequestPacket.class) {
            LoginRequestPacket loginRequestPacket = (LoginRequestPacket) packet;
            // 登录校验
            LoginResponsePacket loginResponsePacket = new LoginResponsePacket();
            if (valid(loginRequestPacket)) {
                // 校验成功
                loginResponsePacket.setSuccess(true);
            } else {
                // 校验失败
                loginResponsePacket.setSuccess(false);
                loginResponsePacket.setReason("账号密码校验失败!");
            }
            // 编码
            ByteBuf responseByteBuf = PacketCodeC.INSTANCE.encode(ctx.alloc(), loginResponsePacket);
            // 写数据
            ctx.channel().writeAndFlush(responseByteBuf);
        } else if (packet.getClass() == MessageRequestPacket.class) {
            // 处理消息
            MessageRequestPacket messageRequestPacket = (MessageRequestPacket) packet;
            System.out.println(new Date() + ": 收到客户端消息: " + messageRequestPacket.getMessage());

            MessageResponsePacket messageResponsePacket = new MessageResponsePacket();
            messageResponsePacket.setMessage("服务端回复【" + messageRequestPacket.getMessage() + "】");
            ByteBuf responseByteBuf = PacketCodeC.INSTANCE.encode(ctx.alloc(), messageResponsePacket);
            ctx.channel().writeAndFlush(responseByteBuf);
        }
    }

    private boolean valid(LoginRequestPacket loginRequestPacket) {
        return true;
    }
}
