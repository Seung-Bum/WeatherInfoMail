package com.items.domain;

import java.sql.Date;

public class Member {
	private int no;
	private String userID; // ȸ�� ���Խ� �ߺ��Ǵ� ���̵� üũ�ؼ� ���Ծȵǰ� �ؾ���
	private String email;  // �̸��ϵ� ���������� �ߺ�üũ �ʿ�
	private String password;
	private java.sql.Date registDate;
	  
	public int getNo() {
		return no;
	}
	public void setNo(int no) {
		this.no = no;
	}
	public String getUserID() {
		return userID;
	}
	public void setUserID(String name) {
		this.userID = name;
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