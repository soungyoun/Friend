package com.example.api;

import com.github.scribejava.core.builder.api.DefaultApi20;

public class FacebookLoginApi extends DefaultApi20{
	
	private static FacebookLoginApi instance;
	
 	public static FacebookLoginApi getInstance() {
 		if(instance == null) {
 			instance = new FacebookLoginApi();
 			return instance;
 		}
 		return instance;
 	}
 	
	@Override
	public String getAccessTokenEndpoint() {
//		access token �߱� url
		return "https://graph.facebook.com/v3.1/oauth/access_token";
	}

	@Override
	protected String getAuthorizationBaseUrl() {
//		authorization �ڵ� �߱� �ޱ� ���� ����â?
		return "https://www.facebook.com/v3.1/dialog/oauth";
	}

}
