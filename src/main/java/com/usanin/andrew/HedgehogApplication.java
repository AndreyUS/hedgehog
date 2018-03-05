package com.usanin.andrew;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.google.inject.Injector;
import com.usanin.andrew.db.HedgehogHibernateBundle;
import com.usanin.andrew.exception.mapper.CommonExceptionMapper;
import com.usanin.andrew.exception.mapper.HedgehogExceptionMapper;
import com.usanin.andrew.resource.BaseResource;
import io.dropwizard.Application;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.db.PooledDataSourceFactory;
import io.dropwizard.migrations.MigrationsBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.federecio.dropwizard.swagger.SwaggerBundle;
import io.federecio.dropwizard.swagger.SwaggerBundleConfiguration;
import ru.vyarus.dropwizard.guice.GuiceBundle;
import ru.vyarus.dropwizard.guice.injector.lookup.InjectorLookup;

public class HedgehogApplication extends Application<HedgehogConfiguration> {

    private static final String BASE_PACKAGE = "com.usanin.andrew";
    private static final String RESOURCES_PACKAGE = BASE_PACKAGE + ".resource";
    private static final String DB_PACKAGE = BASE_PACKAGE + ".db";

    private static Injector guiceInjector;
    private final HedgehogGuiceModule guiceModule;
    private HedgehogHibernateBundle hibernateBundle;

    HedgehogApplication(HedgehogGuiceModule guiceModule) {
        this.guiceModule = guiceModule;
        hibernateBundle = new HedgehogHibernateBundle<HedgehogConfiguration>(DB_PACKAGE) {
            @Override
            public PooledDataSourceFactory getDataSourceFactory(HedgehogConfiguration configuration) {
                return configuration.database;
            }
        };
        guiceModule.setHibernateBundle(hibernateBundle);
    }

    public static void main(final String[] args) throws Exception {
        new HedgehogApplication(new HedgehogGuiceModule()).run(args);
    }

    static Injector getGuiceInjector() {
        return guiceInjector;
    }

    @Override
    public String getName() {
        return "hedgehog";
    }

    @Override
    public void initialize(final Bootstrap<HedgehogConfiguration> bootstrap) {
        bootstrap.addBundle(new MigrationsBundle<HedgehogConfiguration>() {
            @Override
            public DataSourceFactory getDataSourceFactory(HedgehogConfiguration configuration) {
                return configuration.database;
            }
        });
        if (hibernateBundle != null) {
            bootstrap.addBundle(hibernateBundle);
        }
        bootstrap.addBundle(GuiceBundle.<HedgehogConfiguration>builder()
                .enableAutoConfig(BaseResource.class.getPackage().getName(), RESOURCES_PACKAGE)
                .useWebInstallers()
                .modules(guiceModule)
                .build());

        bootstrap.addBundle(new SwaggerBundle<HedgehogConfiguration>() {
            @Override
            protected SwaggerBundleConfiguration getSwaggerBundleConfiguration(HedgehogConfiguration configuration) {
                configuration.swagger.setResourcePackage(RESOURCES_PACKAGE);
                return configuration.swagger;
            }
        });
        bootstrap.getObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @Override
    public void run(final HedgehogConfiguration configuration,
                    final Environment environment) {
        System.setProperty("user.timezone", "UTC");
        guiceInjector = InjectorLookup.getInjector(this).get();

        environment.jersey().register(new HedgehogExceptionMapper());
        environment.jersey().register(new CommonExceptionMapper());
    }

}
