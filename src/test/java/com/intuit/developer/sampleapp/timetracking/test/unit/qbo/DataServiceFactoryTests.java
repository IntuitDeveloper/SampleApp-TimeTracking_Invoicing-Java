package com.intuit.developer.sampleapp.timetracking.test.unit.qbo;

import com.intuit.developer.sampleapp.timetracking.domain.AppInfo;
import com.intuit.developer.sampleapp.timetracking.domain.Company;
import com.intuit.developer.sampleapp.timetracking.qbo.DataServiceFactory;
import com.intuit.developer.sampleapp.timetracking.repository.AppInfoRepository;
import com.intuit.ipp.core.Context;
import com.intuit.ipp.core.ServiceType;
import com.intuit.ipp.security.OAuthAuthorizer;
import com.intuit.ipp.services.DataService;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Mocked;
import mockit.Tested;
import mockit.integration.junit4.JMockit;
import org.junit.Test;
import org.junit.runner.RunWith;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

/**
 * Created with IntelliJ IDEA.
 * User: russellb337
 * Date: 8/21/14
 * Time: 6:03 PM
 */
@RunWith(JMockit.class)
public class DataServiceFactoryTests {

    @Tested
    DataServiceFactory dataServiceFactory;

    @Injectable
    AppInfoRepository appInfoRepository;

    @Mocked
    DataService dataService;

    @Mocked
    OAuthAuthorizer oAuthAuthorizer;

    @Mocked
    Context context;

    @Test
    public void testGetDataService() throws Exception {
        final String consumerKey = "consumerKey";
        final String consumerSecret = "consumerSecret";
        final String appToken = "appToken";
        final AppInfo appInfo = new AppInfo(appToken, consumerKey, consumerSecret);
        final String accessToken = "accessToken";
        final String accessTokenSecret = "accessTokenSecret";
        final String realmId = "1234567";

        Company c = new Company();
        c.setName("The Federalists");
        c.setConnectedToQbo(true);
        c.setAccessToken(accessToken);
        c.setAccessTokenSecret(accessTokenSecret);
        c.setQboId(realmId);

        new Expectations() {{

            appInfoRepository.getFirst();
            result = appInfo;

            new OAuthAuthorizer(consumerKey, consumerSecret, accessToken, accessTokenSecret);
            result = oAuthAuthorizer;

            new Context(oAuthAuthorizer, ServiceType.QBO, realmId);
            result = context;

            new DataService(context);
        }};

        dataServiceFactory.getDataService(c);
    }

    @Test
    public void testGetDataServiceCompanyNotConnectedToQBO() throws Exception {
        boolean exceptionThrown = false;

        final String companyName = "The Federalists";
        try {

            Company c = new Company();
            c.setName(companyName);

            dataServiceFactory.getDataService(c);

        } catch (RuntimeException t) {
            exceptionThrown = true;
            assertEquals("Company is not connected to QBO: " + companyName, t.getMessage());
        }

        assertTrue("exception was not thrown", exceptionThrown);

    }
}
