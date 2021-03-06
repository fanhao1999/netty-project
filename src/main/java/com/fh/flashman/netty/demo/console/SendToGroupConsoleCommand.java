package com.fh.flashman.netty.demo.console;

import com.fh.flashman.netty.demo.pojo.GroupMessageRequestPacket;
import io.netty.channel.Channel;

import java.util.Scanner;

public class SendToGroupConsoleCommand implements ConsoleCommand {

    @Override
    public void exec(Scanner scanner, Channel channel) {
        System.out.print("发送消息给某个群组：");

        String toGroupId = scanner.next();
        String message = scanner.next();
        channel.writeAndFlush(new GroupMessageRequestPacket(toGroupId, message));
    }
}
