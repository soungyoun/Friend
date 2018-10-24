package com.example.repository;

import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.model.Club;

public interface MainRepository extends JpaRepository<Club, Integer> {

	//상위 6개 그룹 가져오기
	@Query(value= "select c.clubid, c.name  " + 
							"from club c  left join " + 
								"(select id,  count(*)*2 hitcnt " + 
								"   from hit " + 
								"     where gubun in (2,3) " + 
								"        and date(hittime) >= date(subdate(now(), Interval 30 day)) " + 
								"    group by id) a  " + 
								"on c.clubid = a.id " + 
								"  left join " + 
								"  (select clubid, count(*) nocnt " + 
								"    from clubnotice " + 
								"  where date(writedate) >= date(subdate(now(), Interval 30 day)) " + 
								" group by clubid) b " + 
								"  on c.clubid = b.clubid " + 
								"order by (ifnull(a.hitcnt,0) + ifnull(b.nocnt,0))  desc " + 
								" limit 6;" , nativeQuery=true)
	public List<Map<String,Object>>  topGroup();
	
	//매니저 리스트
	@Query(value ="select c.id, c.userid, u.name, c.auth from clubuser c, user u where c.userid = u.userid and c.clubid = :clubid and c.auth in (1,2)" , nativeQuery=true )
	public List<Map<String, Object>> GroupManager(@Param("clubid") String clubid);
	
	//매니저 관심사 리스트
	@Query(value ="select code from userinterest where userid = :userid" , nativeQuery=true )
	public List<String> GroupManagerInterest(@Param("userid") String userid);

}
