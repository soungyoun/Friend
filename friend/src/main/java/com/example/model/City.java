package com.example.model;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class City {
	@Id
	int code ;
	String name;
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
		return "City [code=" + code + ", name=" + name + "]";
	}
	
	
}
