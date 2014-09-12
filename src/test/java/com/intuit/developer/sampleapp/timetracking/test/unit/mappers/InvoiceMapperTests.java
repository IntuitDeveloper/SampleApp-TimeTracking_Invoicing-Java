package com.intuit.developer.sampleapp.timetracking.test.unit.mappers;

import com.intuit.developer.sampleapp.timetracking.domain.*;
import com.intuit.developer.sampleapp.timetracking.mappers.InvoiceMapper;
import com.intuit.ipp.data.Line;
import com.intuit.ipp.data.LineDetailTypeEnum;
import com.intuit.ipp.data.SalesItemLineDetail;
import org.joda.money.Money;
import org.joda.time.LocalDate;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.TestCase.assertNotNull;

/**
 * Created with IntelliJ IDEA.
 * User: russellb337
 * Date: 9/11/14
 * Time: 12:18 PM
 */
public class InvoiceMapperTests {

    @Test
    public void testDomainToQBOMapping() throws Exception {

        final String customerQboId = "2222";
        final String serviceItem1Name = "Research";
        final String serviceItem1Rate = "100.00";
        final String serviceItem2Name = "Deposition";
        final String serviceItem2Rate = "50.00";
        int timeActivity1Minutes = 90;
        final String timeActivity1Date = "2014-09-11";
        int timeActivity2Minutes = 30;
        final String timeActivity2Date = "2014-09-12";

        Invoice domainInvoice = new Invoice();

        Customer domainCustomer = new Customer();
        domainCustomer.setQboId(customerQboId);
        domainInvoice.setCustomer(domainCustomer);

        ServiceItem serviceItem1 = new ServiceItem();
        serviceItem1.setName(serviceItem1Name);
        serviceItem1.setQboId("1234");
        serviceItem1.setRate(Money.parse("USD " + serviceItem1Rate));

        ServiceItem serviceItem2 = new ServiceItem();
        serviceItem2.setName(serviceItem2Name);
        serviceItem2.setRate(Money.parse("USD " + serviceItem2Rate));
        serviceItem2.setQboId("4321");

        Employee domainEmployee1 = new Employee();
        domainEmployee1.setFirstName("George");
        domainEmployee1.setLastName("Washington");

        Employee domainEmployee2 = new Employee();
        domainEmployee2.setFirstName("John");
        domainEmployee2.setLastName("Adams");

        TimeActivity timeActivity1 = new TimeActivity();
        timeActivity1.setCustomer(domainCustomer);
        timeActivity1.setServiceItem(serviceItem1);
        timeActivity1.setEmployee(domainEmployee1);
        timeActivity1.setMinutes(timeActivity1Minutes);
        timeActivity1.setDate(LocalDate.parse(timeActivity1Date));

        TimeActivity timeActivity2 = new TimeActivity();
        timeActivity2.setCustomer(domainCustomer);
        timeActivity2.setServiceItem(serviceItem2);
        timeActivity2.setEmployee(domainEmployee2);
        timeActivity2.setMinutes(timeActivity2Minutes);
        timeActivity2.setDate(LocalDate.parse(timeActivity2Date));

        //add them in a different order than expect them on the invoice
        domainInvoice.addTimeActivity(timeActivity2);
        domainInvoice.addTimeActivity(timeActivity1);

        final com.intuit.ipp.data.Invoice invoice = InvoiceMapper.buildQBOObject(domainInvoice);

        assertEquals("customer qbo id", customerQboId, invoice.getCustomerRef().getValue());

        final List<Line> invoiceLines = invoice.getLine();
        assertEquals("number of invoice lines", 2, invoiceLines.size());

        for (int i = 0; i < invoiceLines.size(); i++) {
            final Line invoiceLine = invoiceLines.get(i);
            assertEquals("line num", i + 1, invoiceLine.getLineNum().intValue());

            if (i == 0) {
                assertInvoiceLine(invoiceLine,
                        BigDecimal.valueOf(150.00).setScale(2),
                        domainEmployee1,
                        timeActivity1Date,
                        BigDecimal.valueOf(1.5), serviceItem1);
            } else {
                assertInvoiceLine(invoiceLine,
                        BigDecimal.valueOf(25.00).setScale(2),
                        domainEmployee2,
                        timeActivity2Date,
                        BigDecimal.valueOf(0.5),
                        serviceItem2);

            }
        }
    }

    private void assertInvoiceLine(Line invoiceLine, BigDecimal amount, Employee employee, String date, BigDecimal quantity, ServiceItem serviceItem) {
        assertEquals("amount", amount, invoiceLine.getAmount());
        assertEquals("description", employee.getFirstName() + " " + employee.getLastName() + " on " + date, invoiceLine.getDescription());
        assertEquals("detail type", LineDetailTypeEnum.SALES_ITEM_LINE_DETAIL, invoiceLine.getDetailType());

        final SalesItemLineDetail salesItemLineDetail = invoiceLine.getSalesItemLineDetail();
        assertEquals("unit price", serviceItem.getRate().getAmount(), salesItemLineDetail.getUnitPrice());
        assertEquals("quantity", quantity, salesItemLineDetail.getQty());

        assertNotNull(salesItemLineDetail.getItemRef().getValue());
        assertEquals("service item qbo id", serviceItem.getQboId(), salesItemLineDetail.getItemRef().getValue());
    }
}
