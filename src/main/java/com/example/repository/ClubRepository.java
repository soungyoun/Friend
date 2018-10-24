package com.example.repository;


import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.example.model.Club;

public interface ClubRepository extends JpaRepository<Club, Integer>{

	@Query(value="select c.* from club c where c.clubid=:clubid",nativeQuery=true)
	public List<Object> aa(@Param("clubid") int clubid);
	
	//최재현
		@Query(value="select c.clubid id,c.name groupName,i.imgpath image from club c ,clubuser cu,img i where c.clubid=cu.clubid and cu.userid=:userid and i.gubun=2 and c.clubid=i.id group by c.clubid limit :start,:end",nativeQuery=true)
	   public List<Map<String,Object>> getuserClubinfo(@Param("userid")int userid,@Param("start")int start,@Param("end")int end);
	
	   @Query(value = "select c.* from club c where c.clubid=:clubid", nativeQuery = true)
	   public Club groupInfo(@Param("clubid") int clubid);

	   // 1-1-3 ClubRepository
	   @Query(value = "select c.*  " + "from club c  left join " + "(select id,  count(*)*2 hitcnt " + "   from hit "
	         + "     where gubun in (2,3) " + "        and date(hittime) >= date(subdate(now(), Interval 30 day)) "
	         + "    group by id) a  " + "on c.clubid = a.id " + "  left join " + "  (select clubid, count(*) nocnt "
	         + "    from clubnotice " + "  where date(writedate) >= date(subdate(now(), Interval 30 day)) "
	         + " group by clubid) b " + "  on c.clubid = b.clubid "
	         + "order by (ifnull(a.hitcnt,0) + ifnull(b.nocnt,0))  desc " + " limit 6;", nativeQuery = true)
	   public List<Club> topGroup();

	   
	   // 9.3추가 추천그룹 테스트
	   @Query(value = "select * from club where clubid in(select "
	         + " clubid from clubinterest where code in (:code))", nativeQuery = true)
	   public List<Club> Test(@Param("code") int[] code);

	   //5-1 Groups Page이동 및 그룹 더읽어오기  ClubRepository
	   // 9.3추가 추천그룹 첫번째 (1/5)
	   @Query(value = "select * from club where city=:city and gu=:gu and clubid in(select "
	         + "clubid from clubinterest where code in (:code)) and clubid not in ("
	         + "select clubid from clubuser where userid=:userid) order by (select count(*) from clubuser) desc", nativeQuery = true)
	   public List<Club> recommend_1(@Param("city") int city, @Param("gu") int gu, @Param("code") int[] code,@Param("userid")int userid);

	   // 9.3추가 추천그룹 두번째 (2/5)
	   @Query(value = "select * from club where city=:city and gu!=:gu and clubid in (select"
	         + " clubid from clubinterest where code in (:code)) and clubid not in (" + 
	         "select clubid from clubuser where userid=:userid) order by (select count(*) from clubuser) desc", nativeQuery = true)
	   public List<Club> recommend_2(@Param("city") int city, @Param("gu") int gu, @Param("code") int[] code,@Param("userid")int userid);

	   // 9.3추가 추천그룹 세번째 (3/5)
	   @Query(value = "select * from club where city!=:city and gu!=:gu and clubid in (select"
	         + " clubid from clubinterest where code in (:code)) and clubid not in ("
	         + "select clubid from clubuser where userid=:userid) order by (select count(*) from clubuser) desc", nativeQuery = true)
	   public List<Club> recommend_3(@Param("city") int city, @Param("gu") int gu, @Param("code") int[] code,@Param("userid")int userid);
	   // 9.3추가 추천그룹 세번째 (4/5)
	      @Query(value = "select * from club where city!=:city and gu=:gu and clubid in (select"
	            + " clubid from clubinterest where code in (:code)) and clubid not in ("
	            + "select clubid from clubuser where userid=:userid) order by (select count(*) from clubuser) desc", nativeQuery = true)
	      public List<Club> recommend_4(@Param("city") int city, @Param("gu") int gu, @Param("code") int[] code,@Param("userid")int userid);

	   // 9.3추가 추천그룹 네번째 (5/5)
	   @Query(value = "select * from club where clubid not in (select"
	         + " clubid from clubinterest where code in (:code)) and clubid not in ("
	         + "select clubid from clubuser where userid=:userid) order by (select count(*) from clubuser) desc", nativeQuery = true)
	   public List<Club> recommend_5(/*@Param("city") int city, @Param("gu") int gu,*/ @Param("code") int[] code,@Param("userid")int userid);
	   @Modifying
	   @Transactional
	   @Query(value="update club set name=:name,gender=:gender,city=:city,gu=:gu"
	         + ",content=:content,agestart=:agestart,ageend=:ageend,maxcount=:maxcount,"
	         + "yn=:yn,writedate=now() where clubid=:clubid",nativeQuery=true)
	   public void updateClub(@Param("name")String name,@Param("gender")int gender,
	         @Param("city")int city,@Param("gu")int gu,@Param("content")String content,@Param("agestart")int agestart,
	         @Param("ageend")int ageend,@Param("maxcount")int maxcount,@Param("yn")boolean yn,@Param("clubid")int clubid);

	   //권샘찬
	   @Query(value="select c.clubid, c.name, i.imgpath" + 
		         "  from club c " + 
		         " inner join clubuser cu on (c.clubid = cu.clubid and cu.userid = :userid)" + 
		         " left join img i on (c.clubid = i.id and i.gubun = 2 and i.firstyn = true)", nativeQuery=true)
		   public List<Map<String, Object>> getClubList(@Param("userid") int userid);
	   
	   //김성연
	   @Query(value = "select c.* from club c where c.clubid=:clubid", nativeQuery = true)
	   public Map<String, Object> getClubRequest(@Param("clubid") int clubid);
	   
	   

}








