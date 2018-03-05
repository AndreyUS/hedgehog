package com.usanin.andrew.db;


import com.usanin.andrew.HedgehogConfiguration;
import io.dropwizard.hibernate.ScanningHibernateBundle;
import org.hibernate.cfg.Configuration;

public abstract class HedgehogHibernateBundle<T extends HedgehogConfiguration> extends ScanningHibernateBundle<T> {

    protected HedgehogHibernateBundle(String pckg) {
        super(pckg);
    }

    @Override
    protected void configure(Configuration configuration) {
        configuration.setProperty("hibernate.jdbc.time_zone", "UTC");
        configuration.setProperty("hibernate.jdbc.batch_size", "20");
    }
}
