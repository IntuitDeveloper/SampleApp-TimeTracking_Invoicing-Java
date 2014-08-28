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
import com.intuit.ipp.data.AccountTypeEnum;
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
public class QBOGateway {


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

        // copy SalesItem to QBO Item
        Item qboItem = ServiceItemMapper.buildQBOObject(serviceItem);

        // find an Income Account to associate with QBO item
        ReferenceType accountRef = findAccountReference(dataService, AccountTypeEnum.INCOME, "ServiceFeeIncome");
        qboItem.setIncomeAccountRef(accountRef);

        // save the item in OBO
        qboItem = createObjectInQBO(dataService, qboItem);

        // update the SalesItem in app
        serviceItem.setQboId(qboItem.getId());
        serviceItemRepository.save(serviceItem);
    }

    /**
     * Get a ReferenceType wrapper for a QBO Account
     * <p/>
     * ReferenceType values are used to associate different QBO entities to each other.
     */
    private ReferenceType findAccountReference(DataService dataService, AccountTypeEnum accountType, String accountSubType) {
        Account account = findAccount(dataService, accountType, accountSubType);
        ReferenceType referenceType = new ReferenceType();
        referenceType.setValue(account.getId());
        return referenceType;
    }

    public static final String INCOME_ACCOUNT_QUERY = "select * from account where accounttype = '%s' and accountsubtype = '%s'";

    /**
     * Search for an account in QBO based on AccountType and AccountSubType
     */
    private Account findAccount(DataService dataService, AccountTypeEnum accountType, String accountSubType) {
        String accountQuery = String.format(INCOME_ACCOUNT_QUERY, accountType.value(), accountSubType);
        try {
            final QueryResult queryResult = dataService.executeQuery(accountQuery);
            final List<? extends IEntity> entities = queryResult.getEntities();
            if (entities.size() == 0) {
                throw new RuntimeException("Could not find an account of type " + accountType.value() + " and subtype " + accountSubType);
            }

            return (Account) entities.get(0);
        } catch (FMSException e) {
            throw new RuntimeException("Failed to execute income account query: " + accountQuery, e);
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
