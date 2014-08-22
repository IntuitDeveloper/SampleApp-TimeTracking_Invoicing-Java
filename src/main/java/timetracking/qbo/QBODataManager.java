package timetracking.qbo;

import com.intuit.ipp.core.IEntity;
import com.intuit.ipp.exception.FMSException;
import com.intuit.ipp.services.DataService;
import org.springframework.beans.factory.annotation.Autowired;
import timetracking.domain.Customer;
import timetracking.domain.Employee;
import timetracking.domain.ServiceItem;
import timetracking.mappers.CustomerMapper;
import timetracking.mappers.EmployeeMapper;
import timetracking.mappers.ServiceItemMapper;
import timetracking.repository.CustomerRepository;
import timetracking.repository.EmployeeRepository;
import timetracking.repository.ServiceItemRepository;

/**
 * Created with IntelliJ IDEA.
 * User: russellb337
 * Date: 8/21/14
 * Time: 9:33 AM
 */
public class QBODataManager {

    public static final String INCOME_ACCOUNT_QUERY = "select * from account where accounttype = 'Income' and accountsubtype = 'ServiceFeeIncome'";
    @Autowired
    private DataServiceFactory dataServiceFactory;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ServiceItemRepository serviceItemRepository;

    public void createEmployeeInQBO(Employee employee) {

        DataService dataService = dataServiceFactory.getDataService(employee.getCompany());
        final com.intuit.ipp.data.Employee qboObject = EmployeeMapper.buildQBOObject(employee);
        final com.intuit.ipp.data.Employee returnedQBOObject = createObjectInQBO(dataService, qboObject);

        employee.setQboId(returnedQBOObject.getId());
        employeeRepository.save(employee);
    }

    public void createCustomerInQBO(Customer customer) {
        DataService dataService = dataServiceFactory.getDataService(customer.getCompany());
        final com.intuit.ipp.data.Customer qboObject = CustomerMapper.buildQBOObject(customer);
        final com.intuit.ipp.data.Customer returnedQBOObject = createObjectInQBO(dataService, qboObject);

        customer.setQboId(returnedQBOObject.getId());
        customerRepository.save(customer);
    }

    public void createItemInQBO(ServiceItem buildQBOObject) {
        DataService dataService = dataServiceFactory.getDataService(buildQBOObject.getCompany());
        final com.intuit.ipp.data.Item qboObject = ServiceItemMapper.buildQBOObject(buildQBOObject);

        //item specific logic
        try {
            dataService.executeQuery(INCOME_ACCOUNT_QUERY);
        } catch (FMSException e) {
            throw new RuntimeException("Failed to execute query '" + INCOME_ACCOUNT_QUERY + "'", e);
        }


        final com.intuit.ipp.data.Item returnedQBOObject = createObjectInQBO(dataService, qboObject);

        buildQBOObject.setQboId(returnedQBOObject.getId());
        serviceItemRepository.save(buildQBOObject);
    }

    private <T extends IEntity> T createObjectInQBO(DataService dataService, T qboObject) {
        try {
            final T createdObject = dataService.add(qboObject);
            return createdObject;
        } catch (FMSException e) {
            throw new RuntimeException("Failed create an " + qboObject.getClass().getName() + " in QBO", e);
        }

    }


}
