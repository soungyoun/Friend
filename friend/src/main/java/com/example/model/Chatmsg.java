package com.example.model;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;

@Entity
public class Chatmsg {
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)	
	private int msgid ; 
	private int roomid;
	private int userid;
	private String message;
	private Date writedate;
	@PrePersist
	public void beforeDate() {
		this.writedate = new Date();
	}
	
	public int getMsgid() {
		return msgid;
	}
	public void setMsgid(int msgid) {
		this.msgid = msgid;
	}
	public int getRoomid() {
		return roomid;
	}
	public void setRoomid(int roomid) {
		this.roomid = roomid;
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
	public void setMessage(String message) {
		this.message = message;
	}
	public Date getWritedate() {
		return writedate;
	}
	public void setWritedate(Date writedate) {
		this.writedate = writedate;
	}
	@Override
	public String toString() {
		return "Chatmsg [msgid=" + msgid + ", roomid=" + roomid + ", userid=" + userid + ", message=" + message
				+ ", writedate=" + writedate + "]";
	}
	
	
}
