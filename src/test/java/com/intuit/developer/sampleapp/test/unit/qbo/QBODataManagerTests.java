package com.intuit.developer.sampleapp.test.unit.qbo;

import com.intuit.developer.sampleapp.timetracking.domain.Company;
import com.intuit.developer.sampleapp.timetracking.domain.Customer;
import com.intuit.developer.sampleapp.timetracking.domain.Employee;
import com.intuit.developer.sampleapp.timetracking.domain.ServiceItem;
import com.intuit.developer.sampleapp.timetracking.mappers.CustomerMapper;
import com.intuit.developer.sampleapp.timetracking.mappers.EmployeeMapper;
import com.intuit.developer.sampleapp.timetracking.mappers.ServiceItemMapper;
import com.intuit.developer.sampleapp.timetracking.qbo.DataServiceFactory;
import com.intuit.developer.sampleapp.timetracking.qbo.QBODataManager;
import com.intuit.developer.sampleapp.timetracking.repository.CustomerRepository;
import com.intuit.developer.sampleapp.timetracking.repository.EmployeeRepository;
import com.intuit.developer.sampleapp.timetracking.repository.ServiceItemRepository;
import com.intuit.ipp.core.IEntity;
import com.intuit.ipp.data.Account;
import com.intuit.ipp.services.DataService;
import com.intuit.ipp.services.QueryResult;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Mocked;
import mockit.Tested;
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
 * Date: 8/21/14
 * Time: 10:02 AM
 */
@RunWith(JMockit.class)
public class QBODataManagerTests {

    @Tested
    QBODataManager qboDataManager;

    @Injectable
    DataServiceFactory dataServiceFactory;

    @Mocked
    DataService dataService;


    @Test
    public void testCreateEmployeeInQBO(@Mocked final EmployeeMapper mapper,
                                        @Injectable final EmployeeRepository repository) throws Exception {

        final Employee employee = new Employee();

        final Company c = new Company();
        c.setName("The Federalists");
        c.addEmployee(employee);

        final String expectedQBOId = "987654321";
        final com.intuit.ipp.data.Employee returnedQboObject = new com.intuit.ipp.data.Employee();
        returnedQboObject.setId(expectedQBOId);

        final com.intuit.ipp.data.Employee mappedQboObject = new com.intuit.ipp.data.Employee();

        new Expectations() {{

            dataServiceFactory.getDataService(c);
            result = dataService;

            EmployeeMapper.buildQBOObject(employee);
            result = mappedQboObject;

            dataService.add(mappedQboObject);
            result = returnedQboObject;

            repository.save(employee);
        }};

        qboDataManager.createEmployeeInQBO(employee);

        assertEquals("qboId  was not updated", expectedQBOId, employee.getQboId());
    }

    @Test
    public void testCreateCustomerInQBO(@Mocked final CustomerMapper mapper,
                                        @Injectable final CustomerRepository repository) throws Exception {

        final Customer domainEntity = new Customer();

        final Company c = new Company();
        c.setName("The Federalists");
        c.addCustomer(domainEntity);

        final String expectedQBOId = "987654321";
        final com.intuit.ipp.data.Customer returnedQboObject = new com.intuit.ipp.data.Customer();
        returnedQboObject.setId(expectedQBOId);

        final com.intuit.ipp.data.Customer mappedQboObject = new com.intuit.ipp.data.Customer();

        new Expectations() {{
            dataServiceFactory.getDataService(c);
            result = dataService;

            CustomerMapper.buildQBOObject(domainEntity);
            result = mappedQboObject;

            dataService.add(mappedQboObject);
            result = returnedQboObject;

            repository.save(domainEntity);
        }};

        qboDataManager.createCustomerInQBO(domainEntity);

        assertEquals("qboId  was not updated", expectedQBOId, domainEntity.getQboId());
    }

    @Test
    public void testCreateServiceItemInQBO(@Mocked final ServiceItemMapper mapper,
                                           @Injectable final ServiceItemRepository repository,
                                           @Mocked final QueryResult queryResult) throws Exception {

        final ServiceItem domainEntity = new ServiceItem();

        final Company c = new Company();
        c.setName("The Federalists");
        c.addServiceItem(domainEntity);

        final String expectedQBOId = "987654321";
        final com.intuit.ipp.data.Item returnedQboObject = new com.intuit.ipp.data.Item();
        returnedQboObject.setId(expectedQBOId);

        final com.intuit.ipp.data.Item mappedQboObject = new com.intuit.ipp.data.Item();

        final List<IEntity> list = new ArrayList<>();
        final Account account = new Account();
        list.add(account);
        final String expectedAccountId = "22222223";
        account.setId(expectedAccountId);

        new Expectations() {{
            dataServiceFactory.getDataService(c);
            result = dataService;

            ServiceItemMapper.buildQBOObject(domainEntity);
            result = mappedQboObject;

            dataService.executeQuery(QBODataManager.INCOME_ACCOUNT_QUERY);
            result = queryResult;

            queryResult.getTotalCount();
            result = 1;

            queryResult.getEntities();
            result = list;

            dataService.add(mappedQboObject);
            result = returnedQboObject;

            repository.save(domainEntity);
        }};

        qboDataManager.createItemInQBO(domainEntity);

        assertEquals("qboId  was not updated", expectedQBOId, domainEntity.getQboId());
        assertEquals("income account id was not set", expectedAccountId, mappedQboObject.getIncomeAccountRef().getValue());
    }

    @Test
    public void testCreateServiceItemInQBO_NoMatchingIncomeAccount(@Mocked final ServiceItemMapper mapper,
                                                                   @Injectable final ServiceItemRepository repository,
                                                                   @Mocked final QueryResult queryResult) throws Exception {

        final ServiceItem domainEntity = new ServiceItem();

        final Company c = new Company();
        c.setName("The Federalists");
        c.addServiceItem(domainEntity);

        final com.intuit.ipp.data.Item mappedQboObject = new com.intuit.ipp.data.Item();

        new Expectations() {{
            dataServiceFactory.getDataService(c);
            result = dataService;

            ServiceItemMapper.buildQBOObject(domainEntity);
            result = mappedQboObject;

            dataService.executeQuery(QBODataManager.INCOME_ACCOUNT_QUERY);
            result = queryResult;

            queryResult.getTotalCount();
            result = 0;

            dataService.add(mappedQboObject);
            times = 0;
            repository.save(domainEntity);
            times = 0;
        }};


        boolean exceptionThrown = false;
        try {
            qboDataManager.createItemInQBO(domainEntity);
        } catch (RuntimeException e) {
            exceptionThrown = true;
            assertEquals("Could not find a suitable income account when creating a service item", e.getMessage());
        }

        assertTrue("exception not thrown", exceptionThrown);
    }
}
