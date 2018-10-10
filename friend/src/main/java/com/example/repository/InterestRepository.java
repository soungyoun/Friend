package com.example.repository;

import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.model.Interest;

public interface InterestRepository extends JpaRepository<Interest, Integer>{
	
	//1-1-2 : 로그인시 관심사 정보
	@Query(value="SELECT * FROM interest",nativeQuery=true)
   public List<Map<String, Object>> getInterests();
	
}
