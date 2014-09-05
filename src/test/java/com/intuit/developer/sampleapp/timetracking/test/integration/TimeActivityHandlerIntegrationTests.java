package com.intuit.developer.sampleapp.timetracking.test.integration;

import com.intuit.developer.sampleapp.timetracking.Application;
import com.intuit.developer.sampleapp.timetracking.domain.TimeActivity;
import com.intuit.developer.sampleapp.timetracking.handlers.TimeActivityHandler;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.data.rest.core.annotation.HandleAfterCreate;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import static junit.framework.TestCase.assertNotNull;

/**
 * Created with IntelliJ IDEA.
 * User: russellb337
 * Date: 6/18/14
 * Time: 4:50 PM
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest
public class TimeActivityHandlerIntegrationTests {

    @Autowired
    TimeActivityHandler timeActivityHandler;


    @Test
    public void sampleTest() throws Exception {
        //assert that the time activity handler is accessible from the context when spring runs
        assertNotNull(timeActivityHandler);

        final RepositoryEventHandler annotation = TimeActivityHandler.class.getAnnotation(RepositoryEventHandler.class);
        assertNotNull(annotation);

        final HandleAfterCreate handleAfterCreate = TimeActivityHandler.class.getMethod("handleAfterCreate", TimeActivity.class).getAnnotation(HandleAfterCreate.class);
        assertNotNull(handleAfterCreate);
    }

}
