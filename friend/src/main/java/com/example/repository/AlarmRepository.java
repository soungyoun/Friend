package com.example.repository;

import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.UserFunction;
import com.example.model.Alarm;

public interface AlarmRepository extends JpaRepository<Alarm, Integer>{

   //그룹가입신청을 마스터가 읽었으면 읽었다고 변경!
   @Modifying
   @Transactional
   @Query(value="update alarm set receiveyn=true where gubun=3 and id=:id and userid=:userid",nativeQuery=true)
   public void readGroupJoin(@Param("id")int clubid,@Param("userid")int userid);
   
   //김성연
   //로그인시 친구요청 알람
   @Query(value="select al.alarmid notification, al.gubun, al.id, u.name nickName, al.message, al.writedate, CONCAT('" + UserFunction.ImgPath + "', i.image) image, al.clubid groupid" + 
   		"   		from alarm al inner join user u " + 
   		"   			on (al.id = u.userid and al.userid =:userid and al.receiveyn = 0 )" + 
   		"   		left join (select id, min(imgpath) image from img group by id) i " + 
   		"   			on (al.id = i.id)", nativeQuery=true)
   public List<Map<String, Object>> getUserAlarm(@Param("userid")int userid);
   
    //김성연
   //알람읽기 수정
   @Modifying
   @Transactional
   @Query(value="update alarm set receiveyn=true where alarmid = :alarmid ",nativeQuery=true)
   public void readAlarmUpdate(@Param("alarmid")int alarmid);
   
    
}