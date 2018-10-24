package com.example.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.model.User;


public interface MasterRepository extends JpaRepository<User, Integer> {

	//id_check
	@Query(value="select *  from user u , where id = :id and ", nativeQuery=true)
	public User findById(@Param("id") String id);
	
	//id, pass check
	@Query(value="select id  from user where id = :id and pw =:pw", nativeQuery=true)
	public Object CheckLogin(@Param("id") String id, @Param("pw") String pw);
	
	@Query(value="select u.userid, i.code from user u, userinterest i where u.userid = i.userid and u.userid = :id", nativeQuery=true)
	public List<Object> InterestQuery(@Param("id") int id);



}
