package com.hsrm.umweltrechner.dto;

import lombok.Data;

@Data
public class DtoDashboardPageComponent {

    private String dashboardId;

    private Integer position;

    private DtoDashboardComponent component;

}
