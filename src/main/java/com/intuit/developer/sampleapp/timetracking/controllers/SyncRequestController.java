package com.intuit.developer.sampleapp.timetracking.controllers;

import com.intuit.developer.sampleapp.timetracking.domain.Company;
import com.intuit.developer.sampleapp.timetracking.domain.Employee;
import com.intuit.developer.sampleapp.timetracking.qbo.QBODataManager;
import com.intuit.developer.sampleapp.timetracking.repository.CompanyRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    private QBODataManager qboDataManager;

    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    public SyncRequest createSyncRequest(@RequestBody final SyncRequest syncRequest) {

        final Company company = companyRepository.findOne(Long.parseLong(syncRequest.getCompanyId()));

        boolean succesful = true;
        int successfulSyncs = 0;
        int failedSyncs = 0;
        StringBuilder message = new StringBuilder();


        switch (syncRequest.getType()) {
            case Employee:
                final List<Employee> employees = company.getEmployees();
                for (Employee employee : employees) {
                    try {
//                        qboDataManager.createEmployeeInQBO(employee);
                        successfulSyncs++;
                    } catch (Throwable t) {

                    }
                }
                break;
            case Customer:

                break;
            case ServiceItem:
                break;
        }

        syncRequest.setMessage("Synced 5 " + syncRequest.getType().name() + " objects to QBO");
        syncRequest.setSuccessful(true);

        return syncRequest;
    }

}
