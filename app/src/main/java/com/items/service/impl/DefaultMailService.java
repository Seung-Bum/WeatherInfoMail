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
	public Member loginMailAuth(String email) { // �Ķ���ͷ� �Ѿ�� email�� �������� �����´�. 
		return mailDao.loginMailAuth(email);
	}
	
}
