package com.example.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.model.Club;
import com.example.model.Clubnotice;
import com.example.service.ClubService;
import com.mysql.fabric.xmlrpc.base.Array;





@RestController
@CrossOrigin(origins = "http://localhost:3000", maxAge = 3600)
public class ClubController {
   
   @Autowired
   private ClubService clubService;
   

   //그룹검색 보내줌
   
   @RequestMapping("/groups")
   public Map<String,Object> searchGroup(@RequestParam(defaultValue="1") int page,@RequestParam(defaultValue="false") boolean filter,String keyword,
         @RequestParam(defaultValue="0")int searchOption,@RequestParam(defaultValue="0")int si,@RequestParam(defaultValue="0")int gu,
         @RequestParam(defaultValue="0")int gender,@RequestParam(defaultValue="0")int minAge,@RequestParam(defaultValue="0")int maxAge,
         @RequestParam(defaultValue="0")int interest,@RequestParam(defaultValue="0") int token){
      
      return clubService.searchGroups(page, filter, keyword, searchOption, si, gu, gender, minAge, maxAge, interest,token);
   }
   
   
   
   
   
   //그룹상세정보 보내줌
   @RequestMapping("/group")
   public Map<String,Object> groupinfo(int token,int id) throws IOException{
	 
      return  clubService.groupInfo(token, id,1);
   }
   
   //ModelAttribute : 모델클래스로 데이터 가져올때
   
   
   //4-2 그룹개설 or 수정
   
   @PostMapping("/group/info")
   public ResponseEntity<Map<String, Object>> creategroup(@RequestBody String json) throws JSONException, IOException{
	   
	  
	   return new ResponseEntity<>(clubService.createUpdate(json),HttpStatus.OK);
   }
   
  

   
   //4-4-2 그룹 가입 신청
   //request : ?clubid=integer&token=String
   //response : """{ success : Boolean } 가입성공:OK , 가입실패:BAD_REQUEST"""
   @PostMapping("/group/request")
   public ResponseEntity<Boolean> groupJoin2(@RequestBody String json) throws JSONException {
	   JSONObject obj=new JSONObject(json);
	   int clubid;
	   int userid;
	   String message;
	
		clubid=obj.getInt("id");
		userid=obj.getInt("token");
		message = obj.getString("message");

	
//	   int clubid,int userid,String message
      //가입신청 성공하면 clubuser테이블에 추가,마스터에게 알람 이 가고 실패시 Bad_Request
      int result=clubService.groupJoin2(clubid, userid,message);
      if(result==1) {
         return new ResponseEntity<Boolean>(true,HttpStatus.OK);
      }
      else {
         return new ResponseEntity<Boolean>(false,HttpStatus.BAD_REQUEST);
      }
      
      
   }
 /*  //4-4-3 그룹 가입 신청허락 폼
   @RequestMapping("/group/accept")
   public Map<String,Object> allowGroupJoin(int clubid) {
      return clubService.allowGroupJoin(clubid);
   }
   */
  
   //그룹가입신청 수락
      @PostMapping("/group/accept")
      public ResponseEntity<String> allowGroupJoin2(@RequestBody String json) throws JSONException {
    	  JSONObject obj=new JSONObject(json);
//    	  int token, int id,int groupid,int notification
    	  int token;
    	  int id;
    	  int groupid;
    	  int notification;
    	  token=obj.getInt("token");
    	  id=obj.getInt("id");
    	  groupid=obj.getInt("groupid");
    	  notification=obj.getInt("notification");
         	if(true) {
         		
         		clubService.allowGroupJoin2(token,id,groupid,notification);
            return new ResponseEntity<>(HttpStatus.OK);
         	}
         	else {
         		return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
         	}
         
      
      }
      //그룹가입신청 거절
      @PostMapping("/group/reject")
      public ResponseEntity<String> rejectGroup(@RequestBody String json) throws JSONException {
//    	  int token, int id,int groupid,int notification
    	  JSONObject obj=new JSONObject(json);
//    	  int token, int id,int groupid,int notification
    	  int token;
    	  int id;
    	  int groupid;
    	  int notification;
    	  token=obj.getInt("token");
    	  id=obj.getInt("id");
    	  groupid=obj.getInt("groupid");
    	  notification=obj.getInt("notification");
    	  if(true) {
    		  
    		  clubService.rejectGroup(token,id,groupid,notification);
    	  	  return new ResponseEntity<>(HttpStatus.OK);
    	  }
    	  else {
    		  return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    	  }
      }
      //그룹탈퇴
      @PostMapping("/group/leave")
      public ResponseEntity<String> deleteGroup(@RequestBody String json) throws JSONException{
    	  JSONObject obj=new JSONObject(json);
    	  int token=obj.getInt("token");
    	  int groupid=obj.getInt("groupid");
//    	  int token,int groupid
    	  if(true) {
    		  clubService.deleteGroupUser(token,groupid);
    		  return new ResponseEntity<>(HttpStatus.OK);  
    	  }
    	  else {
    		  return new ResponseEntity<>(HttpStatus.BAD_REQUEST);  
    		  
    	  }
      }
      
      //그룹 멤버 조회
      @RequestMapping("/g"
      		+ "roup/member")
      public Map<String,Object> groupMember(Integer token,int id,int page){
    	  
    	  return clubService.groupMember(token,id,page);
      }
      
      //그룹 게시글 조회하기
      @RequestMapping("/group/board")
      public Map<String,Object> readBoard(int token,int id,int page){
    	  
    	  return clubService.readBoard(token,id,page);
      
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
   @PostMapping("/group/board")
   public Map<String,Object> writeGroupNotice(@RequestBody String json) throws JSONException {
	   JSONObject obj=new JSONObject(json);
	   int token=obj.getInt("token");
	   int id=obj.getInt("id");
	   String title=obj.getString("title");
	   String content=obj.getString("content");
	
      return clubService.writeGroupNotice(token,id,title,content);
     
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
   
   
   @PostMapping("/group/board/read")
   public Map<String,Object> boardReadCount(@RequestBody String json) throws JSONException {
	   JSONObject obj=new JSONObject(json);
	   int userid=obj.getInt("token"); //유저id
	   int id=obj.getInt("id"); //게시글id
	   
	  int result= clubService.addReadCount(userid,id);
	  Map<String,Object> map=new HashMap<>(); 
	  if(result==1) {
		   map.put("increment", true);
		   
	   }
	  else {
		  map.put("increment", false);
	  }
	  return map;
	   
   }
   @PostMapping("/group/board/like")
   public ResponseEntity<String> addLike(@RequestBody String json) throws JSONException{
	   JSONObject obj=new JSONObject(json);
	   int token=obj.getInt("token"); //유저
	   int id=obj.getInt("id"); //게시글
	   Boolean liked=obj.getBoolean("liked"); //true : 좋아요 false : 좋아요 취소
	   if(true) {
		   clubService.addLike(token,id,liked);
		   	return new ResponseEntity<>(HttpStatus.OK);
		   
	   }
	   else {
		   return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
	   }
	   
   }
   
   
   @PostMapping("/group/board/comment")
   public Map<String,Object> addComment(@RequestBody String json) throws JSONException{
	   JSONObject obj=new JSONObject(json);
	   		 
	   
	   int token=obj.getInt("token");
	   int id=obj.getInt("id");
	   String content=obj.getString("content");
	   
	   return clubService.addComment(token,id,content);
   }
   
   
   
}












