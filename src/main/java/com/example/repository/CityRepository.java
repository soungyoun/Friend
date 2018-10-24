package com.example.repository;

import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.model.City;

public interface CityRepository extends JpaRepository<City, Integer>{

	//1-1-2 : 로그인시 시 정보
	@Query(value="select * from city", nativeQuery=true)
	public List<Map<String, Object>> getCityList();

}
