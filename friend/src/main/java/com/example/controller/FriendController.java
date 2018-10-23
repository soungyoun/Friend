package com.example.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.service.FriendService;

@RestController
//@CrossOrigin(origins = {"http://192.168.0.201:3000"}, maxAge = 3600)
@CrossOrigin(origins ="http://localhost:3000", maxAge = 3600)
public class FriendController {
   @Autowired
   private FriendService friendService;
   
// 3-2
   @RequestMapping("/users")
   public Map<String, Object> SearchFriends(
         @RequestParam(defaultValue = "0") Boolean filter, 
         @RequestParam(value="token") Integer userid,
         @RequestParam(defaultValue = "1") Integer page,
         @RequestParam(defaultValue = "0") Integer si,
         @RequestParam(defaultValue = "0") Integer gu, 
         @RequestParam(defaultValue = "0") Integer gender,
         @RequestParam(defaultValue = "0") Integer minAge, 
         @RequestParam(defaultValue = "100") Integer maxAge,
         @RequestParam(defaultValue = "0") Integer interest, 
         @RequestParam(defaultValue = "") String keyword) {
      // 3-2 설정필터로 유저 검색
      Map<String, Object> resultMap = new HashMap<String, Object>();
      if (filter) {
         resultMap = friendService.SearchFriends(userid, page, si, gu, gender, minAge, maxAge, interest, keyword);
      }
      // 3-1 추천친구
      else {
          resultMap = friendService.DefaultSearchFriends(userid, page);
       }
      return resultMap;
   }
   
   // 1-1-4 인기친구
   @RequestMapping(value="/loginfriend", method=RequestMethod.POST)
   public Map<String, Object> popularUsers(){
      return friendService.popularUsers();
   }
   

}