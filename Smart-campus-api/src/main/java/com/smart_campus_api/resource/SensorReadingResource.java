/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.smart_campus_api.resource;

import com.smart_campus_api.dao.SensorDAO;
import com.smart_campus_api.dao.SensorReadingDAO;
import com.smart_campus_api.exception.ResourceNotFoundException;
import com.smart_campus_api.exception.SensorUnavailableException;
import com.smart_campus_api.model.Sensor;
import com.smart_campus_api.model.SensorReading;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.List;
import java.util.UUID;

/**
 * Sub-resource class for sensor reading history.
 * Handles all requests under /api/v1/sensors/{sensorId}/readings.
 *
 * This class is NOT registered directly with JAX-RS. It is instantiated
 * and returned by the sub-resource locator in SensorResource. The JAX-RS
 * runtime then introspects this class for matching @GET / @POST methods.
 *
 * Note: No @Path annotation at class level — the path is defined by the
 * locator method in the parent resource.
 */
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SensorReadingResource {

    private final String sensorId;
    private final SensorDAO sensorDAO;
    private final SensorReadingDAO readingDAO = new SensorReadingDAO();

    public SensorReadingResource(String sensorId, SensorDAO sensorDAO) {
        this.sensorId = sensorId;
        this.sensorDAO = sensorDAO;
    }

    /**
     * GET /api/v1/sensors/{sensorId}/readings
     * Returns the full reading history for the sensor.
     */
    @GET
    public Response getReadings() {
        List<SensorReading> readings = readingDAO.findBySensorId(sensorId);
        return Response.ok(readings).build();
    }

    /**
     * POST /api/v1/sensors/{sensorId}/readings
     * Appends a new reading for this sensor.
     *
     * Side effect: Updates the parent Sensor's currentValue to keep
     * real-time data consistent across the API.
     *
     * Business constraint: Sensors in "MAINTENANCE" or "OFFLINE" status
     * cannot accept new readings — throws SensorUnavailableException (403).
     */
    @POST
    public Response addReading(SensorReading reading) {
        Sensor sensor = sensorDAO.findById(sensorId);
        if (sensor == null) {
            throw new ResourceNotFoundException("Sensor", sensorId);
        }

        // State constraint: only ACTIVE sensors can accept readings
        if (!"ACTIVE".equalsIgnoreCase(sensor.getStatus())) {
            throw new SensorUnavailableException(sensorId, sensor.getStatus());
        }

        // Auto-assign ID and timestamp if not provided
        if (reading.getId() == null || reading.getId().isBlank()) {
            reading.setId(UUID.randomUUID().toString());
        }
        if (reading.getTimestamp() == 0) {
            reading.setTimestamp(System.currentTimeMillis());
        }

        readingDAO.save(sensorId, reading);

        // Side effect: update the parent sensor's currentValue for data consistency
        sensor.setCurrentValue(reading.getValue());
        sensorDAO.update(sensor);

        URI location = URI.create("/api/v1/sensors/" + sensorId + "/readings/" + reading.getId());
        return Response.created(location).entity(reading).build();
    }

    /**
     * GET /api/v1/sensors/{sensorId}/readings/{readingId}
     * Returns a specific reading by ID.
     */
    @GET
    @Path("/{readingId}")
    public Response getReadingById(@PathParam("readingId") String readingId) {
        SensorReading reading = readingDAO.findById(sensorId, readingId);
        if (reading == null) {
            throw new ResourceNotFoundException("SensorReading", readingId);
        }
        return Response.ok(reading).build();
    }
}
