package com.example.api;

import com.github.scribejava.core.builder.api.DefaultApi20;

public class NaverLoginApi extends DefaultApi20{
	
	private static NaverLoginApi instance;
	
 	public static NaverLoginApi getInstance() {
 		if(instance == null) {
 			instance = new NaverLoginApi();
 			return instance;
 		}
 		return instance;
 	}
 	
	@Override
	public String getAccessTokenEndpoint() {
		return "https://nid.naver.com/oauth2.0/token?grant_type=authorization_code";
	}

	@Override
	protected String getAuthorizationBaseUrl() {
		return "https://nid.naver.com/oauth2.0/authorize";
	}

}
