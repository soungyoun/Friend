package com.example.model;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;

@Entity
public class Club {
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int clubid;
	private String name;
	private int gender;
	private int city;
	private int gu;
	private String content;
	private int agestart;
	private int ageend;
	private int maxcount;
	private Date startdate;
	private boolean yn;
	private Date writedate;
	@PrePersist
	public void beforeDate() {
		this.writedate = new Date();
	}
	
	public Date getWritedate() {
		return writedate;
	}
	public void setWritedate(Date writedate) {
		this.writedate = writedate;
	}
	public int getClubid() {
		return clubid;
	}
	public void setClubid(int clubid) {
		this.clubid = clubid;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getGender() {
		return gender;
	}
	public void setGender(int gender) {
		this.gender = gender;
	}
	public int getCity() {
		return city;
	}
	public void setCity(int city) {
		this.city = city;
	}
	public int getGu() {
		return gu;
	}
	public void setGu(int gu) {
		this.gu = gu;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public int getAgestart() {
		return agestart;
	}
	public void setAgestart(int agestart) {
		this.agestart = agestart;
	}
	public int getAgeend() {
		return ageend;
	}
	public void setAgeend(int ageend) {
		this.ageend = ageend;
	}
	public int getMaxcount() {
		return maxcount;
	}
	public void setMaxcount(int maxcount) {
		this.maxcount = maxcount;
	}
	public Date getStartdate() {
		return startdate;
	}
	public void setStartdate(Date startdate) {
		this.startdate = startdate;
	}
	public boolean isYn() {
		return yn;
	}
	public void setYn(boolean yn) {
		this.yn = yn;
	}
	
	@Override
	public String toString() {
		return "Club [clubid=" + clubid + ", name=" + name  + ", gender=" + gender + ", city="
				+ city + ", gu=" + gu + ", content=" + content + ", agestart=" + agestart + ", ageend=" + ageend
				+ ", maxcount=" + maxcount + ", startdate=" + startdate + ", yn=" + yn + ", writedate=" + writedate
				+ "]";
	}
}
