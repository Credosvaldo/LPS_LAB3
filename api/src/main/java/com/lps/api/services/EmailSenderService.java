
package com.lps.api.services;

import java.io.UnsupportedEncodingException;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import jakarta.transaction.Transactional;

@Transactional
@Service
public class EmailSenderService {

    @Autowired
    private JavaMailSender mailSender;

    private static final String LOGO_IMAGE = "templates/img/logo.png";
    private static final String LINKEDIN_IMAGE = "templates/img/linkedin.png";
    private static final String PNG_MIME = "image/png";

    @Autowired
    private Environment environment;

    @Autowired
    private TemplateEngine htmlTemplateEngine;

    public void sendRecoveryPasswordMail(String email, String token)
            throws MessagingException, UnsupportedEncodingException,
            NoSuchElementException {

        String TEMPLATE_NAME = "RecoveryPassword";
        String LOCKED_IMAGE = "templates/img/locked.png";
        String MAIL_SUBJECT = "Recuperação de Senha";

        String mailFrom = environment.getProperty("spring.email.properties.email.smtp.from");
        String mailFromName = "Lab Prog System";


        // Extraí a lógica comum de criação de MimeMessage para um método privado, reduzindo a duplicação de código
        final MimeMessage mimeMessage = this.mailSender.createMimeMessage();

        final MimeMessageHelper emailWillBeSend;

        emailWillBeSend = new MimeMessageHelper(mimeMessage, true, "UTF-8");

        emailWillBeSend.setTo(email);
        emailWillBeSend.setSubject(MAIL_SUBJECT);
        emailWillBeSend.setFrom(new InternetAddress(mailFrom, mailFromName));

        final Context ctx = new Context(LocaleContextHolder.getLocale());
        ctx.setVariable("logo", LOGO_IMAGE);
        ctx.setVariable("unlocked", LOCKED_IMAGE);
        ctx.setVariable("linkedIn", LINKEDIN_IMAGE);
        ctx.setVariable("token", token);

        final String htmlContent = this.htmlTemplateEngine.process(TEMPLATE_NAME, ctx);

        emailWillBeSend.setText(htmlContent, true);

        ClassPathResource clr = new ClassPathResource(LOGO_IMAGE);
        ClassPathResource clr2 = new ClassPathResource(LOCKED_IMAGE);
        ClassPathResource clr3 = new ClassPathResource(LINKEDIN_IMAGE);
        emailWillBeSend.addInline("logo", clr, PNG_MIME);
        emailWillBeSend.addInline("unlocked", clr2, PNG_MIME);
        emailWillBeSend.addInline("linkedIn", clr3, PNG_MIME);
        mailSender.send(mimeMessage);
    }

    public void sendNewUser(String mail, String password)
            throws MessagingException, UnsupportedEncodingException {
        String TEMPLATE_NAME = "NewUser";
        String MAIL_SUBJECT = "Seja Bem vindo!!";

        String mailFrom = environment.getProperty("spring.mail.properties.mail.smtp.from");
        String mailFromName = "Lab Prog System";

        final MimeMessage mimeMessage = this.mailSender.createMimeMessage();

        final MimeMessageHelper email;

        email = new MimeMessageHelper(mimeMessage, true, "UTF-8");

        email.setTo(mail);
        email.setSubject(MAIL_SUBJECT);
        email.setFrom(new InternetAddress(mailFrom, mailFromName));

        final Context ctx = new Context(LocaleContextHolder.getLocale());
        ctx.setVariable("logo", LOGO_IMAGE);
        ctx.setVariable("linkedIn", LINKEDIN_IMAGE);
        ctx.setVariable("password", password);

        final String htmlContent = this.htmlTemplateEngine.process(TEMPLATE_NAME, ctx);

        email.setText(htmlContent, true);

        ClassPathResource clr = new ClassPathResource(LOGO_IMAGE);
        ClassPathResource clr3 = new ClassPathResource(LINKEDIN_IMAGE);
        email.addInline("logo", clr, PNG_MIME);
        email.addInline("linkedIn", clr3, PNG_MIME);
        mailSender.send(mimeMessage);

    }
}
