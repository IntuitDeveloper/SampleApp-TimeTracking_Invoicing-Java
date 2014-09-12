package com.intuit.developer.sampleapp.timetracking.domain;

import org.joda.money.CurrencyUnit;
import org.joda.money.Money;
import org.joda.time.LocalDate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: russellb337
 * Date: 9/10/14
 * Time: 3:47 PM
 */
public class InvoiceSummary {

    private Invoice invoice;
    private Money totalAmount = Money.zero(CurrencyUnit.USD);
    private LocalDate minDate;
    private LocalDate maxDate;
    private List<ServiceItemAmountsSummary> serviceItemAmountsSummaries = new ArrayList<>();

    public InvoiceSummary(Invoice invoice) {
        this.invoice = invoice;
        createServiceItemSummaries();
    }

    private void createServiceItemSummaries() {
        Map<Long, ServiceItemAmountsSummary> serviceItemsAmountSummariesMap = new HashMap<>();

        for (TimeActivity timeActivity : invoice.getTimeActivities()) {
            final ServiceItem serviceItem = timeActivity.getServiceItem();

            ServiceItemAmountsSummary serviceItemAmountsSummary = serviceItemsAmountSummariesMap.get(serviceItem.getId());
            if (serviceItemAmountsSummary == null) {
                serviceItemAmountsSummary = new ServiceItemAmountsSummary(serviceItem);
                serviceItemsAmountSummariesMap.put(serviceItem.getId(), serviceItemAmountsSummary);
            }

            serviceItemAmountsSummary.addTimeActivity(timeActivity);
            recomputeMinAndMaxDates(timeActivity);

        }

        for (ServiceItemAmountsSummary serviceItemAmountsSummary : serviceItemsAmountSummariesMap.values()) {
            totalAmount = totalAmount.plus(serviceItemAmountsSummary.getTotalAmount());
            this.serviceItemAmountsSummaries.add(serviceItemAmountsSummary);
        }
    }

    private void recomputeMinAndMaxDates(TimeActivity timeActivity) {
        final LocalDate timeActivityDate = timeActivity.getDate();
        if (minDate == null) {
            minDate = timeActivityDate;
        } else if (timeActivityDate.isBefore(minDate)) {
            minDate = timeActivityDate;
        }

        if (maxDate == null) {
            maxDate = timeActivityDate;
        } else if (timeActivityDate.isAfter(maxDate)) {
            maxDate = timeActivityDate;
        }
    }

    public LocalDate getMinDate() {
        return minDate;
    }

    public LocalDate getMaxDate() {
        return maxDate;
    }

    public Money getTotalAmount() {
        return totalAmount;
    }

    public List<ServiceItemAmountsSummary> getServiceItemAmountsSummaries() {
        return serviceItemAmountsSummaries;
    }
}
