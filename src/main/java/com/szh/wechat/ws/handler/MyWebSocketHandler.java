package com.szh.wechat.ws.handler;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.websocket.Session;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.concurrent.GlobalEventExecutor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MyWebSocketHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

	public static ChannelGroup channelGroup;

	/**
     * 存放所有在线的客户端userId->session
     */
    private static Map<String, Channel> clients = new ConcurrentHashMap<>();

	static {
		channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		log.info("客户端接入, 通道开启..");
		channelGroup.add(ctx.channel());
		log.info("onOpen前存活: {}", clients.keySet());
    	//String userId = requestParameterMap.get("fromId").get(0);
        //log.info("用户上线了, userId为:{}, sessionId为:{}", userId, session.getId());
        //将新用户存入在线的组
        //clients.put(userId, session);
        log.info("onOpen后存活: {}", clients.keySet());
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		log.info("客户端断开, 通道关闭..");
		channelGroup.remove(ctx.channel());
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
		log.info("服务器收到数据: {}", msg.text());
		// TODO 自己的消息转发业务
	}

	/**
	 * 给固定的人发消息
	 */
	private void sendMessage(ChannelHandlerContext ctx, TextWebSocketFrame msg) {
		String msg2 = "你好，" + ctx.channel().localAddress() + "给固定的人发消息";
		ctx.channel().writeAndFlush(new TextWebSocketFrame(msg2));
	}

	/**
	 * 给所有客户端发群组消息
	 */
	private void sendAllMessage(ChannelHandlerContext ctx, TextWebSocketFrame msg) {
		String msg2 = "你好，这一条是群发消息";
		channelGroup.writeAndFlush(new TextWebSocketFrame(msg2));
	}

}
