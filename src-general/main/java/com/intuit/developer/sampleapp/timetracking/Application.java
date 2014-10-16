package com.intuit.developer.sampleapp.timetracking;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.intuit.developer.sampleapp.timetracking.domain.Company;
import com.intuit.developer.sampleapp.timetracking.domain.SystemProperty;
import com.intuit.developer.sampleapp.timetracking.handlers.InvoiceEventHandler;
import com.intuit.developer.sampleapp.timetracking.handlers.TimeActivityEventHandler;
import com.intuit.developer.sampleapp.timetracking.oauth.OAuthInfoProvider;
import com.intuit.developer.sampleapp.timetracking.oauth.controllers.OAuthInfoProviderImpl;
import com.intuit.developer.sampleapp.timetracking.qbo.DataServiceFactory;
import com.intuit.developer.sampleapp.timetracking.qbo.QBOGateway;
import com.intuit.developer.sampleapp.timetracking.serializers.LocalDateDeserializer;
import com.intuit.developer.sampleapp.timetracking.serializers.LocalDateSerializer;
import com.intuit.developer.sampleapp.timetracking.serializers.MoneyDeserializer;
import com.intuit.developer.sampleapp.timetracking.serializers.MoneySerializer;
import org.joda.money.Money;
import org.joda.time.LocalDate;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.webmvc.config.RepositoryRestMvcConfiguration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import java.util.List;


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

        System.out.println(
                " ______    _______  _______  ______   __   __ \n" +
                        "|    _ |  |       ||   _   ||      | |  | |  |\n" +
                        "|   | ||  |    ___||  |_|  ||  _    ||  |_|  |\n" +
                        "|   |_||_ |   |___ |       || | |   ||       |\n" +
                        "|    __  ||    ___||       || |_|   ||_     _|\n" +
                        "|   |  | ||   |___ |   _   ||       |  |   |  \n" +
                        "|___|  |_||_______||__| |__||______|   |___|  ");
    }

    @Override
    protected void configureRepositoryRestConfiguration(RepositoryRestConfiguration config) {
        config.setReturnBodyOnCreate(true);
        config.setReturnBodyOnUpdate(true);
        config.exposeIdsFor(Company.class);
        config.exposeIdsFor(SystemProperty.class);
    }


    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.add(converter());
    }

    @Bean
    MappingJackson2HttpMessageConverter converter() {
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        //do your customizations here...
        return converter;
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
