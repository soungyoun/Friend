package com.example.controller;

import java.io.UnsupportedEncodingException;
import java.util.Map;

import javax.mail.MessagingException;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.config.SessionBindingListener;
import com.example.model.User;
import com.example.service.LoginService;

@RestController
//@CrossOrigin(origins = "http://192.168.0.201:3000", maxAge = 3600)
@CrossOrigin(origins ="http://192.168.0.200:3000", maxAge = 3600)
public class LoginController {
	
	@Autowired
	private LoginService loginService;
	
	//1-1-1 :로그인  
   //일반 로그인
	@PostMapping("/login")
	public ResponseEntity<?> login(@RequestBody User user, HttpSession session) {
		String message = null;
		switch(loginService.login(user.getEmail(), user.getPw(), session)) {
			case 0:
				session.setAttribute(String.valueOf(user.getUserid()), new SessionBindingListener());
				return new ResponseEntity<>(loginService.isNewbie(user.getEmail()), HttpStatus.OK);
			case 1:	message = "존재하지 않는 아이디입니다."; break;
			case 2: message = "비밀번호가 일치하지 않습니다."; break;
			case 3: message = "네트워크 에러."; break;
			case 4: message = "인증되지 않은 아이디입니다."; break;
		}
		return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
	}
	
	//1-2 : 비밀번호 찾기
	@GetMapping("/password")
	public ResponseEntity<String> password(String email) throws UnsupportedEncodingException, MessagingException {
		if(loginService.password(email)) {
			return new ResponseEntity<String>("임시 비밀번호를 가입하신 이메일로 발송하였습니다.", HttpStatus.OK);
		}
		return new ResponseEntity<>("존재하지 않는 회원입니다.", HttpStatus.BAD_REQUEST);
	}

	//1-3 : 사이트 소개
	@GetMapping("/info")
	public ResponseEntity<Map<String, Object>> info()  {
		return new ResponseEntity<>(loginService.GetInfo(),  HttpStatus.BAD_REQUEST);
		
	}
	
	//1-5-2 : 중복 아이디 등록 방지
	@GetMapping(value="/email/check")
	public ResponseEntity<String> mailcheck(String email) {
		if(loginService.emailcheck(email) == true) {
			return null;
		} else {
			return new ResponseEntity<String>("이미 존재하는 이메일입니다.", HttpStatus.BAD_REQUEST);
		}
	}
	
	//1-5-1 : 회원가입
	@PostMapping("/join")
	public ResponseEntity<String> join(@RequestBody User user) throws UnsupportedEncodingException, MessagingException {
		
		loginService.join(user.getEmail(), user.getPw());
		return new ResponseEntity<>(HttpStatus.OK);
	}


}
