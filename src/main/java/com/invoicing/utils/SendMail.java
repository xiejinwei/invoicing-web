package com.invoicing.utils;

import java.util.Calendar;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMessage.RecipientType;

public class SendMail {

	/**
	 * 发送邮件
	 * 
	 * @param smtpHost		邮件服务提供商  例如：smtp.126.com
	 * @param from			邮件发送方登录名
	 * @param fromloginpass	邮件发送方登录密码
	 * @param to 			接收邮件地址
	 * @param subject 		邮件主题
	 * @param messageText 	邮件内容
	 * @param messageType 	邮件编码格式，例如：text/html;charset=gb2312
	 * @throws MessagingException
	 */
	@SuppressWarnings("static-access")
	public static void sendMessage(String smtpHost, String from,
			String fromloginpass, String to, String subject,
			String messageText, String messageType) {
		if (smtpHost == null)
			smtpHost = "smtp.126.com";
		if (from == null) {
			from = "xiejinwei1986@126.com";
			fromloginpass = "xiehan545483";
		}
		// 第一步：配置javax.mail.Session对象
		Properties props = new Properties();
		props.put("mail.smtp.host", smtpHost);
		props.put("mail.smtp.starttls.enable", "true");// 使用 STARTTLS安全连接
		// props.put("mail.smtp.port", "25"); //google使用465或587端口
		props.put("mail.smtp.auth", "true"); // 使用验证
		// props.put("mail.debug", "true");
		Session mailSession = Session.getInstance(props, new MyAuthenticator(
				from, fromloginpass));
		// 第二步：编写消息
		try {
			InternetAddress fromAddress = new InternetAddress(from);
			InternetAddress toAddress = new InternetAddress(to);

			MimeMessage message = new MimeMessage(mailSession);

			message.setFrom(fromAddress);
			message.addRecipient(RecipientType.TO, toAddress);

			message.setSentDate(Calendar.getInstance().getTime());
			message.setSubject(subject);
			message.setContent(messageText, messageType);

			// 第三步：发送消息
			Transport transport = mailSession.getTransport("smtp");
			transport.connect(smtpHost, "chaofeng19861126", fromloginpass);
			transport.send(message, message.getRecipients(RecipientType.TO));
		} catch (AddressException e) {
			throw new RuntimeException("邮件发送失败，请检查邮件地址是否正确");
		} catch (NoSuchProviderException e) {
			throw new RuntimeException("邮件发送失败，请检查邮件服务提供方配置是否正确");
		} catch (MessagingException e) {
			throw new RuntimeException("邮件发送失败，请检查邮件邮件信息");
		}
		System.out.println("message yes");
	}

	public static void main(String[] args) {
		SendMail.sendMessage("smtp.126.com", "xiejinwei1986@126.com",
				"xiehan545483", "252467894@qq.com", "test1",
				"--------------------this is the test1 content ---------",
				"text/html;charset=gb2312");
	}
}

class MyAuthenticator extends Authenticator {
	String userName = "";
	String password = "";

	public MyAuthenticator() {

	}

	public MyAuthenticator(String userName, String password) {
		this.userName = userName;
		this.password = password;
	}

	protected PasswordAuthentication getPasswordAuthentication() {
		return new PasswordAuthentication(userName, password);
	}
}
