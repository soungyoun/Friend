package com.example.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;

class HitPK  implements Serializable {
	private static final long serialVersionUID = 1L;
	private int gubun; 
    private int id; 
    private Date hittime; 
}

@Entity
@IdClass(HitPK.class)
public class Hit {
	@Id
    private int gubun; //컨텐츠구분 1.친구, 2.그룹게시판,3.게시판',
	@Id
    private int id; //컨텐츠의id',
	@Id
    private Date hittime; //접속시간',
	
    private int userid; //접속회원id',	
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
	public Date getHittime() {
		return hittime;
	}
	public void setHittime(Date hittime) {
		this.hittime = hittime;
	}
	public int getUserid() {
		return userid;
	}
	public void setUserid(int userid) {
		this.userid = userid;
	}
	@Override
	public String toString() {
		return "Hit [gubun=" + gubun + ", id=" + id + ", hittime=" + hittime + ", userid=" + userid + "]";
	}
	
    
}
