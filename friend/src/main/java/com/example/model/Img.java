package com.example.model;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;

@Entity
public class Img {
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int imgid;
	private int gubun;
	private int id ;
	private String imgpath;
	private boolean firstyn;
	private Date writedate;
	
	@PrePersist
	public void beforeDate() {
		this.writedate = new Date();
	}
	
	public Img() {
		super();
	}
	public Img(int imgid, int gubun, int id, String imgpath, boolean firstyn, Date writedate) {
		super();
		this.imgid = imgid;
		this.gubun = gubun;
		this.id = id;
		this.imgpath = imgpath;
		this.firstyn = firstyn;
		this.writedate = writedate;
	}
	public int getImgid() {
		return imgid;
	}
	public void setImgid(int imgid) {
		this.imgid = imgid;
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
	public String getImgpath() {
		return imgpath;
	}
	public void setImgpath(String imgpath) {
		this.imgpath = imgpath;
	}
	public boolean isFirstyn() {
		return firstyn;
	}
	public void setFirstyn(boolean firstyn) {
		this.firstyn = firstyn;
	}
	public Date getWritedate() {
		return writedate;
	}
	public void setWritedate(Date writedate) {
		this.writedate = writedate;
	}
	@Override
	public String toString() {
		return "Img [imgid=" + imgid + ", gubun=" + gubun + ", id=" + id + ", imgpath=" + imgpath + ", firstyn="
				+ firstyn + ", writedate=" + writedate + "]";
	}
	
	
}
