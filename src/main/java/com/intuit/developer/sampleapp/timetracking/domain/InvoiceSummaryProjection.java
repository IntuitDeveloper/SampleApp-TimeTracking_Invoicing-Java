package com.intuit.developer.sampleapp.timetracking.domain;

import org.springframework.data.rest.core.config.Projection;

/**
 * Created with IntelliJ IDEA.
 * User: russellb337
 * Date: 9/10/14
 * Time: 2:20 PM
 */
@Projection(name = "summary", types = Invoice.class)
public interface InvoiceSummaryProjection {
    Long getId();

    String getQboId();

    Customer getCustomer();

    InvoiceSummary getSummary();
}
