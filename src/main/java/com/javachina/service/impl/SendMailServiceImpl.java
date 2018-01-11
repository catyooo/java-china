package com.javachina.service.impl;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import javax.mail.MessagingException;

import com.blade.ioc.annotation.Component;
import com.blade.ioc.annotation.Inject;
import com.javachina.Actions;
import com.javachina.Constant;
import com.javachina.service.SendMailService;
import com.javachina.service.UserlogService;
import com.jmail.MailMessage;
import com.jmail.MailSender;
import com.jmail.MailSenderImpl;

import blade.kit.logging.Logger;
import blade.kit.logging.LoggerFactory;
import jetbrick.template.JetEngine;
import jetbrick.template.JetTemplate;

@Component
public class SendMailServiceImpl implements SendMailService {

	private static final Logger LOGGER = LoggerFactory.getLogger(SendMailServiceImpl.class);
	
	@Inject
	private UserlogService userlogService;
	
	private MailSender mailSender = new MailSenderImpl();
	
	@Override
	public void signup(String username, String email, String code) {

		try {
			String link_url = Constant.SITE_URL + "/active/" + code;
			
			String url = "<a href='"+link_url+"'>"+ link_url + "</a>";
	        JetEngine engine = JetEngine.create();
	        
	        JetTemplate template = engine.getTemplate("/template/register.html");
	        
			Map<String, Object> context = new HashMap<String, Object>();
			context.put("username", username);
			context.put("email", Constant.MAIL_ADMIN);
			context.put("url", url);

			StringWriter writer = new StringWriter();
	        template.render(context, writer);
	        
			String output = writer.toString();
			
//			System.out.println(output);
			
			MailMessage mailMessage = new MailMessage();
			mailMessage
			.subject("注册激活邮件")
			.from(Constant.MAIL_NICK, Constant.MAIL_USER)
			.content(output)
			.addTo(email);
			
			mailSender.host(Constant.MAIL_HOST).username(Constant.MAIL_USER).password(Constant.MAIL_PASS);
			
			mailSender.send(mailMessage, true);
			
			userlogService.save(0L, Actions.SEND_MAIL, email + ":" + code + ":" + "signup");
			
			LOGGER.info("user {} signup, send mail [{}] success, code = [{}]", username, email, code);
		} catch (MessagingException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void forgot(String username, String email, String code) {
		try {
			String link_url = Constant.SITE_URL + "/active/" + code;
			
			String url = "<a href='"+link_url+"'>"+ link_url +"</a>";
	        JetEngine engine = JetEngine.create();
	        
	        JetTemplate template = engine.getTemplate("/template/forgot.html");
	        
			Map<String, Object> context = new HashMap<String, Object>();
			context.put("username", username);
			context.put("email", Constant.MAIL_ADMIN);
			context.put("url", url);
			
			StringWriter writer = new StringWriter();
	        template.render(context, writer);
	        
			String output = writer.toString();
			
//			System.out.println(output);
			
			MailMessage mailMessage = new MailMessage();
			mailMessage
			.subject("找回密码邮件")
			.from(Constant.MAIL_NICK, Constant.MAIL_USER)
			.content(output)
			.addTo(email);
			
			mailSender.host(Constant.MAIL_HOST).username(Constant.MAIL_USER).password(Constant.MAIL_PASS);
			
			mailSender.send(mailMessage, true);
			
			userlogService.save(0L, Actions.SEND_MAIL, email + ":" + code + ":" + "forgot");
			
			LOGGER.info("user {} forgot, send mail [{}] success, code = [{}]", username, email, code);
		} catch (MessagingException e) {
			e.printStackTrace();
		}
	}

}
