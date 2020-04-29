package com.szh.wechat.ws;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import io.netty.channel.ChannelId;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

public class MyChannelHandlerPool {

    public MyChannelHandlerPool(){}

    /**
     * map: userId,ChannelId
     */
    public static Map<String, ChannelId> channelIdMap = new ConcurrentHashMap<>();

    public static ChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

}

