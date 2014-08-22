package timetracking.test.unit.mappers;

import org.joda.money.Money;
import org.junit.Test;
import timetracking.domain.ServiceItem;
import timetracking.mappers.ServiceItemMapper;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

/**
 * Created with IntelliJ IDEA.
 * User: russellb337
 * Date: 8/21/14
 * Time: 9:04 AM
 */
public class ServiceItemMapperTests {

    @Test
    public void testDomainToQBOMapping() throws Exception {

        final String name = "foo";
        final String description = "A really good description";
        final String rate = "100.25";

        ServiceItem domain = new ServiceItem();
        domain.setName(name);
        domain.setDescription(description);
        domain.setRate(Money.parse("USD " + rate));

        final com.intuit.ipp.data.Item item = ServiceItemMapper.buildQBOObject(domain);

        assertEquals("name", name, item.getName());
        assertEquals("description", description, item.getDescription());
        assertNotNull("rate", item.getUnitPrice());
        assertEquals("rate", rate, item.getUnitPrice().toString());

    }
}
