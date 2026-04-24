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

/**
 * Maps LinkedResourceNotFoundException to HTTP 422 Unprocessable Entity.
 * Triggered when a POST /sensors references a roomId that does not exist.
 *
 * 422 is semantically more accurate than 404 here because:
 * - 404 means "the URL/endpoint you requested was not found"
 * - 422 means "your request was syntactically valid JSON, but the
 *   semantic content (the roomId reference) cannot be processed"
 * The resource collection /sensors exists; the issue is a broken
 * reference inside the payload, not a missing endpoint.
 */
@Provider
public class LinkedResourceNotFoundExceptionMapper implements ExceptionMapper<LinkedResourceNotFoundException> {

    @Override
    public Response toResponse(LinkedResourceNotFoundException exception) {
        ErrorMessage error = new ErrorMessage(
                422,
                "ERR_LINKED_RESOURCE_NOT_FOUND",
                exception.getMessage()
        );
        return Response.status(422)
                .entity(error)
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}