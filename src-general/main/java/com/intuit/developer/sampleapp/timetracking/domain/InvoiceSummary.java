package com.intuit.developer.sampleapp.timetracking.domain;

import org.joda.money.CurrencyUnit;
import org.joda.money.Money;
import org.joda.time.LocalDate;

import java.util.ArrayList;
import java.util.List;

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
    private InvoiceStatus status;
    private List<TimeActivitySummary> timeActivities = new ArrayList<>();

    public InvoiceSummary(Invoice invoice) {
        this.invoice = invoice;
        this.status = invoice.getStatus();
        createTimeActivitySummaries();
    }

    private void createTimeActivitySummaries() {

        for (TimeActivity timeActivity : invoice.getTimeActivities()) {
            timeActivities.add(new TimeActivitySummary(timeActivity));
            totalAmount = totalAmount.plus(timeActivity.getAmount());
            recomputeMinAndMaxDates(timeActivity);

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

    public InvoiceStatus getStatus() {
        return status;
    }

    public List<TimeActivitySummary> getTimeActivities() {
        return timeActivities;
    }
}
