package timetracking.unit.mappers;

import org.junit.Test;
import timetracking.domain.Customer;
import timetracking.mappers.CustomerMapper;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

/**
 * Created with IntelliJ IDEA.
 * User: russellb337
 * Date: 8/21/14
 * Time: 9:04 AM
 */
public class CustomerMapperTests {

    @Test
    public void testDomainToQBOMapping() throws Exception {

        final String firstName = "Robert";
        final String lastName = "Oppenheimer";
        final String emailAddress = "jroppenheimer@manhatten.com";
        final String phoneNumber = "916-123-4567";

        Customer domain = new Customer();
        domain.setFirstName(firstName);
        domain.setLastName(lastName);
        domain.setEmailAddress(emailAddress);
        domain.setPhoneNumber(phoneNumber);

        final com.intuit.ipp.data.Customer customer = CustomerMapper.buildQBOObject(domain);

        assertEquals("firstName", firstName, customer.getGivenName());
        assertEquals("lastName", lastName, customer.getFamilyName());
        assertNotNull("emailAddress", customer.getPrimaryEmailAddr());
        assertEquals("emailAddress", emailAddress, customer.getPrimaryEmailAddr().getAddress());
        assertNotNull("phoneNumber", customer.getPrimaryPhone());
        assertEquals("phoneNumber", phoneNumber, customer.getPrimaryPhone().getFreeFormNumber());

    }
}
