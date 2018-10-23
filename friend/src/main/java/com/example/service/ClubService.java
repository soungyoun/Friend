package com.example.service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;
import javax.transaction.Transactional;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.multipart.MultipartFile;

import com.example.UserFunction;
import com.example.model.Alarm;
import com.example.model.Club;
import com.example.model.Clubinterest;
import com.example.model.Clubnotice;
import com.example.model.Clubuser;
import com.example.model.Feel;
import com.example.model.Hit;
import com.example.model.Img;
import com.example.model.Noticecom;
import com.example.model.User;
import com.example.repository.ClubRepository;
import com.example.repository.ClubUserRepository;
import com.example.repository.ClubnoticeRepository;
import com.example.repository.FeelRepository;
import com.example.repository.HitRepository;
import com.example.repository.ImgRepository;
import com.example.repository.NoticecomRepository;
import com.example.repository.AlarmRepository;
import com.example.repository.ClubInterestRepository;
import com.example.repository.UserRepository;

import com.example.repository.UserInterestRepository;

@Service
public class ClubService {
   @Autowired
   EntityManager entityManager;

   @Autowired
   private ClubUserRepository clubUserRepository;
   @Autowired
   private ClubRepository clubRepository;
   @Autowired
   private ClubInterestRepository clubinterestRepository;
   @Autowired
   private UserRepository userRepository;
   @Autowired
   private UserInterestRepository userinterestRepository;
   @Autowired
   private ImgRepository imgRepository;
   @Autowired
   private AlarmRepository alarmRepository;
   @Autowired
   private ClubnoticeRepository clubnoticeRepository;
   @Autowired
   private NoticecomRepository noticecomRepository;
   @Autowired
   private HitRepository hitRepository;
   @Autowired
   private FeelRepository feelRepository;
   
   @Autowired
   private UserFunction userFunction;

   public void setEntityManager(EntityManager entityManager) {
      this.entityManager = entityManager;
   }

   //2-2-2 mepage
   public Map<String,Object> mePage(int token, int id, int page) {
      // TODO Auto-generated method stub

      List<Map<String,Object>> clubinfoMap=new ArrayList<>();
      List<Integer> indexList=new ArrayList<>();
   
      List<Map<String,Object>> mapList=new ArrayList<>();
      if(id!=0) {
         clubinfoMap=clubRepository.getuserClubinfo(id, page*10-10,page*10);
      }
      else {
         clubinfoMap=clubRepository.getuserClubinfo(token, page*10-10, page*10);
      }
      
      
      for(int i=0;i<clubinfoMap.size();i++) {
         Map<String,Object> map=new HashMap<>();
   
         map.put("id", clubinfoMap.get(i).get("clubid"));
         map.put("groupName", clubinfoMap.get(i).get("name"));
         map.put("image",clubinfoMap.get(i).get("imgpath"));
         indexList.add((Integer)clubinfoMap.get(i).get("clubid"));
         mapList.add(map);
      }
      
      int size=clubinfoMap.size()/10+1;
      Map<String,Object> remap=userFunction.ListToMap(mapList,"id");
      remap.put("groupsPages", size);
      
      return remap;
      
 
   }
   
   
   // 그룹정보 가져오기
   public List<Club> searchGroup(int page, boolean filter, String keyword, int searchOption, int si, int gu,
         int gender, int minAge, int maxAge, int interest, int userid) {
      CriteriaBuilder builder = entityManager.getCriteriaBuilder();
      CriteriaQuery<Club> query = builder.createQuery(Club.class);
      Root<Club> root = query.from(Club.class);
      List<Predicate> predicates = new ArrayList<>();

      /*
       * Subquery sub=query.subquery(ClubInterest.class); Root
       * subRoot=sub.from(ClubInterest.class); sub.select(subRoot.get("clubid"));
       * sub.where(builder.equal(subRoot.get("code"),interest));
       */
      if (filter == true) {
         if (keyword != null) {

            switch (searchOption) {
            case 0:
               predicates.add(builder.like(root.get("name"), "%" + keyword + "%"));
               // sql=sql+"and name="+keyword;
               break;
            case 1:
               predicates.add(builder.like(root.get("content"), "%" + keyword + "%"));
               // sql=sql+"and content="+keyword;
               break;
            case 2:
               predicates.add((builder.or(builder.like(root.get("name"), "%" + keyword + "%"),
                     builder.like(root.get("content"), "%" + keyword + "%"))));
               // sql=sql+"and name="+keyword+" or content="+keyword;
               break;
            }
         }
         if (si != 0) {
            predicates.add(builder.equal(root.get("city"), si));
            // sql=sql+"and si="+si;
         }
         if (gu != 0) {
            predicates.add(builder.and(builder.equal(root.get("gu"), gu)));
            // sql=sql+"and gu="+gu;
         }
         if (minAge != 0) {

            // ge : 첫번째식이 두번째식보다 크거나 같은지
            predicates.add(builder.and(builder.ge(root.get("agestart"), minAge)));
            // sql=sql+"and minage>="+minAge;
         }
         if (maxAge != 0) {
            // le : 첫번째식이 두번째식보다 작거나같은지
            predicates.add(builder.and(builder.le(root.get("ageend"), maxAge)));
            // sql=sql+"and maxage<="+maxAge;
         }
         if (interest != 0) {

            Subquery<Clubinterest> sub = query.subquery(Clubinterest.class);
            Root<Clubinterest> inRoot = sub.from(Clubinterest.class);
            Predicate pd = builder.equal(inRoot.get("code"), interest);
            sub.select(inRoot.get("clubid"));
            sub.where(pd);

            predicates.add(builder.and(builder.in(root.get("clubid")).value(sub)));
         }
         query.select(root).where(predicates.toArray(new Predicate[] {}));

         TypedQuery<Club> typedQuery = entityManager.createQuery(query).setFirstResult(page*10-10).setMaxResults(10);
         List<Club> clubList = typedQuery.getResultList();

         // for (Club list : clubList) { System.out.println(list+"아잇"); }
         return clubList;
      }

      // 필터값이 false일때
      // Page값을받아서 화면에 그룹정보 출력
      else {

         return null;
      }

   }

   // 그룹관심사리스트 가져오기
   public List<List<Clubinterest>> getClubInterestList(int page, boolean filter, String keyword, int searchOption,
         int si, int gu, int gender, int minAge, int maxAge, int interest, int userid) {
      CriteriaBuilder builder = entityManager.getCriteriaBuilder();
      CriteriaQuery<Clubinterest> query = builder.createQuery(Clubinterest.class);
      Root<Clubinterest> root = query.from(Clubinterest.class);
      List<Predicate> predicates = new ArrayList<>();
      List<Club> clubList = searchGroup(page, filter, keyword, searchOption, si, gu, gender, minAge, maxAge, interest,
            userid);
      TypedQuery<Clubinterest> typedQuery;
      List<Clubinterest> interestList = new ArrayList<>();
      List<List<Clubinterest>> resultList = new ArrayList<>();
      for (int i = 0; i < clubList.size(); i++) {
         predicates.add(builder.equal(root.get("clubid"), clubList.get(i).getClubid()));
         query.select(root.get("code")).where(predicates.toArray(new Predicate[] {}));
         typedQuery = entityManager.createQuery(query);
         interestList = typedQuery.getResultList();
         resultList.add(interestList);
         predicates.remove(0);

      }
      return resultList;
   }

   // 그룹유저정보 가져오기(인원수)
   public List<List<Clubuser>> getMembercnt(int page, boolean filter, String keyword, int searchOption, int si, int gu,
         int gender, int minAge, int maxAge, int interest, int userid) {
      CriteriaBuilder builder = entityManager.getCriteriaBuilder();
      CriteriaQuery<Clubuser> query = builder.createQuery(Clubuser.class);
      Root<Clubuser> root = query.from(Clubuser.class);
      List<Predicate> predicates = new ArrayList<>();
      List<Club> clubList = searchGroup(page, filter, keyword, searchOption, si, gu, gender, minAge, maxAge, interest,
            userid);
      TypedQuery<Clubuser> typedQuery;
      List<Clubuser> userList = new ArrayList<>();
      List<List<Clubuser>> result = new ArrayList<>();
      for (int i = 0; i < clubList.size(); i++) {
         predicates.add(builder.equal(root.get("clubid"), clubList.get(i).getClubid()));
         query.select(root.get("clubid")).where(predicates.toArray(new Predicate[] {}));

         typedQuery = entityManager.createQuery(query);
         userList = typedQuery.getResultList();
         result.add(userList);
         predicates.remove(0);

      }
      return result;
   }

   // 보내줄 그룹정보 4-1
   public Map<String, Object> groupInfo(int userid, int seq,int page) {
	      List<Integer> userList = new ArrayList<>();
	      userList = clubUserRepository.getMemberId(seq);
	      List<String> userBitrhList = new ArrayList<>();
	      List<String> userYear = new ArrayList<>();
	      List<Integer> userYearInt = new ArrayList<>();
	      int resultYear = 0;
	      int yearAvg = 0;
	      Date nowDate = new Date();
	      // nowDate.getYear() : 현재년도-1900 (1900년 기준)
	      for (int i = 0; i < userList.size(); i++) {
	         userBitrhList.add(userRepository.getUserBirth(userList.get(i)));
	         userYear.add(userBitrhList.get(i).split("-")[0]);
	         userYearInt.add(Integer.parseInt(userYear.get(i)) - 1900);
	         resultYear = resultYear + nowDate.getYear() - userYearInt.get(i) + 1;
	      }

	      yearAvg = resultYear / userYear.size();
	      
	      Club c = clubRepository.groupInfo(seq);
	      Map<String, Object> groupInfoMap = new HashMap<>();
	      Map<String, Object> master = new HashMap<>();
	      List<Integer> interestList = clubinterestRepository.interestList(seq);
	   //   List<Integer> interest = new ArrayList<>();
	      /*
	       * for(int i=0;i<interestList.size();i++) {
	       * interest.add(interestList.get(i).getCode()); }
	       */
	      int masterid = clubUserRepository.getMaster(seq);

	      master.put("id", masterid);
	      master.put("nickName", userRepository.getUserName(masterid));

	      imgRepository.getImgyn(2, c.getClubid());
	      List<String> imgList = new ArrayList<>();
	      List<String> imgPathList = new ArrayList<>();
	      int ismygroup=0;
	         if(clubUserRepository.isMyClub(c.getClubid(), userid)==null) {
	            ismygroup=0;
	         }
	         else {
	            ismygroup=clubUserRepository.isMyClub(c.getClubid(), userid);
	         }
	         imgPathList = imgRepository.getImgpath(2, c.getClubid());
	      
	     /* for (int i = 0; i < imgPathList.size(); i++) {
	         Map<String, Object> imgMap = new HashMap<>();
	         imgMap.put("image",imgPathList.get(i));
	         imgList.add(imgMap);
	      }*/

	 
	      List<Integer> useridList=clubUserRepository.getMemberId(c.getClubid());
	      List<Map<String,Object>> userlist=new ArrayList<>();
	      for(int i=0;i<useridList.size();i++) {
	         Map<String,Object> ma=new HashMap<>();
	         ma.put("username", userRepository.getUser(useridList.get(i)).getName());
	         ma.put("usergender", userRepository.getUser(useridList.get(i)).getGender());
	         ma.put("useremail", userRepository.getUser(useridList.get(i)).getEmail());
	         userlist.add(ma);
	      }
	      
	      int membercnt = clubUserRepository.getMemberCount(seq);
	      groupInfoMap.put("id", c.getClubid());
	 		groupInfoMap.put("groupName", c.getName());
	      groupInfoMap.put("master", master);
	      groupInfoMap.put("interests", interestList);
	      groupInfoMap.put("estDate", c.getStartdate());
	      groupInfoMap.put("memberCnt", membercnt);
	      groupInfoMap.put("maxMember", c.getMaxcount());

	      groupInfoMap.put("minAge",c.getAgestart());
	      groupInfoMap.put("maxAge",c.getAgeend());
	      groupInfoMap.put("avgAge", yearAvg);
	      groupInfoMap.put("gender", c.getGender());
	      groupInfoMap.put("si", c.getCity());
	      groupInfoMap.put("gu", c.getGu());
	      groupInfoMap.put("intro", c.getContent());
	      groupInfoMap.put("images", imgPathList);
	 
	      Map<String,Object> resultMap=new HashMap<>();
	      resultMap.put("group", groupInfoMap);
	      
	      
	      
	      
	      
	      //내 그룹일때
	      if(ismygroup==2) {
	    	  List<Map<String,Object>> cuList=new ArrayList<>();
	    	  List<Clubuser> culist=clubUserRepository.getclubuser(c.getClubid());
	    	  Map<Integer,Object> postMap=new HashMap<>();
	    	  
	    	  int usersu=0;
	    	  List<Clubnotice> noticelist=clubnoticeRepository.getClubnotices(c.getClubid());
	    	  if(page*20>=noticelist.size()&&noticelist.size()>page*20-20) {
	    	  for(int i=page*20-20;i<noticelist.size();i++) {
	    			int count=0;
					List<Feel> flist=feelRepository.liked(3, noticelist.get(i).getNoticeid());
						for(int k=0;k<flist.size();k++) {
							if(flist.get(k).getUserid()==userid) {
								count++;
							}
						}
						boolean liked=false;
						if(count==1) {
							liked=true;
						}
	    		  
	    		  
	    		  Map<String,Object> map=new HashMap<>();
	    		  Map<String,Object> userMap=new HashMap<>();
	    		  userMap.put("id", noticelist.get(i).getUserid());
	    		  userMap.put("nickName",  userRepository.getUserName(noticelist.get(i).getUserid()));
	    		  userMap.put("image", imgRepository.getImg(1, noticelist.get(i).getUserid())); //유저이미지
	    		  
	    		  map.put("type", noticelist.get(i).getType());
	    		  map.put("id", noticelist.get(i).getNoticeid());
	    		  map.put("title", noticelist.get(i).getTitle());
	    		  map.put("content", noticelist.get(i).getContent());
	    		  map.put("writedate", noticelist.get(i).getWritedate());
	    		  map.put("user", userMap);
	    		  map.put("views", hitRepository.readcounts(3, noticelist.get(i).getNoticeid()));
	    		  map.put("likes", feelRepository.likes(3, noticelist.get(i).getNoticeid()));
	    		  map.put("liked", liked);
	    		  Map<String,Object> comments=new HashMap<>();
	    		  comments.put("id", 0);
	    		 List<Noticecom> notice= noticecomRepository.noticecom(noticelist.get(i).getNoticeid());
	    		 List<Map<String,Object>> reMap=new ArrayList<>();
	    		 for(int k=0;k<notice.size();k++) {
	    			 Map<String,Object> coMap=new HashMap<>();
	    			 Map<String,Object> uMap=new HashMap<>();
	    			 uMap.put("id", notice.get(k).getUserid());
	    			 uMap.put("nickName", userRepository.getUserName(notice.get(k).getUserid()));
	    			 uMap.put("image", imgRepository.getImg(1, notice.get(k).getUserid()));
	    			 coMap.put("id", notice.get(k).getComid());
	    			 coMap.put("user", uMap);
	    			 coMap.put("content",notice.get(k).getContent());
	    			 coMap.put("writedate", notice.get(k).getWritedate());
	    			 reMap.add(coMap);
	    		 }
	    		 map.put("comments", userFunction.ListToMap(reMap, "id"));
	    		 
	    		 Boolean hasMorePages=true;
	    		 if(noticelist.size()/20+1==page)
	    			 hasMorePages=false;
	    		 resultMap.put("hasMorePages", hasMorePages);
	    	  Map<String,Object> memberMap=new HashMap<>();
	    	  
	    	  
	    	 
	    	  usersu=culist.size();
	    	
	    	
	    		postMap.put(noticelist.get(i).getNoticeid(), map);
	    	  }
	    
	      }
	    	  
	    	  else if(page*20<noticelist.size()) {
	    		  for(int i=page*20-20;i<page*20;i++) {
	    				int count=0;
	    				List<Feel> flist=feelRepository.liked(3, noticelist.get(i).getNoticeid());
	    					for(int k=0;k<flist.size();k++) {
	    						if(flist.get(k).getUserid()==userid) {
	    							count++;
	    						}
	    					}
	    					boolean liked=false;
	    					if(count==1) {
	    						liked=true;
	    					}
	    			  
	    			  
	    			  Map<String,Object> map=new HashMap<>();
		    		  Map<String,Object> userMap=new HashMap<>();
		    		  userMap.put("id", noticelist.get(i).getUserid());
		    		  userMap.put("nickName",  userRepository.getUserName(noticelist.get(i).getUserid()));
		    		  userMap.put("image", imgRepository.getImg(1, noticelist.get(i).getUserid())); //유저이미지
		    		  
		    		  map.put("type", noticelist.get(i).getType());
		    		  map.put("id", noticelist.get(i).getNoticeid());
		    		  map.put("title", noticelist.get(i).getTitle());
		    		  map.put("content", noticelist.get(i).getContent());
		    		  map.put("writedate", noticelist.get(i).getWritedate());
		    		  map.put("user", userMap);
		    		  map.put("views", hitRepository.readcounts(3, noticelist.get(i).getNoticeid()));
		    		  map.put("likes", feelRepository.likes(3, noticelist.get(i).getNoticeid()));
		    		  map.put("liked", liked);
		    		  Map<String,Object> comments=new HashMap<>();
		    		  comments.put("id", 0);
		    		 List<Noticecom> notice= noticecomRepository.noticecom(noticelist.get(i).getNoticeid());
		    		 List<Map<String,Object>> reMap=new ArrayList<>();
		    		 for(int k=0;k<notice.size();k++) {
		    			 Map<String,Object> coMap=new HashMap<>();
		    			 Map<String,Object> uMap=new HashMap<>();
		    			 uMap.put("id", notice.get(k).getUserid());
		    			 uMap.put("nickName", userRepository.getUserName(notice.get(k).getUserid()));
		    			 uMap.put("image", imgRepository.getImg(1, notice.get(k).getUserid()));
		    			 coMap.put("id", notice.get(k).getComid());
		    			 coMap.put("user", uMap);
		    			 coMap.put("content",notice.get(k).getContent());
		    			 coMap.put("writedate", notice.get(k).getWritedate());
		    			 reMap.add(coMap);
		    		 }
		    		 map.put("comments", userFunction.ListToMap(reMap, "id"));
		    		 
		    		 Boolean hasMorePages=true;
		    		 if(noticelist.size()/20+1==page)
		    			 hasMorePages=false;
		    		 resultMap.put("hasMorePages", hasMorePages);
		    	  Map<String,Object> memberMap=new HashMap<>();
		    	  
		    	  
		    	 
		    	  usersu=culist.size();
		    	
		    	
		    		postMap.put(noticelist.get(i).getNoticeid(), map);
		    	  }
		    
		      }
	    		for(int a=0;a<culist.size();a++) {
	    			Map<String,Object> cuMap=new HashMap<>();
	    			cuMap.put("id", culist.get(a).getUserid());
	    			cuMap.put("nickName", userRepository.getUserName(culist.get(a).getUserid()));
	    			cuMap.put("gender", userRepository.getUser(culist.get(a).getUserid()).getGender());
	    			cuMap.put("image", imgRepository.getImg(1, culist.get(a).getUserid()));
	    			cuMap.put("online", false);
	    			cuList.add(cuMap);
	    		}
	    	  resultMap.put("posts", postMap);
	    	  resultMap.put("memberPages", culist.size()/10+1);
	    	  
	    	  resultMap.put("members", userFunction.ListToMap(cuList, "id"));
	      }
	      
	      resultMap.put("isMyGroup", ismygroup);
	      
	      
	      return resultMap;
	   }

// 5-1 Groups Page이동 및 그룹 더읽어오기 ClubService
   public Map<String, Object> searchGroups(int page, boolean filter, String keyword, int searchOption, int si, int gu,
         int gender, int minAge, int maxAge, int interest, int userid) {
      if (filter == true) {
         List<Club> clubList = searchGroup(page, filter, keyword, searchOption, si, gu, gender, minAge, maxAge,
               interest, userid);

         List<Map<String, Object>> groups = new ArrayList<>();
         List<List<Clubinterest>> interestList = getClubInterestList(page, filter, keyword, searchOption, si, gu,
               gender, minAge, maxAge, interest, userid);
         List<List<Clubuser>> userList = getMembercnt(page, filter, keyword, searchOption, si, gu, gender, minAge,
               maxAge, interest, userid);
         Map<String, Object> returnMap = new HashMap<>();
         Map<String, Object> mMap = new HashMap<>();

      
         for(int i=0;i<clubList.size();i++) {
        	 
        
            	  Map<String, Object> map = new HashMap<>();
                  map.put("id", clubList.get(i).getClubid());
                  map.put("groupName", clubList.get(i).getName());
                  map.put("memberCnt", userList.get(i).size());
                  map.put("maxMember", clubList.get(i).getMaxcount());
                  map.put("image",imgRepository.getImg(2, clubList.get(i).getClubid()));
                  map.put("intro", clubList.get(i).getContent());
                  map.put("interests", interestList.get(i));
                 
                  
                  map.put("si",  clubRepository.groupInfo(clubList.get(i).getClubid()).getCity());
                  map.put("gu",  clubRepository.groupInfo(clubList.get(i).getClubid()).getGu());
                  groups.add(map);
                  //si gu 
         }
            //   }
               boolean cuPage=true;
               if(clubList.size()!=10) {
               	cuPage=false;
               }
               returnMap = userFunction.ListToMap(groups, "id");
               Map<String,Object> remap=new HashMap<>();
               remap.put("groups", returnMap);
               remap.put("hasMorePages", cuPage);
               return remap;
      

      } else {
         return recoGroup(userid, page);
      }
   }
   public Map<String,Object> searchResult(int page, boolean filter, String keyword, int searchOption, int si, int gu,
	         int gender, int minAge, int maxAge, int interest, int userid){
	   Map<String,Object> map=new HashMap<>();
	   boolean has;
	  
	   return null;
   }
  
// 1-1-3 ClubService 인기그룹
   public Map<String, Object> topClub() {
      List<Club> topClubList = clubRepository.topGroup();

      List<Map<String, Object>> topGroups = new ArrayList<>();
      Map<String, Object> returnMap = new HashMap<>();
      for (int i = 0; i < topClubList.size(); i++) {
         
         Map<String, Object> masterMap = new HashMap<>();

         masterMap.put("userid", clubUserRepository.getMaster(topClubList.get(i).getClubid()));
         masterMap.put("name",
               userRepository.getUserName(clubUserRepository.getMaster(topClubList.get(i).getClubid())));
         List<Integer> interestList = clubinterestRepository.interestList(topClubList.get(i).getClubid());

         Map<String, Object> map = new HashMap<>();
         map.put("seq", topClubList.get(i).getClubid());
         map.put("groupName", topClubList.get(i).getName());
         map.put("master", masterMap);
         map.put("estDate", topClubList.get(i).getStartdate());
         map.put("memberCnt", clubUserRepository.getMemberCount(topClubList.get(i).getClubid()));
         map.put("maxMember", topClubList.get(i).getMaxcount());
         
         map.put("img", imgRepository.getImg(2, topClubList.get(i).getClubid()));
         
         map.put("groupintro", topClubList.get(i).getContent());
         map.put("interests", interestList);
         
         topGroups.add(map);
         
      }
      returnMap = userFunction.ListToMap(topGroups, "seq");

      return returnMap;
   }
   
   // 추천그룹 userid가져온다고 가정.(아닐시 token을 이용해 userid구함)

   // 5-1 Groups Page이동 및 그룹 더읽어오기 ClubService
   public Map<String, Object> recoGroup(int userid, int page) {
      int city = userRepository.getCity(userid);
      int gu = userRepository.getGu(userid);
      int[] code = userinterestRepository.getUserInterestS(userid);
      List<Club> clubList1 = clubRepository.recommend_1(city, gu, code,userid);
      List<Club> clubList2 = clubRepository.recommend_2(city, gu, code,userid);
      List<Club> clubList3 = clubRepository.recommend_3(city, gu, code,userid);
      List<Club> clubList4 = clubRepository.recommend_4(city, gu, code,userid);
      List<Club> clubList5 = clubRepository.recommend_5(code,userid);
      List<Club> result = new ArrayList<>();
      for (int i = 0; i < clubList1.size(); i++) {
         result.add(clubList1.get(i));
      }

      for (int i = 0; i < clubList2.size(); i++) {
         result.add(clubList2.get(i));
      }
      for (int i = 0; i < clubList3.size(); i++) {
         result.add(clubList3.get(i));
      }
      for (int i = 0; i < clubList4.size(); i++) {
         result.add(clubList4.get(i));
      }
      for (int i = 0; i < clubList5.size(); i++) {
         result.add(clubList5.get(i));
      }
      // seq값으로 찾을 수 있게 순번리스트를 하나 만들어서 보냄

      Map<String, Object> recoGroups = new HashMap<>();
   //   Map<String, Object> recoGroups2 = new HashMap<>();
      List<Map<String, Object>> resultList = new ArrayList<>();
      if (page * 10 >= result.size() && result.size() > page * 10 - 10) {
         for (int i = page * 10 - 10; i < result.size(); i++) {
   //         List<Integer> seqList = new ArrayList<>();
        

            Map<String, Object> innerMap = new HashMap<>();
            Map<String, Object> masterMap = new HashMap<>();
      //      masterMap.put("userid", clubUserRepository.getMaster(result.get(i).getClubid()));
      //      masterMap.put("name",
        //          userRepository.getUserName(clubUserRepository.getMaster(result.get(i).getClubid())));
            List<Integer> interestList = clubinterestRepository.interestList(result.get(i).getClubid());
            // seqList.add(result.get(i).getClubid());
            innerMap.put("id", result.get(i).getClubid());
            innerMap.put("groupName", result.get(i).getName());
          //  innerMap.put("master", masterMap);
            innerMap.put("interests", interestList);
       //     innerMap.put("estDate", result.get(i).getStartdate());
            String imglist=imgRepository.getImg(2, result.get(i).getClubid());
            innerMap.put("image",imglist);
            innerMap.put("si",  clubRepository.groupInfo(result.get(i).getClubid()).getCity());
            innerMap.put("gu",  clubRepository.groupInfo(result.get(i).getClubid()).getGu());
            innerMap.put("intro", result.get(i).getContent());
            innerMap.put("maxMember", clubRepository.groupInfo(result.get(i).getClubid()).getMaxcount());
            innerMap.put("memberCnt", clubUserRepository.getMemberCount(result.get(i).getClubid()));
            resultList.add(innerMap);
            //maxMember memerCnt
         }

         /*
          * recoGroups2=userFunction.ListToMap(seqMap, "key"); resultList.add(recoGroups2);
          */
         recoGroups = userFunction.ListToMap(resultList, "id");
         boolean cuPage=true;
         if(page==result.size()/10+1) {
         	cuPage=false;
         }
         Map<String,Object> remap=new HashMap<>();
         remap.put("groups", recoGroups);
         remap.put("hasMorePages", cuPage);
         return remap;
      } else if (page * 10 < result.size()) {
         for (int i = page * 10 - 10; i < page * 10; i++) {
            Map<String, Object> innerMap = new HashMap<>();
            Map<String, Object> masterMap = new HashMap<>();
       //     masterMap.put("userid", clubUserRepository.getMaster(result.get(i).getClubid()));
      //      masterMap.put("name",
      //            userRepository.getUserName(clubUserRepository.getMaster(result.get(i).getClubid())));
            List<Integer> interestList = clubinterestRepository.interestList(result.get(i).getClubid());

            innerMap.put("id", result.get(i).getClubid());
            innerMap.put("groupName", result.get(i).getName());
     //       innerMap.put("master", masterMap);
            innerMap.put("interests", interestList);
        //    innerMap.put("estDate", result.get(i).getStartdate());
      //      innerMap.put("image",imgRepository.getImgpath(2, result.get(i).getClubid()));
            String imglist=imgRepository.getImg(2, result.get(i).getClubid());
            innerMap.put("image", imglist);
            innerMap.put("si",  clubRepository.groupInfo(result.get(i).getClubid()).getCity());
            innerMap.put("gu",  clubRepository.groupInfo(result.get(i).getClubid()).getGu());
            innerMap.put("Intro", result.get(i).getContent());
            innerMap.put("maxMember", clubRepository.groupInfo(result.get(i).getClubid()).getMaxcount());
            innerMap.put("memberCnt", clubUserRepository.getMemberCount(result.get(i).getClubid()));
            
            resultList.add(innerMap);
         }
         recoGroups = userFunction.ListToMap(resultList, "id");
         boolean cuPage=true;
         if(page==result.size()/10+1) {
         	cuPage=false;
         }
         Map<String,Object> remap=new HashMap<>();
         remap.put("groups", recoGroups);
         remap.put("hasMorePages", cuPage);
         return remap;
      } 
      
      else {
    	  Map<String,Object> map=new HashMap<>();
    	  map.put("dddd", 000);
    	  return map;
      }
   }

   

   // 그룹생성 4-2
   public Map<String,Object> createGroup(int userid, String groupName, String groupIntro, JSONArray interests, int city, int gu,
         int minAge, int maxAge, int gender, int maxMember,JSONArray images) throws JSONException {
	   	
         // 그룹개설

         Club c = new Club();
         c.setAgeend(maxAge);
         c.setAgestart(minAge);
         c.setCity(city);
         c.setClubid(0);
         c.setContent(groupIntro);
         c.setGender(gender);
         c.setGu(gu);
         c.setMaxcount(maxMember);
         c.setName(groupName);
         c.setStartdate(new Date());
         c.setWritedate(new Date());
         c.setYn(true);
         Club newClub = clubRepository.save(c);
         int clubid= newClub.getClubid();
         // 그룹유저 추가(방장)
         Clubuser cu = new Clubuser();
         cu.setClubid(clubid);
         cu.setBreakdate(null);
         cu.setId(0);
         cu.setJoindate(new Date());
         cu.setMessage(null);
         cu.setReqdate(new Date());
         cu.setUserid(userid);
         cu.setUserstate(2);
         cu.setWritedate(new Date());
         cu.setAuth(1);
         clubUserRepository.save(cu);

         List<Integer> interlist=new ArrayList<>();
         // 그룹관심사 추가
         for (int i = 0; i < interests.length(); i++) {
            Clubinterest ci = new Clubinterest();
            ci.setClubid(newClub.getClubid());
//            try {
				ci.setCode(interests.getInt(i));
				interlist.add(interests.getInt(i));
            ci.setWritedate(new Date());
            clubinterestRepository.save(ci);
         }
         
         // 사진추가
     		userFunction.imgUpdate(2, clubid, images);
         
       //    userFunction.imgUpdate(2, clubid, images);
        
           
           Map<String,Object> clubMap=new HashMap<>();
           
           Map<String,Object> masterMap=new HashMap<>();
           masterMap.put("id", userid);
           masterMap.put("nickName", userRepository.getUserName(userid));
           clubMap.put("id",newClub.getClubid());
           clubMap.put("groupName",newClub.getName());
           clubMap.put("master",masterMap);
           clubMap.put("interests",interlist);
           clubMap.put("estDate",newClub.getStartdate());
           clubMap.put("memberCnt",clubUserRepository.getMemberCount(newClub.getClubid()));
           clubMap.put("maxMember",newClub.getMaxcount());
           clubMap.put("minAge",newClub.getAgestart());
           clubMap.put("maxAge",newClub.getAgeend());
           clubMap.put("gender",newClub.getGender());
           clubMap.put("si",newClub.getCity());
           clubMap.put("gu",newClub.getGu());
           clubMap.put("intro",newClub.getContent());
           clubMap.put("images",imgRepository.getImgpath(2, newClub.getClubid()));
           Map<String,Object> reMap=new HashMap<>();
           reMap.put("group", clubMap);
          
           List<Clubuser> culist=clubUserRepository.getclubuser(clubid);
           List<Map<String,Object>> cuList=new ArrayList<>();
           for(int a=0;a<culist.size();a++) {
   			Map<String,Object> cuMap=new HashMap<>();
   			cuMap.put("id", culist.get(a).getUserid());
   			cuMap.put("nickName", userRepository.getUserName(culist.get(a).getUserid()));
   			cuMap.put("gender", userRepository.getUser(culist.get(a).getUserid()).getGender());
   			cuMap.put("image", imgRepository.getImg(1, culist.get(a).getUserid()));
   			cuMap.put("online", false);
   			cuList.add(cuMap);
   		}
           reMap.put("members", userFunction.ListToMap(cuList, "id"));
           
           return reMap;
      

   

   }
   //그룹생성, 수정
   @Transactional
   public Map<String,Object> createUpdate(String json) throws JSONException{
	   JSONObject obj=new JSONObject(json);

	     JSONArray images=(JSONArray)obj.get("images");
	     String groupName=(String)obj.get("groupName");
    
	      int minAge=obj.getInt("minAge");
	      int maxAge=obj.getInt("maxAge");
	      int token= obj.getInt("token");
	      int si=obj.getInt("si");
	      int gu=obj.getInt("gu");
	      int maxMember=obj.getInt("maxMember");
    
	      int gender=obj.getInt("gender");
    String intro=(String)obj.get("intro");
    


   JSONArray interests=(JSONArray)obj.get("interests");

    
   Map<String,Object> map=new HashMap<>();
    if(!obj.has("id")) {
  	  
    map=createGroup(token, groupName, intro, interests,
  			  si, gu, minAge, maxAge,
  			  gender, maxMember, images);
    }
    else {
  	  int id=obj.getInt("id");
  	  map=updateGroup(token, groupName, intro, interests,
  			  si, gu, minAge, maxAge,
  			  gender, maxMember, images,id);
    }
   return map;
   }
   
   

   // 그룹생성시 사진저장하는 메소드
   public void saveFile(MultipartFile[] files, Boolean[] mainImgYN, Integer userid) throws IOException {
      String fileName = null;

      // 그룹이미지 추가
      for (int i = 0; i < files.length; i++) {
         if (!files[i].getOriginalFilename().equals("")) {
            Img img = new Img();
            img.setFirstyn(mainImgYN[i]);
            img.setGubun(2);
            img.setId(userid);
            img.setImgid(0);
            img.setWritedate(new Date());
            fileName = UUID.randomUUID() + files[i].getOriginalFilename();
            File saveFile = new File(
                  "C:\\Users\\Xnote\\새 폴더 (2)\\demo--11\\src\\main\\resources\\static\\img\\" + fileName);
            // C:\temp\friendimg
            // imgs.insertImgList(1, userid, fileName, mainImgYN.get(i));
            img.setImgpath(fileName);
            imgRepository.save(img);

            files[i].transferTo(saveFile);

         }
      }
      

   }
   // 새로운 리스트 하나만들어서 리턴하는맵 string값에 넣음. value를 리스트 순서.

   // 그룹 수정 4-2-2
   public Map<String,Object> updateGroup(int userid, String groupName, String groupIntro, JSONArray interests, int city, int gu,
	         int minAge, int maxAge, int gender, int maxMember,JSONArray images,Integer clubid) {
      // 마스터가 자신일시
      if (clubUserRepository.getMaster(clubid) == userid) {

       userFunction.imgUpdate(2, clubid, images);
       
       
         clubinterestRepository.deleteByClubid(clubid);
         List<Integer> intlist=new ArrayList<>();
         for (int i = 0; i < interests.length(); i++) {
            Clubinterest ci = new Clubinterest();
            ci.setClubid(clubid);
            try {
            	intlist.add(interests.getInt(i));
				ci.setCode(interests.getInt(i));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            ci.setWritedate(new Date());
            clubinterestRepository.save(ci);
         }

         clubRepository.updateClub(groupName, gender, city, gu, groupIntro, minAge, maxAge, maxMember, true, clubid);
         
         
         Map<String,Object> clubMap=new HashMap<>();
         Club c=clubRepository.groupInfo(clubid);
         Map<String,Object> masterMap=new HashMap<>();
         masterMap.put("id", userid);
         masterMap.put("nickName", userRepository.getUserName(userid));
         clubMap.put("id",clubid);
         clubMap.put("groupName",groupName);
         clubMap.put("master",masterMap);
         clubMap.put("interests",intlist);
         clubMap.put("estDate",c.getStartdate());
         clubMap.put("memberCnt",clubUserRepository.getMemberCount(c.getClubid()));
         clubMap.put("maxMember",c.getMaxcount());
         clubMap.put("minAge",c.getAgestart());
         clubMap.put("maxAge",c.getAgeend());
         clubMap.put("gender",c.getGender());
         clubMap.put("si",c.getCity());
         clubMap.put("gu",c.getGu());
         clubMap.put("intro",c.getContent());
         clubMap.put("images",imgRepository.getImgpath(2, c.getClubid()));
         Map<String,Object> reMap=new HashMap<>();
         reMap.put("group", clubMap);
        
         
         return reMap;
    

      }
      else {
    	  
    	  return null;
      }

   }

   // 4-4-1 그룹 가입 신청 폼으로 이동
   // Request : ?clubid=integer&token=String
   // Response : "그룹 가입조건, 내 정보 ,그룹 메세지"
   public int groupJoin(int clubid, int userid,String message) {
      // 비교할 정보 - 필수 : 나이제한,성별제한,
      
	  
      Club c = clubRepository.groupInfo(clubid);
      User u=userRepository.getUser(userid);
      int minAge=c.getAgestart();
      int maxAge=c.getAgeend();
      int myAge=userFunction.UserAge(u.getBirth());
      int groupGender=c.getGender();
      int myGender=u.getGender();
      
      if(groupGender==3) {
    	  if(minAge<=myAge&&myAge<=maxAge) {
    		  return 1;
    	  }
    	  else {
    		  return 2;
    	  }
      }
      else {
    	  if(myGender==groupGender) {
    		  if(minAge<=myAge&&myAge<=maxAge) {
        		  return 1;
        	  }
        	  else {
        		  return 2;
        	  }
    	  }
    	  else {
    		  return 3;
    	  }
      }
      
      // 1 : 성공 , 2 : 나이제한 , 3 : 성별제한
      
      
     /* Map<String, Object> map = new HashMap<>();
      map.put("age", c.getAgestart() + "~" + c.getAgeend());
      map.put("gender", c.getGender());
      map.put("intro", c.getContent());
      map.put("membercnt", clubUserRepository.getMemberCount(clubid));
      map.put("maxmember", clubRepository.groupInfo(clubid).getMaxcount());
      // 현재인원 , 최대인원
      */
      

   }

   // 4-4-2 그룹 가입 신청
   // request : ?clubid=integer&token=String
   // response : """{ success : Boolean } 가입성공:OK , 가입실패:BAD_REQUEST"""
   // 멤버수제한 설정안함... 내일와서하기!
   public int groupJoin2(int clubid, int userid, String message) {
      Club c = clubRepository.groupInfo(clubid);
      User u = userRepository.getUser(userid);
      int userage = userFunction.UserAge(u.getBirth());
      Clubuser cu = new Clubuser();
      Alarm al = new Alarm();
      int membercount = clubUserRepository.getMemberCount(clubid);
      int maxmember = clubRepository.groupInfo(clubid).getMaxcount();
      if (membercount < maxmember) {

         if (c.getGender() == 3) {
            if (c.getAgestart() < userage && c.getAgeend() > userage) {
               // 가입가능
               cu.setAuth(9);
               cu.setBreakdate(null);
               cu.setClubid(clubid);
               cu.setId(0);
               cu.setJoindate(null);
               cu.setMessage(message);
               cu.setReqdate(new Date());
               cu.setUserid(userid);
               cu.setUserstate(1);
               cu.setWritedate(null);
               clubUserRepository.save(cu);
               al.setGubun(1);
               al.setId(clubid);
               al.setReceiveyn(false);
               al.setUserid(clubUserRepository.getMaster(clubid));
               al.setWritedate(new Date());
               al.setClubid(clubid);
               al.setMessage(message);
               alarmRepository.save(al);
               userFunction.sendAlarmSocketBoolean(clubUserRepository.getMaster(clubid), true);
               return 1;
            } else {
               // 나이제한 걸림.! 가입불가
               return 0;
            }

         } else {
            if (c.getGender() == u.getGender()) {
               if (c.getAgestart() < userage && c.getAgeend() > userage) {
                  // 나이제한안걸리고 성별같으니 가입가능
                  cu.setAuth(9);
                  cu.setBreakdate(null);
                  cu.setClubid(clubid);
                  cu.setId(0);
                  cu.setJoindate(null);
                  cu.setMessage(message);
                  cu.setReqdate(new Date());
                  cu.setUserid(userid);
                  cu.setUserstate(1);
                  cu.setWritedate(null);
                  clubUserRepository.save(cu);
                  al.setGubun(1);
                  al.setId(userid);
                  al.setReceiveyn(false);
                  al.setUserid(clubUserRepository.getMaster(clubid));
                  al.setClubid(clubid);
                  al.setWritedate(new Date());
                  al.setMessage(message);
                  alarmRepository.save(al);
                  userFunction.sendAlarmSocketBoolean(clubUserRepository.getMaster(clubid), true);
                  return 1;
               } else {
                  // 나이제한걸림! 가입불가
                  return 0;
               }

            } else {
               // 성별제한 걸림 가입불가.!
               return 0;

            }
         }
      } else {
         return 0;
      }

   }

   // 4-4-3 그룹 가입 신청 허락폼
   public Map<String, Object> allowGroupJoin(int clubid) {
      List<Clubuser> waitingClubuser = clubUserRepository.waitingUser(clubid);
      // 이름 나이 성별 메세지 요청날짜
      List<Map<String, Object>> map = new ArrayList<>();
      alarmRepository.readGroupJoin(clubid, clubUserRepository.getMaster(clubid));
      for (int i = 0; i < waitingClubuser.size(); i++) {
         Map<String, Object> inMap = new HashMap<>();
         waitingClubuser.get(i).getUserid();
         int userage = userFunction.UserAge(userRepository.getUser(waitingClubuser.get(i).getUserid()).getBirth());

         inMap.put("name", userRepository.getUserName(waitingClubuser.get(i).getUserid()));
         inMap.put("age", userage);
         inMap.put("gender", userRepository.getUser(waitingClubuser.get(i).getUserid()).getGender());
         inMap.put("message", waitingClubuser.get(i).getMessage());
         inMap.put("reqdate", waitingClubuser.get(i).getReqdate());
         inMap.put("index", i + 1);
         map.add(inMap);
      }

      return userFunction.ListToMap(map, "index");
   }

   // 4-4-4 그룹 가입 신청 허락
   public void allowGroupJoin2(int masterid, int userid,int groupid,int notification ) {

     /*  else {
         clubUserRepository.deleteByClubidAndUserid(clubid, userid);
         return 0;
      }*/
	   alarmRepository.readAlarmUpdate(notification);
	   clubUserRepository.allowGroup(groupid, userid);
   }
   // 그룹가입 거절
   public void rejectGroup(int masterid, int userid,int groupid,int notification ) {
	   
	   alarmRepository.readAlarmUpdate(notification);
	   clubUserRepository.deleteByClubidAndUserid(groupid, userid);
	   
   }

   // 4-5-1 : 그룹 공지글 작성폼 보이기
   public Map<String, Object> recentGroupNotice(int clubid, int userid) {
      // 아직 기간안지난 공지글
      List<Clubnotice> noticeList = clubnoticeRepository.ongoingnotice(clubid);
      List<Map<String, Object>> list = new ArrayList<>();
      for (int i = 0; i < noticeList.size(); i++) {
         Map<String, Object> map = new HashMap<>();
         map.put("meetdate", noticeList.get(i).getMeetdate());
         map.put("title", noticeList.get(i).getTitle());
         map.put("content", noticeList.get(i).getContent());
         map.put("meetlocation", noticeList.get(i).getMeetlocation());
         map.put("meettime", noticeList.get(i).getMeettime());
         map.put("writedate", noticeList.get(i).getWritedate());
         map.put("writer", noticeList.get(i).getUserid());
         map.put("noticeid", noticeList.get(i).getNoticeid());
         list.add(map);
      }
      Map<String, Object> returnmap = userFunction.ListToMap(list, "noticeid");

      return returnmap;
   }

   // 4-5-2 : 그룹 공지글 작성하기
   // request : 공지글 내용
   // response : """{ success : Boolean } 가입성공:OK , 가입실패:BAD_REQUEST"""
   @PostMapping("/group/notice")
   public Map<String,Object> writeGroupNotice(int userid,int clubid,String title,String content) {
     
         Clubnotice cn = new Clubnotice();
         cn.setClubid(clubid);
         cn.setContent(content);
         cn.setMeetdate(null);
         cn.setMeetlocation(null);
         cn.setMeettime(null);
         cn.setNoticeid(0);
         cn.setTitle(title);
         cn.setType(3);
         cn.setUserid(userid);
         cn.setWritedate(new Date());
         clubnoticeRepository.save(cn);
         
         List<Integer> users=clubUserRepository.getMemberId(clubid);
         for(int i=0;i<users.size();i++) {
        	 Alarm al=new Alarm();
        	 al.setAlarmid(0);
            al.setGubun(6);
            al.setId(clubid);
            al.setMessage("그룹의 새 공지글이 작성되었어요");
            al.setReceiveyn(false);
            al.setUserid(users.get(i));
            al.setWritedate(new Date());
            alarmRepository.save(al);
         }
         Map<String,Object> posts=new HashMap<>();
         List<Clubnotice> clubnotice=clubnoticeRepository.getClubnotices(clubid);
         List<Map<String,Object>> list=new ArrayList<>();
         boolean hasMorePages=false;
         if(clubnotice.size()>=20) {
        	 hasMorePages=true;
        	 for(int i=0;i<20;i++) {
        		 Map<String,Object> map=new HashMap<>();
	    		  Map<String,Object> userMap=new HashMap<>();
	    		  
	    		  int count=0;
	    		  List<Feel> flist=feelRepository.liked(3, clubnotice.get(i).getNoticeid());
					for(int k=0;k<flist.size();k++) {
						if(flist.get(k).getUserid()==userid) {
							count++;
						}
					}
					boolean liked=false;
					if(count==1) {
						liked=true;
					}
	    		  userMap.put("id", clubnotice.get(i).getUserid());
	    		  userMap.put("nickName",  userRepository.getUserName(clubnotice.get(i).getUserid()));
	    		  userMap.put("image", imgRepository.getImg(1, clubnotice.get(i).getUserid())); //유저이미지
	    		  
	    		  map.put("type", clubnotice.get(i).getType());
	    		  map.put("id", clubnotice.get(i).getNoticeid());
	    		  map.put("title", clubnotice.get(i).getTitle());
	    		  map.put("content", clubnotice.get(i).getContent());
	    		  map.put("writedate", clubnotice.get(i).getWritedate());
	    		  map.put("user", userMap);
	    		  map.put("views", hitRepository.readcounts(3, clubnotice.get(i).getNoticeid()));
	    		  map.put("likes", feelRepository.likes(3, clubnotice.get(i).getNoticeid()));
	    		  map.put("liked", liked);
	    		  Map<String,Object> comments=new HashMap<>();
	    		  comments.put("id", 0);
	    		 List<Noticecom> notice= noticecomRepository.noticecom(clubnotice.get(i).getNoticeid());
	    		 List<Map<String,Object>> reMap=new ArrayList<>();
	    		 for(int k=0;k<notice.size();k++) {
	    			 Map<String,Object> coMap=new HashMap<>();
	    			 Map<String,Object> uMap=new HashMap<>();
	    			 uMap.put("id", notice.get(k).getUserid());
	    			 uMap.put("nickName", userRepository.getUserName(notice.get(k).getUserid()));
	    			 uMap.put("image", imgRepository.getImg(1, notice.get(k).getUserid()));
	    			 coMap.put("id", notice.get(k).getComid());
	    			 coMap.put("user", uMap);
	    			 coMap.put("content",notice.get(k).getContent());
	    			 coMap.put("writedate", notice.get(k).getWritedate());
	    			 reMap.add(coMap);
	    		 }
	    		 map.put("comments", userFunction.ListToMap(reMap, "id"));
	    		 list.add(map);
				
        	 }
        	 posts.put("posts",userFunction.ListToMap(list, "id"));
   
         }
         else {
        	 for(int i=0;i<clubnotice.size();i++) {
        		 Map<String,Object> map=new HashMap<>();
	    		  Map<String,Object> userMap=new HashMap<>();
	    		  userMap.put("id", clubnotice.get(i).getUserid());
	    		  userMap.put("nickName",  userRepository.getUserName(clubnotice.get(i).getUserid()));
	    		  userMap.put("image", imgRepository.getImg(1, clubnotice.get(i).getUserid())); //유저이미지
	    		  
	    		  map.put("type", clubnotice.get(i).getType());
	    		  map.put("id", clubnotice.get(i).getNoticeid());
	    		  map.put("title", clubnotice.get(i).getTitle());
	    		  map.put("content", clubnotice.get(i).getContent());
	    		  map.put("writedate", clubnotice.get(i).getWritedate());
	    		  map.put("user", userMap);
	    		  map.put("views", hitRepository.readcounts(3, clubnotice.get(i).getNoticeid()));
	    		  map.put("likes", feelRepository.likes(3, clubnotice.get(i).getNoticeid()));
	    		  
	    		  Map<String,Object> comments=new HashMap<>();
	    		  comments.put("id", 0);
	    		 List<Noticecom> notice= noticecomRepository.noticecom(clubnotice.get(i).getNoticeid());
	    		 List<Map<String,Object>> reMap=new ArrayList<>();
	    		 for(int k=0;k<notice.size();k++) {
	    			 Map<String,Object> coMap=new HashMap<>();
	    			 Map<String,Object> uMap=new HashMap<>();
	    			 uMap.put("id", notice.get(k).getUserid());
	    			 uMap.put("nickName", userRepository.getUserName(notice.get(k).getUserid()));
	    			 uMap.put("image", imgRepository.getImg(1, notice.get(k).getUserid()));
	    			 coMap.put("id", notice.get(k).getComid());
	    			 coMap.put("user", uMap);
	    			 coMap.put("content",notice.get(k).getContent());
	    			 coMap.put("writedate", notice.get(k).getWritedate());
	    			 reMap.add(coMap);
	    		 }
	    		 map.put("comments", userFunction.ListToMap(reMap, "id"));
				
	    		 list.add(map);
        	 }
        	 posts.put("posts",userFunction.ListToMap(list, "id"));
        	 }
         posts.put("hasMorePages", hasMorePages);
         
         
         
         
      return posts;
      
   }

   // 4-6 : 그룹 공지글 댓글
   // request : 댓글 내용
   // response : { success : Boolean } 가입성공:OK , 가입실패:BAD_REQUEST
   public int noticecom(int noticeid, int userid, boolean attendyn, String content) {
      if (true) {
         Noticecom nc = new Noticecom();
         nc.setAttendyn(attendyn);
         nc.setComid(0);
         nc.setContent(content);
         nc.setNoticeid(noticeid);
         nc.setUserid(userid);
         nc.setWritedate(new Date());

         noticecomRepository.save(nc);
         
         Alarm al=new Alarm();
         al.setGubun(7);
         al.setId(clubnoticeRepository.getClubnotice(noticeid).getClubid());
         al.setMessage("내공지에 댓글이달렸어요");
         al.setReceiveyn(false);
         al.setUserid(clubnoticeRepository.getClubnotice(noticeid).getUserid());
         al.setWritedate(new Date());
         alarmRepository.save(al);
         return 1;
      } else {
         return 0;
      }
   }
   //4-7 : 그룹 매니저 권한 변경

      public int changeManager(int clubid,int myid,int userid,int auth) {
         
         if(clubUserRepository.getMaster(clubid)==myid) {
            clubUserRepository.changeAuth(clubid, userid, auth);
            return 1;
         }
         else {
            return 0;
         }
         
      }

	public void deleteGroupUser(int token, int groupid) {
		
		clubUserRepository.deleteClubUser(token,groupid);
		
		
	}

	
	//그룹멤버 정보
	public Map<String,Object> groupMember(int token, int id, int page) {
		// TODO Auto-generated method stub
		Map<String,Object> Map=new HashMap<>();
		Map<String,Object> m=new HashMap<>();
		List<Clubuser> cu=clubUserRepository.getclubuser(id);
		List<Map<String,Object>> list=new ArrayList<>();
		Map<Integer,Object> reMap=new HashMap<>();
		if(page*10>=cu.size()&&cu.size()>page*10-10) {
			for(int i=page*10-10;i<cu.size();i++) {
				Map<String,Object> map=new HashMap<>();
				int userid=cu.get(i).getUserid();
				map.put("id", userid);
				map.put("nickName",userRepository.getUserName(userid));
				map.put("gender",userRepository.getUser(userid).getGender());
				map.put("image", imgRepository.getImg(1,userid));
				map.put("online", false);
				reMap.put(userid, map);
			}
		}
		else if(page*10<cu.size()) {
			for(int i=page*10-10;i<page*10;i++) {
				Map<String,Object> map=new HashMap<>();
				int userid=cu.get(i).getUserid();
				map.put("id", userid);
				map.put("nickName",userRepository.getUserName(userid));
				map.put("gender",userRepository.getUser(userid).getGender());
				map.put("image", imgRepository.getImg(1,userid));
				map.put("online", false);
				reMap.put(userid, map);
			}
		}
		m.put("members", reMap);
		
	
		return m;
		
	}
	
	
	
	//그룹게시글 조회
	public Map<String,Object> readBoard(int token, int id, int page) {
		Map<String,Object> posts=new HashMap<>();
		List<Clubnotice> clubnotice=clubnoticeRepository.getClubnotices(id);
		
		List<Map<String,Object>> maplist=new ArrayList<>();
		
		if(page*20>=clubnotice.size()&&clubnotice.size()>page*20-20) {
			for(int i=page*20-20;i<clubnotice.size();i++) {
				int count=0;
				List<Feel> flist=feelRepository.liked(3, clubnotice.get(i).getNoticeid());
					for(int k=0;k<flist.size();k++) {
						if(flist.get(k).getUserid()==token) {
							count++;
						}
					}
					boolean liked=false;
					if(count==1) {
						liked=true;
					}
				Map<String,Object> map=new HashMap<>();
	    		  Map<String,Object> userMap=new HashMap<>();
	    		  userMap.put("id", clubnotice.get(i).getUserid());
	    		  userMap.put("nickName",  userRepository.getUserName(clubnotice.get(i).getUserid()));
	    		  userMap.put("image", imgRepository.getImg(1, clubnotice.get(i).getUserid())); //유저이미지
	    		  
	    		  map.put("type", clubnotice.get(i).getType());
	    		  map.put("id", clubnotice.get(i).getNoticeid());
	    		  map.put("title", clubnotice.get(i).getTitle());
	    		  map.put("content", clubnotice.get(i).getContent());
	    		  map.put("writedate", clubnotice.get(i).getWritedate());
	    		  map.put("user", userMap);
	    		  map.put("views", hitRepository.readcounts(3, clubnotice.get(i).getNoticeid()));
	    		  map.put("likes", feelRepository.likes(3, clubnotice.get(i).getNoticeid()));
	    		  map.put("liked", liked);
	    		  Map<String,Object> comments=new HashMap<>();
	    		  comments.put("id", 0);
	    		 List<Noticecom> notice= noticecomRepository.noticecom(clubnotice.get(i).getNoticeid());
	    		 List<Map<String,Object>> reMap=new ArrayList<>();
	    		
	    		 for(int k=0;k<notice.size();k++) {
	    			 Map<String,Object> coMap=new HashMap<>();
	    			 Map<String,Object> uMap=new HashMap<>();
	    			 uMap.put("id", notice.get(k).getUserid());
	    			 uMap.put("nickName", userRepository.getUserName(notice.get(k).getUserid()));
	    			 uMap.put("image", imgRepository.getImg(1, notice.get(k).getUserid()));
	    			 coMap.put("id", notice.get(k).getComid());
	    			 coMap.put("user", uMap);
	    			 coMap.put("content",notice.get(k).getContent());
	    			 coMap.put("writedate", notice.get(k).getWritedate());
	    			 reMap.add(coMap);
	    		 }
	    		 map.put("comments", userFunction.ListToMap(reMap, "id"));
	    		 maplist.add(map);
			}
			posts.put("posts",userFunction.ListToMap(maplist, "id"));
		}
		else if(page*20<clubnotice.size()) {
			for(int i=page*20-20;i<page*20;i++) {
				int count=0;
				List<Feel> flist=feelRepository.liked(3, clubnotice.get(i).getNoticeid());
					for(int k=0;k<flist.size();k++) {
						if(flist.get(k).getUserid()==token) {
							count++;
						}
					}
					boolean liked=false;
					if(count==1) {
						liked=true;
					}
				
				 Map<String,Object> map=new HashMap<>();
	    		  Map<String,Object> userMap=new HashMap<>();
	    		  userMap.put("id", clubnotice.get(i).getUserid());
	    		  userMap.put("nickName",  userRepository.getUserName(clubnotice.get(i).getUserid()));
	    		  userMap.put("image", imgRepository.getImg(1, clubnotice.get(i).getUserid())); //유저이미지
	    		  
	    		  map.put("type", clubnotice.get(i).getType());
	    		  map.put("id", clubnotice.get(i).getNoticeid());
	    		  map.put("title", clubnotice.get(i).getTitle());
	    		  map.put("content", clubnotice.get(i).getContent());
	    		  map.put("writedate", clubnotice.get(i).getWritedate());
	    		  map.put("user", userMap);
	    		  map.put("views", hitRepository.readcounts(3, clubnotice.get(i).getNoticeid()));
	    		  map.put("likes", feelRepository.likes(3, clubnotice.get(i).getNoticeid()));
	    		  map.put("liked", liked);
	    		  Map<String,Object> comments=new HashMap<>();
	    		  comments.put("id", 0);
	    		 List<Noticecom> notice= noticecomRepository.noticecom(clubnotice.get(i).getNoticeid());
	    		 List<Map<String,Object>> reMap=new ArrayList<>();
	    		 for(int k=0;k<notice.size();k++) {
	    			 Map<String,Object> coMap=new HashMap<>();
	    			 Map<String,Object> uMap=new HashMap<>();
	    			 uMap.put("id", notice.get(k).getUserid());
	    			 uMap.put("nickName", userRepository.getUserName(notice.get(k).getUserid()));
	    			 uMap.put("image", imgRepository.getImg(1, notice.get(k).getUserid()));
	    			 coMap.put("id", notice.get(k).getComid());
	    			 coMap.put("user", uMap);
	    			 coMap.put("content",notice.get(k).getContent());
	    			 coMap.put("writedate", notice.get(k).getWritedate());
	    			 reMap.add(coMap);
	    		 }
	    		 map.put("comments", userFunction.ListToMap(reMap, "id"));
	    		 maplist.add(map);
			}
			posts.put("posts",userFunction.ListToMap(maplist, "id"));			
		}
		System.out.println(clubnotice.size());
		Boolean hasMorePages=true;
		if(clubnotice.size()/20+1==page)
			hasMorePages=false;
		posts.put("hasMorePages", hasMorePages);
	
		return posts;
		
	}

	public int addReadCount(int userid, int id) {
		// TODO Auto-generated method stub
		
		List<Hit> list=hitRepository.addReadCount(3, id, userid);
		if(list.size()==0) {
			Hit h=new Hit();
			h.setGubun(3);
			h.setHittime(new Date());
			h.setId(id);
			h.setUserid(userid);
			hitRepository.save(h);
			return 1;
		}
		else {
			return 0;
		}
	}

	//좋아요 수 추가하기
	public void addLike(int token, int id, Boolean liked) {
		// TODO Auto-generated method stub
		if(liked==true) {
			Feel feel=new Feel();
			
			feel.setGubun(3);
			feel.setId(id);
			feel.setType(1);
			feel.setUserid(token);
			feel.setWritedate(new Date());
			feelRepository.save(feel);
		}
		else {
			feelRepository.deleteByGubunAndIdAndUseridAndType(3, id, token, 1);
		}
	}
	
	public Map<String,Object> addComment(int token, int id, String content) {
		// TODO Auto-generated method stub
	
		Noticecom nc=new Noticecom();
		nc.setAttendyn(false);
		nc.setComid(0);
		nc.setContent(content);
		nc.setUserid(token);
		nc.setNoticeid(id);
		nc.setWritedate(new Date());
		noticecomRepository.save(nc);
		
		Map<String,Object> reMap=new HashMap<>();
		
		/*	"DB에 댓글 등록후 해당 게시글의 댓글 전체 반환
		comments: { 
		    id(댓글ID): { 
		        id: Integer,  // 댓글 id
		        user: {
		            id: Integer,  // 댓글 작성자 id
		            nickName: String,  // 댓글 작성자 닉네임
		            image: String,  // 댓글 작성자 프사 url
		        },
		        content: String  // 댓글 내용
		        writedate: Date  // 댓글 작성일
		    },
		    ...
		}"
*/	
		List<Noticecom> nlist=noticecomRepository.noticecom(id);
		List<Map<String,Object>> commentList=new ArrayList<>();
		
			for(int i=0;i<nlist.size();i++) {
				Map<String,Object> map=new HashMap<>();
				Map<String,Object> uMap=new HashMap<>();
				uMap.put("id", nlist.get(i).getUserid());
				uMap.put("nickName", userRepository.getUserName(nlist.get(i).getUserid()));
				uMap.put("image",imgRepository.getImg(1, nlist.get(i).getUserid()));
				
				map.put("id", nlist.get(i).getComid());
				map.put("user",uMap);
				map.put("content", nlist.get(i).getContent());
				map.put("writedate",nlist.get(i).getWritedate());
				commentList.add(map);
			}
			reMap.put("comments", userFunction.ListToMap(commentList, "id"));
		return reMap;
		}
}











