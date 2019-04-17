

package com.hand.hcf.app.core.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.thymeleaf.spring5.templateresolver.SpringResourceTemplateResolver;

import java.nio.charset.StandardCharsets;
import java.util.Properties;

@Configuration
public class MailConfiguration implements EnvironmentAware {
    private static final String ENV_SPRING_MAIL = "mail.";
    private static final String PROP_HOST = "mail.host";
    private static final String PROP_PORT = "mail.port";
    private static final String PROP_USER = "mail.username";
    private static final String PROP_PASSWORD = "mail.password";
    private static final String PROP_PROTO = "mail.protocol";
    private static final String PROP_TLS = "mail.tls";
    private static final String PROP_AUTH = "mail.auth";
    private static final String PROP_SMTP_AUTH = "mail.smtp.auth";
    private static final String PROP_STARTTLS = "mail.smtp.starttls.enable";
    private static final String PROP_TRANSPORT_PROTO = "mail.transport.protocol";
    private static final String MAIL_CONFIG_ENABLED = "mail.enabled";
    private final Logger log = LoggerFactory.getLogger(MailConfiguration.class);

    private Environment env;
    @Override
    public void setEnvironment(Environment environment) {
        this.env=environment;
    }

    @Bean
    public JavaMailSenderImpl javaMailSender() {
        if (env.getProperty(MAIL_CONFIG_ENABLED, Boolean.class, false)) {
            log.debug("Configuring mail server");
            String host = env.getProperty(PROP_HOST);
            int port = env.getProperty(PROP_PORT, Integer.class, 0);
            String user = env.getProperty(PROP_USER);
            String password = env.getProperty(PROP_PASSWORD);
            String protocol = env.getProperty(PROP_PROTO);
            Boolean tls = env.getProperty(PROP_TLS, Boolean.class, false);
            Boolean auth = env.getProperty(PROP_AUTH, Boolean.class, false);

            JavaMailSenderImpl sender = new JavaMailSenderImpl();
            sender.setHost(host);
            sender.setPort(port);
            sender.setUsername(user);
            sender.setPassword(password);
            sender.setProtocol("smtp");
            Properties sendProperties = new Properties();
            sendProperties.setProperty(PROP_SMTP_AUTH, auth.toString());
            sendProperties.setProperty(PROP_STARTTLS, tls.toString());
            sendProperties.setProperty(PROP_TRANSPORT_PROTO, protocol);
            sender.setJavaMailProperties(sendProperties);
            log.debug("Configure mail server completed");
            return sender;
        } else {
            return null;
        }
    }

    @Bean
    public SpringResourceTemplateResolver emailTemplateResolver() {
        log.info("Loading Email TemplateResolver");
        SpringResourceTemplateResolver templateResolver = new SpringResourceTemplateResolver();
        templateResolver.setPrefix("templates/mail/");
        templateResolver.setSuffix(".html");
        templateResolver.setTemplateMode("HTML5");
        templateResolver.setCharacterEncoding(StandardCharsets.UTF_8.name());
        templateResolver.setOrder(1);
        return templateResolver;
    }
}
