package com.intuit.developer.sampleapp.timetracking.domain;

import org.joda.money.CurrencyUnit;
import org.joda.money.Money;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: russellb337
 * Date: 9/10/14
 * Time: 2:38 PM
 */
public class ServiceItemAmountsSummary {

    private String serviceItemName;

    private Money totalAmount = Money.zero(CurrencyUnit.USD);

    private Money rate;

    private List<TimeActivitySummary> timeActivities = new ArrayList<>();

    public ServiceItemAmountsSummary(ServiceItem serviceItem) {
        this.serviceItemName = serviceItem.getName();
        rate = serviceItem.getRate();
    }

    public String getServiceItemName() {
        return serviceItemName;
    }

    public Money getTotalAmount() {
        return totalAmount;
    }

    public Money getRate() {
        return rate;
    }

    public void addTimeActivity(TimeActivity timeActivity) {
        totalAmount = totalAmount.plus(timeActivity.getAmount());
        timeActivities.add(new TimeActivitySummary(timeActivity));
    }

    public List<TimeActivitySummary> getTimeActivities() {
        return timeActivities;
    }

}
