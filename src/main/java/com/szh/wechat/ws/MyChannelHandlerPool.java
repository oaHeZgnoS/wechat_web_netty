package com.szh.wechat.ws;

import java.util.HashMap;
import java.util.Map;

import io.netty.channel.ChannelId;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

public class MyChannelHandlerPool {

    public MyChannelHandlerPool(){}

    /**
     * map: userId,ChannelId
     */
    public static Map<String, ChannelId> channelIdMap = new HashMap<>();

    public static ChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

}

