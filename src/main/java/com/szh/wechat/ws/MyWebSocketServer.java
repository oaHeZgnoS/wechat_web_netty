package com.szh.wechat.ws;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.szh.wechat.handler.MyWebSocketHandler;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class MyWebSocketServer {

	@Value("${server.port}")
	private int port;

	public void start() throws Exception {
		EventLoopGroup bossGroup = new NioEventLoopGroup(1);
		EventLoopGroup workerGroup = new NioEventLoopGroup(200);
		try {
			ServerBootstrap boot = new ServerBootstrap().group(bossGroup, workerGroup)
					.channel(NioServerSocketChannel.class)
					.childHandler(new ChannelInitializer<SocketChannel>() {
						
						@Override
						protected void initChannel(SocketChannel ch) throws Exception {
							log.info("初始化SocketChannel..");
							// 将请求和应答消息编码或解码为Http消息
							ch.pipeline().addLast(new HttpServerCodec());
							// 将Http消息的多个部分组合成一条完整的Http消息
							ch.pipeline().addLast(new HttpObjectAggregator(8129));
							// 向客户端发送Html5文件，主要用于支持浏览器和客户端进行WebSocket通信
							ch.pipeline().addLast(new ChunkedWriteHandler());
							ch.pipeline().addLast(new WebSocketServerProtocolHandler("/test", "WebSocket", true, 65536 * 10));
							ch.pipeline().addLast(new MyWebSocketHandler());
						}
					});
			// 绑定端口，同步等待成功
			ChannelFuture f = boot.bind(port).sync();
			// 等待服务端监听端口关闭
			f.channel().closeFuture().sync();
		} finally {
			// 优雅退出，释放线程池资源
			bossGroup.shutdownGracefully();
			workerGroup.shutdownGracefully();
		}
	}

}
