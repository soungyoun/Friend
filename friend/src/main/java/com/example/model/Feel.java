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
    private int userid;
   
}

@Entity
@IdClass(FeelPK.class)
public class Feel {
	@Id
    private int gubun; //1.친구, 2.그룹, 3.그룹게시판 4.게시판
	@Id
    private int id;	//而⑦뀗痢좎쓽id',
	@Id
    private int userid; //�쉶�썝id',
    private int type; //1:醫뗭븘�슂,2:�떊怨좏븯湲�, 3.議고쉶',
    private Date writedate;	//�옉�꽦�씪�옄',
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
