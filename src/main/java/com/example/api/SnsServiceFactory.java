package com.example.api;

import java.math.BigInteger;
import java.security.SecureRandom;

import org.springframework.stereotype.Component;

import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.oauth.OAuth20Service;

@Component
public class SnsServiceFactory {
	
	public final static String STATE = new BigInteger(130, new SecureRandom()).toString();
	
	public OAuth20Service naver() {
		return new ServiceBuilder()
					.apiKey("Bc2zvL52e8sCG5i1R9K1")
					.apiSecret("8ZI4NtryKk")
					.callback("http://192.168.0.200:8080/callback")
					.state(STATE)
					.build(NaverLoginApi.getInstance());
	}
	
	public OAuth20Service facebook() {
		return new ServiceBuilder()
					.apiKey("247715745847061")
					.apiSecret("bbf4e9321946387b9d3beb1ade67e8c6")
					.callback("http://192.168.0.200:8080/callback")
					.state(STATE)
					.build(FacebookLoginApi.getInstance());
	}
	
	public OAuth20Service google() {
		return new ServiceBuilder()
					.apiKey("620369664252-c6agita2dniti7h9f01hhd21ufoqp715.apps.googleusercontent.com")
					.apiSecret("SeGOSb8hzZwerAh0_E75rYcK")
					.callback("http://192.168.0.200.xip.io:8080/callback")
					.scope("https://www.googleapis.com/auth/plus.login") 
					.state(STATE)
					.build(GoogleLoginApi.getInstance());
	}
	
}
