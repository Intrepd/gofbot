package com.guildoffools.utils;

import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.EmailAttachment;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.MultiPartEmail;

public class MailUtils
{
	public static EmailAttachment createAttachment(final String path, final String name, final String description)
	{
		final EmailAttachment attachment = new EmailAttachment();
		attachment.setPath(path);
		attachment.setDisposition(EmailAttachment.ATTACHMENT);
		attachment.setName(name);
		attachment.setDescription(description);

		return attachment;
	}

	public static void sendMail(final String host, final int port, final String username, final String password, final boolean useSSL, final String fromAddress,
			final String[] toAddressList, final String subject, final String msg, final EmailAttachment attachment) throws EmailException
	{
		final MultiPartEmail email = new MultiPartEmail();
		email.setHostName(host);
		email.setSmtpPort(port);
		if (username != null && password != null)
		{
			email.setAuthenticator(new DefaultAuthenticator(username, password));
		}
		email.setSSLOnConnect(useSSL);
		for (final String toAddress : toAddressList)
		{
			email.addTo(toAddress.trim());
		}
		email.setFrom(fromAddress);
		email.setSubject(subject);
		email.setMsg(msg);
		email.attach(attachment);

		email.send();
	}
}
