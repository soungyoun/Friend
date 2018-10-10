package com.example.model;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;

@Entity
public class Friend {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
    private int frid;   //id',
    private int userid;	// 요청받은id',
    private int requserid;  //요청한id',
    private String message; //요청메세지',
    private Date reqdate; //요청일자',
    private Date joindate;	//가입일자',
    private Date breakdate;	//탈퇴일자',
    private Date outdate;	//추방일자',
    private int userstate;	//1:요청, 2:허락, 3:거절, 4:삭제',
    private Date writedate;	 //작성일자',
	@PrePersist
	public void beforeDate() {
		this.writedate = new Date();
	}
	
    public Friend(int userid, int requserid, String message, int userstate) {
        this.userid = userid;
        this.requserid = requserid;
        this.message = message;
        this.userstate = userstate;
     }
     public Friend(int userid, int requserid, int userstate) {
        this.userid = userid;
        this.requserid = requserid;
        this.userstate = userstate;
     }
     
	public int getFrid() {
		return frid;
	}
	public void setFrid(int frid) {
		this.frid = frid;
	}
	public int getUserid() {
		return userid;
	}
	public void setUserid(int userid) {
		this.userid = userid;
	}
	public int getRequserid() {
		return requserid;
	}
	public void setRequserid(int requserid) {
		this.requserid = requserid;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public Date getReqdate() {
		return reqdate;
	}
	public void setReqdate(Date reqdate) {
		this.reqdate = reqdate;
	}
	public Date getJoindate() {
		return joindate;
	}
	public void setJoindate(Date joindate) {
		this.joindate = joindate;
	}
	public Date getBreakdate() {
		return breakdate;
	}
	public void setBreakdate(Date breakdate) {
		this.breakdate = breakdate;
	}
	public Date getOutdate() {
		return outdate;
	}
	public void setOutdate(Date outdate) {
		this.outdate = outdate;
	}
	public int getUserstate() {
		return userstate;
	}
	public void setUserstate(int userstate) {
		this.userstate = userstate;
	}
	public Date getWritedate() {
		return writedate;
	}
	public void setWritedate(Date writedate) {
		this.writedate = writedate;
	}
	@Override
	public String toString() {
		return "Friend [frid=" + frid + ", userid=" + userid + ", requserid=" + requserid + ", message=" + message
				+ ", reqdate=" + reqdate + ", joindate=" + joindate + ", breakdate=" + breakdate + ", outdate="
				+ outdate + ", userstate=" + userstate + ", writedate=" + writedate + "]";
	}
    
    
}
