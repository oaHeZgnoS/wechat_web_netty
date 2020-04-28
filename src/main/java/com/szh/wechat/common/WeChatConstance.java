package com.szh.wechat.common;

public class WeChatConstance {

	public static class MsgType {
		/**
		 * 消息类型：注册进nettyChannel
		 */
		public static String REGISTER = "register";
	}

	public static class MsgKey {
		/**
		 * 消息类型
		 */
		public static String TYPE = "type";
		/**
		 * 消息发起者
		 */
		public static String FROM_ID = "fromId";
		/**
		 * 消息接收者
		 */
		public static String TO_ID = "toId";
		/**
		 * 消息文本
		 */
		public static String MESSAGE = "message";
		/**
		 * 目标好友的在线状态
		 */
		public static String STATE = "state";
	}

	public static class MsgState {
		/**
		 * 目标好友离线，消息发送失败的标志
		 */
		public static String FAIL = "fail";
	}
}
