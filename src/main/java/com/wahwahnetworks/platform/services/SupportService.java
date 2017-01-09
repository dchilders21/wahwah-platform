package com.wahwahnetworks.platform.services;

import com.wahwahnetworks.platform.models.SupportMessage;
import com.wahwahnetworks.platform.models.UserModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;

/**
 * Created by jhaygood on 2/23/15.
 */

@Service
public class SupportService
{

	@Autowired
	private MailSender mailSender;

	public void sendSupportMessage(UserModel userModel, SupportMessage supportMessage)
	{

		StringBuilder sb = new StringBuilder();
        sb.append("Requester Name: ").append(userModel.getFirstName()).append(" ").append(userModel.getLastName()).append("\r\n");
		sb.append("Requester Email: ").append(userModel.getEmailAddress()).append("\r\n");
        sb.append("Requester Account: ").append(userModel.getAccountName()).append("\r\n");
        sb.append("Requester Account Type: ").append(userModel.getAccountType()).append("\r\n\r\n");
        sb.append(supportMessage.getBody());

		SimpleMailMessage activationMessage = new SimpleMailMessage();
		activationMessage.setFrom("support@redpandaplatform.com");
		activationMessage.setTo("support@redpandaplatform.com");
		activationMessage.setReplyTo(userModel.getEmailAddress());
		activationMessage.setSubject("Support Request: " + supportMessage.getSubject());
		activationMessage.setText(sb.toString());

		mailSender.send(activationMessage);

	}
}
