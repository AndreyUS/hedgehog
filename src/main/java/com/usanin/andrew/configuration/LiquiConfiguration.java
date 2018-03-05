package com.usanin.andrew.configuration;


import io.dropwizard.client.JerseyClientConfiguration;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

public class LiquiConfiguration {

    @NotBlank
    public String baseUrl;

    @Valid
    @NotNull
    public JerseyClientConfiguration httpClient;
}
