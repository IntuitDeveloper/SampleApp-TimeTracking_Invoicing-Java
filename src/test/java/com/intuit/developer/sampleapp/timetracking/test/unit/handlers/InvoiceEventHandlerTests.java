package com.intuit.developer.sampleapp.timetracking.test.unit.handlers;

import com.intuit.developer.sampleapp.timetracking.domain.Invoice;
import com.intuit.developer.sampleapp.timetracking.domain.InvoiceStatus;
import com.intuit.developer.sampleapp.timetracking.handlers.InvoiceEventHandler;
import com.intuit.developer.sampleapp.timetracking.qbo.QBOGateway;
import mockit.Injectable;
import mockit.Tested;
import mockit.Verifications;
import org.junit.Test;

/**
 * Created with IntelliJ IDEA.
 * User: russellb337
 * Date: 9/11/14
 * Time: 2:36 PM
 */
public class InvoiceEventHandlerTests {
    @Injectable
    QBOGateway qboGateway;

    @Tested
    InvoiceEventHandler invoiceEventHandler;


    @Test
    public void testHandleAfterSave_invoiceReadyToBeBilled() throws Exception {

        final Invoice invoice = new Invoice();
        invoice.setStatus(InvoiceStatus.ReadyToBeBilled);

        invoiceEventHandler.handleAfterSave(invoice);

        new Verifications() {{
            qboGateway.createInvoiceInQBO(invoice);
            times = 1;
        }};
    }

    @Test
    public void testHandleAfterSave_invoicePending() throws Exception {

        final Invoice invoice = new Invoice();
        invoice.setStatus(InvoiceStatus.Pending);

        invoiceEventHandler.handleAfterSave(invoice);

        new Verifications() {{
            qboGateway.createInvoiceInQBO(invoice);
            times = 0;
        }};
    }
}
