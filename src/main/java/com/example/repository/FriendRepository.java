package com.example.repository;

import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.UserFunction;
import com.example.model.Friend;

public interface FriendRepository extends JpaRepository<Friend, Integer> {
//	   @Query(value="select  f.requserid, u.userid, u.name, u.intro, i.imgpath, f.userstate " + 
//		         "    from friend f inner join user u on (f.requserid = u.userid and f.userstate = 2 and f.userid = :userid) " + 
//		         "   left join img i on (f.requserid = i.id and i.gubun = 1 and i.firstyn = 1)", nativeQuery=true)
//		   public List<Map<String, Object>> getFriendList(@Param("userid") int userid);

	   
	   //권샘찬
	@Query(value="SELECT * FROM friend WHERE requserid IN (:requserid, :userid) AND userid IN (:requserid, :userid) AND userstate=:userstate", nativeQuery = true)
	   public Map<String, Object> isFriend(@Param("requserid") int requserid, @Param("userid") int userid, @Param("userstate") int userstate);
	
	@Query(value="DELETE FROM friend WHERE userid=:userid and requserid=:requserid", nativeQuery=true)
	public void deleteFriend(@Param("userid") int userid, @Param("requserid") int requserid);
	
	@Query(value="SELECT count(*) FROM friend WHERE (userid = :userid or requserid = :userid) and userstate=2", nativeQuery=true)
	public Integer friendsCount(@Param("userid") int userid);
	
	@Query(value="SELECT userstate FROM friend WHERE (userid = :userid and requserid = :requserid) AND userstate = :userstate", nativeQuery=true)
	   public Integer isCurious(@Param("userid") int userid, @Param("requserid") int requser, @Param("userstate") int userstate);
	   
	   @Query(value="SELECT userstate FROM friend WHERE requserid IN (:requserid, :userid) AND userid IN (:requserid, :userid) AND userstate = 2", nativeQuery = true)
	   public List<Integer> isFriend(@Param("requserid") int requserid, @Param("userid") int userid);
	
		@Query(value="select u.userid, u.name, u.gender, CONCAT('" + UserFunction.ImgPath + "', i.imgpath) imgpath from user u"
				+ ", (select requserid id from friend where userid = :userid and userstate = 2"
				+ " union all"
				+ " select userid id from friend where requserid = :userid and userstate = 2) f"
				+ ", (select i.id, i.imgpath	from img i"
				+ ", (select id, min(imgid) imgid from img where gubun = 1 group by id) mi "
				+ " where i.gubun = 1 and i.imgid = mi.imgid) i"
				+ " where u.userid = f.id and u.userid = i.id ORDER BY u.userid LIMIT :first, :last", nativeQuery=true)
		public List<Map<String, Object>> getFriendList(@Param("userid") int userid, @Param("first") int first, @Param("last") int last);
		
		
		//친구 상태 update
		   @Modifying
		   @Transactional
		   @Query(value="update friend set userstate = :userstate where userid = :userid and requserid = :requestid ",nativeQuery=true)
		   public void FriendUpdate(@Param("userstate")int userstate, @Param("userid")int userid, @Param("requestid")int requestid);
		   
		//김성연
		//로그인시 친구목록리스트
		@Query(value="select u.userid id, u.name nickName, CONCAT('" + UserFunction.ImgPath + "', i.imgpath)  image " + 
				"  from user u inner join  " + 
				"		(select requserid id " + 
				"		   from friend " + 
				"				  where userid = :userid " + 
				"				    and userstate = 2 " + 
				"				  union all  " + 
				"				  select userid id  " + 
				"				   from friend  " + 
				"				  where requserid = :userid" + 
				"				    and userstate = 2) f on (u.userid = f.id) " + 
				"			left join " + 
				"				(select id, imgpath " + 
				"				from img  " + 
				"				where gubun = 1 " + 
				"				group by id) i  on (u.userid = i.id )" , nativeQuery=true)
		public List<Map<String, Object>> getLoginFriendList(@Param("userid") int userid);
		
		
		
}