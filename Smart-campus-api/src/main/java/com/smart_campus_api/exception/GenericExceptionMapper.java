/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.smart_campus_api.exception;

import com.smart_campus_api.model.ErrorMessage;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Global catch-all ExceptionMapper for any unhandled Throwable.
 *
 * Security rationale: Exposing raw Java stack traces to API consumers is a
 * critical security risk. A stack trace reveals:
 *   1. Internal class names and package structure (aids targeted attacks)
 *   2. Framework and library versions (enables known-CVE exploitation)
 *   3. Database schema hints and query structure (SQL injection vectors)
 *   4. File system paths on the server (directory traversal opportunities)
 *   5. Business logic flow, helping attackers understand how to craft malicious inputs
 *
 * This mapper ensures ALL unexpected errors are sanitized before reaching the client,
 * while the full stack trace is logged server-side for diagnostics.
 *
 * JAX-RS uses a "closest match" algorithm when resolving mappers: it first
 * looks for an exact exception class match, then traverses up the inheritance
 * tree. Mapping Throwable ensures this is always the final safety net.
 */
@Provider
public class GenericExceptionMapper implements ExceptionMapper<Throwable> {

    private static final Logger LOGGER = Logger.getLogger(GenericExceptionMapper.class.getName());

    @Override
    public Response toResponse(Throwable exception) {
        // Log the FULL stack trace internally — never send it to the client
        LOGGER.log(Level.SEVERE, "Unhandled exception caught by global safety net", exception);

        // Return a sanitized, generic message to the client
        ErrorMessage error = new ErrorMessage(
                500,
                "ERR_INTERNAL_SERVER_ERROR",
                "An unexpected internal server error occurred. Please contact support."
        );
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(error)
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}
