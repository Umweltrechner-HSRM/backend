package com.hsrm.umweltrechner.services;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.hsrm.umweltrechner.dao.mapper.CustomerAlertsMapper;
import com.hsrm.umweltrechner.dto.DtoVariable;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class CustomerAlertsService {

  @Autowired
  private CustomerAlertsMapper customerAlertsMapper;


  @Autowired
  private MailService mailService;

  @Autowired
  private VariableService variableService;


  @Autowired
  private MetaSettingsService metaSettingsService;


  private final static String ALERT_MESSAGE = """
      WARNING:
      The data point %s is over the threshold!
      Measured value %s was reached at %s.
      Allowed range: %s - %s.
      Please check the sensor immediately.
      """;

  @Transactional
  public void processThresholds(HashMap<String, Double> interpreter) {
    List<DtoVariable> variableList =
        variableService.selectAllWithCustomerAlerts();

    for (var variable : variableList) {
      boolean isDanger = isOverThreshold(interpreter.get(variable.getName()),
          variable.getMinThreshold(), variable.getMaxThreshold());


      if (!isDanger) {
        continue;
      }

      List<String> emails = new ArrayList<>();

      if (variable.getLastOverThreshold() == null || variable.getLastOverThreshold().isBefore(ZonedDateTime.now().minusMinutes(metaSettingsService.getMailFrequency()))) {
        String defaultMail = metaSettingsService.getDefaultMail();
        if (StringUtils.hasText(defaultMail)) {
          emails.add(defaultMail);
        }
      }

      variable.setLastOverThreshold(ZonedDateTime.now());
      variableService.updateLastOverThreshold(variable.getName(), variable.getLastOverThreshold());
      variable.getCustomerAlertList()
          .stream()
          .filter(x -> x.getLastNotified() == null || x.getLastNotified().isBefore(ZonedDateTime
              .now().minusMinutes(metaSettingsService.getMailFrequency())))
          .filter(x -> StringUtils.hasText(x.getEmail()))
          .map(x -> {
            x.setLastNotified(ZonedDateTime.now());
            customerAlertsMapper.updateLastNotified(x.getId(), x.getLastNotified());
            return x.getEmail();
          })
          .forEach(emails::add);


      if (!emails.isEmpty()) {
        log.info("Sending email to " + emails);
        String message = String.format(ALERT_MESSAGE, variable.getName(),
            interpreter.get(variable.getName()),
            variable.getLastOverThreshold(), variable.getMinThreshold(),
            variable.getMaxThreshold());
        mailService.sendWarningMails(emails, message);
      }
    }

  }

  private static boolean isOverThreshold(Double value, Double min, Double max) {
    boolean isDanger = min != null && value <= min;
    if (max != null && value >= max) {
      isDanger = true;
    }
    return isDanger;
  }

}
