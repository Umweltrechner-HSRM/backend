package com.hsrm.umweltrechner.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mailgun.api.v3.MailgunMessagesApi;
import com.mailgun.model.message.Message;

@Service
public class MailService {

  @Autowired
  private MailgunMessagesApi mailgunMessagesApi;

  private final static String FROM = "mailgun@mail.sensorguard.systems";

  private final static String DOMAIN = "mail.sensorguard.systems";


  public void sendWarningMail(String to, String msg) {
    try {
      Message message = Message.builder()
          .from(FROM)
          .to(to)
          .subject("Sensor Warning")
          .text(msg)
          .build();
      mailgunMessagesApi.sendMessage(DOMAIN,
          message);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}