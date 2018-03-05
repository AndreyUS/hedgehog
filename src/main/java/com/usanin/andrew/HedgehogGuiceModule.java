package com.usanin.andrew;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.google.inject.Provides;
import com.google.inject.Scopes;
import com.google.inject.Singleton;
import com.usanin.andrew.client.LiquiHttpClient;
import com.usanin.andrew.client.LiquiHttpClientImpl;
import com.usanin.andrew.db.HedgehogHibernateBundle;
import com.usanin.andrew.db.OrderDao;
import com.usanin.andrew.db.model.OrderType;
import com.usanin.andrew.json.OrderTypeDeserialized;
import com.usanin.andrew.json.SecondsToDateTimeDeserializer;
import com.usanin.andrew.service.LiquiService;
import com.usanin.andrew.service.LiquiServiceImpl;
import io.dropwizard.client.JerseyClientBuilder;
import io.dropwizard.setup.Environment;
import org.hibernate.SessionFactory;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import ru.vyarus.dropwizard.guice.module.support.DropwizardAwareModule;

import javax.ws.rs.client.Client;

import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES;

public class HedgehogGuiceModule extends DropwizardAwareModule<HedgehogConfiguration> {

    private HedgehogHibernateBundle hibernateBundle;

    void setHibernateBundle(HedgehogHibernateBundle hibernateBundle) {
        this.hibernateBundle = hibernateBundle;
    }

    @Override
    protected void configure() {
        //Need for joda-dates
        DateTimeZone.setDefault(DateTimeZone.UTC);

        bind(SessionFactory.class).toProvider(() -> hibernateBundle.getSessionFactory());
        bind(DateTime.class).toProvider(DateTime::now);

        bind(LiquiService.class).to(LiquiServiceImpl.class).in(Scopes.SINGLETON);
        bind(OrderDao.class).in(Scopes.SINGLETON);
    }

    @Provides
    @Singleton
    public ObjectMapper provideObjectMapper() {
        final ObjectMapper mapper = new ObjectMapper();
        mapper.configure(FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        final SimpleModule module = new SimpleModule("CustomModule");
        module.addDeserializer(OrderType.class, new OrderTypeDeserialized());
        module.addDeserializer(DateTime.class, new SecondsToDateTimeDeserializer());
        mapper.registerModule(module);
        return mapper;
    }

    @Provides
    @Singleton
    public LiquiHttpClient provideLiquiHttpClient(ObjectMapper objectMapper, HedgehogConfiguration configuration,
                                                  Environment environment) {
        final Client client = new JerseyClientBuilder(environment)
                .using(configuration.liqui.httpClient)
                .build(LiquiHttpClientImpl.CLIENT_NAME);
        return new LiquiHttpClientImpl.Builder(configuration.liqui.baseUrl)
                .setHttpClient(client)
                .setObjectMapper(objectMapper)
                .build();
    }

}
