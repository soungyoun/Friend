package com.example.model;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;

@Entity
public class Chatroom {
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)	
	private int roomid;
	private String roomname;
	private int clubid;
	private int userid;
	private Date writedate;
	
	@PrePersist
	public void beforeDate() {
		this.writedate = new Date();
	}
	
	public int getRoomid() {
		return roomid;
	}
	public void setRoomid(int roomid) {
		this.roomid = roomid;
	}
	public String getRoomname() {
		return roomname;
	}
	public void setRoomname(String roomname) {
		this.roomname = roomname;
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
	public Date getWritedate() {
		return writedate;
	}
	public void setWritedate(Date writedate) {
		this.writedate = writedate;
	}


	@Override
	public String toString() {
		return "Chatroom [roomid=" + roomid + ", roomname=" + roomname + ", clubid=" + clubid + ", userid=" + userid
				 + "]";
	}

	
	
}
