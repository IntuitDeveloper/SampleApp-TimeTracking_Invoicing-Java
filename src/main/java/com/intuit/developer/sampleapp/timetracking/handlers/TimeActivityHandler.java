package com.intuit.developer.sampleapp.timetracking.handlers;

import com.intuit.developer.sampleapp.timetracking.domain.TimeActivity;
import com.intuit.developer.sampleapp.timetracking.qbo.QBOGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.core.annotation.HandleAfterCreate;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;

/**
 * Created with IntelliJ IDEA.
 * User: russellb337
 * Date: 6/20/14
 * Time: 9:55 AM
 * <p/>
 * See http://docs.spring.io/spring-data/rest/docs/2.1.0.RELEASE/reference/html/events-chapter.html#d5e443
 */
@RepositoryEventHandler(TimeActivity.class)
public class TimeActivityHandler {

    @Autowired
    private QBOGateway qboGateway;


    @HandleAfterCreate
    public void handleAfterCreate(TimeActivity p) {
        qboGateway.createTimeActivityInQBO(p);
    }


}