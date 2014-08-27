package com.intuit.developer.sampleapp.timetracking.qbo;

import com.intuit.developer.sampleapp.timetracking.domain.Customer;
import com.intuit.developer.sampleapp.timetracking.domain.Employee;
import com.intuit.developer.sampleapp.timetracking.domain.ServiceItem;
import com.intuit.developer.sampleapp.timetracking.mappers.CustomerMapper;
import com.intuit.developer.sampleapp.timetracking.mappers.EmployeeMapper;
import com.intuit.developer.sampleapp.timetracking.mappers.ServiceItemMapper;
import com.intuit.developer.sampleapp.timetracking.repository.CustomerRepository;
import com.intuit.developer.sampleapp.timetracking.repository.EmployeeRepository;
import com.intuit.developer.sampleapp.timetracking.repository.ServiceItemRepository;
import com.intuit.ipp.core.IEntity;
import com.intuit.ipp.data.Account;
import com.intuit.ipp.data.Item;
import com.intuit.ipp.data.ReferenceType;
import com.intuit.ipp.exception.FMSException;
import com.intuit.ipp.services.DataService;
import com.intuit.ipp.services.QueryResult;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

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

    public void createItemInQBO(ServiceItem serviceItem) {
        DataService dataService = dataServiceFactory.getDataService(serviceItem.getCompany());
        final com.intuit.ipp.data.Item qboObject = ServiceItemMapper.buildQBOObject(serviceItem);

        //item specific logic
        findAndAssociateIncomeAccount(dataService, qboObject);

        final com.intuit.ipp.data.Item returnedQBOObject = createObjectInQBO(dataService, qboObject);
        serviceItem.setQboId(returnedQBOObject.getId());
        serviceItemRepository.save(serviceItem);
    }

    private void findAndAssociateIncomeAccount(DataService dataService, Item qboObject) {
        try {
            final QueryResult queryResult = dataService.executeQuery(INCOME_ACCOUNT_QUERY);
            if (queryResult.getTotalCount() == 0) {
                throw new RuntimeException("Could not find a suitable income account when creating a service item");
            }

            final List<Account> entities = (List<Account>) queryResult.getEntities();
            final Account account = entities.get(0);

            ReferenceType referenceType = new ReferenceType();
            referenceType.setValue(account.getId());
            qboObject.setIncomeAccountRef(referenceType);

        } catch (FMSException e) {
            throw new RuntimeException("Failed to execute income account query '" + INCOME_ACCOUNT_QUERY + "'", e);
        }
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
