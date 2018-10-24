package com.example.model;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;

import com.fasterxml.jackson.annotation.JsonFormat;

@Entity
public class User {
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int userid;  //순번
	private String email; //아이디
	private String pw; //패스워드
	private String name; //이름
	private int city; //시
	private int gu; //구
	private boolean areayn; //지역공개여부
	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern = "yyyy. M. d", timezone = "Aisa/Seoul")
	private Date birth;//생년월일
	private boolean birthyn; //생년월일공개여부
	private int gender; //성별
	private boolean genderyn; //성별공개여부
	private String phone; //핸드폰번호 
	private boolean phoneyn; //핸드폰번호공개여부(사용안함)
	private String msg; //원하는친구
	private String intro; //자기소개
	private boolean friendsyn; //친구들목록공개여부
	private boolean groupsyn; //그룹목록공개여부
	private boolean auth; //인증여부
	private String authkey; //인증key
	private int jointype;//가입방식
	private Date writedate;  //작성일자
	
	public User() {}
	
	//가입시
	public User(String email, String pw,  String authkey) {
		this.email = email;
		this.pw = pw;
		this.authkey = authkey;
	}
	
	@PrePersist
	public void beforeDate() {
		this.writedate = new Date();
	}

	public int getUserid() {
		return userid;
	}

	public void setUserid(int userid) {
		this.userid = userid;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPw() {
		return pw;
	}

	public void setPw(String pw) {
		this.pw = pw;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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

	public Date getBirth() {
		return birth;
	}

	public void setBirth(Date birth) {
		this.birth = birth;
	}

	public int getGender() {
		return gender;
	}

	public void setGender(int gender) {
		this.gender = gender;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public String getIntro() {
		return intro;
	}

	public void setIntro(String intro) {
		this.intro = intro;
	}

	public boolean isAreayn() {
		return areayn;
	}

	public void setAreayn(boolean areayn) {
		this.areayn = areayn;
	}

	public boolean isBirthyn() {
		return birthyn;
	}

	public void setBirthyn(boolean birthyn) {
		this.birthyn = birthyn;
	}

	public boolean isGenderyn() {
		return genderyn;
	}

	public void setGenderyn(boolean genderyn) {
		this.genderyn = genderyn;
	}

	public boolean isPhoneyn() {
		return phoneyn;
	}

	public void setPhoneyn(boolean phoneyn) {
		this.phoneyn = phoneyn;
	}

	public boolean isFriendsyn() {
		return friendsyn;
	}

	public void setFriendsyn(boolean friendsyn) {
		this.friendsyn = friendsyn;
	}

	public boolean isGroupsyn() {
		return groupsyn;
	}

	public void setGroupsyn(boolean groupsyn) {
		this.groupsyn = groupsyn;
	}

	public boolean isAuth() {
		return auth;
	}

	public void setAuth(boolean auth) {
		this.auth = auth;
	}

	public String getAuthkey() {
		return authkey;
	}

	public void setAuthkey(String authkey) {
		this.authkey = authkey;
	}

	public int getJointype() {
		return jointype;
	}

	public void setJointype(int jointype) {
		this.jointype = jointype;
	}

	public Date getWritedate() {
		return writedate;
	}

	public void setWritedate(Date writedate) {
		this.writedate = writedate;
	}

	@Override
	public String toString() {
		return "User [userid=" + userid + ", email=" + email + ", pw=" + pw + ", name=" + name + ", city=" + city
				+ ", gu=" + gu + ", birth=" + birth + ", gender=" + gender + ", phone=" + phone + ", msg=" + msg
				+ ", intro=" + intro + ", areayn=" + areayn + ", birthyn=" + birthyn + ", genderyn=" + genderyn
				+ ", phoneyn=" + phoneyn + ", friendsyn=" + friendsyn + ", groupsyn=" + groupsyn + ", auth=" + auth
				+ ", authkey=" + authkey + ", jointype=" + jointype + ", writedate=" + writedate + "]";
	}


 
	
}
