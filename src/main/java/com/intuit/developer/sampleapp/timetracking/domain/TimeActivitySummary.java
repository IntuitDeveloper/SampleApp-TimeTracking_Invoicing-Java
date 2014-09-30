package com.intuit.developer.sampleapp.timetracking.domain;

import org.joda.money.Money;
import org.joda.time.LocalDate;

import java.math.BigDecimal;

/**
 * Created with IntelliJ IDEA.
 * User: russellb337
 * Date: 9/10/14
 * Time: 5:39 PM
 */
public class TimeActivitySummary {

    private final String employeeName;
    private final LocalDate date;
    private final BigDecimal hours;
    private final Money amount;
    private final String serviceItemName;
    private final Money serviceItemRate;


    public TimeActivitySummary(TimeActivity timeActivity) {
        employeeName = timeActivity.getEmployee().getFirstName() + " " + timeActivity.getEmployee().getLastName();
        date = timeActivity.getDate();
        hours = timeActivity.getHours();
        amount = timeActivity.getAmount();
        serviceItemName = timeActivity.getServiceItem().getName();
        serviceItemRate = timeActivity.getServiceItem().getRate();
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public LocalDate getDate() {
        return date;
    }

    public BigDecimal getHours() {
        return hours;
    }

    public Money getAmount() {
        return amount;
    }

    public String getServiceItemName() {
        return serviceItemName;
    }

    public Money getServiceItemRate() {
        return serviceItemRate;
    }
}
