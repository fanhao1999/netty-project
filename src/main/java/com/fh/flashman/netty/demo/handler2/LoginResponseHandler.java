package com.fh.flashman.netty.demo.handler2;

import com.fh.flashman.netty.demo.pojo.LoginResponsePacket;
import com.fh.flashman.netty.demo.pojo.Session;
import com.fh.flashman.netty.demo.util.SessionUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.Date;

public class LoginResponseHandler extends SimpleChannelInboundHandler<LoginResponsePacket> {

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, LoginResponsePacket loginResponsePacket) throws Exception {
        if (loginResponsePacket.isSuccess()) {
            String userId = loginResponsePacket.getUserId();
            String username = loginResponsePacket.getUsername();
            // 绑定session使客户端的channel上hasLogin方法为true
            SessionUtil.bindSession(new Session(userId, username), channelHandlerContext.channel());

            System.out.println("[" + username + "]登录成功，userId 为: " + userId);
        } else {
            System.out.println(new Date() + ": 客户端登录失败，原因：" + loginResponsePacket.getReason());
        }
    }
}
