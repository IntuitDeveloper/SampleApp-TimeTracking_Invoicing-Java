package com.intuit.developer.sampleapp.timetracking.handlers;

import com.intuit.developer.sampleapp.timetracking.domain.Invoice;
import com.intuit.developer.sampleapp.timetracking.domain.InvoiceStatus;
import com.intuit.developer.sampleapp.timetracking.domain.TimeActivity;
import com.intuit.developer.sampleapp.timetracking.qbo.QBOGateway;
import com.intuit.developer.sampleapp.timetracking.repository.InvoiceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.core.annotation.HandleAfterCreate;
import org.springframework.data.rest.core.annotation.HandleBeforeCreate;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: russellb337
 * Date: 6/20/14
 * Time: 9:55 AM
 * <p/>
 * See http://docs.spring.io/spring-data/rest/docs/2.1.0.RELEASE/reference/html/events-chapter.html#d5e443
 */
@RepositoryEventHandler(TimeActivity.class)
public class TimeActivityEventHandler {

    @Autowired
    private QBOGateway qboGateway;

    @Autowired
    private InvoiceRepository invoiceRepository;


    @HandleBeforeCreate
    public void handleBeforeCreate(TimeActivity timeActivity) {
        final List<Invoice> pendingInvoicesForCustomer = invoiceRepository.findByCustomer_IdAndStatus(timeActivity.getCustomer().getId(), InvoiceStatus.Pending);

        Invoice pendingInvoice;
        if (pendingInvoicesForCustomer.isEmpty()) {
            //there is no pending invoice for this customer, create one
            pendingInvoice = new Invoice();
            pendingInvoice.setCustomer(timeActivity.getCustomer());
            pendingInvoice.setCompany(timeActivity.getCompany());
            pendingInvoice.setStatus(InvoiceStatus.Pending);

        } else if (pendingInvoicesForCustomer.size() == 1) {
            pendingInvoice = pendingInvoicesForCustomer.get(0);

        } else {
            //there is more than one pending invoice, this shouldn't happen
            throw new RuntimeException("More than one pending invoice for customer " + timeActivity.getCustomer().getId());
        }

        pendingInvoice.addTimeActivity(timeActivity);
        invoiceRepository.save(pendingInvoice);
    }

    @HandleAfterCreate
    public void handleAfterCreate(TimeActivity p) {
        qboGateway.createTimeActivityInQBO(p);
    }


}