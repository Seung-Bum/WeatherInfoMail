package com.items.domain;

public class Member {
	private int no;
	private String auth; 
	private String email;
	private String password;
	private java.sql.Date registDate;
	  
	public int getNo() {
		return no;
	}
	public void setNo(int no) {
		this.no = no;
	}
	public String getAuth() {
		return auth;
	}
	public void setAuth(String name) {
		this.auth = name;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public java.sql.Date getRegistDate() {
		return registDate;
	}
	public void setRegistDate(java.sql.Date registDate) {
		this.registDate = registDate;
	}
	  
}