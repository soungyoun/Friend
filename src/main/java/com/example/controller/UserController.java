package com.example.controller;

import java.io.IOException;
import java.util.Map;

import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.service.UserService;

@RestController
//@CrossOrigin(origins ="http://192.168.0.201:3000", maxAge = 3600)
@CrossOrigin(origins ="http://localhost:3000", maxAge = 3600)
public class UserController {
   
   @Autowired
   private UserService userService;
   
   @GetMapping("/me/info")
   public ResponseEntity<?> updateInfo(Integer userid){
      // 2-1-1 or  2-1-1 내 정보 수정 페이지
      return new ResponseEntity<>(userService.myInfo(userid), HttpStatus.OK);
   }
   
	//2-1-3 상세 정보 수정
	@PostMapping("/me/info")
	public ResponseEntity<Map<String, Object>> updateInfo(@RequestBody String json) throws JSONException, IOException {
		return new ResponseEntity<>(userService.updateInfo(json), HttpStatus.OK);
	}
	
	   ////2-2-1, 2-2-2 마이페이지에 필요한 리스트
	   @GetMapping("/me")
	   public Map<String, Object> mypage(Integer token, Integer id,
			   @RequestParam(defaultValue="false") Boolean memberlist,
			   @RequestParam(defaultValue="false") Boolean grouplist, Integer page) {
	      return userService.intoMyPage(token, id, memberlist, grouplist,  page);
	   }
   
	   //2-3-1 관심 유저 설정
	   @PostMapping("curious")
	   public ResponseEntity<?> setCurious(@RequestBody String json){
	      userService.setFriendState(json, 4);
	      return new ResponseEntity<>(HttpStatus.OK);
	   }
	   //2-3-2 관심 유저 해제
	   @DeleteMapping("curious")
	   public ResponseEntity<?> deleteCurious(@RequestBody String json){
	      userService.setFriendState(json, 7);
	      return new ResponseEntity<>(HttpStatus.OK);
	   }
	   // 2-4 유저 신고
	   @PostMapping("/report/user")
	   public ResponseEntity<?> reportUser(@RequestBody String json) {
	      userService.setFriendState(json, 5);
	      return new ResponseEntity<>(HttpStatus.OK);
	   }
	   // 2-5 유저 차단
	   @PostMapping("/block/user")
	   public ResponseEntity<?> blockUser(@RequestBody String json) {
	      userService.setFriendState(json, 6);
	      return new ResponseEntity<>(HttpStatus.OK);
	   }
	   //2-6-1 친구 요청
	   @PostMapping("/friend/request")
	   public ResponseEntity<?> requstFriend(@RequestBody String json){
		   System.out.println("requstFriend:" + json);
	      userService.setFriendState(json, 1);
	      return new ResponseEntity<>(HttpStatus.OK);
	   }
	   //2-7 친구 신청 승낙
	   @PostMapping("/friend/accept")
	   public ResponseEntity<?> acceptFriend(@RequestBody String json){
	      return new ResponseEntity<>(userService.setFriendState(json, 2), HttpStatus.OK);
	   }
	   //2-6-3 친구 거절 
	   @PostMapping("/friend/reject")
	   public ResponseEntity<?> rejectFriend(@RequestBody String json){
		   System.out.println("rejectFriend:" + json);
	      userService.setFriendState(json, 3);
	      return new ResponseEntity<>(HttpStatus.OK);
	   }
	   //2-6-2 (친구 신청 취소), 2-7(친구 삭제)
	   @DeleteMapping("/friend")
	   public ResponseEntity<?> deleteFriend(@RequestBody String json) throws JSONException{
	      
	      userService.setFriendState(json, 7);
	      return new ResponseEntity<>(HttpStatus.OK);
	   }
}