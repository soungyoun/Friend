package com.example.api;

import com.github.scribejava.core.builder.api.DefaultApi20;

public class GoogleLoginApi extends DefaultApi20{
	
	private static GoogleLoginApi instance;
	
 	public static GoogleLoginApi getInstance() {
 		if(instance == null) {
 			instance = new GoogleLoginApi();
 			return instance;
 		}
 		return instance;
 	}
 	
	@Override
	public String getAccessTokenEndpoint() {
//		access token �߱� url
		return "https://www.googleapis.com/oauth2/v4/token";
	}

	@Override
	protected String getAuthorizationBaseUrl() {
//		authrization token �߱� url
		return "https://accounts.google.com/o/oauth2/auth";
	}
}