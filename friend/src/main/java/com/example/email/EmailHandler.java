package com.example.email;

import java.io.UnsupportedEncodingException;

import javax.activation.DataSource;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

@Component
public class EmailHandler {
	private JavaMailSender mailSender; //이메일 전송 객체
	private MimeMessage mm; //인코딩 방식 객체?
	private MimeMessageHelper mmh; // 이메일 전송 전 세팅을 위한 객체
	
	public EmailHandler(JavaMailSender mailSender) throws MessagingException {
		this.mailSender = mailSender;
		mm = this.mailSender.createMimeMessage(); //Mime 객체를 생성
		mmh = new MimeMessageHelper(mm, true, "UTF-8"); //2번째 파라미터: 멀티파트 메시지 여부, 3번째: 인코딩 형식
	}
	
	// 이메일 타이틀 설정
	public void setSubject(String subject) throws MessagingException {
		mmh.setSubject(subject);
	}
	
	// 이메일 내용 설정
	public void setText(String text) throws MessagingException {
		mmh.setText(text, true); //2번째 파라미터에 true를 주면 html텍스트 사용 가능
	}
	
	// 발신자, 이메일 설정
	public void setFrom(String sendEmail, String name) throws UnsupportedEncodingException, MessagingException {
		mmh.setFrom(sendEmail, name);
	}
	
	// 수신자 이메일 설정
	public void setTo(String receiveEmail) throws MessagingException {
		mmh.setTo(receiveEmail);
	}
	
	public void addInline(String contentId, DataSource dataSource) throws MessagingException { //데이터(사진등)를 보낼때 사용하는 객체
		mmh.addInline(contentId, dataSource); //파라미터명, 파일
	}
	
	// 메일 발송
	public void send() {
		mailSender.send(mm);
	}
}
