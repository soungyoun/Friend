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
import com.example.model.Img;
import com.example.model.Noticecom;
import com.example.model.User;
import com.example.repository.ClubRepository;
import com.example.repository.ClubUserRepository;
import com.example.repository.ClubnoticeRepository;
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
   public Map<String, Object> groupInfo(int userid, int seq) {
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

      master.put("userid", masterid);
      master.put("name", userRepository.getUserName(masterid));

      imgRepository.getImgyn(2, c.getClubid());
      List<Map<String, Object>> imgList = new ArrayList<>();
      List<String> imgPathList = new ArrayList<>();
      int ismygroup=0;
         if(clubUserRepository.isMyClub(c.getClubid(), userid)==null) {
            ismygroup=0;
         }
         else {
            ismygroup=clubUserRepository.isMyClub(c.getClubid(), userid);
         }
         imgPathList = imgRepository.getImgpath(2, c.getClubid());
      
      for (int i = 0; i < imgPathList.size(); i++) {
         Map<String, Object> imgMap = new HashMap<>();
         imgMap.put("image",imgPathList.get(i));
         imgMap.put("imgyn", imgRepository.getImgyn(2, c.getClubid()).get(i));
         imgList.add(imgMap);
      }
      List<Clubuser> cuList=clubUserRepository.recentmember(c.getClubid());
      List<Map<String,Object>> recentmemberList=new ArrayList<>();
      Map<String,Object> m=new HashMap<>();
      for(int i=0;i<cuList.size();i++) {
         m.put("id", cuList.get(i).getUserid());
         m.put("name", userRepository.getUserName(cuList.get(i).getUserid()));
         recentmemberList.add(m);
      }
      List<Clubnotice> anList=clubnoticeRepository.recentAnnouncements(c.getClubid());
      List<Map<String,Object>> announcements=new ArrayList<>();
      for(int i=0;i<anList.size();i++) {
         Map<String,Object> ma=new HashMap<>();
         ma.put("n_title", anList.get(i).getTitle());
         ma.put("n_content", anList.get(i).getContent());
         ma.put("n_writedate", anList.get(i).getWritedate());
         
         announcements.add(ma);
      }
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
      groupInfoMap.put("seq", c.getClubid());
      groupInfoMap.put("master", master);
      groupInfoMap.put("interests", interestList);
      groupInfoMap.put("estDate", c.getStartdate());
      groupInfoMap.put("memberCnt", membercnt);
      groupInfoMap.put("maxMember", c.getMaxcount());
      groupInfoMap.put("si", c.getCity());
      groupInfoMap.put("gu", c.getGu());
      groupInfoMap.put("avgAge", yearAvg);
      groupInfoMap.put("groupintro", c.getContent());
      groupInfoMap.put("images", imgList);
      groupInfoMap.put("isMyGroup", ismygroup);
      groupInfoMap.put("newMembers", recentmemberList);
      groupInfoMap.put("announcements", announcements);
      groupInfoMap.put("members", userlist);
      
      return groupInfoMap;
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

            for (int i = 0; i < clubList.size(); i++) {
               Map<String, Object> masterMap = new HashMap<>();
               mMap.put("clubid", clubList.get(i).getClubid());

               masterMap.put("userid", clubUserRepository.getMaster(clubList.get(i).getClubid()));
               masterMap.put("name",
                     userRepository.getUserName(clubUserRepository.getMaster(clubList.get(i).getClubid())));
               Map<String, Object> map = new HashMap<>();
               map.put("seq", clubList.get(i).getClubid());
               map.put("groupName", clubList.get(i).getName());
               map.put("master", masterMap);
               map.put("estDate", clubList.get(i).getStartdate());
               map.put("memberCnt", userList.get(i).size());
               map.put("maxMember", clubList.get(i).getMaxcount());
               map.put("image",imgRepository.getImg(2, clubList.get(i).getClubid()));
               map.put("groupintro", clubList.get(i).getContent());
               map.put("interests", interestList.get(i));
               groups.add(map);
               
            }
            returnMap = userFunction.ListToMap(groups, "seq");
            return returnMap;
            
      } else {
         return recoGroup(userid, page);
      }
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
            masterMap.put("userid", clubUserRepository.getMaster(result.get(i).getClubid()));
            masterMap.put("name",
                  userRepository.getUserName(clubUserRepository.getMaster(result.get(i).getClubid())));
            List<Integer> interestList = clubinterestRepository.interestList(result.get(i).getClubid());

            // seqList.add(result.get(i).getClubid());
            innerMap.put("seq", result.get(i).getClubid());
            innerMap.put("groupName", result.get(i).getName());
            innerMap.put("master", masterMap);
            innerMap.put("interests", interestList);


            innerMap.put("estDate", result.get(i).getStartdate());
            List<String> imglist=imgRepository.getImgpath(2, result.get(i).getClubid());
            innerMap.put("image",imglist);
            
            innerMap.put("groupIntro", result.get(i).getContent());
            resultList.add(innerMap);
         }

         /*
          * recoGroups2=userFunction.ListToMap(seqMap, "key"); resultList.add(recoGroups2);
          */
         recoGroups = userFunction.ListToMap(resultList, "seq");

         return recoGroups;
      } else if (page * 10 < result.size()) {
         for (int i = page * 10 - 10; i < page * 10; i++) {
            Map<String, Object> innerMap = new HashMap<>();
            Map<String, Object> masterMap = new HashMap<>();
            masterMap.put("userid", clubUserRepository.getMaster(result.get(i).getClubid()));
            masterMap.put("name",
                  userRepository.getUserName(clubUserRepository.getMaster(result.get(i).getClubid())));
            List<Integer> interestList = clubinterestRepository.interestList(result.get(i).getClubid());

            innerMap.put("seq", result.get(i).getClubid());
            innerMap.put("groupName", result.get(i).getName());
            innerMap.put("master", masterMap);
            innerMap.put("interests", interestList);
            innerMap.put("estDate", result.get(i).getStartdate());
            innerMap.put("image",imgRepository.getImgpath(2, result.get(i).getClubid()));
            
            innerMap.put("groupIntro", result.get(i).getContent());
            resultList.add(innerMap);
         }
         recoGroups = userFunction.ListToMap(resultList, "seq");
         return recoGroups;
      } else {
         return null;
      }

   }

   // 이 아래부터 9월5일 보내줄값

   // 그룹생성 4-2
   public int createGroup(int userid, String groupName, String groupIntro, int[] interests, int city, int gu,
         int minAge, int maxAge, int gender, int maxMember, MultipartFile[] files, Boolean[] mainImgYN) {

      if (true) {
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

         // 그룹관심사 추가
         for (int i = 0; i < interests.length; i++) {
            Clubinterest ci = new Clubinterest();
            ci.setClubid(newClub.getClubid());
            ci.setCode(interests[i]);
            ci.setWritedate(new Date());
            clubinterestRepository.save(ci);
         }
         // 사진추가
         
            try {
               saveFile(files, mainImgYN, clubid);
            } catch (IOException e) {
               // TODO Auto-generated catch block
               e.printStackTrace();
            }
         return 1;
      }

      else {
         return 0;
      }

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
   public int updateGroup(int userid, String groupName, String groupIntro, int[] interests, int city, int gu,
         int minAge, int maxAge, int gender, int maxMember, MultipartFile[] files, Boolean[] mainImgYN, boolean yn,
         int clubid) {
      // 마스터가 자신일시
      if (clubUserRepository.getMaster(clubid) == userid) {

         // 이미지를 지우고
         imgRepository.deleteByIdAndGubun(clubid, 2);
         
         
         // 이미지 생성
         try {
            saveFile(files, mainImgYN, clubid);
         } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
         }
         clubinterestRepository.deleteByClubid(clubid);
         for (int i = 0; i < interests.length; i++) {
            Clubinterest ci = new Clubinterest();
            ci.setClubid(clubid);
            ci.setCode(interests[i]);
            ci.setWritedate(new Date());
            clubinterestRepository.save(ci);
         }

         clubRepository.updateClub(groupName, gender, city, gu, groupIntro, minAge, maxAge, maxMember, yn, clubid);

         return 1;
      } else {
         return 0;

      }

   }

   // 4-4-1 그룹 가입 신청 폼으로 이동
   // Request : ?clubid=integer&token=String
   // Response : "그룹 가입조건, 내 정보 ,그룹 메세지"
   public Map<String, Object> groupJoin(int clubid, int userid) {
      // 비교할 정보 - 필수 : 나이제한,성별제한,
      //
      Club c = clubRepository.groupInfo(clubid);
      Map<String, Object> map = new HashMap<>();
      map.put("age", c.getAgestart() + "~" + c.getAgeend());
      map.put("gender", c.getGender());
      map.put("intro", c.getContent());
      map.put("membercnt", clubUserRepository.getMemberCount(clubid));
      map.put("maxmember", clubRepository.groupInfo(clubid).getMaxcount());
      // 현재인원 , 최대인원

      return map;
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
               al.setGubun(3);
               al.setId(clubid);
               al.setReceiveyn(false);
               al.setUserid(clubUserRepository.getMaster(clubid));
               al.setWritedate(new Date());
               al.setMessage(userRepository.getUserName(userid) + "님이 그룹가입을 신청했어요!");
               alarmRepository.save(al);
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
                  al.setGubun(3);
                  al.setId(clubid);
                  al.setReceiveyn(false);
                  al.setUserid(clubUserRepository.getMaster(clubid));
                  al.setWritedate(new Date());
                  al.setMessage(userRepository.getUserName(userid) + "님이 그룹가입을 신청했어요!");
                  alarmRepository.save(al);
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

   // 4-4-4 그룹 가입 신청 허락/거절
   public int allowGroupJoin2(int clubid, boolean yn, int userid) {

      if (yn == true) {
         clubUserRepository.allowGroup(clubid, userid);
         return 1;
      } else {
         clubUserRepository.deleteByClubidAndUserid(clubid, userid);
         return 0;
      }

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
   public int writeGroupNotice(Clubnotice clubnotice) {
      if (true) {
         Alarm al=new Alarm();
         Clubnotice cn = new Clubnotice();
         cn.setClubid(clubnotice.getClubid());
         cn.setContent(clubnotice.getContent());
         cn.setMeetdate(clubnotice.getMeetdate());
         cn.setMeetlocation(clubnotice.getMeetlocation());
         cn.setMeettime(clubnotice.getMeettime());
         cn.setNoticeid(clubnotice.getNoticeid());
         cn.setNoticeid(0);
         cn.setTitle(clubnotice.getTitle());
         cn.setType(clubnotice.getType());
         cn.setUserid(clubnotice.getUserid());
         cn.setWritedate(new Date());
         clubnoticeRepository.save(cn);
         
         List<Integer> users=clubUserRepository.getMemberId(clubnotice.getClubid());
         for(int i=0;i<users.size();i++) {
            al.setGubun(6);
            al.setId(clubnotice.getClubid());
            al.setMessage("그룹의 새 공지글이 작성되었어요");
            al.setReceiveyn(false);
            al.setUserid(users.get(i));
            al.setWritedate(new Date());
            alarmRepository.save(al);
         }
         
         return 1;
      } else {
         return 0;
      }

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
}
