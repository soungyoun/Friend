package com.example.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.example.UserFunction;
import com.example.model.User;
import com.example.repository.UserRepository;
import com.example.service.ChatService;
import com.example.service.FriendService;
import com.example.service.LoginService;
import com.example.service.TestService;
import com.example.service.UserService;

@Controller
//@CrossOrigin(origins = "http://192.168.0.201:3000", maxAge = 3600)
public class TestController {

	@Autowired
	private TestService testService;
	
	@Autowired
	private ChatService chatService;

	@Autowired
	private LoginService loginService;
	
	@Autowired
	private UserService userService;
	@Autowired
	private FriendService friendService;

	   @Autowired
	   private UserRepository userRepository;

	   
	   //test
	//1-1-2 : 마스터 정보 
	//1-1-3 : 인기그룹
	@GetMapping("/master")
	public ResponseEntity<Map<String, Object>> Master() {
		return new ResponseEntity<>(userService.popularUsers(), HttpStatus.BAD_REQUEST);
	}
	
	//채팅방 리스트 보이기
	@GetMapping("/chat")
	public ResponseEntity<Map<String, Object>> Chat(int loginid) {
		return new ResponseEntity<>(chatService.ListChatRoom(loginid), HttpStatus.BAD_REQUEST);
	}

	//알람 소켓 전송
	@GetMapping("/alarm")
	public ResponseEntity<String> Alarm() {
		testService.SendAlarm();
		return new ResponseEntity<>("OK", HttpStatus.BAD_REQUEST);
	}
	
	@GetMapping("/test")
	public ResponseEntity<String> Test() throws InterruptedException {
		System.out.println("test");
		testService.SaveTest();
		return new ResponseEntity<>("OK", HttpStatus.BAD_REQUEST);
	}

//	@GetMapping("/test2")
//	public ResponseEntity<?> Test2(HttpServletRequest request) throws InterruptedException {
////		System.out.println("test2");
////		intoMyPage(Integer userid, Integer seq, Boolean memberlist, Boolean grouplist, Integer page)
////		//return new ResponseEntity<>(loginService.isNewbie("kim@gmail.com"), HttpStatus.BAD_REQUEST);
////		return new ResponseEntity<>(userService.intoMyPage("kim@gmail.com"), HttpStatus.BAD_REQUEST);
//	}
			
	

	   
}
