package com.fh.flashman.netty.demo.handler2;

import com.fh.flashman.netty.demo.pojo.LoginRequestPacket;
import com.fh.flashman.netty.demo.pojo.LoginResponsePacket;
import com.fh.flashman.netty.demo.pojo.Session;
import com.fh.flashman.netty.demo.util.SessionUtil;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.UUID;

// 1. 加上注解标识，表明该 handler 是可以多个 channel 共享的
@ChannelHandler.Sharable
public class LoginRequestHandler extends SimpleChannelInboundHandler<LoginRequestPacket> {

    // 2. 构造单例
    public static final LoginRequestHandler INSTANCE = new LoginRequestHandler();

    protected LoginRequestHandler() {
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, LoginRequestPacket loginRequestPacket) throws Exception {
        // 登录校验
        LoginResponsePacket loginResponsePacket = new LoginResponsePacket();
        if (valid(loginRequestPacket)) {
            // 校验成功
            loginResponsePacket.setSuccess(true);
            String userId = randomUserId();
            String username = loginRequestPacket.getUsername();
            loginResponsePacket.setUserId(userId);
            loginResponsePacket.setUsername(username);
            SessionUtil.bindSession(new Session(userId, username), channelHandlerContext.channel());

            System.out.println("[" + username + "]登录成功");
        } else {
            // 校验失败
            loginResponsePacket.setSuccess(false);
            loginResponsePacket.setReason("账号密码校验失败!");
        }
        // 写数据
        channelHandlerContext.channel().writeAndFlush(loginResponsePacket);
    }

    // 用户断线之后取消绑定
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        SessionUtil.unBindSession(ctx.channel());
    }

    private boolean valid(LoginRequestPacket loginRequestPacket) {
        return true;
    }

    private static String randomUserId() {
        return UUID.randomUUID().toString().split("-")[0];
    }
}
