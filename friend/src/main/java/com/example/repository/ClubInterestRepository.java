package com.example.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.model.Clubinterest;

public interface ClubInterestRepository extends JpaRepository<Clubinterest, String>{

	   @Query(value="select c.code from clubinterest c where c.clubid=:clubid",nativeQuery=true)
	   public List<Integer> interestList(@Param("clubid") int clubid);
	   
	   @Query(value="delete from clubinterest where clubid=:clubid",nativeQuery=true)
	   public void deleteClubinterest(@Param("clubid")int clubid);

	   @Modifying
	   @Transactional
	   public void deleteByClubid(int clubid);
	   
}
