package com.intuit.developer.sampleapp.timetracking.handlers;

import com.intuit.developer.sampleapp.timetracking.domain.Invoice;
import com.intuit.developer.sampleapp.timetracking.domain.InvoiceStatus;
import com.intuit.developer.sampleapp.timetracking.qbo.QBOGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.core.annotation.HandleAfterSave;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;


/**
 * Created with IntelliJ IDEA.
 * User: russellb337
 * Date: 9/11/14
 * Time: 9:53 AM
 */
@RepositoryEventHandler(Invoice.class)
public class InvoiceEventHandler {

    @Autowired
    private QBOGateway qboGateway;

    @HandleAfterSave
    public void handleAfterSave(Invoice invoice) {
        if (invoice.getStatus() == InvoiceStatus.ReadyToBeBilled) {
            qboGateway.createInvoiceInQBO(invoice);
        }
    }
}
