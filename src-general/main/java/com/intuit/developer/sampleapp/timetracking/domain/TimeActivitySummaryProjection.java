package com.intuit.developer.sampleapp.timetracking.domain;

import org.joda.money.Money;
import org.joda.time.LocalDate;
import org.springframework.data.rest.core.config.Projection;

import java.math.BigDecimal;

/**
 * Created with IntelliJ IDEA.
 * User: russellb337
 * Date: 9/29/14
 * Time: 2:40 PM
 */
@Projection(name = "summary", types = TimeActivity.class)
public interface TimeActivitySummaryProjection {
    Customer getCustomer();

    Employee getEmployee();

    ServiceItem getServiceItem();

    LocalDate getDate();

    BigDecimal getHours();

    Money getAmount();

    String getQboId();
}
