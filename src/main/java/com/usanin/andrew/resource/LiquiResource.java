package com.usanin.andrew.resource;


import com.codahale.metrics.annotation.Timed;
import com.google.inject.Inject;
import com.usanin.andrew.db.model.Order;
import com.usanin.andrew.service.LiquiService;
import com.usanin.andrew.util.HedgehogUtils;
import io.dropwizard.hibernate.UnitOfWork;
import io.swagger.annotations.*;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;
import java.util.Optional;

@Path("liqui")
@Api("/liqui")
public class LiquiResource extends BaseResource {

    private final LiquiService liquiService;

    @Inject
    public LiquiResource(LiquiService liquioService) {
        this.liquiService = liquioService;
    }

    @POST
    @Timed
    @Path("order/load/{currencyPair}")
    @ApiOperation(value = "Loads the last trades from http://liqui.io" +
            " and saves orders to a database if it were not saved before.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "The count of saved orders."),
            @ApiResponse(code = 400, message = "Invalid request data supplied, \"errorType\": \"VALIDATION\""),
            @ApiResponse(code = 422, message = "Can't read data from http://liqui.io"),
            @ApiResponse(code = 500, message = "Internal server error")})
    @UnitOfWork
    @Produces(MediaType.WILDCARD)
    public Integer loadOrders(@ApiParam(value = "originCurrency_dstCurrency", defaultValue = "btc_usdt") @PathParam("currencyPair") String currencyPair,
                           @ApiParam(value = "Max size of result. Max is 2000.", defaultValue = "150", allowableValues = "range[infinity, 2000]")
                           @QueryParam("limit") Integer limit) {
        return liquiService.loadLastTrades(currencyPair, Optional.ofNullable(limit));
    }

    @GET
    @Timed
    @Path("order/last-trades/{currencyPair}")
    @ApiOperation("Finds the last loaded trades for the specified period of time.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Orders in desc order by time of execution. Otherwise, zero records."),
            @ApiResponse(code = 400, message = "Invalid request data supplied, \"errorType\": \"VALIDATION\""),
            @ApiResponse(code = 500, message = "Internal server error")})
    @UnitOfWork
    public List<Order> findLastTrades(@ApiParam(value = "originCurrency_dstCurrency", defaultValue = "btc_usdt") @PathParam("currencyPair") String currencyPair,
                                      @ApiParam("Default now() - 24h, should be before or equal to 'toTs'. Format: 'dd.MM.yyyy HH:mm:ss'")
                                      @QueryParam("fromTs") String fromTs,
                                      @ApiParam("Default now(), should be after or equal to 'fromTs'. Format: 'dd.MM.yyyy HH:mm:ss'")
                                      @QueryParam("toTs") String toTs,
                                      @ApiParam(value = "Max size of result. Max is 2000.", defaultValue = "150", allowableValues = "range[infinity, 2000]")
                                      @QueryParam("limit") Integer limit,
                                      @ApiParam(value = "Offset for start, default 0", defaultValue = "0")
                                      @QueryParam("offset") Integer offset) {
        return liquiService.findLastTrades(currencyPair,
                Optional.ofNullable(fromTs).map(HedgehogUtils::parseDateTime),
                Optional.ofNullable(toTs).map(HedgehogUtils::parseDateTime),
                Optional.ofNullable(limit),
                Optional.ofNullable(offset));
    }
}
