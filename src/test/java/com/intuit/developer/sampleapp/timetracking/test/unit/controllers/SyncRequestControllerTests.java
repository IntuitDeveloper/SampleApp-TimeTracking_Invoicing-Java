package com.intuit.developer.sampleapp.timetracking.test.unit.controllers;


import com.intuit.developer.sampleapp.timetracking.controllers.SyncRequest;
import com.intuit.developer.sampleapp.timetracking.controllers.SyncRequestController;
import com.intuit.developer.sampleapp.timetracking.domain.Company;
import com.intuit.developer.sampleapp.timetracking.domain.Customer;
import com.intuit.developer.sampleapp.timetracking.domain.Employee;
import com.intuit.developer.sampleapp.timetracking.domain.ServiceItem;
import com.intuit.developer.sampleapp.timetracking.qbo.QBOGateway;
import com.intuit.developer.sampleapp.timetracking.repository.CompanyRepository;
import mockit.Injectable;
import mockit.NonStrictExpectations;
import mockit.Tested;
import mockit.Verifications;
import mockit.integration.junit4.JMockit;
import org.joda.money.Money;
import org.junit.Test;
import org.junit.runner.RunWith;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

/**
 * Created by connorm659 on 8/27/14.
 */
@RunWith(JMockit.class)
public class SyncRequestControllerTests {
    @Tested
    SyncRequestController controller;

    @Injectable
    QBOGateway mockedQBOGateway;

    @Injectable
    CompanyRepository companyRepository;

    @Test
    public void testCustomerSync() {
        final Customer customer = new Customer("firstName", "lastName", "emailAddress", "phoneNumber");
        final Company company = new Company("accessToken", "accessTokenSecret", "1234567");
        company.addCustomer(customer);

        SyncRequest syncRequest = new SyncRequest();
        syncRequest.setCompanyId("1234");
        syncRequest.setType(SyncRequest.EntityType.Customer);

        new NonStrictExpectations() {{
            companyRepository.findOne(anyLong);
            result = company;
        }};

        SyncRequest syncRequestReturn = controller.createSyncRequest(syncRequest);
        assertTrue(syncRequestReturn.isSuccessful());
        assertTrue(company.isCustomersSynced());
        assertFalse(company.isServiceItemsSynced());
        assertFalse(company.isEmployeesSynced());

        new Verifications() {{
            mockedQBOGateway.createCustomerInQBO(withSameInstance(customer));
            times = 1;
            mockedQBOGateway.createItemInQBO(withInstanceOf(ServiceItem.class));
            times = 0;
            mockedQBOGateway.createEmployeeInQBO(withInstanceOf(Employee.class));
            times = 0;
            companyRepository.save(withSameInstance(company));
            times = 1;
        }};
    }

    @Test
    public void testServiceItemSync() {
        final ServiceItem serviceItem = new ServiceItem("name", "description", Money.parse("USD 1"));
        final Company company = new Company("accessToken", "accessTokenSecret", "1234567");
        company.addServiceItem(serviceItem);

        SyncRequest syncRequest = new SyncRequest();
        syncRequest.setCompanyId("1234");
        syncRequest.setType(SyncRequest.EntityType.ServiceItem);

        new NonStrictExpectations() {{
            companyRepository.findOne(anyLong);
            result = company;
        }};

        SyncRequest syncRequestReturn = controller.createSyncRequest(syncRequest);
        assertTrue(syncRequestReturn.isSuccessful());
        assertTrue(company.isServiceItemsSynced());
        assertFalse(company.isCustomersSynced());
        assertFalse(company.isEmployeesSynced());

        new Verifications() {{
            mockedQBOGateway.createItemInQBO(withSameInstance(serviceItem));
            times = 1;
            mockedQBOGateway.createCustomerInQBO(withInstanceOf(Customer.class));
            times = 0;
            mockedQBOGateway.createEmployeeInQBO(withInstanceOf(Employee.class));
            times = 0;
            companyRepository.save(withSameInstance(company));
            times = 1;
        }};
    }

    @Test
    public void testEmployeeSync() {
        final Employee employee = new Employee("name", "description", "email@something.com", "916-333-5555");
        final Company company = new Company("accessToken", "accessTokenSecret", "1234567");
        company.addEmployee(employee);

        SyncRequest syncRequest = new SyncRequest();
        syncRequest.setCompanyId("1234");
        syncRequest.setType(SyncRequest.EntityType.Employee);

        new NonStrictExpectations() {{
            companyRepository.findOne(anyLong);
            result = company;
        }};

        SyncRequest syncRequestReturn = controller.createSyncRequest(syncRequest);
        assertTrue(syncRequestReturn.isSuccessful());
        assertTrue(company.isEmployeesSynced());
        assertFalse(company.isCustomersSynced());
        assertFalse(company.isServiceItemsSynced());

        new Verifications() {{
            mockedQBOGateway.createEmployeeInQBO(withSameInstance(employee));
            times = 1;
            mockedQBOGateway.createCustomerInQBO(withInstanceOf(Customer.class));
            times = 0;
            mockedQBOGateway.createItemInQBO(withInstanceOf(ServiceItem.class));
            times = 0;
            companyRepository.save(withSameInstance(company));
            times = 1;
        }};
    }
}
