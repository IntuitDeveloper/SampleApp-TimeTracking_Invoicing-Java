package com.intuit.developer.sampleapp.timetracking.qbo;

import com.intuit.developer.sampleapp.timetracking.domain.Customer;
import com.intuit.developer.sampleapp.timetracking.domain.Employee;
import com.intuit.developer.sampleapp.timetracking.domain.Invoice;
import com.intuit.developer.sampleapp.timetracking.domain.*;
import com.intuit.developer.sampleapp.timetracking.domain.TimeActivity;
import com.intuit.developer.sampleapp.timetracking.mappers.*;
import com.intuit.developer.sampleapp.timetracking.repository.*;
import com.intuit.ipp.core.IEntity;
import com.intuit.ipp.data.*;
import com.intuit.ipp.exception.FMSException;
import com.intuit.ipp.services.DataService;
import com.intuit.ipp.services.QueryResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.Class;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: russellb337
 * Date: 8/21/14
 * Time: 9:33 AM
 */
public class QBOGateway {

    private static final Logger LOGGER = LoggerFactory.getLogger(QBOGateway.class);

    @Autowired
    private DataServiceFactory dataServiceFactory;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ServiceItemRepository serviceItemRepository;

    @Autowired
    private TimeActivityRepository timeActivityRepository;

    @Autowired
    private InvoiceRepository invoiceRepository;

    public void createEmployeeInQBO(Employee employee) {

        DataService dataService = dataServiceFactory.getDataService(employee.getCompany());

        /* In order to prevent syncing the same data into QBO more than once, query to see if the entity already exists.
           This solution is only meant to provide a better sample app experience (e.g. if you wipe out your database,
           we don't want the sample app to keep creating the same data over and over in QBO).

           In a production app keeping data in two systems in sync is a difficult problem to solve; this code is not
           meant to demonstrate production quality sync functionality.

         */
        com.intuit.ipp.data.Employee returnedQBOObject = findExistingEmployee(dataService, employee);
        if (returnedQBOObject == null) {
            final com.intuit.ipp.data.Employee qboObject = EmployeeMapper.buildQBOObject(employee);
            returnedQBOObject = createObjectInQBO(dataService, qboObject);
        }

        employee.setQboId(returnedQBOObject.getId());
        employeeRepository.save(employee);
    }


    public void createCustomerInQBO(Customer customer) {
        DataService dataService = dataServiceFactory.getDataService(customer.getCompany());


        /* In order to prevent syncing the same data into QBO more than once, query to see if the entity already exists.
           This solution is only meant to provide a better sample app experience (e.g. if you wipe out your database,
           we don't want the sample app to keep creating the same data over and over in QBO).

           In a production app keeping data in two systems in sync is a difficult problem to solve; this code is not
           meant to demonstrate production quality sync functionality.

         */

        com.intuit.ipp.data.Customer returnedQBOObject = findExistingCustomer(dataService, customer);
        if (returnedQBOObject == null) {
            final com.intuit.ipp.data.Customer qboObject = CustomerMapper.buildQBOObject(customer);
            returnedQBOObject = createObjectInQBO(dataService, qboObject);
        }

        customer.setQboId(returnedQBOObject.getId());
        customerRepository.save(customer);
    }


    public void createItemInQBO(ServiceItem serviceItem) {
        DataService dataService = dataServiceFactory.getDataService(serviceItem.getCompany());


        /* In order to prevent syncing the same data into QBO more than once, query to see if the entity already exists.
           This solution is only meant to provide a better sample app experience (e.g. if you wipe out your database,
           we don't want the sample app to keep creating the same data over and over in QBO).

           In a production app keeping data in two systems in sync is a difficult problem to solve; this code is not
           meant to demonstrate production quality sync functionality.

         */

        com.intuit.ipp.data.Item returnedQBOObject = findExistingServiceItem(dataService, serviceItem);
        if (returnedQBOObject == null) {
            // copy SalesItem to QBO Item
            Item qboItem = ServiceItemMapper.buildQBOObject(serviceItem);

            // find an Income Account to associate with QBO item
            ReferenceType accountRef = findAccountReference(dataService, AccountTypeEnum.INCOME, "ServiceFeeIncome");
            qboItem.setIncomeAccountRef(accountRef);

            // save the item in OBO
            returnedQBOObject = createObjectInQBO(dataService, qboItem);
        }

        // update the SalesItem in app
        serviceItem.setQboId(returnedQBOObject.getId());
        serviceItemRepository.save(serviceItem);
    }

    public void createTimeActivityInQBO(TimeActivity timeActivity) {
        DataService dataService = dataServiceFactory.getDataService(timeActivity.getCompany());
        final com.intuit.ipp.data.TimeActivity qboObject = TimeActivityMapper.buildQBOObject(timeActivity);

        /* I'm setting to Not Billable because I don't want this time time activity to show up on the QBO Create Invoice
           screen because this time activity will be billed through invoices created through THIS app
         */
        qboObject.setBillableStatus(BillableStatusEnum.NOT_BILLABLE);
        qboObject.setNameOf(TimeActivityTypeEnum.EMPLOYEE);

        final com.intuit.ipp.data.TimeActivity returnedQboObject = createObjectInQBO(dataService, qboObject);

        timeActivity.setQboId(returnedQboObject.getId());
        timeActivityRepository.save(timeActivity);
    }

    public void createInvoiceInQBO(Invoice invoice) {
        DataService dataService = dataServiceFactory.getDataService(invoice.getCompany());
        final com.intuit.ipp.data.Invoice qboObject = InvoiceMapper.buildQBOObject(invoice);

        //find a net 30 term, should exist by default
        ReferenceType termsRef = findTermsReference(dataService, "STANDARD", 30);
        qboObject.setSalesTermRef(termsRef);

        final com.intuit.ipp.data.Invoice returnedQBOObject = createObjectInQBO(dataService, qboObject);

        invoice.setQboId(returnedQBOObject.getId());
        invoice.setStatus(InvoiceStatus.Billed);
        invoiceRepository.save(invoice);
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

    /**
     * Get a ReferenceType wrapper for a QBO Term
     * <p/>
     * ReferenceType values are used to associate different QBO entities to each other.
     */
    private ReferenceType findTermsReference(DataService dataService, String type, int dueDays) {
        final Term term = findTerm(dataService, type, dueDays);
        ReferenceType referenceType = new ReferenceType();
        referenceType.setValue(term.getId());
        return referenceType;
    }

    private Term findTerm(DataService dataService, String type, int dueDays) {

        /*
         * Terms have no fields that are marked 'filterable'. So pull them all down and find the right one
         */

        try {
            final List<Term> terms = dataService.findAll(new Term());

            for (Term term : terms) {
                if (term.getDueDays() == dueDays && term.getType().equals(type)) {
                    return term;
                }
            }

        } catch (FMSException e) {
            throw new RuntimeException("Failed to load terms from QBO", e);
        }

        throw new RuntimeException("Could not find a term of type " + type + " and DueDays = " + dueDays);
    }

    private <T extends IEntity> T createObjectInQBO(DataService dataService, T qboObject) {
        try {
            final T createdObject = dataService.add(qboObject);
            return createdObject;
        } catch (FMSException e) {
            throw new RuntimeException("Failed create an " + qboObject.getClass().getName() + " in QBO", e);
        }

    }


    public static final String EXISTING_EMPLOYEE_QUERY = "select * from employee where active = true and givenName = '%s' and familyName = '%s'";
    public static final String EXISTING_CUSTOMER_QUERY = "select * from customer where active = true and givenName = '%s' and familyName = '%s'";
    public static final String EXISTING_SERVICE_ITEM_QUERY = "select * from item where active = true and name = '%s'";

    private com.intuit.ipp.data.Employee findExistingEmployee(DataService dataService, Employee employee) {
        String query = String.format(EXISTING_EMPLOYEE_QUERY, employee.getFirstName(), employee.getLastName());
        return executeQuery(dataService, query, com.intuit.ipp.data.Employee.class);
    }

    private com.intuit.ipp.data.Customer findExistingCustomer(DataService dataService, Customer customer) {
        String query = String.format(EXISTING_CUSTOMER_QUERY, customer.getFirstName(), customer.getLastName());
        return executeQuery(dataService, query, com.intuit.ipp.data.Customer.class);
    }

    private com.intuit.ipp.data.Item findExistingServiceItem(DataService dataService, ServiceItem serviceItem) {
        String query = String.format(EXISTING_SERVICE_ITEM_QUERY, serviceItem.getName());
        return executeQuery(dataService, query, com.intuit.ipp.data.Item.class);
    }

    private <T extends IEntity> T executeQuery(DataService dataService, String query, Class<T> qboType) {
        try {
            final QueryResult queryResult = dataService.executeQuery(query);
            final List<? extends IEntity> entities = queryResult.getEntities();
            if (entities.size() == 0) {
                return null;
            } else {
                final IEntity entity = entities.get(0);
                return (T) entity;
            }

        } catch (FMSException e) {
            throw new RuntimeException("Failed to execute an entity query: " + query, e);
        }
    }
}
