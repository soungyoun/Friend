//package com.example.config;
//
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import javax.servlet.http.HttpSession;
//
//import org.springframework.stereotype.Component;
//import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
//
//@Component
//public class Interceptor extends HandlerInterceptorAdapter {
//
//	@Override
//	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
//			throws Exception {
//		response.setHeader("Pragma", "No-Cache");
//		response.setHeader("Cache-Control", "No-Cache"); // 캐쉬 없애기
//		
//		HttpSession session = request.getSession();
//		String email = (String) session.getAttribute("email");
//		
//		if(email != null && request.getRequestURI().equals("/loginForm.html")) {
//			response.sendRedirect("/home");
//			return false;
//		} else if (request.getRequestURI().equals("/loginForm.html") && email == null) {
//			response.sendRedirect("/loginForm");
//			return false;
//		}
//		else {
//			return true;
//		}
//
//	}
//
//}
