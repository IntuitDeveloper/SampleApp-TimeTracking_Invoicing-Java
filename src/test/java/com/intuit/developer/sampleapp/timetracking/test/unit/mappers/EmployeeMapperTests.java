package com.intuit.developer.sampleapp.timetracking.test.unit.mappers;

import com.intuit.developer.sampleapp.timetracking.domain.Employee;
import com.intuit.developer.sampleapp.timetracking.mappers.EmployeeMapper;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

/**
 * Created with IntelliJ IDEA.
 * User: russellb337
 * Date: 8/21/14
 * Time: 9:04 AM
 */
public class EmployeeMapperTests {

    @Test
    public void testDomainToQBOMapping() throws Exception {

        final String firstName = "Robert";
        final String lastName = "Oppenheimer";
        final String emailAddress = "jroppenheimer@manhatten.com";
        final String phoneNumber = "916-123-4567";

        Employee domain = new Employee();
        domain.setFirstName(firstName);
        domain.setLastName(lastName);
        domain.setEmailAddress(emailAddress);
        domain.setPhoneNumber(phoneNumber);

        final com.intuit.ipp.data.Employee employee = EmployeeMapper.buildQBOObject(domain);

        assertEquals("firstName", firstName, employee.getGivenName());
        assertEquals("lastName", lastName, employee.getFamilyName());
        assertNotNull("emailAddress", employee.getPrimaryEmailAddr());
        assertEquals("emailAddress", emailAddress, employee.getPrimaryEmailAddr().getAddress());
        assertNotNull("phoneNumber", employee.getPrimaryPhone());
        assertEquals("phoneNumber", phoneNumber, employee.getPrimaryPhone().getFreeFormNumber());

    }
}
