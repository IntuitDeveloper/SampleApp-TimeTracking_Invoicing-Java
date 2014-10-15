package com.intuit.developer.sampleapp.timetracking;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.intuit.developer.sampleapp.timetracking.domain.*;
import com.intuit.developer.sampleapp.timetracking.repository.*;
import com.intuit.ipp.util.Config;
import org.apache.commons.io.FileUtils;
import org.joda.money.Money;
import org.springframework.context.ConfigurableApplicationContext;

import java.io.File;
import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: russellb337
 * Date: 8/20/14
 * Time: 3:32 PM
 */
public class DataLoader {
    /**
     * Loads oauth information from oauth.json, which is expected to be in the project root
     *
     * @param context
     */
    public static void initializeData(ConfigurableApplicationContext context) {
        if (oauthInfoNeeded(context)) {
            try {
                final File file = new File("oauth.json");
                final String jsonStr = FileUtils.readFileToString(file);
                ObjectMapper mapper = new ObjectMapper();
                final JsonNode jsonNode = mapper.readTree(jsonStr);

                createAppInfo(jsonNode, context);
                createCompany(context);
                createSystemProperties(context);

            } catch (IOException e) {
                throw new RuntimeException("Failed to read oauth information from oauth.json. Please make sure" +
                        " oauth.json is in the root of the project directory. This file should contain your appToken," +
                        " consumerKey, and consumerSecret which can be copied from the intuit developer portal. See the" +
                        " readme for more information.");
            }
        }
    }

    private static void createSystemProperties(ConfigurableApplicationContext springContext) {
        final SystemPropertyRepository repository = springContext.getBean(SystemPropertyRepository.class);

        //create the QBO UI URL system property
        String qboUiHostname;

        //grab the API URL from the SDK configuration
        final String qboApiURL = Config.getProperty(Config.BASE_URL_QBO);

        switch (qboApiURL) {
            case "https://quickbooks.api.intuit.com/v3/company":
                qboUiHostname = "qbo.intuit.com";
                break;
            case "https://sandbox-quickbooks.api.intuit.com/v3/company":
                qboUiHostname = "sandbox.qbo.intuit.com";
                break;
            default:
                qboUiHostname = "qa.qbo.intuit.com";
        }

        SystemProperty systemProperty = new SystemProperty("qboUiHostname", qboUiHostname);
        repository.save(systemProperty);

    }


    private static void createCompany(ConfigurableApplicationContext springContext) {
        final CompanyRepository repository = springContext.getBean(CompanyRepository.class);

        if (repository.count() == 0) {
            System.out.println("No company data in the app, creating data");

            Company company = new Company("Your Law Firm");
            repository.save(company);

            createEmployees(company, springContext);
            createCustomers(company, springContext);
            createServiceItems(company, springContext);
        }
    }

    private static void createServiceItems(Company company, ConfigurableApplicationContext springContext) {
        final ServiceItemRepository repository = springContext.getBean(ServiceItemRepository.class);

        final ServiceItem serviceItem1 = new ServiceItem("Research", "Reading large ponderous tomes", Money.parse("USD 50.00"));
        company.addServiceItem(serviceItem1);
        final ServiceItem serviceItem2 = new ServiceItem("Deposition", "Asking people serious questions", Money.parse("USD 100.00"));
        company.addServiceItem(serviceItem2);

        repository.save(serviceItem1);
        repository.save(serviceItem2);
    }

    private static void createCustomers(Company company, ConfigurableApplicationContext springContext) {
        final CustomerRepository repository = springContext.getBean(CustomerRepository.class);

        final Customer customer1 = new Customer("Alvin", "Lee", "alvin.lee@example.com", "650-555-7777");
        company.addCustomer(customer1);

        final Customer customer2 = new Customer("Brad", "Hall", "brad.hall@example.com", "650-555-9999");
        company.addCustomer(customer2);

        repository.save(customer1);
        repository.save(customer2);
    }

    private static void createEmployees(Company company, ConfigurableApplicationContext springContext) {
        final EmployeeRepository repository = springContext.getBean(EmployeeRepository.class);

        final Employee employee1 = new Employee("Bryan", "Ruff", "bryan.ruff@example.com", "650-555-4444");
        company.addEmployee(employee1);
        final Employee employee2 = new Employee("Paul", "Simmons", "paul.simmons@example.com", "650-555-5555");
        company.addEmployee(employee2);

        repository.save(employee1);
        repository.save(employee2);
    }

    private static boolean oauthInfoNeeded(ConfigurableApplicationContext context) {
        AppInfoRepository appInfoRepository = context.getBean(AppInfoRepository.class);
        return appInfoRepository.count() == 0;
    }


    private static AppInfo createAppInfo(JsonNode jsonNode, ConfigurableApplicationContext context) {
        AppInfoRepository repository = context.getBean(AppInfoRepository.class);

        final JsonNode jsonAppInfo = jsonNode.get("appInfo");

        String appToken;
        String consumerKey;
        String consumerSecret;

        try {
            appToken = jsonAppInfo.get("appToken").asText();
            consumerKey = jsonAppInfo.get("consumerKey").asText();
            consumerSecret = jsonAppInfo.get("consumerSecret").asText();
        } catch (NullPointerException e) {
            RuntimeException rte = new RuntimeException("Exception occurred loading oauth.json verify that file contains valid json and that field names are correct.");
            rte.setStackTrace(e.getStackTrace());
            throw rte;
        }

        if (appToken.isEmpty()) {
            throw new RuntimeException("In 'oauth.json': 'appToken' property is empty");
        }
        if (consumerKey.isEmpty()) {
            throw new RuntimeException("In 'oauth.json': 'consumerKey' property is empty");
        }
        if (consumerSecret.isEmpty()) {
            throw new RuntimeException("In 'oauth.json': 'consumerSecret' property is empty");
        }

        AppInfo appInfo = new AppInfo(appToken, consumerKey, consumerSecret);
        repository.save(appInfo);

        return appInfo;
    }

}
