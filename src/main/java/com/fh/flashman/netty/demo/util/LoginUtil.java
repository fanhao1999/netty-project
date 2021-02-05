package com.fh.flashman.netty.demo.util;

import com.fh.flashman.netty.demo.pojo.Attributes;
import io.netty.channel.Channel;

public class LoginUtil {

    public static void markAsLogin(Channel channel) {
        channel.attr(Attributes.LOGIN).set(true);
    }

    public static boolean hasLogin(Channel channel) {
        return channel.attr(Attributes.LOGIN).get() != null;
    }
}
