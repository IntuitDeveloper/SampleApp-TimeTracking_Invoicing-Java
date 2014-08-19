package timetracking;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import oauth.OAuthInfoProvider;
import org.apache.commons.io.FileUtils;
import org.joda.money.Money;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.core.event.ValidatingRepositoryEventListener;
import org.springframework.data.rest.webmvc.config.RepositoryRestMvcConfiguration;
import timetracking.controllers.OAuthInfoProviderImpl;
import timetracking.domain.AppInfo;
import timetracking.domain.Company;
import timetracking.handlers.RoleEventHandler;
import timetracking.repository.AppInfoRepository;
import timetracking.repository.CompanyRepository;
import timetracking.serializers.MoneyDeserializer;
import timetracking.serializers.MoneySerializer;
import timetracking.validation.RoleValidator;

import java.io.File;
import java.io.IOException;


/**
 * Created with IntelliJ IDEA.
 * User: russellb337
 * Date: 6/18/14
 * Time: 9:33 AM
 */


@Configuration
@EnableJpaRepositories
@Import(RepositoryRestMvcConfiguration.class)
@EnableAutoConfiguration
@ComponentScan
public class Application extends RepositoryRestMvcConfiguration {

    public static void main(String[] args) {

        try {
            FileUtils.deleteDirectory(new File("database"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        final ConfigurableApplicationContext context = SpringApplication.run(Application.class, args);

        initializeData(context);
    }

    /**
     * Loads oauth information from oauth.json, which is expected to be in the project root
     *
     * @param context
     */
    private static void initializeData(ConfigurableApplicationContext context) {

        if (oauthInfoNeeded(context)) {
            try {
                final File file = new File("oauth.json");
                final String jsonStr = FileUtils.readFileToString(file);
                ObjectMapper mapper = new ObjectMapper();
                final JsonNode jsonNode = mapper.readTree(jsonStr);

                createAppInfo(jsonNode, context);
                createCompany(jsonNode, context);

            } catch (IOException e) {
                System.err.println("Failed to read oauth information from oauth.json. Please make sure oauth.json is in the root of the project directory");
                e.printStackTrace();
            }
        }
    }

    private static void createCompany(JsonNode jsonNode, ConfigurableApplicationContext springContext) {
        final CompanyRepository repository = springContext.getBean(CompanyRepository.class);

        final JsonNode jsonCompanyInfo = jsonNode.get("companyInfo");

        Company company;
        if (jsonCompanyInfo != null) {
            company = new Company(jsonCompanyInfo.get("qboId").asText(),
                    jsonCompanyInfo.get("accessToken").asText(),
                    jsonCompanyInfo.get("accessTokenSecret").asText()
            );

        } else {
            company = new Company("Russell's Law Firm");
        }

//        final QBOCompanyServiceFactory qboCompanyServiceFactory = springContext.getBean(QBOCompanyServiceFactory.class);
//        final QboCompanyService companyService = qboCompanyServiceFactory.create(company);
//        companyService.syncFromQBO();
//        company.addRole(new Role("Lawyer", "A person who knows the law", Money.parse("USD 100.00")));
        repository.save(company);
    }

    private static boolean oauthInfoNeeded(ConfigurableApplicationContext context) {
        AppInfoRepository appInfoRepository = context.getBean(AppInfoRepository.class);
        return appInfoRepository.count() == 0;
    }

    private static AppInfo createAppInfo(JsonNode jsonNode, ConfigurableApplicationContext context) {
        AppInfoRepository repository = context.getBean(AppInfoRepository.class);

        final JsonNode jsonAppInfo = jsonNode.get("appInfo");

        AppInfo appInfo = new AppInfo(jsonAppInfo.get("appToken").asText(),
                jsonAppInfo.get("consumerKey").asText(),
                jsonAppInfo.get("consumerSecret").asText());

        repository.save(appInfo);

        return appInfo;

    }

    @Override
    protected void configureRepositoryRestConfiguration(RepositoryRestConfiguration config) {
        config.setReturnBodyOnCreate(true);
        config.exposeIdsFor(Company.class);
    }

    @Override
    protected void configureValidatingRepositoryEventListener(ValidatingRepositoryEventListener validatingListener) {
        //add validators here
        validatingListener.addValidator("beforeCreate", new RoleValidator());
    }


    //add REST event handler beans here
    @Bean
    RoleEventHandler roleEventHandler() {
        return new RoleEventHandler();
    }

    @Override
    protected void configureJacksonObjectMapper(ObjectMapper objectMapper) {
        final SimpleModule myCustomModule = new SimpleModule("MyCustomModule");

        myCustomModule.addSerializer(Money.class, new MoneySerializer());
        myCustomModule.addDeserializer(Money.class, new MoneyDeserializer());

        objectMapper.registerModule(myCustomModule);

    }

    @Bean
    OAuthInfoProvider oAuthInfoProvider() {
        return new OAuthInfoProviderImpl();
    }

}
