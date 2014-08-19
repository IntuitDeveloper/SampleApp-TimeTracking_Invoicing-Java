package timetracking.unit;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

/**
 * Created with IntelliJ IDEA.
 * User: russellb337
 * Date: 6/18/14
 * Time: 2:08 PM
 */
public class SampleUnitTest {


    @Test
    public void testOne() throws Exception {
        assertEquals(1, 1);
        assertFalse("hello".equals("world"));

//        final ConfigurableApplicationContext context = SpringApplication.run(Application.class, new String[0]);
//        final CompanyRepository repository = context.getBean(CompanyRepository.class);
//
//        repository.save(new Company("123456", "https://api.qbo.intuit.com/companies/123456"));
//        repository.save(new Company("654321", "https://api.qbo.intuit.com/companies/654321"));
//
//        final Iterable<Company> all = repository.findAll();
//
//        int count = 0;
//        for (Company company : all) {
//            count++;
//        }
//
//        assertEquals(2, count);
//
//        context.close();
    }


}
