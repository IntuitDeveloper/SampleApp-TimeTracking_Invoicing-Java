package com.intuit.developer.sampleapp.timetracking.test.unit.handlers;

import com.intuit.developer.sampleapp.timetracking.domain.*;
import com.intuit.developer.sampleapp.timetracking.handlers.TimeActivityEventHandler;
import com.intuit.developer.sampleapp.timetracking.qbo.QBOGateway;
import com.intuit.developer.sampleapp.timetracking.repository.InvoiceRepository;
import mockit.*;
import mockit.integration.junit4.JMockit;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created with IntelliJ IDEA.
 * User: russellb337
 * Date: 9/4/14
 * Time: 8:16 PM
 */
@RunWith(JMockit.class)
public class TimeActivityEventHandlerTests {

    @Tested
    TimeActivityEventHandler timeActivityHandler;

    @Injectable
    QBOGateway qboGateway;

    @Injectable
    InvoiceRepository invoiceRepository;

    @Mocked
    Customer customer;

    @Mocked
    Company company;


    @Test
    public void testHandleAfterCreate() throws Exception {

        final TimeActivity timeActivity = new TimeActivity();

        timeActivityHandler.handleAfterCreate(timeActivity);

        new Verifications() {{
            qboGateway.createTimeActivityInQBO(timeActivity);
        }};
    }

    @Test
    public void testHandleBeforeCreate_NoPendingInvoice(final @Mocked Invoice invoice) throws Exception {
        final TimeActivity timeActivity = new TimeActivity();
        timeActivity.setCustomer(customer);
        timeActivity.setCompany(company);

        final Long expectedCustomerId = 123891723987L;

        //empty to represent no pending invoices
        final List<Invoice> pendingInvoices = new ArrayList<>();

        new NonStrictExpectations() {{

            customer.getId();
            result = expectedCustomerId;

            invoiceRepository.findByCustomer_IdAndStatus(expectedCustomerId, InvoiceStatus.Pending);
            result = pendingInvoices;

            new Invoice();
            result = invoice;
        }};

        timeActivityHandler.handleBeforeCreate(timeActivity);

        new Verifications() {{
            invoice.setCustomer(customer);
            invoice.setCompany(company);
            invoice.setStatus(InvoiceStatus.Pending);

            invoice.addTimeActivity(timeActivity);
            invoiceRepository.save(invoice);
        }};
    }

    @Test
    public void testHandleBeforeCreate_OnePendingInvoice() throws Exception {
        final TimeActivity timeActivity = new TimeActivity();
        timeActivity.setCustomer(customer);
        timeActivity.setCompany(company);

        final Long expectedCustomerId = 123891723987L;

        //empty to represent no pending invoices
        final List<Invoice> pendingInvoices = new ArrayList<>();
        final Invoice pendingInvoice = new Invoice();
        pendingInvoices.add(pendingInvoice);

        new NonStrictExpectations() {{

            customer.getId();
            result = expectedCustomerId;

            invoiceRepository.findByCustomer_IdAndStatus(expectedCustomerId, InvoiceStatus.Pending);
            result = pendingInvoices;

        }};

        timeActivityHandler.handleBeforeCreate(timeActivity);

        new Verifications() {{
            pendingInvoice.addTimeActivity(timeActivity);
            invoiceRepository.save(pendingInvoice);
        }};

    }

    @Test
    public void testHandleBeforeCreate_MoreThanOnePendingInvoice() throws Exception {
        final TimeActivity timeActivity = new TimeActivity();
        timeActivity.setCustomer(customer);
        timeActivity.setCompany(company);

        final Long expectedCustomerId = 123891723987L;

        //empty to represent no pending invoices
        final List<Invoice> pendingInvoices = new ArrayList<>();
        final Invoice pendingInvoice1 = new Invoice();
        final Invoice pendingInvoice2 = new Invoice();
        pendingInvoices.add(pendingInvoice1);
        pendingInvoices.add(pendingInvoice2);

        new NonStrictExpectations() {{

            customer.getId();
            result = expectedCustomerId;

            invoiceRepository.findByCustomer_IdAndStatus(expectedCustomerId, InvoiceStatus.Pending);
            result = pendingInvoices;

        }};

        boolean exceptionThrown = false;
        try {
            timeActivityHandler.handleBeforeCreate(timeActivity);
        } catch (RuntimeException e) {
            exceptionThrown = true;
            assertEquals("exception message", "More than one pending invoice for customer " + expectedCustomerId, e.getMessage());
        }

        assertTrue("expected exception not thrown", exceptionThrown);

    }
}
