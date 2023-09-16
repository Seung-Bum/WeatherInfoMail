package com.items.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.items.dao.MailDao;
import com.items.domain.Member;
import com.items.service.MailService;

@Service
public class DefaultMailService implements MailService {	
	
	@Autowired(required=true)
	MailDao mailDao;

	@Override
	public Member loginMailAuth(String email) { // 파라미터로 넘어온 email의 고객정보를 가져온다. 
		return mailDao.loginMailAuth(email);
	}
	
}
