package com.example.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.model.Clubnotice;

public interface ClubnoticeRepository extends JpaRepository<Clubnotice, Integer>{

   @Query(value="select * from clubnotice where meetdate>now() and clubid=:clubid order by meetdate",nativeQuery=true)
   public List<Clubnotice> ongoingnotice(@Param("clubid")int clubid);
   
   //최근 7일이내 공지글 
   @Query(value="select * from clubnotice where type=1 and clubid=:clubid and date(writedate)>=subdate(now(),Interval 7 day)) order by writedate desc",nativeQuery=true)
   public List<Clubnotice> recentAnnouncements(int clubid);
   
   @Query(value="select * from clubnotice where noticeid=:noticeid",nativeQuery=true)
   public Clubnotice getClubnotice(@Param("noticeid")int noticeid);

   @Query(value="select * from clubnotice where clubid=:clubid",nativeQuery=true)
   public List<Clubnotice> getClubnotices(@Param("clubid") int clubid);
}