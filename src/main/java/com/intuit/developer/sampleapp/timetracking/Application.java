package com.intuit.developer.sampleapp.timetracking;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.intuit.developer.sampleapp.timetracking.controllers.OAuthInfoProviderImpl;
import com.intuit.developer.sampleapp.timetracking.domain.Company;
import com.intuit.developer.sampleapp.timetracking.handlers.InvoiceEventHandler;
import com.intuit.developer.sampleapp.timetracking.handlers.TimeActivityEventHandler;
import com.intuit.developer.sampleapp.timetracking.qbo.DataServiceFactory;
import com.intuit.developer.sampleapp.timetracking.qbo.QBOGateway;
import com.intuit.developer.sampleapp.timetracking.serializers.LocalDateDeserializer;
import com.intuit.developer.sampleapp.timetracking.serializers.LocalDateSerializer;
import com.intuit.developer.sampleapp.timetracking.serializers.MoneyDeserializer;
import com.intuit.developer.sampleapp.timetracking.serializers.MoneySerializer;
import oauth.OAuthInfoProvider;
import org.apache.catalina.connector.Connector;
import org.joda.money.Money;
import org.joda.time.LocalDate;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.embedded.ConfigurableEmbeddedServletContainer;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.boot.context.embedded.tomcat.TomcatConnectorCustomizer;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.core.event.ValidatingRepositoryEventListener;
import org.springframework.data.rest.webmvc.config.RepositoryRestMvcConfiguration;


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
        //start the spring app
        final ConfigurableApplicationContext context = SpringApplication.run(Application.class, args);
        //initialize some data
        DataLoader.initializeData(context);
    }

    @Override
    protected void configureRepositoryRestConfiguration(RepositoryRestConfiguration config) {
        config.setReturnBodyOnCreate(true);
        config.setReturnBodyOnUpdate(true);
        config.exposeIdsFor(Company.class);
    }

    @Override
    protected void configureValidatingRepositoryEventListener(ValidatingRepositoryEventListener validatingListener) {
        //add validators here
//        validatingListener.addValidator("beforeCreate", new SyncRequestValidator());
    }

    /**
     * A bean to configure the embedded tomcat.
     * <p/>
     * Right now we are only setting the port to 9001
     */
    @Bean
    public EmbeddedServletContainerCustomizer containerCustomizer() {
        return new EmbeddedServletContainerCustomizer() {
            @Override
            public void customize(ConfigurableEmbeddedServletContainer factory) {
                if (factory instanceof TomcatEmbeddedServletContainerFactory) {
                    customizeTomcat((TomcatEmbeddedServletContainerFactory) factory);
                }
            }

            private void customizeTomcat(TomcatEmbeddedServletContainerFactory factory) {
                factory.addConnectorCustomizers(new TomcatConnectorCustomizer() {

                    @Override
                    public void customize(Connector connector) {
                        connector.setPort(9001);
                    }
                });
            }
        };
    }



    //add REST event handler beans here
    @Bean
    TimeActivityEventHandler timeActivityHandler() {
        return new TimeActivityEventHandler();
    }

    @Bean
    InvoiceEventHandler invoiceEventHandler() {
        return new InvoiceEventHandler();
    }

    @Override
    protected void configureJacksonObjectMapper(ObjectMapper objectMapper) {
        final SimpleModule myCustomModule = new SimpleModule("MyCustomModule");

        myCustomModule.addSerializer(Money.class, new MoneySerializer());
        myCustomModule.addDeserializer(Money.class, new MoneyDeserializer());
        myCustomModule.addSerializer(LocalDate.class, new LocalDateSerializer());
        myCustomModule.addDeserializer(LocalDate.class, new LocalDateDeserializer());

        objectMapper.registerModule(myCustomModule);
    }

    @Bean
    OAuthInfoProvider oAuthInfoProvider() {
        return new OAuthInfoProviderImpl();
    }

    @Bean
    QBOGateway qboDataManager() {
        return new QBOGateway();
    }

    @Bean
    DataServiceFactory dataServiceFactory() {
        return new DataServiceFactory();
    }
}
