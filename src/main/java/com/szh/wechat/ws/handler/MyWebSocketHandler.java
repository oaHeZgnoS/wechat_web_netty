package com.szh.wechat.ws.handler;

import com.alibaba.fastjson.JSONObject;
import com.szh.wechat.common.WeChatConstance.MsgKey;
import com.szh.wechat.common.WeChatConstance.MsgState;
import com.szh.wechat.common.WeChatConstance.MsgType;
import com.szh.wechat.ws.MyChannelHandlerPool;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelId;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MyWebSocketHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
    	log.info("与客户端建立连接，通道开启！");
        // 添加到channelGroup通道组
        MyChannelHandlerPool.channelGroup.add(ctx.channel());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
    	log.info("与客户端断开连接，通道关闭！");
        // 从channelGroup通道组移除
        MyChannelHandlerPool.channelGroup.remove(ctx.channel());
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
    	log.info("服务器收到客户端数据：{}", msg.text());
        String msgText = msg.text();
        JSONObject jsonObject = JSONObject.parseObject(msgText);
        if (MsgType.REGISTER.equals(jsonObject.getString(MsgKey.TYPE))) {
        	register(jsonObject, ctx.channel().id());
        } else {
            sendToSomeone(jsonObject);
        }
    }

    //ping、pong
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        //用于触发用户事件，包含触发读空闲、写空闲、读写空闲
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            if (event.state() == IdleState.ALL_IDLE) {
                Channel channel = ctx.channel();
                //关闭无用channel，以防资源浪费
                channel.close();
            }
        }
    }

    /**
     * 群发所有人
     */
    private void sendAllMessage(String message){
        //收到信息后，群发给所有channel
        MyChannelHandlerPool.channelGroup.writeAndFlush( new TextWebSocketFrame(message));
    }

    /**
     * 私聊
     */
    private void sendToSomeone(JSONObject map) {
    	String toId = map.getString(MsgKey.TO_ID);
    	ChannelId toChannelId = MyChannelHandlerPool.channelIdMap.get(toId);
		// 目标对象是否已下线,若ignore此元素，会造成自身session被关闭
		if (toChannelId != null) {
			Channel targetChannel = MyChannelHandlerPool.channelGroup.find(toChannelId);
			targetChannel.writeAndFlush(new TextWebSocketFrame(map.toString()));
		} else {
			log.warn("对方{}已下线", toId);
			JSONObject data = new JSONObject();
			data.put(MsgKey.FROM_ID, toId);
			data.put(MsgKey.STATE, MsgState.FAIL);
			data.put(MsgKey.MESSAGE, String.format("<发送失败>: 对方%s已下线", toId));
			String fromId = map.getString(MsgKey.FROM_ID);
			ChannelId fromChannelId = MyChannelHandlerPool.channelIdMap.get(fromId);
			Channel selfChannel = MyChannelHandlerPool.channelGroup.find(fromChannelId);
			selfChannel.writeAndFlush(new TextWebSocketFrame(data.toJSONString()));
		}
    }

    private void register(JSONObject map, ChannelId channelId) {
        String userId = map.getString(MsgKey.FROM_ID);
        MyChannelHandlerPool.channelIdMap.put(userId, channelId);
        log.info("{}注册后的连接：{}", userId, MyChannelHandlerPool.channelIdMap.keySet());
    }
}
