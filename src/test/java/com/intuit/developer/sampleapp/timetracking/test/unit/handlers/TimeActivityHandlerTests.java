package com.intuit.developer.sampleapp.timetracking.test.unit.handlers;

import com.intuit.developer.sampleapp.timetracking.domain.TimeActivity;
import com.intuit.developer.sampleapp.timetracking.handlers.TimeActivityHandler;
import com.intuit.developer.sampleapp.timetracking.qbo.QBOGateway;
import mockit.Injectable;
import mockit.Tested;
import mockit.Verifications;
import mockit.integration.junit4.JMockit;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Created with IntelliJ IDEA.
 * User: russellb337
 * Date: 9/4/14
 * Time: 8:16 PM
 */
@RunWith(JMockit.class)
public class TimeActivityHandlerTests {

    @Tested
    TimeActivityHandler timeActivityHandler;

    @Injectable
    QBOGateway qboGateway;

    @Test
    public void testTimeActivityHandler() throws Exception {

        final TimeActivity timeActivity = new TimeActivity();

        timeActivityHandler.handleAfterCreate(timeActivity);

        new Verifications() {{
            qboGateway.createTimeActivityInQBO(timeActivity);
        }};
    }
}
