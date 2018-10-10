package com.example.service;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.example.UserFunction;
import com.example.model.Alarm;
import com.example.model.Chatmsg;
import com.example.model.Chatread;
import com.example.model.Chatroom;
import com.example.model.Chatuser;
import com.example.model.User;
import com.example.repository.ChatMsgRepository;
import com.example.repository.ChatReadRepository;
import com.example.repository.ChatRoomRepository;
import com.example.repository.ChatUserRepository;
import com.example.repository.UserRepository;

@Service
public class TestService {

	@Autowired
	private ChatRoomRepository chatRoomRepository;
	@Autowired
	private ChatUserRepository chatUserRepository;
	@Autowired
	private ChatMsgRepository chatMsgRepository;
	@Autowired
	private ChatReadRepository chatReadRepository;
	@Autowired
	private UserRepository userRepository;
	
   @Autowired
   private UserFunction userFunction;
	   
	//방생성
	public void CreateChatRoom(String roomname, int clubid, int userid) {
		Chatroom chatroom = new Chatroom();
		chatroom.setRoomname(roomname);
		chatroom.setClubid(clubid);
		chatroom.setUserid(userid);
		Chatroom resultroom = chatRoomRepository.save(chatroom);
		SaveChatUser(resultroom.getRoomid(), userid);
		
	}
	
	//멤버생성
	public void SaveChatUser(int roomid, int userid) {
		Chatuser chatUser = new Chatuser();
		chatUser.setRoomid(roomid);
		chatUser.setUserid(userid);
		chatUserRepository.save(chatUser);
	}
	//방 리스트
	public List<Chatroom> ListChatRoom() {
		List<Chatroom> chatroom = chatRoomRepository.findAll();
		return chatroom;
	}

   
	//채팅메세지저장
	public void SaveChatMsg(Chatmsg msg) {
		Chatmsg chatmsg = new Chatmsg();
		chatmsg.setRoomid(msg.getRoomid());        //방번호
		chatmsg.setUserid(msg.getUserid());            //작성자
		chatmsg.setMessage(msg.getMessage());      //메세지
		chatmsg.setWritedate(new Date());
		chatMsgRepository.save(chatmsg);
	}

	//메세지읽은 멤버 저장
	public void SaveChatRead(int msgid, int userid) {
		Chatread chatread = new Chatread();
		chatread.setMsgid(msgid);
		chatread.setUserid(userid);
		chatReadRepository.save(chatread);
	}
	
	//알람 소켓 전송 테스트
	public void SendAlarm() {
		Alarm alarm = new Alarm();
		alarm.setGubun(1);
		alarm.setId(1);
		alarm.setUserid(2);  //받을 userid
		alarm.setMessage("alarm test");
		
		//userFunction.sendAlarmSocket(alarm);
	}
	
	public void SaveTest() {
//		User user = new User();
//		user.setUserid(10);
//		user.setEmail("555555");
//		user.setName("hello");
		userRepository.updateTest(11, "ddddd");
		
	}
	

}
