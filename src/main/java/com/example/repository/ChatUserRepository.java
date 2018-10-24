package com.example.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.model.Chatuser;

public interface ChatUserRepository extends JpaRepository<Chatuser, Integer>{
	
	//채팅룸의 일반 멤버 리스트
	@Query(value = "select userid	from chatuser where roomid = :roomid", nativeQuery=true)
	List<Integer> findChatUser(@Param("roomid") int roomid);

	//채팅룸의 그룹멤버 리스트
	@Query(value = "select userid from clubuser where userstate = 2 and clubid = :clubid", nativeQuery=true)
	List<Integer> findClubUser(@Param("clubid")  int clubid);
	
}
