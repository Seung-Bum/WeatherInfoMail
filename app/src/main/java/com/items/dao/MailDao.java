package com.items.dao;

import org.apache.ibatis.annotations.Mapper;

import com.items.domain.Member;

@Mapper
public interface MailDao {
	Member loginMailAuth(String email); // mail �������� �α��� �����ʿ�
}