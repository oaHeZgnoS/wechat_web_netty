package com.szh.wechat.ws;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NettyServerRunnable implements Runnable {

	@Override
	public void run() {
		try {
			new MyWebSocketServer().start();
		} catch (Exception e) {
			log.error("Error occurred while starting NettyServer! Caused by: {}", e.getMessage());
		}
	}

}
