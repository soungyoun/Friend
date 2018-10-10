package com.example.repository;

import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.UserFunction;
import com.example.model.Chatroom;

public interface ChatRoomRepository extends JpaRepository<Chatroom, Integer>{
	
	//개인창리스트
//	@Query(value="select cr.roomid, cr.roomname, cr.userid from chatroom cr , chatuser cu where cr.roomid = cu.roomid and cr.clubid = 0 and cu.userid = :userid", nativeQuery=true)

	@Query(value="select cu.roomid, cu.userid id, u.name nickname, CONCAT('" + UserFunction.ImgPath + "', i.imgpath) image " + 
			"	from chatuser cu, user u , " + 
			"	(select id, imgpath " + 
			"	   from img " + 
			"	 where gubun = 1 " + 
			"	    and id " + 
			"	  group by id) i " + 
			"	where cu.userid <> :userid " + 
			"	   and cu.roomid in ( " + 
			"	       select roomid " + 
			"	         from chatuser " + 
			"	       where userid = :userid) " + 
			"	and cu.userid = u.userid " + 
			"	and u.userid = i.id "  , nativeQuery=true)
	public List<Map<String,Object>> getChatPersonalList(@Param("userid") int userid);

   //그룹창리스트
	@Query(value="select cr.roomid, cr.roomname, cr.userid, cu.clubid, c.name " + 
						"	 from chatroom cr , clubuser cu, club c " + 
						"	where cr.clubid = cu.clubid " + 
						"	  and cr.clubid = c.clubid " + 
						"	  and cr.clubid != 0 " + 
						"	  and cu.userstate = 2 " + 
						"	  and cu.userid = :userid ", 
			nativeQuery=true)
	public List<Map<String,Object>> getChatGroupList(@Param("userid") int userid);
	  
}
