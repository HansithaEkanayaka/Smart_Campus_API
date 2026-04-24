/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.smart_campus_api.exception;

/**
 * Thrown when a POST reading is attempted on a sensor that is in MAINTENANCE status.
 * Maps to HTTP 403 Forbidden.
 */
public class SensorUnavailableException extends RuntimeException {
    private final String sensorId;
    private final String status;

    public SensorUnavailableException(String sensorId, String status) {
        super("Sensor '" + sensorId + "' is currently in '" + status + "' state and cannot accept new readings.");
        this.sensorId = sensorId;
        this.status = status;
    }

    public String getSensorId() { return sensorId; }
    public String getStatus() { return status; }
}
