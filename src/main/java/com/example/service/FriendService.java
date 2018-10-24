package com.example.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.UserFunction;
import com.example.model.User;
import com.example.model.Userinterest;
import com.example.repository.ImgRepository;
import com.example.repository.UserInterestRepository;
import com.example.repository.UserRepository;

@Service
public class FriendService {
   @Autowired
   private UserRepository userRepository;
   @Autowired
   private UserInterestRepository userInterestRepository;
   @Autowired
   private ImgRepository imgRepository;
   @Autowired
   EntityManager entitymanager;
   @Autowired
   private UserFunction userFunction;

// 1-1-4 인기친구
   public Map<String, Object> popularUsers() {
      // 인기친구 쿼리
      List<User> userList = userRepository.topFriends();
      // 구분 1: 회원
      final int gubun = 1;

      Map<Integer, Object> popUserMap = new HashMap<>();
      for (User u : userList) {
         Map<String, Object> UserMap = new HashMap<>();
         // userid 맵
         UserMap.put("id", u.getUserid());
         // name 맵
         UserMap.put("nickName", u.getName());
         List<Map<String, String>> imgsList = new ArrayList<Map<String, String>>();
         // 구분1, 유저아이디 에 해당하는 imgpath
         // 이미지 맵
         List<String> img = imgRepository.imgpathListByGubunAndId(gubun, u.getUserid());
         if (!img.isEmpty()) {
            UserMap.put("image", img.get(0));
         } else {
            UserMap.put("image", null);
         }
         // 유저아이디:유저정보
         popUserMap.put(u.getUserid(), UserMap);

      }

      Map<String, Object> popularUsersMap = new HashMap<String, Object>();
      popularUsersMap.put("popularUsers", popUserMap);

      return popularUsersMap;
   }

// 3-2
   // 필터 검색
   public Map<String, Object> SearchFriends(Integer userid, Integer page, Integer si, Integer gu, Integer gender,
         Integer minAge, Integer maxAge, Integer interest, String keyword) {
      // TODO Auto-generated method stub
      CriteriaBuilder builder = entitymanager.getCriteriaBuilder();
      CriteriaQuery<User> query = builder.createQuery(User.class);
      Root<User> root = query.from(User.class);
      List<Predicate> predicates = new ArrayList<Predicate>();
      // 자신을 제외
      predicates.add(builder.notEqual(root.get("userid"), userid));
      // 시가 있다면
      if (si != 0) {
         predicates.add(builder.equal(root.get("city"), si));
      }
      // 구가 있다면
      if (gu != 0) {
         predicates.add(builder.equal(root.get("gu"), gu));
      }
      // 성별이 있다면
      if (gender != 0) {
         predicates.add(builder.equal(root.get("gender"), gender));
      }
      // 나이가 있다면
      if (minAge != 0 || maxAge != 100) {
         // between : 사이 값
         Date min = new Date();
         Date max = new Date();
         min.setYear(min.getYear() - minAge);
         max.setYear(max.getYear() - maxAge);
         predicates.add(builder.between(root.get("birth"), max, min));
      }
      // 관심사가 있다면
      if (interest != 0) {
         Subquery<Userinterest> sub = query.subquery(Userinterest.class);
         Root<Userinterest> subroot = query.from(Userinterest.class);
         Predicate pd = builder.equal(subroot.get("code"), interest);
         sub.from(Userinterest.class);
         sub.select(subroot.get("userid")).where(pd);
         predicates.add(builder.and(builder.in(root.get("userid")).value(sub)));
      }
      // 유저의 닉네임 like검색
      if (!keyword.equals("")) {
         predicates.add(builder.like(root.get("name"), "%" + keyword + "%"));
      }
      query.select(root).where(predicates.toArray(new Predicate[] {}));
      // page당 20명씩 반환
      TypedQuery<User> typedQuery = entitymanager.createQuery(query).setFirstResult(page * 20 - 20).setMaxResults(20);
      List<User> userListPage = typedQuery.getResultList();

      // 더 불러올 페이지가 있는지의 여부
      TypedQuery<User> alltypedQuery = entitymanager.createQuery(query);
      List<User> alluserListPage = alltypedQuery.getResultList();
      Boolean hasMorePages = ((alluserListPage.size() - page * 20) > 0) ? true : false;

      final int gubun = 1;
      Map<String, Object> popularMap = new HashMap<>();
      Map<Object, Object> useridMap = new HashMap<>();
      List<Integer> index = new ArrayList<>();

      // user정보 매핑
      if (!userListPage.isEmpty()) {
         popularMap.put("hasMorePages", hasMorePages);
         for (User u : userListPage) {
            Map<String, Object> userMap = new HashMap<>();
            userMap.put("id", u.getUserid());
            userMap.put("nickName", u.getName());
            userMap.put("gender", u.getGender());
            // age 계산
            if (u.getBirth() != null) {
               userMap.put("age", new Date().getYear() - u.getBirth().getYear() + 1);
            } else {
               userMap.put("age", null);
            }
            userMap.put("si", u.getCity());
            userMap.put("gu", u.getGu());
            // user정보에 userinterest 추가
            List<Integer> interestList = userInterestRepository.codeByUserid(u.getUserid());
            userMap.put("interests", interestList);
            // user정보에 imgpath 추가
            List<String> img = imgRepository.imgpathListByGubunAndId(gubun, u.getUserid());
            if (!img.isEmpty()) {
               userMap.put("image", img.get(0));
            } else {
               userMap.put("image", null);
            }
            // userid 매핑
            useridMap.put(userMap.get("id"), userMap);
            popularMap.put("users", useridMap);
            index.add(u.getUserid());
            popularMap.put("index", index);
         }
      } else {
         popularMap.put("users", null);
      }

      return popularMap;
   }

// 3-1 필터없는 기본검색
   public Map<String, Object> DefaultSearchFriends(Integer userid, Integer page) {
      System.out.println("추천친구");
      // userid에 해당하는 유저정보
      User user = userRepository.findByUserid(userid);
      // userid에 해당하는 userinterest값
      List<Integer> userCodeList = userInterestRepository.codeByUserid(user.getUserid());
      System.out.println(userCodeList.get(0));

      List<User> userList = new ArrayList<>();
      userList.addAll(userRepository.recommend_1(user.getUserid(), user.getCity(), user.getGu(), userCodeList));
      userList.addAll(userRepository.recommend_2(user.getUserid(), user.getCity(), user.getGu(), userCodeList));
      userList.addAll(userRepository.recommend_3(user.getUserid(), user.getCity(), user.getGu(), userCodeList));
      userList.addAll(userRepository.recommend_4(user.getUserid(), user.getCity(), user.getGu(), userCodeList));
      userList.addAll(userRepository.recommend_5(user.getUserid(), userCodeList));

      for (int i = 0; i < userList.size(); i++) {
         if (userList.get(i).getUserid() == userid) {
            userList.remove(i);
         }
      }

      // page당 20명씩 반환
      List<User> userListPage = new ArrayList<>();
      if (page * 20 >= userList.size() && userList.size() > page * 20 - 20) {
         for (int i = page * 20 - 20; i < userList.size(); i++) {
            userListPage.add(userList.get(i));
         }
      } else if (page * 20 < userList.size()) {
         for (int i = page * 20 - 20; i < page * 20; i++) {
            userListPage.add(userList.get(i));
         }
      } else {
         return null;
      }

      // 더 불러올 페이지가 있는지의 여부
      Boolean hasMorePages = ((userList.size() - page * 20) > 0) ? true : false;

      final int gubun = 1;
      System.out.println(userListPage);
      Map<String, Object> popularMap = new HashMap<>();
      Map<Object, Object> useridMap = new HashMap<>();
      List<Integer> useridList = new ArrayList<>();
      List<Integer> index = new ArrayList<>();
      // user정보 매핑
      for (User u : userListPage) {
         Map<String, Object> userMap = new HashMap<>();
         userMap.put("id", u.getUserid());
         userMap.put("nickName", u.getName());
         userMap.put("gender", u.getGender());
         // age 계산
         if (u.getBirth() != null) {
            userMap.put("age", new Date().getYear() - u.getBirth().getYear() + 1);
         } else {
            userMap.put("age", null);
         }
         userMap.put("si", u.getCity());
         userMap.put("gu", u.getGu());
         // user정보에 userinterest 추가
         List<Integer> interestList = userInterestRepository.codeByUserid(u.getUserid());
         userMap.put("interests", interestList);
         // user정보에 imgpath 추가
         List<String> img = imgRepository.imgpathListByGubunAndId(gubun, u.getUserid());
         if (!img.isEmpty()) {
            userMap.put("image", img.get(0));
         } else {
            userMap.put("image", null);
         }
         // userid 매핑
         useridMap.put(userMap.get("id"), userMap);
         popularMap.put("users", useridMap);
         index.add(u.getUserid());
      }
      popularMap.put("hasMorePages", hasMorePages);

      popularMap.put("index", index);

      return popularMap;
   }

}