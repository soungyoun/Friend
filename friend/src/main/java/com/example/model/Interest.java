package com.example.model;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Interest {
	@Id
	private int code;
	private String name;
	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	@Override
	public String toString() {
		return "Interest [code=" + code + ", name=" + name + "]";
	}
	
}
