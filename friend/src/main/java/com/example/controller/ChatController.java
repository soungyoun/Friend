package com.example.controller;

import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.model.Chatroom;
import com.example.service.ChatService;

@RestController
//@CrossOrigin(origins = "http://192.168.0.201:3000", maxAge = 3600)
@CrossOrigin(origins ="http://192.168.0.200:3000", maxAge = 3600)
public class ChatController {
	@Autowired
	private ChatService chatService;
	
	//채팅방 만들기
	@PostMapping("/contact")
	public  ResponseEntity<?>  makeChatRoom(@RequestBody String json) throws JSONException {
		return new ResponseEntity<>(chatService.CreateChatRoom(json), HttpStatus.OK);
	}

	//알림메시지 읽음 체크
	@PostMapping("/notification")
	public ResponseEntity<?> AlarmRead(@RequestBody String json) throws JSONException {
		return new ResponseEntity<>(chatService.SaveAlarmRead(json), HttpStatus.OK);
		
	}

	//채팅메시지 읽음 체크
	@PostMapping("/message")
	public ResponseEntity<?> ChatRead(@RequestBody String json) throws JSONException {
		return new ResponseEntity<>(chatService.SaveChatRead(json), HttpStatus.OK);
		
	}
	
//	//채팅방 리스트 보이기
//	@GetMapping("/listChatRoom")
//	public String listChatRoom(int loginid, Model model) {
//		model.addAttribute("rooms", chatService.ListChatRoom(loginid));
//		model.addAttribute("loginid", loginid);
//		return "listChatRoom";
//	}
//	
//	//채팅방 리스트 보이기 테스트
//	@GetMapping("/listChatRoomTest")
//	public String listChatRoomTest(int loginid, Model model) {
//		model.addAttribute("roomsGroup", chatService.ListChatRoom_group(loginid));
//		model.addAttribute("roomsPersonal", chatService.ListChatRoom_personal(loginid));
//		model.addAttribute("loginid", loginid);
//		return "listChatRoom";
//	}

	//멤버 방 입장
	@GetMapping("/ChatRoom")
	public String ChatRoomComeIn(int roomid, int clubid, int userid, Model model) {
		chatService.ChatComeIn(roomid, clubid, userid);		
		model.addAttribute("roomid" , roomid);
		model.addAttribute("clubid" , clubid);
		model.addAttribute("userid" , userid);
		return "room";
	}
}
