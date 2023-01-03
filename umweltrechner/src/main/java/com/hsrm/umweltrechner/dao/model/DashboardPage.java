package com.hsrm.umweltrechner.dao.model;

import lombok.Data;

@Data
public class DashboardPage {

    private String dashboardId;

    private String dashboardComponentId;

    private Integer position;


    public static DashboardPage from(String dashboardId, String dashboardComponentId, Integer position) {
        DashboardPage dashboardPage = new DashboardPage();
        dashboardPage.setDashboardId(dashboardId);
        dashboardPage.setDashboardComponentId(dashboardComponentId);
        dashboardPage.setPosition(position);
        return dashboardPage;
    }


}
