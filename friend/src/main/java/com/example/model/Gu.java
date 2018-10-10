package com.example.model;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;

class GuPK implements Serializable{
	private static final long serialVersionUID = 1L;
	private int citycode;
	private int gucode;
	
}

@Entity
@IdClass(GuPK.class)
public class Gu {
	@Id
	private int citycode;
	@Id
	private int gucode;
	private String name;
	public int getCitycode() {
		return citycode;
	}
	public void setCitycode(int citycode) {
		this.citycode = citycode;
	}
	public int getGucode() {
		return gucode;
	}
	public void setGucode(int gucode) {
		this.gucode = gucode;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	@Override
	public String toString() {
		return "Gu [citycode=" + citycode + ", gucode=" + gucode + ", name=" + name + "]";
	}
	
}
