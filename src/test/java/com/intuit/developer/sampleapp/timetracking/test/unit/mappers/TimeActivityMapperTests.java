package com.intuit.developer.sampleapp.timetracking.test.unit.mappers;

import com.intuit.developer.sampleapp.timetracking.domain.Customer;
import com.intuit.developer.sampleapp.timetracking.domain.Employee;
import com.intuit.developer.sampleapp.timetracking.domain.ServiceItem;
import com.intuit.developer.sampleapp.timetracking.domain.TimeActivity;
import com.intuit.developer.sampleapp.timetracking.mappers.TimeActivityMapper;
import org.joda.money.Money;
import org.joda.time.LocalDate;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;

/**
 * Created with IntelliJ IDEA.
 * User: russellb337
 * Date: 8/21/14
 * Time: 9:04 AM
 */
public class TimeActivityMapperTests {

    @Test
    public void testDomainToQBOMapping() throws Exception {

        final int minutes = 90;
        final LocalDate date = LocalDate.parse("2014-08-21");
        final String description = "A really good description";
        final String customerQboId = "2222";
        final String employeeQboId = "3333";
        final String serviceItemQboId = "4444";

        TimeActivity domainTimeActivity = new TimeActivity();
        domainTimeActivity.setMinutes(minutes);
        domainTimeActivity.setDate(date);
        domainTimeActivity.setDescription(description);

        Customer domainCustomer = new Customer();
        domainCustomer.setQboId(customerQboId);
        domainTimeActivity.setCustomer(domainCustomer);

        Employee domainEmployee = new Employee();
        domainEmployee.setQboId(employeeQboId);
        domainTimeActivity.setEmployee(domainEmployee);

        ServiceItem domainServiceItem = new ServiceItem();
        domainServiceItem.setQboId(serviceItemQboId);
        domainServiceItem.setRate(Money.parse("USD 10.00"));
        domainTimeActivity.setServiceItem(domainServiceItem);

        final com.intuit.ipp.data.TimeActivity timeActivity = TimeActivityMapper.buildQBOObject(domainTimeActivity);

        assertEquals("minutes", minutes % 60, timeActivity.getMinutes().intValue());
        assertEquals("hours", minutes / 60, timeActivity.getHours().intValue());
        assertEquals("date", date.toDate().getTime(), timeActivity.getTxnDate().getTime());
        assertEquals("description", description, timeActivity.getDescription());
        assertEquals("customer qbo id", customerQboId, timeActivity.getCustomerRef().getValue());
        assertEquals("employee qbo id", employeeQboId, timeActivity.getEmployeeRef().getValue());
        assertEquals("service item qbo id", serviceItemQboId, timeActivity.getItemRef().getValue());

    }
}
