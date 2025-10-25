package org.example.tiktokproject.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import jakarta.annotation.Resource;
import jakarta.mail.Message;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.example.tiktokproject.AOP.MyLog;
import org.example.tiktokproject.pojo.DetectionResponse;
import org.example.tiktokproject.pojo.PhotoAndVideo;
import org.example.tiktokproject.pojo.RecognitionRequest;
import org.example.tiktokproject.pojo.Video;
import org.example.tiktokproject.repository.LoginAndRegisterRedis;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
@RabbitListener(queues = "SendOTP.direct.queue")
public class SendOTPConsumer {

    @Autowired
    private JavaMailSender mailSender;

    @Resource
    private LoginAndRegisterRedis loginAndRegisterRedis;

    @MyLog
    @RabbitHandler
    public void receiveMessage(String userEmail) throws IOException {
        StringBuffer stringBuffer = new StringBuffer();
        Random random = new Random();
        for(int i = 0;i<6;i++)
            stringBuffer.append(random.nextInt(10));
        MimeMessagePreparator preparation = getMimeMessagePreparator(userEmail, stringBuffer);
        mailSender.send(preparation);
        System.out.println("邮件发送成功至: " + userEmail);
        loginAndRegisterRedis.storeOTP(stringBuffer.toString() , userEmail);
    }

    private static MimeMessagePreparator getMimeMessagePreparator(String userEmail, StringBuffer stringBuffer) {
        String message = stringBuffer.toString();
        return new MimeMessagePreparator() {
            @Override
            public void prepare(MimeMessage mimeMessage) throws Exception {
                mimeMessage.setRecipient(Message.RecipientType.TO,
                        new InternetAddress(userEmail));
                mimeMessage.setFrom(new InternetAddress("3887768494@qq.com"));
                mimeMessage.setText("您的验证码是" + message + "(有效期为5分钟)");
                mimeMessage.setSubject("验证码通知");
            }
        };
    }
}
