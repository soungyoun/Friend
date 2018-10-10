package com.example.repository;

import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.model.Gu;

public interface GuRepository extends JpaRepository<Gu, Integer>{
	List<Gu> findByCitycode(int code);
	
	//1-1-2 : 로그인시 구 정보
	@Query(value="SELECT * FROM gu", nativeQuery=true)
   public List<Map<String, Object>> getGuList();	
	
}
