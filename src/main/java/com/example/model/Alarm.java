package com.example.model;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;

@Entity
public class Alarm{
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)	
	private int alarmid; 
	private int gubun; 
	private int id; //컨텐츠의id',
	private int userid; //읽어야할 user', 
	private int clubid; //그룹일경우 clubid, 
	private String message; //메세지
	private boolean receiveyn; //읽기여부',
	private Date writedate;

	public Alarm(int gubun, int id, int userid, int clubid, String message) {
		this.gubun = gubun;
		this.id = id;
		this.userid = userid;
		this.clubid = clubid;
		this.message = message;
	}

	public Alarm() {}

	@PrePersist
	public void beforeDate() {
		this.writedate = new Date();
	}

	public int getAlarmid() {
		return alarmid;
	}

	public void setAlarmid(int alarmid) {
		this.alarmid = alarmid;
	}

	public int getGubun() {
		return gubun;
	}

	public void setGubun(int gubun) {
		this.gubun = gubun;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getUserid() {
		return userid;
	}

	public void setUserid(int userid) {
		this.userid = userid;
	}

	public String getMessage() {
		return message;
	}

	public int getClubid() {
		return clubid;
	}

	public void setClubid(int clubid) {
		this.clubid = clubid;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public boolean isReceiveyn() {
		return receiveyn;
	}

	public void setReceiveyn(boolean receiveyn) {
		this.receiveyn = receiveyn;
	}

	public Date getWritedate() {
		return writedate;
	}

	public void setWritedate(Date writedate) {
		this.writedate = writedate;
	}

	@Override
	public String toString() {
		return "Alarm [alarmid=" + alarmid + ", gubun=" + gubun + ", id=" + id + ", userid=" + userid + ", clubid="
				+ clubid + ", message=" + message + ", receiveyn=" + receiveyn + ", writedate=" + writedate + "]";
	}

	
}
