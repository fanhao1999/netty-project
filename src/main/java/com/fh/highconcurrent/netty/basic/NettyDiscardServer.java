package com.fh.highconcurrent.netty.basic;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * @author hao.fan
 * @dateTime 2022/1/7 9:55 上午
 * @description
 */
public class NettyDiscardServer {

    private final int serverPort;

    ServerBootstrap sb = new ServerBootstrap();

    public NettyDiscardServer(int serverPort) {
        this.serverPort = serverPort;
    }

    public void runServer() {
        NioEventLoopGroup bossGroup = new NioEventLoopGroup(1);
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            sb.group(bossGroup, workerGroup);
            sb.channel(NioServerSocketChannel.class);
            sb.localAddress(serverPort);
            sb.option(ChannelOption.SO_KEEPALIVE, true);
            sb.option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
            sb.childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    ch.pipeline().addLast();
                }
            });
            ChannelFuture channelFuture = sb.bind().sync();
            System.out.println("服务器启动成功，监听端口：" + channelFuture.channel().localAddress());
            ChannelFuture closeFuture = channelFuture.channel().closeFuture();
            closeFuture.sync();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) {
        int port = 8000;
        new NettyDiscardServer(port).runServer();
    }
}
