package com.example.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.PrePersist;

class FeelPK implements Serializable{
	private static final long serialVersionUID = 1L;
    private int gubun; 
    private int id;	
}

@Entity
@IdClass(FeelPK.class)
public class Feel {
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
    private int gubun; //컨텐츠구분 1.그룹,2:그룹알림,3:그룹알림댓글, 4.친구',
	@Id
    private int id;	//컨텐츠의id',
	@Id
    private int userid; //회원id',
    private int type; //1:좋아요,2:신고하기, 3.조회',
    private Date writedate;	//작성일자',
	@PrePersist
	public void beforeDate() {
		this.writedate = new Date();
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
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public Date getWritedate() {
		return writedate;
	}
	public void setWritedate(Date writedate) {
		this.writedate = writedate;
	}
	@Override
	public String toString() {
		return "Feel [gubun=" + gubun + ", id=" + id + ", userid=" + userid + ", type=" + type + ", writedate="
				+ writedate + "]";
	}
    
    
}
