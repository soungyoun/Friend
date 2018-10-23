package com.example.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.model.Noticecom;

public interface NoticecomRepository extends JpaRepository<Noticecom, Integer>{
	@Query(value="select * from noticecom where noticeid=:noticeid",nativeQuery=true)
	public List<Noticecom> noticecom(@Param("noticeid")int noticeid);
}
