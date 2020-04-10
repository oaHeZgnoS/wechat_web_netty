package com.szh.wechat.ws.handler;

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

	static {
		channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		log.info("客户端接入, 通道开启..");
		channelGroup.add(ctx.channel());
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
