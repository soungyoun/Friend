package com.example.repository;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.model.Feel;

public interface FeelRepository extends JpaRepository<Feel, Integer>{

	@Query(value="select count(*) from feel where gubun=:gubun and id=:id and type=1",nativeQuery=true)
	public int likes(@Param("gubun")int gubun,@Param("id") int id);
	
	@Transactional
	public void deleteByGubunAndIdAndUseridAndType(int gubun,int id,int userid,int type);
}
