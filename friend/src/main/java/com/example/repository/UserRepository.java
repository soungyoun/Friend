package com.example.repository;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.UserFunction;
import com.example.model.User;

public interface UserRepository extends JpaRepository<User, Integer>{
	//김성연
	@Query(value="select u.email from user u where u.userid=:userid",nativeQuery=true)
	   public String getUserEmail(@Param("userid") int userid);

	//최재현
	   @Query(value="select u.name from user u where u.userid=:userid",nativeQuery=true)
	   public String getUserName(@Param("userid") int userid);
	
	   @Query(value="select birth from user where userid=:userid",nativeQuery=true)
	   public String getUserBirth(@Param("userid") int userid);
	   @Query(value="select city from user where userid=:userid",nativeQuery=true)
	   public int getCity(@Param("userid") int userid);
	   
	   @Query(value="select gu from user where userid=:userid",nativeQuery=true)
	   public int getGu(@Param("userid") int userid);

	   @Query(value="select * from user where userid=:userid",nativeQuery=true)
	   public User getUser(@Param("userid")int userid);	   
	   
	   // 1-1-4 인기친구
	   @Query(value = "select u.* from user u left join" + 
	         "(select id, count(*)*2 hitcnt from hit where gubun = 1 and date(hittime) >= date(subdate(now(), Interval 30 day)) group by id) a " + 
	         "on u.userid = a.id left join " + 
	         " (select userid, count(*) nocnt from noticecom " + 
	         "  where date(writedate) >= date(subdate(now(), Interval 30 day)) group by userid) b " + 
	         " on u.userid = b.userid order by (ifnull(a.hitcnt,0) + ifnull(b.nocnt,0)) desc limit 6;", nativeQuery = true)
	   public List<User> topFriends();
	   
	   
	   
	@Query(value="select userid from user where email=:email",nativeQuery=true)
	public int getUserId(@Param("email") String email);

	//id에 해당하는 모든정보
	User findByEmail(@Param("email") String id);
	
	//seq에 해당하는 모든정보
	User findByUserid(@Param("userid") int seq);
	
	//id, pass check
	@Query(value="select id  from user where id = :id and pw =:pw", nativeQuery=true)
	public Object CheckLogin(@Param("id") String id, @Param("pw") String pw);
	
	// find pw
	@Query(value="select pw from user where id = :id", nativeQuery=true)
	public String passwardById(@Param("id") String id);
	
	
	//권샘찬
	//id_check
	User findByemail(String email);
	//정보 수정
	   @Modifying
	   @Query(value="UPDATE user SET pw=:pw, name=:name, city=:city, gu=:gu, areayn=:areayn, birth=:birth, birthyn=:birthyn"
	         + ", gender=:gender, genderyn=:genderyn, msg=:msg, intro=:intro, friendsyn=:friendsyn, groupsyn=:groupsyn"
	         + " WHERE userid=:userid", nativeQuery=true)
	   public void updateUser(@Param("userid") int userid, @Param("pw") String pw, @Param("name")String name, @Param("city")int city, @Param("gu")int gu
	         , @Param("birth")Date birth, @Param("gender")int gender, @Param("areayn")boolean areayn
	         , @Param("genderyn")boolean genderyn, @Param("birthyn")boolean birthyn, @Param("friendsyn")boolean friendsyn
	         , @Param("groupsyn")boolean groupsyn, @Param("msg")String msg, @Param("intro")String intro);
	
//	//id, pass check
//	@Query(value="SELECT email FROM user WHERE email = :email and pw =:pw", nativeQuery=true)
//	public Object CheckLogin(String email, String pw);
	
	@Modifying
	@Transactional
	@Query(value="UPDATE user SET auth = 1 WHERE email = :email", nativeQuery=true)
	public void setAuth(@Param("email") String email);
	
	@Modifying
	@Transactional
	@Query(value="UPDATE user SET pw = :pw WHERE email = :email", nativeQuery=true)
	public void setPassword(@Param("email") String email, @Param("pw") String pw);
	
	   @Query(value="SELECT email FROM user WHERE userid=:userid", nativeQuery=true)
	   public String getUserEmail(@Param("userid") Integer userid);
	   
	   //마이 페이지에 필요한 친구의 정보
	   @Query(value="SELECT userid, name, city, gu, birth, gender, intro " + 
	         "FROM user" + 
	         "WHERE userid = :userid", nativeQuery=true)
	   public Map<String, Object> getFriendProfile(@Param("userid") int userid);
	   
	   //마이 페이지에 필요한 나의 정보
	   @Query(value="SELECT name, city, gu, birth, gender, intro " + 
	         "FROM user" + 
	         "WHERE userid = :userid", nativeQuery=true)
	   public Map<String, Object> getMyProfile(@Param("userid") Integer userid);
	   
	   //정보 수정에 필요한 나의 정보
	   @Query(value="SELECT email, userid, name, city, gu, birth, gender, intro, msg"
	         + ", areayn, birthyn, phoneyn, friendsyn, groupsyn, genderyn"
	         + " FROM user WHERE userid = :userid", nativeQuery=true)
	   public Map<String, Object> getMyAllProfile(@Param("userid") int userid);	
	   //이메일로 시퀀스 찾기
	   @Query(value="SELECT userid FROM user WHERE email=:email", nativeQuery=true)
	   public int getUserid(@Param("email") String email);
   
   //성기훈
	// 3-1 추천친구 첫번째 (1/5) 관심사 중 하나가 일치하고 city와 gu가 일치하는
	@Query(value = "select * from user where name is not null and city=:city and gu=:gu and userid in "
			+ "(select userid from userinterest where code in (:code)) and userid not in "
			+ "(select userid id from friend where requserid = :userid and userstate = 2 union "
			+ "select requserid id from friend where userid = :userid and userstate = 2)", nativeQuery = true)
	public List<User> recommend_1(@Param("userid") int userid, @Param("city") int city, @Param("gu") int gu,
			@Param("code") List<Integer> code);

	// 3-1 추천친구 두번째 (2/5) 관심사 중 하나가 일치하고 city가 일치하고 gu가 일치하지 않는
	@Query(value = "select * from user where name is not null and city=:city and gu!=:gu and userid in "
			+ "(select userid from userinterest where code in (:code)) and userid not in "
			+ "(select userid id from friend where requserid = :userid and userstate = 2 union "
			+ "select requserid id from friend where userid = :userid and userstate = 2)", nativeQuery = true)
	public List<User> recommend_2(@Param("userid") int userid, @Param("city") int city, @Param("gu") int gu,
			@Param("code") List<Integer> code);

	// 3-1 추천친구 세번째 (3/5) 관심사 중 하나가 일치하고 city가 일치하지않고 gu가 일치하는
	@Query(value = "select * from user where name is not null and city!=:city and gu=:gu and userid in "
			+ "(select userid from userinterest where code in (:code)) and userid not in "
			+ "(select userid id from friend where requserid = :userid and userstate = 2 union "
			+ "select requserid id from friend where userid = :userid and userstate = 2)", nativeQuery = true)
	public List<User> recommend_3(@Param("userid") int userid, @Param("city") int city, @Param("gu") int gu,
			@Param("code") List<Integer> code);

	// 3-1 추천친구 네번째 (4/5) 관심사 중 하나가 일치하고 city와 gu가 일치하는 않는
	@Query(value = "select * from user where name is not null and city!=:city and gu!=:gu and userid in "
			+ "(select userid from userinterest where code in (:code)) and userid not in "
			+ "(select userid id from friend where requserid = :userid and userstate = 2 union "
			+ "select requserid id from friend where userid = :userid and userstate = 2)", nativeQuery = true)
	public List<User> recommend_4(@Param("userid") int userid, @Param("city") int city, @Param("gu") int gu,
			@Param("code") List<Integer> code);

	// 3-1 추천친구 다섯번째 (5/5) 관심사가 일치하지 않는
	@Query(value = "select * from user where name is not null and userid not in"
			+ "(select userid from userinterest where code in (:code)) and userid not in "
			+ "(select userid id from friend where requserid = :userid and userstate = 2 union "
			+ "select requserid id from friend where userid = :userid and userstate = 2)", nativeQuery = true)
	public List<User> recommend_5(@Param("userid") int userid, @Param("code") List<Integer> code);

   @Query(value="SELECT pw FROM user WHERE userid = :userid", nativeQuery=true)
   public String getPassword(@Param("userid") int userid);
   
	@Modifying
	@Transactional
	@Query(value="UPDATE user SET name = :name WHERE userid = :userid", nativeQuery=true)
	public void updateTest(@Param("userid") int userid, @Param("name") String name);

	
	//김성연
	//친구요청 목록(실시간 소켓 전송)
	@Query(value="select u.birth, u.name, u.gender, CONCAT('" + UserFunction.ImgPath + "', i.image) image " + 
			            " from user u inner join " + 
			            "(select id, min(imgpath) image from img where gubun = 1 and id = :userid ) i " + 
			            "on (u.userid = i.id and u.userid = :userid ) ", nativeQuery=true)
	public Map<String, Object> getFrindRequest(@Param("userid") int userid);
	
	//친구요청 목록(로그인시)
	@Query(value="select u.birth, u.name, u.gender,  CONCAT('" + UserFunction.ImgPath + "', i.image) image " + 
			            " from user u inner join " + 
			            "(select id, min(imgpath) image from img where gubun = 1 and id = :userid ) i " + 
			            "on (u.userid = i.id and u.userid = :userid ) ", nativeQuery=true)
	public List<Map<String, Object>> getFrindRequestList(@Param("userid") int userid);
}




