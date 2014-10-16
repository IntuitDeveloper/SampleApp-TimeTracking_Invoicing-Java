package com.intuit.developer.sampleapp.timetracking.controllers;

import com.intuit.developer.sampleapp.timetracking.domain.Company;
import com.intuit.developer.sampleapp.timetracking.domain.Customer;
import com.intuit.developer.sampleapp.timetracking.domain.Employee;
import com.intuit.developer.sampleapp.timetracking.domain.ServiceItem;
import com.intuit.developer.sampleapp.timetracking.qbo.QBOGateway;
import com.intuit.developer.sampleapp.timetracking.repository.CompanyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * A REST controller that is triggered by the sample app UI and initiates syncing between the sample app and QBO
 * User: russellb337
 * Date: 8/22/14
 * Time: 4:47 PM
 */
@RestController
@RequestMapping(value = "/syncrequest", consumes = "application/json", produces = "application/json", headers = "Content-Type=application/json")
public class SyncRequestController {

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private QBOGateway qboGateway;

    @RequestMapping(method = RequestMethod.POST, consumes = "application/json", produces = "application/json", headers = "Content-Type=application/json")
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
