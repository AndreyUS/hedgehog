package com.usanin.andrew;

import com.usanin.andrew.configuration.LiquiConfiguration;
import io.dropwizard.Configuration;
import io.dropwizard.db.DataSourceFactory;
import io.federecio.dropwizard.swagger.SwaggerBundleConfiguration;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

public class HedgehogConfiguration extends Configuration {

    @Valid
    public DataSourceFactory database;

    @NotNull
    @Valid
    public SwaggerBundleConfiguration swagger = new SwaggerBundleConfiguration();

    @NotNull
    @Valid
    public LiquiConfiguration liqui;

}
