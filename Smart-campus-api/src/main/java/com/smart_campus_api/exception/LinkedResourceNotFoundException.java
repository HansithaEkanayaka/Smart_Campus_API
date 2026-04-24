/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.smart_campus_api.exception;

/**
 * Thrown when a POST request references a resource (e.g. roomId) that does not exist.
 * Maps to HTTP 422 Unprocessable Entity.
 */
public class LinkedResourceNotFoundException extends RuntimeException {
    private final String fieldName;
    private final String fieldValue;

    public LinkedResourceNotFoundException(String fieldName, String fieldValue) {
        super("The referenced resource '" + fieldName + "' with value '" + fieldValue + "' does not exist.");
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
    }

    public String getFieldName() { return fieldName; }
    public String getFieldValue() { return fieldValue; }
}
