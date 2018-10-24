package com.example.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.model.Hit;

public interface HitRepository extends JpaRepository<Hit, Integer>{
	@Query(value="select count(*) from hit where gubun=:gubun and id=:id",nativeQuery=true)
	public int readcounts(@Param("gubun") int gubun,@Param("id")int id);
	
	@Query(value="select * from hit where gubun=:gubun and id=:id and userid=:userid and hittime"
			+ " >= date(subdate(now(),interval 1 day))",nativeQuery=true)
	public List<Hit> addReadCount(@Param("gubun")int gubun,@Param("id")int id,@Param("userid")int userid);
	
}
