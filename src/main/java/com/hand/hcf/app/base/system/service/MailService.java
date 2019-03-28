package com.hand.hcf.app.base.system.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.CharEncoding;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.exceptions.TemplateInputException;
import org.thymeleaf.spring5.SpringTemplateEngine;

import javax.annotation.PostConstruct;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeUtility;
import javax.mail.util.ByteArrayDataSource;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Service for sending e-mails.
 * <p>
 * We use the @Async annotation to send e-mails asynchronously.
 * </p>
 * update by lichao 2016-07-12
 */
@Slf4j
@Service
public class MailService {
    @Autowired
    Environment env;
    @Autowired
    private JavaMailSenderImpl javaMailSender;
    @Autowired
    private MessageSource messageSource;
    @Autowired
    private SpringTemplateEngine templateEngine;
    private String from;
    private String mock;
    private String failedTravelApprovalMock;

    @PostConstruct
    public void init() {
        this.from = env.getProperty("mail.from");
        this.mock = env.getProperty("mail.mock");
        this.failedTravelApprovalMock = env.getProperty("mail.failedTravelApprovalMock");
    }

    /**
     * 邮件发送带附件
     *
     * @param to
     * @param subject
     * @param content
     * @param isMultipart
     * @param isHtml
     */

    public boolean sendEmail(String to, String subject, String content, boolean isMultipart, boolean isHtml, byte[] attachment, String fileName) {
        // Prepare message using a Spring helper
        log.info("Sending  Email");
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper message = new MimeMessageHelper(mimeMessage, isMultipart, CharEncoding.UTF_8);
            if (StringUtils.isNotEmpty(mock)) {
                message.setTo(mock.split(";"));
            } else {
                message.setTo(to);
            }
            message.setFrom(from);
            message.setSubject(subject);
            message.setText(content, isHtml);
            if (attachment != null) {
                ByteArrayDataSource source = new ByteArrayDataSource(attachment, "application/octet-stream");
                message.addAttachment(MimeUtility.encodeText(fileName), source);
            }
            javaMailSender.send(mimeMessage);
            log.info("Email Sented");
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Email Sending failed with exception {}",e.getMessage());

            return false;
        }


    }

    public boolean sendEmailNoMock(String to, String subject, String content, boolean isMultipart, boolean isHtml, byte[] attachment, String fileName) {
        // Prepare message using a Spring helper
        log.info("Sending  Email no mock");
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper message = new MimeMessageHelper(mimeMessage, isMultipart, CharEncoding.UTF_8);
            message.setTo(to);
            message.setFrom(from);
            message.setSubject(subject);
            message.setText(content, isHtml);
            if (attachment != null) {
                ByteArrayDataSource source = new ByteArrayDataSource(attachment, "application/octet-stream");
                message.addAttachment(MimeUtility.encodeText(fileName), source);
            }
            javaMailSender.send(mimeMessage);
            log.info("Email Sented");
            return true;
        } catch (Exception e) {

            HashMap param = new HashMap();
            param.put("email", to);
            param.put("content", content);
            param.put("subject", subject);
            param.put("fileName", fileName);
            //记录错误日志
            e.printStackTrace();
            log.error("Email Sending failed with exception {}", e.getMessage());
            return false;
        }


    }

//    public boolean sendEmail(String to, String subject, String content)


    public boolean sendActivationTokenEmail(String email, String token, String companyName, Locale locale) {
        log.debug("Sending Activation Email Link Email");
        Context context = new Context(locale);
        context.setVariable("activationToken", token);
        context.setVariable("companyName", companyName);
        String templateName = "activationTokenEmail-".concat(locale.getLanguage());
        String content="";
        String subject="";
        try{
            content = templateEngine.process(templateName, context);
            subject = messageSource.getMessage("email.activation.title", new Object[]{companyName}, locale);
        }
        catch(TemplateInputException e){
            //使用默认文件
            templateName = "activationTokenEmail";
            content = templateEngine.process(templateName, context);
            subject = messageSource.getMessage("email.activation.title", new Object[]{companyName}, locale);
        }
        catch(Exception e){
            throw e;
        }
        log.debug("Activation Email Link Email Sent");
        return sendEmail(email, subject, content, true, true, null, null);
    }



    public boolean sendRestPasswordTokenEmail(String email, String token, String companyName, Locale locale) {
        log.debug("Sending Rest Password Email Link Email");
        Context context = new Context(locale);
        context.setVariable("activationToken", token);
        context.setVariable("companyName", companyName);
        String content = templateEngine.process("restPasswordTokenEmail", context);
        String subject = messageSource.getMessage("email.resetpassword.title", new Object[]{companyName}, locale);
        log.debug("Rest Password Email Link Email Sent");
        return sendEmail(email, subject, content, true, true, null, null);
    }

    /**
     * 邮件发送pdf
     *
     * @param downlowdLink
     * @param email
     * @param locale
     */

    public boolean sendPDFLinkEmail(String downlowdLink, String email, byte[] att, String fileName, Locale locale) {
        log.debug("Sending PDF download link Email");
        Context context = new Context(locale);
        context.setVariable("email", email);
        String link = downlowdLink;
        context.setVariable("downlowdLink", link);
        String content = templateEngine.process("PDFLink", context);
        String subject = messageSource.getMessage("email.paste.invoice.pdf.generate.ok", null, locale);
        log.debug("PDF download link Email Sent");
        return sendEmail(email, subject, content, true, true, att, fileName);
    }

    /**
     * @param subKey        主题国际化key
     * @param email         email地址
     * @param param         其他参数
     * @param emailTemplate 邮件模板
     * @param att           附件
     * @param fileName      文件名称
     * @param locale        地区
     */
    public boolean sendEmail(String subKey, String email, Map<String, Object> param, String emailTemplate, byte[] att, String fileName, Locale locale) {
        Context context = new Context(locale);
        for (Map.Entry<String, Object> entry : param.entrySet()) {
            context.setVariable(entry.getKey(), entry.getValue());
        }
        String content = templateEngine.process(emailTemplate, context);
        String subject = messageSource.getMessage(subKey, null, locale);
        if(subKey.equals("email.travel.booker.tickets.title")){
            subject = subject+"("+param.get("companyName")+"公司)";
        }
        if(subKey.equals("email.travel.application.title1")){
            subject = "您的"+param.get("applicationType")+param.get("businessCode")+"已通过审批。";
        }

        return sendEmail(email, subject, content, true, true, att, fileName);
    }

    /**
     * 发送邮件-含附件
     * @param email     邮箱地址
     * @param title     标题
     * @param content   正文
     * @param att       附件字节流
     * @param fileName  文件名称
     * @return
     */
    public boolean sendEmail(String email, String title, String content, byte[] att, String fileName) {
        return sendEmail(email, title, content, true, true, att, fileName);
    }


    /**
     * 邮件发送-不含附件
     * @param email         email地址
     * @param param         其他参数
     * @param emailTemplate 邮件模板
     * @param locale        地区
     */
    @Async
    public void sendEmail(String subject, String email, Map<String, Object> param, String emailTemplate, Locale locale) {
        Context context = new Context(locale);
        for (Map.Entry<String, Object> entry : param.entrySet()) {
            context.setVariable(entry.getKey(), entry.getValue());
        }
        String content = templateEngine.process(emailTemplate, context);
        sendEmail(email, subject, content, true, true, null, null);
    }

    public boolean sendAcountEmail(String email, String companyName, String userName,String login,String password, Locale locale) {
        log.debug("Sending Activation Email Link Email");
        Context context = new Context(locale);
        context.setVariable("companyName", companyName);
        context.setVariable("emailAccountTitle", messageSource.getMessage("email.account.main.title", new Object[]{userName,companyName}, locale));
        context.setVariable("emailAccountText2", messageSource.getMessage("email.account.text2", new Object[]{login,password}, locale));
        String content = templateEngine.process("accountUseEmail", context);
        String subject = messageSource.getMessage("email.activation.title", new Object[]{companyName}, locale);
        log.debug("Activation Email Link Email Sent");
        return sendEmail(email, subject, content, true, true, null, null);
    }

    public boolean sendInvitationEmail(String email, String userName, String companyName, Locale locale) {
        log.debug("Sending Activation Email Link Email");
        log.info("Send email to:{}",email);
        Context context = new Context(locale);
        context.setVariable("companyName", companyName);
        context.setVariable("emailAccountTitle", messageSource.getMessage("email.invitation.main.title", new Object[]{userName,companyName}, locale));
        String content = templateEngine.process("inviteUser", context);
        String subject = messageSource.getMessage("email.activation.title", new Object[]{companyName}, locale);
        log.debug("Activation Email Link Email Sent");
        return sendEmail(email, subject, content, true, true, null, null);
    }

    public boolean sendDeviceBindTokenEmail(String email, String token, String companyName, Locale locale,String loginTime) {
        log.debug("Sending Device Bind Email Link Email");
        Context context = new Context(locale);
        context.setVariable("deviceBindToken", token);
        context.setVariable("loginTime", loginTime);
        String content = templateEngine.process("deviceBindTokenEmail", context);
        String subject = messageSource.getMessage("email.device.bind.title", new Object[]{}, locale);
        log.debug("Device Bind Email Link Email Sent");
        return sendEmail(email, subject, content, true, true, null, null);
    }

    /**
     * 发送审批单拆分、推送失败邮件
     */
    public boolean sendTravelApprovalRequestFailEmail(String subject, String email, Map<String, Object> param, String emailTemplate ,Locale locale){
        log.info("Sending Travel ApprovalRequest Failed Email");
        Context context = new Context(locale);
        for (Map.Entry<String, Object> entry : param.entrySet()) {
            context.setVariable(entry.getKey(), entry.getValue());
        }
        String content = templateEngine.process(emailTemplate, context);
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper message = new MimeMessageHelper(mimeMessage, true, CharEncoding.UTF_8);
            if (StringUtils.isNotEmpty(failedTravelApprovalMock)) {
                message.setTo(failedTravelApprovalMock.split(";"));
            } else {
                message.setTo(email);
            }
            message.setFrom(from);
            message.setSubject(subject);
            message.setText(content, true);
            javaMailSender.send(mimeMessage);
            log.info("Email Sented");
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
