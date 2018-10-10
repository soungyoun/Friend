package com.example.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.model.Club;
import com.example.model.Clubnotice;
import com.example.service.ClubService;

@RestController
//@CrossOrigin(origins = "http://192.168.0.201:3000", maxAge = 3600)
@CrossOrigin(origins ="http://192.168.0.200:3000", maxAge = 3600)
public class ClubController {
   
   @Autowired
   private ClubService clubService;
   

   //그룹검색 보내줌
   
   @RequestMapping("/groups")
   public Map<String,Object> searchGroup(@RequestParam(defaultValue="1") int page,@RequestParam(defaultValue="false") boolean filter,String keyword,
         @RequestParam(defaultValue="0")int searchOption,@RequestParam(defaultValue="0")int si,@RequestParam(defaultValue="0")int gu,
         @RequestParam(defaultValue="0")int gender,@RequestParam(defaultValue="0")int minAge,@RequestParam(defaultValue="0")int maxAge,
         @RequestParam(defaultValue="0")int interest,@RequestParam(defaultValue="0") int token){
      Map<String,Object> map=new HashMap<>();
      map.put("groups", clubService.searchGroups(page, filter, keyword, searchOption, si, gu, gender, minAge, maxAge, interest,token));
   return map;
   }
   //그룹상세정보 보내줌
   @RequestMapping("/group")
   public Map<String,Object> groupinfo(int userid,int seq) throws IOException{
	   Map<String,Object> map=new HashMap<>();
	   map.put("group",  clubService.groupInfo(userid, seq));
      return map;
   }
   
   //ModelAttribute : 모델클래스로 데이터 가져올때
   //4-2 그룹개설
   @PostMapping("/group")
   public ResponseEntity<Integer> creategroup(@ModelAttribute Club club,int userid,
          MultipartFile[] files,Boolean[] mainImgYN,int[] interests) {
      
      int a=clubService.createGroup(userid, club.getName(), club.getContent(), interests,
            club.getCity(), club.getGu(), club.getAgestart(), club.getAgeend(),
            club.getGender(), club.getMaxcount(), files,mainImgYN);
      if(a==1) {
      return new ResponseEntity<Integer>(a,HttpStatus.OK);
      }
      else {
         return new ResponseEntity<Integer>(a,HttpStatus.BAD_REQUEST);
      }
   }
   
   //putMapping -그룹수정할때
   //4-2-2
   @PutMapping("/group")
   //@RequestMapping(method=RequestMethod.PUT, value="/group")
   public ResponseEntity<Map<String,Object>> updategroup(@ModelAttribute Club club,int userid,
          MultipartFile[] files,Boolean[] mainImgYN,int[] interests) {
      Map<String,Object> map=new HashMap<String, Object>();
      
      int result=clubService.updateGroup(userid, club.getName(), club.getContent(), interests, club.getCity(), club.getGu(),
            club.getAgestart(), club.getAgeend(), club.getGender(), club.getMaxcount(), files, mainImgYN, club.isYn(),club.getClubid());
      if(result==1) {
         map.put("msg", "정보수정 완료");
         return new ResponseEntity<Map<String,Object>>(map,HttpStatus.OK);
      }
      else {
         map.put("msg","정보수정 실패! 방장이 아닐걸요?");
         return new ResponseEntity<Map<String,Object>>(map,HttpStatus.BAD_REQUEST);
      }
      
   }
   
   //4-4-1 그룹 가입 신청 폼으로 이동
   //Request : ?clubid=integer&token=String
   //Response : "그룹 가입조건, 내 정보 ,그룹 메세지"
   @RequestMapping("/group/join")
   public Map<String,Object> groupJoin(int clubid,int userid) {
      return clubService.groupJoin(clubid, userid);
   }
   
   //4-4-2 그룹 가입 신청
   //request : ?clubid=integer&token=String
   //response : """{ success : Boolean } 가입성공:OK , 가입실패:BAD_REQUEST"""
   @PostMapping("/group/join")
   public ResponseEntity<Boolean> groupJoin2(int clubid,int userid,String message) {
      //가입신청 성공하면 clubuser테이블에 추가,마스터에게 알람 이 가고 실패시 Bad_Request
      int result=clubService.groupJoin2(clubid, userid,message);
      if(result==1) {
         return new ResponseEntity<Boolean>(true,HttpStatus.OK);
      }
      else {
         return new ResponseEntity<Boolean>(false,HttpStatus.BAD_REQUEST);
      }
      
      
   }
   //4-4-3 그룹 가입 신청허락 폼
   @RequestMapping("/group/accept")
   public Map<String,Object> allowGroupJoin(int clubid) {
      return clubService.allowGroupJoin(clubid);
   }
   
   //4-4-4 그룹 가입 신청 허락/거절
      @PostMapping("/group/accept")
      public ResponseEntity<String> allowGroupJoin2(int clubid,boolean yn,int userid) {
         int result=clubService.allowGroupJoin2(clubid, yn, userid);
         String msg="";
         if(result==1) {
            msg="그룹가입 수락";
            return new ResponseEntity<String>(msg,HttpStatus.OK);
         }
         else {
            msg="그룹가입 거절";
            return new ResponseEntity<String>(msg,HttpStatus.BAD_REQUEST);
         }
      }
   
   
   //4-5-1 : 그룹 공지글 작성폼 보이기
   //request : ?clubid=integer&token=String
   //response : 그룹의 최근 작성된 글
   @RequestMapping("/group/notice")
   public Map<String,Object> recentGroupNotice(int clubid,int userid) {
      return clubService.recentGroupNotice(clubid, userid);
   }
   
   //4-5-2 : 그룹 공지글 작성하기
   //request : 공지글 내용
   //response : """{ success : Boolean } 가입성공:OK , 가입실패:BAD_REQUEST"""
   @PostMapping("/group/notice")
   public ResponseEntity<String> writeGroupNotice(Clubnotice clubnotice) {
      int result=clubService.writeGroupNotice(clubnotice);
      if(result==1) {
         return new ResponseEntity<String>("작성성공",HttpStatus.OK);
      }
      else {
         return new ResponseEntity<String>("작성실패",HttpStatus.BAD_REQUEST);
      }
   }
   //4-6 : 그룹 공지글 댓글
   //request : 댓글 내용
   //response : { success : Boolean } 가입성공:OK , 가입실패:BAD_REQUEST
   @PostMapping("/group/noticecom")
   public ResponseEntity<String> noticecom(int noticeid,int userid,boolean attendyn,String content) {
   int result=clubService.noticecom(noticeid, userid, attendyn, content);
      if(result==1) {
         return new ResponseEntity<String>("댓글작성완료",HttpStatus.OK);
      }
      else {
         
         return new ResponseEntity<String>("댓글작성실패",HttpStatus.BAD_REQUEST);
      }
   }
   //4-7 : 그룹 매니저 권한 변경
   @PostMapping("/group/manager")
   public ResponseEntity<String> changeManager(int clubid,int myid,int userid,int auth) {
      int result=clubService.changeManager(clubid, myid, userid, auth);
      if(result==1) {
         return new ResponseEntity<String>("변경완료",HttpStatus.OK);
      }
      else {
         return new ResponseEntity<String>("변경실패!",HttpStatus.BAD_REQUEST);
      }
   }
   
}












