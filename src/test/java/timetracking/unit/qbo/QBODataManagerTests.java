package timetracking.unit.qbo;

import com.intuit.ipp.services.DataService;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Mocked;
import mockit.Tested;
import mockit.integration.junit4.JMockit;
import org.junit.Test;
import org.junit.runner.RunWith;
import timetracking.domain.Company;
import timetracking.domain.Customer;
import timetracking.domain.Employee;
import timetracking.domain.ServiceItem;
import timetracking.mappers.CustomerMapper;
import timetracking.mappers.EmployeeMapper;
import timetracking.mappers.ServiceItemMapper;
import timetracking.qbo.DataServiceFactory;
import timetracking.qbo.QBODataManager;
import timetracking.repository.CustomerRepository;
import timetracking.repository.EmployeeRepository;
import timetracking.repository.ServiceItemRepository;

import static junit.framework.Assert.assertEquals;

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
                                           @Injectable final ServiceItemRepository repository) throws Exception {

        final ServiceItem domainEntity = new ServiceItem();

        final Company c = new Company();
        c.setName("The Federalists");
        c.addServiceItem(domainEntity);

        final String expectedQBOId = "987654321";
        final com.intuit.ipp.data.Item returnedQboObject = new com.intuit.ipp.data.Item();
        returnedQboObject.setId(expectedQBOId);

        final com.intuit.ipp.data.Item mappedQboObject = new com.intuit.ipp.data.Item();

        new Expectations() {{
            dataServiceFactory.getDataService(c);
            result = dataService;

            ServiceItemMapper.buildQBOObject(domainEntity);
            result = mappedQboObject;

            dataService.add(mappedQboObject);
            result = returnedQboObject;

            repository.save(domainEntity);
        }};

        qboDataManager.createItemInQBO(domainEntity);

        assertEquals("qboId  was not updated", expectedQBOId, domainEntity.getQboId());
    }
}
