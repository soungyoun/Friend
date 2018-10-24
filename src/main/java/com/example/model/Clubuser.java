package com.example.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;

@Entity
public class Clubuser implements Serializable{

	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)	
	int id;
	private int clubid;
	private int userid;
	private int auth;
	private String message;
	private Date reqdate;
	private Date joindate;
	private Date breakdate;
	private Date outdate;
	private int userstate; //'1:요청, 2:가입, 3:탈퇴, 4:추방'
	private Date writedate;
	@PrePersist
	public void beforeDate() {
		this.writedate = new Date();
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
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
	public int getAuth() {
		return auth;
	}
	public void setAuth(int auth) {
		this.auth = auth;
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
		return "ClubUser [id=" + id + ", clubid=" + clubid + ", userid=" + userid + ", auth=" + auth + ", message="
				+ message + ", reqdate=" + reqdate + ", joindate=" + joindate + ", breakdate=" + breakdate
				+ ", outdate=" + outdate + ", userstate=" + userstate + ", writedate=" + writedate + "]";
	}
	
}
