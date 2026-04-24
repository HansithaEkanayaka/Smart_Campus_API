/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.smart_campus_api.filter;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.util.logging.Logger;

/**
 * API observability filter that logs every inbound request and outbound response.
 *
 * Implements BOTH ContainerRequestFilter (inbound) and ContainerResponseFilter (outbound)
 * in a single class, registered globally via @Provider.
 *
 * Why filters over manual Logger.info() calls in each resource method:
 *   - DRY Principle: A single class handles logging for ALL endpoints. Adding a new
 *     resource automatically inherits logging without any extra code.
 *   - Separation of Concerns: Resource methods stay focused on business logic only.
 *   - Consistency: Logging format is guaranteed to be identical across the entire API.
 *   - Maintainability: Changing the log format requires editing one file, not dozens.
 *   - Cross-cutting concerns (logging, auth, CORS) belong at the framework boundary,
 *     not scattered across business classes — this is the foundational pipeline for
 *     enterprise cloud observability (ELK stack, AWS CloudWatch integration).
 */
@Provider
public class ApiLoggingFilter implements ContainerRequestFilter, ContainerResponseFilter {

    private static final Logger LOGGER = Logger.getLogger(ApiLoggingFilter.class.getName());

    /**
     * Inbound filter: executed BEFORE the request is matched to a resource method.
     * Logs the HTTP method and request URI for every incoming request.
     */
    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        LOGGER.info(String.format("[REQUEST]  Method: %-7s | URI: %s",
                requestContext.getMethod(),
                requestContext.getUriInfo().getRequestUri().toString()));
    }

    /**
     * Outbound filter: executed AFTER the resource method has completed.
     * Logs the final HTTP status code for every outgoing response.
     */
    @Override
    public void filter(ContainerRequestContext requestContext,
                       ContainerResponseContext responseContext) throws IOException {
        LOGGER.info(String.format("[RESPONSE] Method: %-7s | URI: %s | Status: %d",
                requestContext.getMethod(),
                requestContext.getUriInfo().getRequestUri().toString(),
                responseContext.getStatus()));
    }
}
