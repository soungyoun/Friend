package com.example.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.PrePersist;

class UserinterestPK implements Serializable{
	private static final long serialVersionUID = 1L;
	private int userid;
	private int code;
}

@Entity
@IdClass(UserinterestPK.class)
public class Userinterest {
	@Id
	private int userid;
	@Id
	private int code;
	
	private Date writedate;  //작성일자
	
	@PrePersist
	public void beforeDate() {
		this.writedate = new Date();
	}	
	
	public Userinterest() {
		
	}
	public Userinterest(int userid, int code) {
		this.userid = userid;
		this.code = code;
		
	}
	public int getUserid() {
		return userid;
	}
	public void setUserid(int userid) {
		this.userid = userid;
	}
	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
	public Date getWritedate() {
		return writedate;
	}

	public void setWritedate(Date writedate) {
		this.writedate = writedate;
	}

	@Override
	public String toString() {
		return "Userinterest [userid=" + userid + ", code=" + code + ", writedate=" + writedate + "]";
	}

	
	
	
}
