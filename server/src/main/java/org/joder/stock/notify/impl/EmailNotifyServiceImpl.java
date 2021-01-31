package org.joder.stock.notify.impl;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import lombok.extern.slf4j.Slf4j;
import org.joder.stock.model.constant.StringConstant;
import org.joder.stock.notify.NotifyService;
import org.joder.stock.notify.domain.Message;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.util.List;

/**
 * @author Joder 2020/8/26 23:41
 */
@Service
@Primary
@Slf4j
public class EmailNotifyServiceImpl implements NotifyService {

    private JavaMailSender mailSender;
    private Configuration configuration;
    private List<String> sendTos;
    private String from;

    public EmailNotifyServiceImpl(JavaMailSender mailSender, Configuration configuration,
                                  @Value("${app.notify.mail}") List<String> sendTos, @Value("${spring.mail.username}") String from) {
        configuration.setClassForTemplateLoading(this.getClass(), "/email");
        this.mailSender = mailSender;
        this.configuration = configuration;
        this.sendTos = sendTos;
        this.from = from;
    }

    @Override
    public boolean notifyMessage(Message message) {
        try {
            String content = getMessage(message);
            for (String to : sendTos) {
                notifyMessage(to, content, StringConstant.EMAIL_MESSAGE);
            }
            log.info("发送邮件成功 {}", message);
            return true;
        } catch (Exception e) {
            log.error("发送邮件失败 {}", message);
            throw new RuntimeException(e);
        }
    }

    private void notifyMessage(String to, String text, String subject) throws MessagingException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
        helper.setFrom(from);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(text, true);
        mailSender.send(mimeMessage);
    }

    private String getMessage(Message message) throws IOException, TemplateException {
        Template template = configuration.getTemplate("template.ftl");
        return FreeMarkerTemplateUtils.processTemplateIntoString(template, message);
    }
}
