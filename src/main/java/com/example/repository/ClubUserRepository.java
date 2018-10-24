package com.example.repository;

import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.model.Clubuser;

public interface ClubUserRepository extends JpaRepository<Clubuser, Integer>{
   //유저수 구하기
   @Query(value="select count(*) from clubuser c where c.clubid=:clubid",nativeQuery=true)
   public int getMemberCount(@Param("clubid") int clubid);
   
   //해당그룹의 유저들 정보
   @Query(value="select c.userid from clubuser c where c.clubid=:clubid",nativeQuery=true)
   public List<Integer> getMemberId(@Param("clubid") int clubid);
   @Query(value="select * from clubuser where clubid=:clubid",nativeQuery=true)
   public List<Clubuser> getclubuser(@Param("clubid") int clubid);
   //마스터의 seq값
   @Query(value="select userid from clubuser where clubid=:clubid and auth=1",nativeQuery=true)
   public int getMaster(@Param("clubid") int clubid);
   

   //가입신청 허락대기중인 유저들을 가입신청날짜 순으로 내림차순
   @Query(value="select * from clubuser where clubid=:clubid and userstate=1 order by reqdate desc",nativeQuery=true)
   public List<Clubuser> waitingUser(@Param("clubid")int clubid);
   
   //그룹가입신청 허락
   @Transactional
   @Modifying
   @Query(value="update clubuser set userstate=2 where clubid=:clubid and userid=:userid",nativeQuery=true)
   public void allowGroup(@Param("clubid")int clubid,@Param("userid")int userid);
   
   //그룹가입신청 거절
   @Transactional
   public void deleteByClubidAndUserid(int clubid,int userid);
   
   
   //그룹유저 권한 바꾸기
   @Transactional
   @Modifying
   @Query(value="update clubuser set auth=:auth where clubid=:clubid and userid=:userid",nativeQuery=true)
   public void changeAuth(@Param("clubid")int clubid,@Param("userid")int userid,@Param("auth") int auth);
   
   @Query(value="select userstate from clubuser where clubid=:clubid and userid=:userid",nativeQuery=true)
   public Integer isMyClub(@Param("clubid")int clubid,@Param("userid")int userid);
   
   @Query(value="select * from clubuser where clubid=:clubid and date(joindate)>=date(subdate(now(),Interval 7day))",nativeQuery=true)
   public List<Clubuser> recentmember(@Param("clubid")int clubid);
   
   
   //최재현
   @Query(value="SELECT count(*) FROM clubuser WHERE userid = :userid", nativeQuery=true)
	public Integer clubCount(@Param("userid") int userid);

	@Query(value="select c.clubid,c.name,i.imgpath from club c ,clubuser cu,img i where c.clubid=cu.clubid and cu.userid=:userid and i.gubun=:gubun and c.clubid=i.id group by c.clubid",nativeQuery=true)
	   public List<Map<String,Object>> getUserClubinfo(@Param("userid")int userid, @Param("gubun")int gubun);
	

	@Query(value="update clubuser set userstate=3,breakdate=now() where userid=:userid and clubid=:clubid",nativeQuery=true)
		public void deleteClubUser(@Param("userid")int userid,@Param("clubid")int clubid);
	
	
}



