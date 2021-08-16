package com.szh.wechat.controller;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import com.szh.wechat.model.User;
import com.szh.wechat.service.UserService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class LoginController {

	@Autowired
	private UserService userService;

	@GetMapping("/login")
	public String login() {
		return "login";
	}

	@PostMapping("/logout")
	public String logout(@CookieValue("msgKey") String msgKey) {
		// userService.removeFromRedis(msgKey);
		return "login";
	}

	@PostMapping("/doLogin")
	public void dispatchLogin(HttpServletRequest req, HttpServletResponse resp) throws Exception {
		String account = req.getParameter("account");
		String password = req.getParameter("password");
		User user = userService.selectByAccountAndPassword(account, password);
		if (user != null) {
			log.debug("用户{}登陆验证成功", user);
			Cookie cookie = new Cookie("userId", user.getId() + "");
			// 负数代表浏览器关闭则删除cookie
			cookie.setMaxAge(-1);
			cookie.setPath("/");
			resp.addCookie(cookie);
			resp.setStatus(HttpStatus.FOUND.value());
			resp.setHeader("location", req.getContextPath() + "/main");
		} else {
			// 验证失败
			log.info("用户{}登陆验证失败", account);
			resp.setStatus(HttpStatus.FOUND.value());
			resp.setHeader("location", req.getContextPath() + "/fail");
			return;
		}
	}

	@GetMapping("/main")
	public String toMain() {
		return "main";
	}

	@GetMapping("/fail")
	public String toFail() {
		return "fail";
	}

}
