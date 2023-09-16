package com.items.controller;

import java.util.Date;
import java.util.HashMap;
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

	// 메일 발송 (get으로 호출 x)
	@PostMapping("/sendmail") 
	public String sendPlainTextEmail(Model model, HttpSession sentSession, String email, String subject, String message) {
    	
    	String user_id = (String) sentSession.getAttribute("user_id"); // 발신자 메일    	
    	log.info("수신자 메일 : " + email + ", 발신자 메일 : " + user_id);
    	
        Properties p = System.getProperties();
        p.put("mail.smtp.starttls.enable", "true");     // gmail은 true 고정
        p.put("mail.smtp.host", "smtp.naver.com");      // smtp 서버 주소
        p.put("mail.smtp.auth","true");                 // gmail은 true 고정
        p.put("mail.smtp.port", "587");                 // 네이버 포트
        log.info("smtp 설정");
        
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
        log.info("auth 생성");
        
        //session 생성 및  MimeMessage생성
        Session session = Session.getDefaultInstance(p, auth);
        MimeMessage msg = new MimeMessage(session);        
        log.info("session 생성");
        
        try{
            //편지보낸시간
            msg.setSentDate(new Date());
            InternetAddress from = new InternetAddress();
            from = new InternetAddress(user_id); //발신자 아이디            
            log.info("발신자 설정");
            
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
            
            log.info("mail 발송 완료");
            model.addAttribute("mailSuccessYn", "Y"); // 메일전송 과정이 모두 끝나면 트리거를 Y로 설정
        }catch (AddressException addr_e) {
        	model.addAttribute("mailSuccessYn", "N"); // 메일전송 과정에 문제가 생겼을때 N으로 설정
            addr_e.printStackTrace();
        }catch (MessagingException msg_e) {
        	model.addAttribute("mailSuccessYn", "N");
            msg_e.printStackTrace();
        }catch (Exception msg_e) {
        	model.addAttribute("mailSuccessYn", "N");
            msg_e.printStackTrace();
        }        
        return "mailPage";
    }
	
	// 메일 발송 스케줄
	@PostMapping("/sendmailScheduled")
	//@Scheduled(cron = "0 58 22 * * ?") // 초 분 시 일 월 요일
	public void sendPlainTextEmailScheduled() { 	
		
    	String user_id = "pkapka_@naver.com"; // 발신자 메일 
    	String email = "hlpark0209@naver.com";
    	String subject = "예약메일 발송";
    	String message = "예약메일을 발송합니다.";
    	
    	log.info("메일 스케줄러 - 수신자 메일 : " + email + "발신자 메일 : " + user_id);
    	
        Properties p = System.getProperties();
        p.put("mail.smtp.starttls.enable", "true");     // gmail은 true 고정
        p.put("mail.smtp.host", "smtp.naver.com");      // smtp 서버 주소
        p.put("mail.smtp.auth","true");                 // gmail은 true 고정
        p.put("mail.smtp.port", "587");                 // 네이버 포트
        log.info("smtp 설정");
        
        class MyAuthentication extends Authenticator {            
            PasswordAuthentication pa;
            
            public MyAuthentication(String idPram){
                String id = idPram;  //네이버 이메일 아이디
                String pw = mailService.loginMailAuth(idPram).getPassword();  // DB에서 비밀번호 가져오기
         
                // ID와 비밀번호를 입력한다.
                pa = new PasswordAuthentication(id, pw);
            }
         
            // 시스템에서 사용하는 인증정보
            public PasswordAuthentication getPasswordAuthentication() {
                return pa;
            }
        }
                
        Authenticator auth = new MyAuthentication(user_id);
        log.info("auth 생성");
        
        //session 생성 및 MimeMessage생성
        Session session = Session.getDefaultInstance(p, auth);
        MimeMessage msg = new MimeMessage(session);        
        log.info("session 생성");
        
        try{
            //편지보낸시간
            msg.setSentDate(new Date());
            InternetAddress from = new InternetAddress();
            from = new InternetAddress(user_id); //발신자 아이디            
            log.info("발신자 설정");
            
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
            
            log.info("mail 발송 완료");
        }catch (AddressException addr_e) {
            addr_e.printStackTrace();
        }catch (MessagingException msg_e) {
            msg_e.printStackTrace();
        }catch (Exception msg_e) {
            msg_e.printStackTrace();
        }        
    } 

	// 날씨정보 메일 스케줄
	@PostMapping("/weatherInfoMailSch")
	@Scheduled(cron = "0 53 20 * * ?") // 초 분 시 일 월 요일
	public void weatherInfoMailScheduled() throws Exception {
		
		// user 정보 가져와서 출국일이 지났을경우 메일발송 x
		HashMap<String, Object> userInfo = restAPIService.selectUserInfo();
		
		//userInfo.get(userInfo);
		
		// 메일 내용으로 사용할 기상정보 발췌
		HashMap<String, Object> weatherInfo = restAPIService.selectWeatherInfo();
		
    	String user_id = "pkapka_@naver.com"; // 발신자 메일 
    	String email = "hlpark0209@naver.com";
    	String subject = " ";
    	String message = (String) weatherInfo.get("LINE0") + weatherInfo.get("LINE1") + weatherInfo.get("LINE2") 
    							+ weatherInfo.get("LINE3") + weatherInfo.get("LINE4");
    	
    	log.info("메일 스케줄러 - 수신자 메일 : " + email + "발신자 메일 : " + user_id);
    	
        Properties p = System.getProperties();
        p.put("mail.smtp.starttls.enable", "true");     // gmail은 true 고정
        p.put("mail.smtp.host", "smtp.naver.com");      // smtp 서버 주소
        p.put("mail.smtp.auth","true");                 // gmail은 true 고정
        p.put("mail.smtp.port", "587");                 // 네이버 포트
        log.info("smtp 설정");
        
        class MyAuthentication extends Authenticator {            
            PasswordAuthentication pa;
            
            public MyAuthentication(String idPram){
                String id = idPram;  //네이버 이메일 아이디
                String pw = mailService.loginMailAuth(idPram).getPassword();  // DB에서 비밀번호 가져오기
         
                // ID와 비밀번호를 입력한다.
                pa = new PasswordAuthentication(id, pw);
            }
         
            // 시스템에서 사용하는 인증정보
            public PasswordAuthentication getPasswordAuthentication() {
                return pa;
            }
        }
                
        Authenticator auth = new MyAuthentication(user_id);
        log.info("auth 생성");
        
        //session 생성 및 MimeMessage생성
        Session session = Session.getDefaultInstance(p, auth);
        MimeMessage msg = new MimeMessage(session);        
        log.info("session 생성");
        
        try{
            //편지보낸시간
            msg.setSentDate(new Date());
            InternetAddress from = new InternetAddress();
            from = new InternetAddress(user_id); //발신자 아이디            
            log.info("발신자 설정");
            
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
            
            log.info("mail 발송 완료");
        }catch (AddressException addr_e) {
            addr_e.printStackTrace();
        }catch (MessagingException msg_e) {
            msg_e.printStackTrace();
        }catch (Exception msg_e) {
            msg_e.printStackTrace();
        }        
    } 
}
