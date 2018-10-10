//package com.example.config;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
//import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
//
//@Configuration
//public class InterceptorConfig implements WebMvcConfigurer {
//
//	@Autowired
//	private Interceptor httpInterceptor;
//	
//	@Override
//	public void addInterceptors(InterceptorRegistry registry) {
//		registry.addInterceptor(httpInterceptor)
//				.addPathPatterns("/**").excludePathPatterns("/login"
//										, "/callback", "/join", "/email/check", "/email/validate"
//										, "/me/info", "/me", "/confirm");
//	}
//	
//	
//
//}
