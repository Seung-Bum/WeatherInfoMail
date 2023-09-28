package com.items.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.items.service.MailService;
import com.items.service.RestAPIService;

@Controller
@RequestMapping("/mail")
public class MailController {
	
	private static final Logger log = LogManager.getLogger(MailController.class);
	
	@Autowired
	MailService mailService;
	
	@Autowired
	RestAPIService restAPIService;

	/**
	 * 메일 발송
	 *  - PostMapping은 RequestBody로 전달
	 * @param  String email, String subject, String message
	 * @return void
	 */
	@PostMapping("/sendmail") 
	public void sendPlainTextEmail(String email, String subject, String message) {
    	
		// 세션에서 로그인된 아이디를 가져온다 (파라미터로 받음)
    	//String user_id = (String) sentSession.getAttribute("user_id"); // 발신자 메일
		
		String user_id = "bum1272@naver.com";
		log.info("* mail 발송 시작");
    	log.info(" - 수신자 메일 : " + email + ", 발신자 메일 : " + user_id);
    	
        Properties p = System.getProperties();
        p.put("mail.smtp.starttls.enable", "true");     // gmail은 true 고정
        p.put("mail.smtp.host", "smtp.naver.com");      // smtp 서버 주소
        p.put("mail.smtp.auth","true");                 // gmail은 true 고정
        p.put("mail.smtp.port", "587");                 // 네이버 포트
        log.info(" - smtp 설정");
        
        class MyAuthentication extends Authenticator {            
            PasswordAuthentication pa;
            
            public MyAuthentication(String idPram){
                String id = idPram;  //네이버 이메일 아이디
                String pw = mailService.loginMailAuth(idPram).getPassword();  //네이버 비밀번호
         
                // ID와 비밀번호를 입력한다.
                pa = new PasswordAuthentication(id, pw);
            }
         
            // 시스템에서 사용하는 인증정보
            public PasswordAuthentication getPasswordAuthentication() {
                return pa;
            }
        }
                
        Authenticator auth = new MyAuthentication(user_id);
        log.info("  - auth 생성");
        
        //session 생성 및  MimeMessage생성
        Session session = Session.getDefaultInstance(p, auth);
        MimeMessage msg = new MimeMessage(session);        
        log.info("  - session 생성");
        
        try{
            //편지보낸시간
            msg.setSentDate(new Date());
            InternetAddress from = new InternetAddress();
            from = new InternetAddress(user_id); //발신자 아이디            
            log.info(" - 발신자 설정");
            
            // 이메일 발신자
            msg.setFrom(from);
            
            // 이메일 수신자
            InternetAddress to = new InternetAddress(email);
            msg.setRecipient(Message.RecipientType.TO, to);
            
            // 이메일 제목
            msg.setSubject(subject, "UTF-8");
            
            // 이메일 내용
            msg.setText(message, "UTF-8");
            
            // 이메일 헤더
            msg.setHeader("content-Type", "text/html");
            
            //메일보내기
            javax.mail.Transport.send(msg, msg.getAllRecipients());
            
            log.info(" - mail 발송 완료");
            
        }catch (AddressException addr_e) {
            addr_e.printStackTrace();
        }catch (MessagingException msg_e) {
            msg_e.printStackTrace();
        }catch (Exception msg_e) {
            msg_e.printStackTrace();
        }
    }

	/**
	 * 날씨정보 메일 스케줄
	 *  - 매일 당일 오전6시 날씨정보를 출국일 전까지 발송한다. (사용자의 출국일이 지나면 메일 발송 중지)
	 * @return String @ author yang @ version 1.0
	 */
	@GetMapping("/weatherInfoMailSch")
	@Scheduled(cron = "0 0 6 * * ?") // 초 분 시 일 월 요일
	public String weatherInfoMailScheduled() throws Exception {
		
		try {
			// 메일발송 대상자 조회
			//  - user 정보 가져와서 출국일이 지났을 경우 메일발송 x
			List<HashMap<String, Object>> targetUser = restAPIService.targetUserInfo();
			
			// 메일 내용으로 사용할 기상정보 발췌
			HashMap<String, Object> weatherInfo = restAPIService.selectWeatherInfo();
			 
	    	String subject = "WeatherInfo";
	    	String message = (String) weatherInfo.get("LINE0") + weatherInfo.get("LINE1") + 
	    							weatherInfo.get("LINE2") + weatherInfo.get("LINE3") + weatherInfo.get("LINE4");
	    	
	    	targetUser.forEach(user -> {
	    		sendPlainTextEmail((String)user.get("EMAIL"), subject, message);
	    	});
	    	
	    	return "success";
		} catch(Exception e) {
			log.error("weatherInfoMailSch: " + e.getMessage());
			return "fail";
		}
    }

}
