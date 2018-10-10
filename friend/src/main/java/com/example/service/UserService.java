package com.example.service;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.UserFunction;
import com.example.config.SessionBindingListener;
import com.example.model.Alarm;
import com.example.model.Friend;
import com.example.model.User;
import com.example.model.Userinterest;
import com.example.repository.AlarmRepository;
import com.example.repository.ClubRepository;
import com.example.repository.ClubUserRepository;
import com.example.repository.FriendRepository;
import com.example.repository.ImgRepository;
import com.example.repository.UserInterestRepository;
import com.example.repository.UserRepository;

@Service
public class UserService {

   @Autowired
   private ClubService clubService;
   @Autowired
   private ChatService chatService;
   @Autowired
   private UserRepository userRepository;
   @Autowired
   private ImgRepository imgRepository;
   @Autowired
   private UserInterestRepository userInterestRepository;
   @Autowired
   private FriendRepository friendRepository;
   @Autowired
   private ClubRepository clubRepository;
   @Autowired
   private ClubUserRepository clubUserRepository;
   @Autowired
   private UserFunction userFunction;
   @Autowired
   private LoginService loginService;
   @Autowired
   private AlarmRepository alarmRepository;
   
   // 1-1-4 인기친구
   public Map<String, Object> popularUsers(){
      // 인기친구 쿼리
      List<User> userList = userRepository.topFriends();
      // 구분 1: 회원
      final int gubun = 1;
      
      Map<Integer, Object> popUserMap = new HashMap<>();
      for(User u : userList) {
         try {
            Map<String, Object> UserMap = new HashMap<>();
            // userid 맵
            UserMap.put("userid", u.getUserid());
            // name 맵
            UserMap.put("name", u.getName());
            List<Map<String, String>> imgsList = new ArrayList<Map<String, String>>();
            // 구분1, 유저아이디 에 해당하는 imgpath
            List<String> imgList = imgRepository.imgpathListByGubunAndId(gubun, u.getUserid());
            // 이미지가 있다면 경로의 사진을 읽어와서 바이너리 형식의 문자열로 변환
            if(!imgList.isEmpty()) {
               imgList = userFunction.fileload(imgList);
            }
            // 이미지를 이미지리스트에 추가
            for(String img : imgList) {
               Map<String, String> imgMap = new HashMap<String, String>();
               imgMap.put("img", img);
               imgsList.add(imgMap);
            }
            // 이미지리스트
            UserMap.put("imgs", imgsList);
            // 유저아이디:유저정보
            popUserMap.put(u.getUserid(), UserMap);
            
         } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
         }
      }
      
      Map<String, Object> popularUsersMap = new HashMap<String, Object>();
      popularUsersMap.put("popularUsers", popUserMap);
      List<Integer> useridList = new ArrayList<>();
      // 인기유저 순서 리스트
      for(User user : userRepository.topFriends()) {
         useridList.add(user.getUserid());
      }
      popularUsersMap.put("popularIndex", useridList);
      
      return popularUsersMap;
   }
   
// 2-2-1 나의 마이 페이지에 뿌려줄 정보들
   public Map<String, Object> intoMyPage(Integer userid, Integer seq, Boolean memberlist, Boolean grouplist,
         Integer page) {

      Map<String, Object> myPage = new HashMap<>();

      // 2-2-2 친구의 마이 페이지
      //상대방의 초기 페이지
      // 2-2-2 친구의 마이 페이지
      if (seq != null && page == null) {
          int isFriend = 0; //아무 관계 아님
          if(friendRepository.isFriend(userid, seq).size() == 0) {
             isFriend = friendRepository.isCurious(userid, seq, 1) == null ? 0 : 1; //요청 중
          } else {
             isFriend = 2;
          }
          Map<String, Object> me = loginService.getUserInfo(seq, false);
          myPage.put("me", me);
          me.put("isFriend", isFriend);
          System.out.println(friendRepository.isCurious(userid, seq, 4));
          me.put("isCurious", friendRepository.isCurious(userid, seq, 4) ==null ? false : true);
          userid = seq;
      } else if (seq != null)
         userid = seq;

      int friendsCount = 0;
      int clubsCount = 0;

      // 친구 목록 카운트
      if (friendRepository.friendsCount(userid) % 10 == 0) {
         friendsCount = friendRepository.friendsCount(userid) / 10;
      } else {
         friendsCount = friendRepository.friendsCount(userid) / 10 + 1;
      }
      
      if (clubUserRepository.clubCount(userid) % 10 == 0) {
         clubsCount = clubUserRepository.clubCount(userid) / 10;
      } else {
         clubsCount = clubUserRepository.clubCount(userid) / 10 + 1;
      }
      int firstNum = 0;

      List<Map<String, Object>> _friendsList = null;

      if (page != null) {  //페이지가 있을때
         firstNum = page * 10 - 10;
         if (memberlist) {
            _friendsList = friendRepository.getFriendList(userid, firstNum, 10);
            myPage.put("friendsPages", friendsCount);
         } else if(grouplist){
            myPage.put("groups", userFunction.ListToMap(clubRepository.getuserClubinfo(userid, firstNum, 10), "id"));
            myPage.put("groupsPages", clubsCount);
         }
      } else { //초기 페이지
         _friendsList = friendRepository.getFriendList(userid, 0, 10);
         myPage.put("groups", userFunction.ListToMap(clubRepository.getuserClubinfo(userid, 0, 10), "id"));

         myPage.put("friendsPages", friendsCount);
         myPage.put("groupsPages", clubsCount);
      }

      List<Map<String, Object>> friendsList = new ArrayList<>();

      if(!grouplist || grouplist == null){
	      for (int i = 0; i < _friendsList.size(); i++) {
	    	  Map<String, Object> friend = new HashMap<>();
	         friend.put("id", _friendsList.get(i).get("userid"));
	         friend.put("nickName", _friendsList.get(i).get("name"));
	         friend.put("gender", _friendsList.get(i).get("gender"));
	         friend.put("image", _friendsList.get(i).get("imgpath"));
	
	         boolean online = false;
	         if (SessionBindingListener.loginList.size() != 0 && SessionBindingListener.loginList.get(String.valueOf(userid)) != null) {
	            online = true;
	         }
	         friend.put("online", online);
	         friendsList.add(friend);
	      }
	      myPage.put("friends", userFunction.ListToMap(friendsList, "id"));
      }
      return myPage;
   }
   
  /* // 2-2-1 나의 마이 페이지에 뿌려줄 정보들
   public Map<String, Object> intoMyPage(Integer userid, Boolean memberlist, Boolean grouplist, Integer page) {

      int friendsCount = 0;
      int clubsCount = 0;
      if(friendRepository.friendsCount(userid) % 10 == 0) {
         friendsCount = friendRepository.friendsCount(userid) / 10;
      } else {
         friendsCount = friendRepository.friendsCount(userid) / 10 + 1;
      }
      if(clubUserRepository.clubCount(userid) % 10 == 0) {
         clubsCount = friendRepository.friendsCount(userid) / 10;
      } else {
         clubsCount = friendRepository.friendsCount(userid) / 10 + 1;
      }
     
     
      int firstNo = 1;
      int LastNo = 10;
      Map<String, Object> myPage = new HashMap<>();
      System.out.println("page:" +page);
      if (page != null) {
         firstNo = page - (page -1) % 10;
         LastNo = firstNo + 9;
         if(memberlist) {
            myPage.put("friends", userFunction.ListToMap(friendRepository.getFriendList(userid, firstNo, LastNo), "userid"));
            myPage.put("friendsPages", friendsCount);
         } else if(grouplist) {
            myPage.put("groups", clubService.mePage(userid,0,page));
            myPage.put("groupsPages", clubsCount);
         }
      }else {
         myPage.put("friends", userFunction.ListToMap(friendRepository.getFriendList(userid, 0, 9), "userid"));
         myPage.put("groups", clubService.mePage(userid,0,1));

         myPage.put("friendsPages", friendsCount);
         myPage.put("groupsPages", clubsCount);
      }

      return myPage;
   }

	// 2-2-2 친구 마이 페이지에 뿌려줄 정보들
	public Map<String, Object> intoFriendPage(Integer userid, Integer seq, Boolean memberlist, Boolean grouplist,
			Integer page) {

		Map<String, Object> friendMyPage = new HashMap<>();
		Map<String, Object> user = userRepository.getMyAllProfile(seq);
		List<Map<String, Object>> friendList = friendRepository.getFriendList(userid, page, page);
		// 접속 유저 판별해 리스트에 넣음
		for (Map<String, Object> friend : friendList) {
			if (SessionBindingListener.loginList.get((String) friend.get("userid")) != null)
				friend.put("online", friend.get("userid"));
		}

		// 나이 계산
		SimpleDateFormat sdf = new SimpleDateFormat("YYYY");
		Date birth = (Date) user.get("birth");
		int age = Integer.parseInt(sdf.format(new Date())) - Integer.parseInt(sdf.format(birth));
		Map<String, Object> isFriend = friendRepository.isFriend(userid, seq);
	      Integer isFriend2 = 1;

	      if (isFriend != null) {
	         isFriend2 = 2;
	      }

		user.put("images", imgRepository.getImgList(seq, 1));
		user.put("interests", userInterestRepository.getUserInterest(seq));
		user.put("age", age);

		friendMyPage.put("me", user);
		friendMyPage.put("friends", userFunction.ListToMap(friendList, "userid"));
		friendMyPage.put("groups", userFunction.ListToMap(clubUserRepository.getUserClubinfo(seq, 2), "clubid"));
		friendMyPage.put("friendsPages", friendRepository.friendsCount(userid));
		friendMyPage.put("groupsPages", clubUserRepository.clubCount(userid));
		friendMyPage.put("isFriend", isFriend2);
		friendMyPage.put("isCurious", friendRepository.isFriend(userid, seq));

		return friendMyPage;
	}*/

	// 2-1-3 상세 정보 등록 및 수정
	@Transactional
	public Map<String, Object> updateInfo(String json) throws JSONException {

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		JSONObject obj = new JSONObject(json);
		JSONArray inputImgList = obj.getJSONArray("images");

		Calendar cal = Calendar.getInstance();
		Date birth = null;
		Date birth2 = null;
		try {
			birth = sdf.parse(obj.getString("birth"));
			cal.setTime(birth);
            cal.add(Calendar.DATE, 1);
            birth2 = cal.getTime();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		String pw = null;
		int userid = obj.getInt("token");
		// 패스워드가 안 오면
        if (obj.has("pw")) {
           pw = userRepository.getPassword(userid);
        } else {
           pw = userFunction.encrypt(obj.getString("pw"));
        }
		//수정
        userRepository.updateUser(userid, pw, obj.getString("nickName"), obj.getInt("si"), obj.getInt("gu"), birth2
                , obj.getInt("gender"), obj.getBoolean("areayn"), obj.getBoolean("genderyn")
                , obj.getBoolean("birthyn"), obj.getBoolean("friendsyn"), obj.getBoolean("groupsyn")
                      , obj.getString("msg"), obj.getString("intro"));
		//이미지 저장
		userFunction.imgUpdate(1, userid, inputImgList);
		
		//인터레스트 삭제 후 저장
		JSONArray interests = obj.getJSONArray("interests");
        userInterestRepository.deleteInterests(userid);
        for(int i = 0; i < interests.length(); i++)
           userInterestRepository.save(new Userinterest(userid, interests.getInt(i)));
		
		//유저정보 다시 넘겨줌
        Map<String, Object> myInfo = new HashMap<>();
        myInfo.put("myInfo", loginService.getUserInfo(userid, true));
        
		return myInfo;

	}
   
	// 2-1-2 내 정보 수정을 위해 필요한 정보들
	public Map<String, Object> myInfo(Integer userid) {
		Map<String, Object> profile = new HashMap<>();
		profile.put("profile", userRepository.getMyAllProfile(userid));
		profile.put("img", imgRepository.getImgList(userid, 1));
		
		return profile;
	}
   
   //2-1-1 디폴트 메시지, 인트로 생성
   public Map<String,Object> getDefaultMsg(){
      List<String> defaultMsg = new ArrayList<>();
      defaultMsg.add("안녕하세요. 반갑습니다.");
      defaultMsg.add("행복한 하루 보내세요.");
      defaultMsg.add("후후후");
      
      List<String> defaultIntro = new ArrayList<>();
      defaultIntro.add("테스트입니다.");
      defaultIntro.add("인트로입니다.");
      
      Map<String, Object> msg = new HashMap<>();
      msg.put("defaultMsg", defaultMsg);
      msg.put("defaultIntro", defaultIntro);
      return msg;
   }
   
// 2-3-1, 2-3-2, 2-4, 2-5, 2-6-1, 2-6-2, 2-7
   public Map<String, Object> setFriendState(String json, Integer state) {
      Friend friend = null;
      Alarm  alarm = null;
      int token = 0;
      int id = 0;
      int notification = 0;
      String message = ""; 
      JSONObject obj = null;
      System.out.println("json:" + json);
      try {
         obj = new JSONObject(json);
         token = obj.getInt("token");
         id = obj.getInt("id");
         if(obj.has("message"))
            message = obj.getString("message");
         if(obj.has("notification"))
        	 notification = Integer.parseInt(obj.getString("notification"));
      } catch (JSONException e) {
         e.printStackTrace();
      }
      
      switch (state) {
      case 1: // 친구요청
         friend = new Friend(id, token, message, state);  //요청받은유저,요청한유저,메시지,상태
         friend.setReqdate(new Date());    //요청일자
         friendRepository.save(friend);
         alarm = alarmRepository.save(new Alarm(0, token, id, 0, message));  //구분,요청한유저,읽어야할유저,그룹id, 메시지
         chatService.sendAlarmSocket(alarm);   //알림 소켓 발송
         break;
      case 2: // 친구허락
         friendRepository.FriendUpdate(2, token, id ); //상태,요청받은친구,요청하는 친구
         alarmRepository.readAlarmUpdate(notification);  //요청한 자료 읽음 update
         alarm = alarmRepository.save(new Alarm(2, token, id, 0, message)); //구분,요청한유저,읽어야할유저,그룹id, 메시지
         chatService.sendAlarmSocket(alarm);    //알림 소켓 발송
         return userFunction.ListToMap(friendRepository.getLoginFriendList(token),"id");
      case 3: // 거절
         friend = new Friend(token, id, state);
         friend.setBreakdate(new Date());
         friendRepository.save(friend);
         alarmRepository.readAlarmUpdate(notification);
         break;
      case 4: // 관심 친구
         friend = new Friend(token, id, state);
         friendRepository.save(friend);
         break;
      case 5: // 유저 신고
         friend = new Friend(token, id, message, state);
         friendRepository.save(friend);
         break;
      case 6: // 유저 차단
         friend = new Friend(token, id, state);
         friendRepository.save(friend);
         break;
      case 7: // 유저 삭제
         friendRepository.deleteFriend(token, id);
      }
	return null;
   }
}