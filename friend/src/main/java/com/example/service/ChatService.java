package com.example.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.example.UserFunction;
import com.example.config.SessionBindingListener;
import com.example.model.Alarm;
import com.example.model.Chatmsg;
import com.example.model.Chatread;
import com.example.model.Chatroom;
import com.example.model.Chatuser;
import com.example.repository.AlarmRepository;
import com.example.repository.ChatMsgRepository;
import com.example.repository.ChatReadRepository;
import com.example.repository.ChatRoomRepository;
import com.example.repository.ChatUserRepository;
import com.example.repository.ClubRepository;
import com.example.repository.FriendRepository;
import com.example.repository.UserRepository;

@Service
public class ChatService {
	
	@Autowired
	private ChatRoomRepository chatRoomRepository;
	@Autowired
	private ChatUserRepository chatUserRepository;
	@Autowired
	private ChatMsgRepository chatMsgRepository;
	@Autowired
	private ChatReadRepository chatReadRepository;
	@Autowired
	private AlarmRepository alarmRepository;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private ClubRepository clubRepository;
	@Autowired
	private FriendRepository friendRepository;
	@Autowired
	private UserFunction userFunction;
	@Autowired
	private ChatService chatService;

	private final SimpMessagingTemplate template;
    public ChatService(SimpMessagingTemplate template) {
        this.template = template;
    }
	
	//방생성
	public Map<String, Object> CreateChatRoom(String json) throws JSONException {
		JSONObject obj = new JSONObject(json);
		int fromuserid = Integer.parseInt(obj.getString("token"));  //요청하는 자
		int  touserid = Integer.parseInt(obj.getString("id"));        //요청 받은자
		
		Chatroom chatroom = new Chatroom();
		chatroom.setRoomname("개인방");
		chatroom.setClubid(0);
		chatroom.setUserid(fromuserid);
		Chatroom resultroom = chatRoomRepository.save(chatroom);

		//개인방일때
		SaveChatUser(resultroom.getRoomid(), chatroom.getUserid());
		SaveChatUser(resultroom.getRoomid(), touserid);
		
        Map<String, Object> map = new HashMap<>();
	    map.put("contacts", chatService.ListChatRoom(fromuserid));
	    return map;
	}
	
	//방입장
	public void ChatComeIn(int roomid, int clubid, int userid) {
		if (clubid == 0 ) {  //개인방일 때
			SaveChatUser(roomid, userid);
		}
	}

	//멤버생성
	public void SaveChatUser(int roomid, int userid) {
		Chatuser chatUser = new Chatuser();
		chatUser.setRoomid(roomid);
		chatUser.setUserid(userid);
		chatUserRepository.save(chatUser);
		
	}

	//방 리스트 그룹 리스트 테스트
	public List<Map<String, Object>> ListChatRoom_group(int userid) {
		
		List<Map<String, Object>> groupList = chatRoomRepository.getChatGroupList(userid);
	
		return groupList;
	}

	//방 리스트 개인 리스트 테스트
	public List<Map<String, Object>> ListChatRoom_personal(int userid) {
		
		List<Map<String, Object>> PersonalList = chatRoomRepository.getChatPersonalList(userid);
	
		return PersonalList; 
	}

	//방 리스트
	public Map<String, Object> ListChatRoom(int userid) {
		//그룹
//		List<Map<String, Object>> groupList = chatRoomRepository.getChatGroupList(userid);
//		Map<String , Object> groupMap = userFunction.ListToMap(groupList, "roomid");
//		if (groupMap.size() !=0)  resultMap.put("groupChat", groupMap);

		//개인
		List<Map<String, Object>> PersonalList = chatRoomRepository.getChatPersonalList(userid);
		List<Map<String, Object>> newPersonalList = new ArrayList<>();
		String roomid, id, nickName, image, email = null;
		boolean online;
		for (int i = 0 ; i<PersonalList.size(); i++) {
			Map<String, Object> newMap= new HashMap<>(); 
			roomid = PersonalList.get(i).get("roomid").toString();
			id = PersonalList.get(i).get("id").toString();
			nickName = PersonalList.get(i).get("nickName").toString();
			image = PersonalList.get(i).get("image").toString();
			email = userRepository.getUserEmail(Integer.parseInt(id));
			if (SessionBindingListener.loginList.get(email) == null) {
				online = false;
			}else {
				online = true;
			}
			newMap.put("roomid", roomid);
			newMap.put("id", id);
			newMap.put("nickName", nickName);
			newMap.put("image", image);
			newMap.put("online", online);
			newPersonalList.add(newMap);
		}
		
		return userFunction.ListToMap(newPersonalList, "id");
	}

	//메세지 보내기
    public void SendChatMsg(String message) {
    	JSONObject obj;
		int fromuserid = 0, touserid = 0, roomid = 0;
		String msg = null;
		System.out.println("SendChatMsg:" +message);  

		try {
			obj = new JSONObject(message);
			fromuserid = Integer.parseInt(obj.getString("token"));  
			touserid = Integer.parseInt(obj.getString("id"));  
			roomid = Integer.parseInt(obj.getString("roomid"));        
			msg = obj.getString("message");        
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		//DB저장
		Chatmsg chatmsg = new Chatmsg();
		chatmsg.setRoomid(roomid);        //방번호
		chatmsg.setUserid(fromuserid);     //작성자
		chatmsg.setMessage(msg);          //메세지
		Chatmsg chatMsg =   chatMsgRepository.save(chatmsg);
		
    	//채팅 보낼 멤버 (개인창)
    	List<Integer> listchatuser = ListChatUser(roomid, 0);
    	for (Integer id : listchatuser) {
			//전송데이타
			int msgid = chatMsg.getMsgid();
			Date writedate = chatMsg.getWritedate();
			Map<String, Object> map = new HashMap<>();
			map.put("messageid", msgid);
			map.put("roomid", roomid);
			map.put("id", fromuserid);
			map.put("message", msg);
			map.put("writedate", writedate);
    		//보낸사람은 readyn을 1로 변경
    		if (fromuserid == id) {
    			map.put("readyn", 1);  
    		}else {
    			map.put("readyn", 0);  
    		}
			
	        Map<String, Object> resultMap = new HashMap<>();
	        resultMap.put("type", "message");
	        resultMap.put("message", map);
    		
    		template.convertAndSend("/topic/"+id,resultMap );
    	}
    }

    //채팅멤버 리스트
	public List<Integer> ListChatUser(int roomid, int clubid) {
		List<Integer> userList ;
    	if (clubid == 0) {   //그룹이 아닐때
    		userList = chatUserRepository.findChatUser(roomid);
    	}else {   //그룹일때
    		userList = chatUserRepository.findClubUser(clubid);
    	}
		return  userList;
	}

	//알림 읽은 여부 저장
	public Boolean SaveAlarmRead(String json) throws JSONException {
		JSONObject obj = new JSONObject(json);
		int alarmid = Integer.parseInt(obj.getString("notification"));  //알림ID
		alarmRepository.readAlarmUpdate(alarmid);
		return true;
	}

	//채팅읽은 멤버 저장
	public Boolean SaveChatRead(String json) throws NumberFormatException, JSONException {
		JSONObject obj = new JSONObject(json);
		int msgid = Integer.parseInt(obj.getString("msgid"));  // 채팅방id
		int  userid = Integer.parseInt(obj.getString("userid"));    // 읽은 멤버
		
		Chatread chatread = new Chatread();
		chatread.setMsgid(msgid);
		chatread.setUserid(userid);
		chatReadRepository.save(chatread);
		return true;
	}
	
	//로그인시 chat메세지 보내기
	public Map<String, Object> ListChatMessage(int userid) {
		List<Map<String, Object>> listMap = chatMsgRepository.getMsgList(userid);
		Map<String, Object> map = userFunction.ListToMap(listMap, "messageid");
		return map;
	}

	//로그인시 알림 메세지 보내기
	public Map<String, Object> GetAlarmList(int userid) {
		List<Map<String, Object>> listMap = alarmRepository.getUserAlarm(userid);
		Map<String, Object> map = userFunction.ListToMap(listMap, "notification");
		return map;
	}

	   //알람 소켓으로 전송(Gubun(0:친구신청, 1:그룹가입신청, 2: 친구추가됨, 3: 그룹가입수락됨))
	   public void sendAlarmSocket(Alarm alarm) {
		   Map<String, Object> map = new HashMap<>();
		   int id = alarm.getId();  			//요청/수락 회원
		   int userid = alarm.getUserid();  //읽어야할 회원
		   int clubid = alarm.getClubid();  //그룹id
		   Map<String, Object> userMap = userRepository.getFrindRequest(id);
		   Map<String, Object> clubMap = clubRepository.getClubRequest(clubid);

		   map.put("notification", alarm.getAlarmid());
		   map.put("gubun", alarm.getGubun());
		   map.put("id", alarm.getId());  
		   
		   map.put("nickName",userMap.get("name"));
		   map.put("image",userMap.get("image"));

		   map.put("message", alarm.getMessage());  //메세지
		   map.put("writedate", alarm.getWritedate());
		   if (alarm.getGubun()==1 || alarm.getGubun()==3) {   //그룹가입요청, 그룹가입수락
			   map.put("groupid",clubMap.get("clubid") );  //그룹id
			   map.put("groupName", clubMap.get("name"));  //그룹이름
		   }
	        
		   Map<String, Object> resultMap = new HashMap<>();
	       resultMap.put("type", "notification");
	       resultMap.put("notification", map);
		   if (alarm.getGubun()==2)  //친구요청수락
			   resultMap.put("myFriends", userFunction.ListToMap(friendRepository.getLoginFriendList(userid),"id"));
	       userFunction.sendAlarmSocket(userid, resultMap);

	   }
}
