package com.intuit.developer.sampleapp.timetracking.controllers;

import com.intuit.developer.sampleapp.timetracking.domain.Company;
import com.intuit.developer.sampleapp.timetracking.domain.Customer;
import com.intuit.developer.sampleapp.timetracking.domain.Employee;
import com.intuit.developer.sampleapp.timetracking.domain.ServiceItem;
import com.intuit.developer.sampleapp.timetracking.qbo.QBOGateway;
import com.intuit.developer.sampleapp.timetracking.repository.CompanyRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Created with IntelliJ IDEA.
 * User: russellb337
 * Date: 8/22/14
 * Time: 4:47 PM
 */
@RestController
@RequestMapping("/syncrequest")
public class SyncRequestController {

    private static final Logger LOGGER = LoggerFactory.getLogger(SyncRequestController.class);

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private QBOGateway qboGateway;

    @RequestMapping(method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    @ResponseBody
    public SyncRequest createSyncRequest(@RequestBody final SyncRequest syncRequest) {

        final Company company = companyRepository.findOne(Long.parseLong(syncRequest.getCompanyId()));

        int successfulSyncs = 0;

        switch (syncRequest.getType()) {
            case Employee:
                for (Employee employee : company.getEmployees()) {
                    qboGateway.createEmployeeInQBO(employee);
                    successfulSyncs++;
                }
                company.setEmployeesSynced(true);
                break;
            case Customer:
                for (Customer customer : company.getCustomers()) {
                    qboGateway.createCustomerInQBO(customer);
                    successfulSyncs++;
                }
                company.setCustomersSynced(true);
                break;
            case ServiceItem:
                for (ServiceItem serviceItem : company.getServiceItems()) {
                    qboGateway.createItemInQBO(serviceItem);
                    successfulSyncs++;
                }
                company.setServiceItemsSynced(true);
                break;
        }

        companyRepository.save(company);

        syncRequest.setMessage("Synced " + successfulSyncs + " " + syncRequest.getType().name() + " objects to QBO");
        syncRequest.setSuccessful(true);

        return syncRequest;
    }

}
