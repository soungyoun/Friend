package com.example.controller;

import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.model.Chatmsg;
import com.example.model.Chatroom;
import com.example.service.ChatService;


@Controller
//@CrossOrigin(origins = "http://192.168.0.201:3000", maxAge = 3600)
@CrossOrigin(origins ="http://localhost:3000", maxAge = 3600)
public class ChatMessageController {

    @Autowired
    private ChatService chatservice ;
    
    @MessageMapping("/message")
    public void alarmSave(@RequestBody String message) throws InterruptedException, JSONException {
        //Thread.sleep(1000); // simulated delay
    	//System.out.println(SessionBindingListener.loginList);
		
    	chatservice.SendChatMsg(message);

//    	if (SessionBindingListener.loginList.get(receiveid) != null) {
//    		
//    	}
    	

    }
    
}