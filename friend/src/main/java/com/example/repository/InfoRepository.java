package com.example.repository;

import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.model.Info;

public interface InfoRepository extends JpaRepository<Info, Integer>{
	
	//1-3 : 사이트 정보
	@Query(value="select * from info", nativeQuery=true)
	public List<Map<String, Object>> GetInfo();
	
}
