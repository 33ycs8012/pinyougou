package com.pinyougou.user.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/login")
public class LoginController {

	@RequestMapping("/name")
	public Map showName() {
		Map map = new HashMap<>();
		String name = SecurityContextHolder.getContext().getAuthentication().getName();
		System.out.println("用户"+name+"经过controller层");
		map.put("loginName",name);
		return map;
	}

}