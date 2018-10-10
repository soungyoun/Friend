package com.example.model;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;

@Entity
public class Noticecom {
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int comid;
	private int noticeid;
	private int userid;
	private boolean attendyn;
	private String content;
	private Date writedate;
	
	@PrePersist
	public void beforeDate() {
		this.writedate = new Date();
	}
	
	public int getComid() {
		return comid;
	}
	public void setComid(int comid) {
		this.comid = comid;
	}
	public int getNoticeid() {
		return noticeid;
	}
	public void setNoticeid(int noticeid) {
		this.noticeid = noticeid;
	}

	public int getUserid() {
		return userid;
	}
	public void setUserid(int userid) {
		this.userid = userid;
	}
	public boolean isAttendyn() {
		return attendyn;
	}
	public void setAttendyn(boolean attendyn) {
		this.attendyn = attendyn;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public Date getWritedate() {
		return writedate;
	}
	public void setWritedate(Date writedate) {
		this.writedate = writedate;
	}
	@Override
	public String toString() {
		return "Noticecom [comid=" + comid + ", noticeid=" + noticeid + ", userid=" + userid + ", attendyn=" + attendyn
				+ ", content=" + content + ", writedate=" + writedate + "]";
	}
	
	
}
