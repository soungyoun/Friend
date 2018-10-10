package com.example.config;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;

import org.springframework.context.annotation.Configuration;

@Configuration
public class SessionBindingListener implements HttpSessionBindingListener {

	public static Map<String , Object> loginList = new HashMap<>();
	
	@Override
	public void valueBound(HttpSessionBindingEvent event) {
		if(loginList.get(event.getName()) != null) {
			HttpSession session = (HttpSession)loginList.get(event.getName());
			session.invalidate();
			loginList.remove(event.getName());
		}
		System.out.println("로그인: " + event.getName());
		loginList.put(event.getName(), event.getSession());

	}

	@Override
	public void valueUnbound(HttpSessionBindingEvent event) {
		System.out.println("로그아웃: " + event.getName());
		loginList.remove(event.getName());

	}


}
