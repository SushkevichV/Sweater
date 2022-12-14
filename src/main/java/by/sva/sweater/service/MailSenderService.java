package by.sva.sweater.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

//для работы с почтой отключить антивирус

@Service
public class MailSenderService {
	@Autowired
	private JavaMailSender emailSender;
	
	@Value("${spring.mail.username}")
    private String username;
	
	public void send(String emailTo, String subject, String message) {
		SimpleMailMessage mailMessage = new SimpleMailMessage();
		mailMessage.setFrom(username);
		mailMessage.setTo(emailTo);
		mailMessage.setSubject(subject);
		mailMessage.setText(message);
		
		emailSender.send(mailMessage);
		
	}

}
