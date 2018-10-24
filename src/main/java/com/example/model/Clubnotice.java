package com.example.model;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;

@Entity
public class Clubnotice {
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
    private int noticeid; //'그룹알림순번
    private int clubid; //그룹id
    private int userid; //회원id(작성자)
    private String title;	//제목
    private String content; //내용
    private int type; //1:공지, 2:모임, 3.게시글
    private Date meetdate; //모임날짜
    private String meettime; //모임시간
    private String meetlocation; //모임위치
    private Date writedate;
	@PrePersist
	public void beforeDate() {
		this.writedate = new Date();
	}
	
	public int getNoticeid() {
		return noticeid;
	}
	public void setNoticeid(int noticeid) {
		this.noticeid = noticeid;
	}
	public int getClubid() {
		return clubid;
	}
	public void setClubid(int clubid) {
		this.clubid = clubid;
	}
	public int getUserid() {
		return userid;
	}
	public void setUserid(int userid) {
		this.userid = userid;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public Date getMeetdate() {
		return meetdate;
	}
	public void setMeetdate(Date meetdate) {
		this.meetdate = meetdate;
	}
	public String getMeettime() {
		return meettime;
	}
	public void setMeettime(String meettime) {
		this.meettime = meettime;
	}
	public String getMeetlocation() {
		return meetlocation;
	}
	public void setMeetlocation(String meetlocation) {
		this.meetlocation = meetlocation;
	}
	public Date getWritedate() {
		return writedate;
	}
	public void setWritedate(Date writedate) {
		this.writedate = writedate;
	}
	@Override
	public String toString() {
		return "Clubnotice [noticeid=" + noticeid + ", clubid=" + clubid + ", userid=" + userid + ", title=" + title
				+ ", content=" + content + ", type=" + type + ", meetdate=" + meetdate + ", meettime=" + meettime
				+ ", meetlocation=" + meetlocation + ", writedate=" + writedate + "]";
	}
    
    
}
