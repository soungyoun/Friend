package com.example.repository;

import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.model.Chatmsg;

public interface ChatMsgRepository extends JpaRepository<Chatmsg, Integer>{
	
	//로그인시 메세지 전송
	@Query(value=" select cm.msgid messageid, cm.roomid, cm.userid id, cm.message, cm.writedate, if(cm.userid=:userid, true, if(isnull(cr.userid), false, true )) as readyn  " +
		  " from chatuser cu inner join chatmsg cm on (cu.roomid = cm.roomid and cu.userid = :userid) " + 
		  "  left join chatread cr on ( cr.msgid = cm.msgid ) " , nativeQuery=true)
	public List<Map<String,Object>> getMsgList(@Param("userid") int userid);
	
}
