package com.hsrm.umweltrechner.services;

import java.time.ZonedDateTime;
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

  private final static int ALERT_FREQUENCY = 15; // in minutes


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
      variable.setLastOverThreshold(ZonedDateTime.now());
      variableService.updateLastOverThreshold(variable.getName(), variable.getLastOverThreshold());

      List<String> emails = variable.getCustomerAlertList()
          .stream()
          .filter(x -> x.getLastNotified() == null || x.getLastNotified().isBefore(ZonedDateTime
              .now().minusMinutes(ALERT_FREQUENCY)))
          .filter(x -> StringUtils.hasText(x.getEmail()))
          .map(x -> {
            x.setLastNotified(ZonedDateTime.now());
            customerAlertsMapper.updateLastNotified(x.getId(), x.getLastNotified());
            return x.getEmail();
          })
          .toList();
      if (!emails.isEmpty()) {
        log.info("Sending email to " + emails);
        mailService.sendWarningMails(emails, variable.getName());
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
