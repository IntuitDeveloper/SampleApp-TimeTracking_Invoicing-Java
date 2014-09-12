package com.intuit.developer.sampleapp.timetracking.test.integration;

import com.intuit.developer.sampleapp.timetracking.Application;
import com.intuit.developer.sampleapp.timetracking.domain.Invoice;
import com.intuit.developer.sampleapp.timetracking.handlers.InvoiceEventHandler;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.data.rest.core.annotation.HandleAfterSave;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import static junit.framework.Assert.assertEquals;
import static junit.framework.TestCase.assertNotNull;

/**
 * Created with IntelliJ IDEA.
 * User: russellb337
 * Date: 6/18/14
 * Time: 4:50 PM
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest
public class InvoiceEventHandlerIntegrationTests {

    @Autowired
    InvoiceEventHandler invoiceEventHandler;


    @Test
    public void assertAnnotatedEventHandlerMethodsExist() throws Exception {
        //assert that the time activity handler is accessible from the context when spring runs
        assertNotNull(invoiceEventHandler);

        //the class is annotated with the RepositoryEventHandler annotation
        final RepositoryEventHandler annotation = InvoiceEventHandler.class.getAnnotation(RepositoryEventHandler.class);
        assertNotNull(annotation);
        //and typed with Invoice
        assertEquals("generic type", Invoice.class, annotation.value()[0]);

        //has a handleAfterSave method annotated with the HandleAfterSave annotation
        final HandleAfterSave handleAfterSave = InvoiceEventHandler.class.getMethod("handleAfterSave", Invoice.class).getAnnotation(HandleAfterSave.class);
        assertNotNull(handleAfterSave);
    }

}
