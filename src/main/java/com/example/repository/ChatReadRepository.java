package com.example.repository;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.model.Chatread;

public interface ChatReadRepository extends JpaRepository<Chatread, Integer> {
	   @Modifying
	   @Transactional
	   @Query(value="insert into chatread  " + 
	   		" select msgid, :userid userid, now() from chatmsg " + 
	   		" where roomid = :roomid" + 
	   		"   and msgid not in (select msgid from chatread where userid = :userid)" ,nativeQuery=true)
	   public void chatReadInsert(@Param("roomid")int roomid, @Param("userid")int userid) ;
}
