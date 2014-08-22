package timetracking.test.integration;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.TestRestTemplate;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.client.RestTemplate;
import timetracking.Application;

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
public class SampleIntegrationTests {

//    @Autowired
//    CompanyRepository repository;

    RestTemplate restTemplate = new TestRestTemplate();

    @Before
    public void setUp() throws Exception {
//        repository.deleteAll();

    }

    @Test
    public void sampleTest() throws Exception {
        System.out.println("Sample Test");

    }

    //    @Test
//    public void testCompanyCreate() throws Exception {
//
//        final String qboCompanyId = "123456";
//        final String qboUrl = "http://some.url.com/companies/" + qboCompanyId;
//        assertTrue(repository.findByQboId(qboCompanyId).isEmpty());
//
//        Company company = new Company(qboCompanyId, qboUrl);
//        final ResponseEntity<String> postResponse = restTemplate.postForEntity("http://localhost:8080/companies", company, String.class);
//
//        assertEquals(HttpStatus.CREATED, postResponse.getStatusCode());
//
//        final List<Company> byQboId = repository.findByQboId(qboCompanyId);
//        assertEquals(1, byQboId.size());
//
//        final Company companyFromRepo = byQboId.get(0);
//        assertEquals(qboCompanyId, companyFromRepo.getQboId());
//        assertEquals(qboUrl, companyFromRepo.getQboUrl());
//    }

//    @Test
//    public void testCompanyGet() throws Exception {
//
//        final String qboCompanyId = "123456";
//        String accessToken = "bar";
//        String accessTokenSecret = "foo";
//
//        Company company = new Company(qboCompanyId, accessToken, accessTokenSecret);
//        repository.save(company);
//
//        final ResponseEntity<String> searchResult = restTemplate.getForEntity("http://localhost:8080/companies/" + company.getId(), String.class);
//
//        final String json = searchResult.getBody();
//
//        ObjectMapper mapper = new ObjectMapper();
//        JsonNode rootNode = mapper.readValue(json, JsonNode.class);
//
//        assertEquals(qboCompanyId, rootNode.get("qboId").textValue());
//
//    }
//
//    @Test
//    public void testEmptyDatabase() throws Exception {
//        assertEquals(0, repository.count());
//
//    }
}
