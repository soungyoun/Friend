package com.example.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.PrePersist;

class ClubinterestPK implements Serializable{

	private static final long serialVersionUID = 1L;
	private int clubid;
	private int code;
	
}
@Entity
@IdClass(ClubinterestPK.class)
public class Clubinterest {
	@Id
	private int clubid;
	@Id
	private int code;
	private Date writedate;
	@PrePersist
	public void beforeDate() {
		this.writedate = new Date();
	}
	
	public int getClubid() {
		return clubid;
	}
	public void setClubid(int clubid) {
		this.clubid = clubid;
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
		return "ClubInterest [clubid=" + clubid + ", code=" + code + ", writedate=" + writedate + "]";
	}

}
