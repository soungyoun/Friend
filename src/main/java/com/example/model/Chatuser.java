package com.example.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.PrePersist;

class ChatuserPK  implements Serializable{
	private static final long serialVersionUID = 1L;
	private int roomid;
	private int userid;
}

@Entity
@IdClass(ChatuserPK.class)
public class Chatuser {

	@Id
	private int roomid;
	@Id
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
		return "Chatuser [roomid=" + roomid + ", userid=" + userid + "]";
	}
	
	
}
