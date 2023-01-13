package com.hsrm.umweltrechner.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.mailgun.api.v3.MailgunMessagesApi;
import com.mailgun.client.MailgunClient;


@Configuration
public class MailGunConfig {

  @Bean
  public MailgunMessagesApi mailgunMessagesApi(@Value("${mailgun.api.key}") String apiKey) {
    return MailgunClient.config(apiKey)
        .createApi(MailgunMessagesApi.class);
  }
}

