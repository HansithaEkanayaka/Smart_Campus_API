package com.smart_campus_api.resource;

import com.smart_campus_api.dao.RoomDAO;
import com.smart_campus_api.dao.SensorDAO;
import com.smart_campus_api.exception.LinkedResourceNotFoundException;
import com.smart_campus_api.exception.ResourceNotFoundException;
import com.smart_campus_api.model.Room;
import com.smart_campus_api.model.Sensor;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.List;

/**
 * JAX-RS Resource for Sensor management at /api/v1/sensors.
 *
 * Also acts as the entry point for the sub-resource locator pattern:
 * GET/POST /api/v1/sensors/{sensorId}/readings delegates to SensorReadingResource.
 */
@Path("/sensors")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SensorResource {

    private final SensorDAO sensorDAO = new SensorDAO();
    private final RoomDAO roomDAO = new RoomDAO();

    /**
     * GET /api/v1/sensors
     * Returns all sensors, with optional filtering by ?type=
     *
     * Query parameter vs path parameter design:
     * Using @QueryParam for filtering (/sensors?type=CO2) is superior to a path
     * parameter design (/sensors/type/CO2) because:
     *   1. Query params are semantically correct for filtering/searching a collection;
     *      path segments should identify a resource, not describe a filter.
     *   2. Multiple filters compose naturally: ?type=CO2&status=ACTIVE
     *   3. The parameter is optional — without it, the full collection is returned.
     *      A path-based approach would require a separate route for the unfiltered case.
     *   4. Follows HTTP/REST convention: paths = nouns/resources, query = modifiers.
     */
    @GET
    public Response getSensors(@QueryParam("type") String type) {
        List<Sensor> result;
        if (type != null && !type.isBlank()) {
            result = sensorDAO.findByType(type);
        } else {
            result = sensorDAO.findAll();
        }
        return Response.ok(result).build();
    }

    /**
     * POST /api/v1/sensors
     * Registers a new sensor. Validates that the referenced roomId exists.
     *
     * @Consumes(APPLICATION_JSON) consequence: If a client sends a request with
     * Content-Type: text/plain or application/xml, JAX-RS immediately returns
     * HTTP 415 Unsupported Media Type before the method body is even entered.
     * The framework's MessageBodyReader selection logic finds no reader that
     * can deserialize the mismatched content type into the Sensor parameter,
     * so it rejects the request at the framework boundary — cleanly, with no
     * business logic involvement.
     */
    @POST
    public Response createSensor(Sensor sensor) {
        if (sensor == null || sensor.getId() == null || sensor.getId().isBlank()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"status\":400,\"code\":\"ERR_INVALID_INPUT\",\"message\":\"Sensor id is required.\"}")
                    .build();
        }
        if (sensorDAO.exists(sensor.getId())) {
            return Response.status(Response.Status.CONFLICT)
                    .entity("{\"status\":409,\"code\":\"ERR_SENSOR_EXISTS\",\"message\":\"A sensor with that id already exists.\"}")
                    .build();
        }

        // Validate that the referenced roomId actually exists in the system
        if (sensor.getRoomId() == null || !roomDAO.exists(sensor.getRoomId())) {
            throw new LinkedResourceNotFoundException("roomId", sensor.getRoomId());
        }

        sensorDAO.save(sensor);

        // Register this sensor's ID in the Room's sensorIds list
        Room room = roomDAO.findById(sensor.getRoomId());
        room.getSensorIds().add(sensor.getId());
        roomDAO.update(room);

        URI location = URI.create("/api/v1/sensors/" + sensor.getId());
        return Response.created(location).entity(sensor).build();
    }

    /**
     * GET /api/v1/sensors/{sensorId}
     * Returns a specific sensor by ID.
     */
    @GET
    @Path("/{sensorId}")
    public Response getSensorById(@PathParam("sensorId") String sensorId) {
        Sensor sensor = sensorDAO.findById(sensorId);
        if (sensor == null) {
            throw new ResourceNotFoundException("Sensor", sensorId);
        }
        return Response.ok(sensor).build();
    }

    /**
     * DELETE /api/v1/sensors/{sensorId}
     * Removes a sensor and unregisters it from its parent room.
     */
    @DELETE
    @Path("/{sensorId}")
    public Response deleteSensor(@PathParam("sensorId") String sensorId) {
        Sensor sensor = sensorDAO.findById(sensorId);
        if (sensor == null) {
            throw new ResourceNotFoundException("Sensor", sensorId);
        }

        // Remove this sensor from its room's sensorIds list
        if (sensor.getRoomId() != null) {
            Room room = roomDAO.findById(sensor.getRoomId());
            if (room != null) {
                room.getSensorIds().remove(sensorId);
                roomDAO.update(room);
            }
        }

        sensorDAO.delete(sensorId);
        return Response.noContent().build();
    }

    /**
     * Sub-Resource Locator: /api/v1/sensors/{sensorId}/readings
     *
     * This method does NOT handle the request itself. Instead it returns
     * an instance of SensorReadingResource, and the JAX-RS runtime then
     * dispatches the actual HTTP method (GET/POST) to that class.
     *
     * Architectural benefits of this pattern:
     *   1. Separation of concerns: SensorResource manages sensors;
     *      SensorReadingResource manages readings. Each class has one responsibility.
     *   2. Complexity management: In a large API, defining every nested path
     *      (/sensors/{id}/readings, /sensors/{id}/readings/{rid}) in one class
     *      produces monolithic, unmaintainable controllers. Delegation keeps
     *      each class focused and small.
     *   3. Testability: SensorReadingResource can be unit-tested independently.
     *   4. Reusability: SensorReadingResource could be reused by another locator
     *      if the same reading logic were needed elsewhere.
     *
     * @param sensorId injected from the URL path segment
     * @return a new SensorReadingResource instance scoped to this sensorId
     */
    @Path("/{sensorId}/readings")
    public SensorReadingResource getReadingsResource(@PathParam("sensorId") String sensorId) {
        // Validate the parent sensor exists before delegating
        Sensor sensor = sensorDAO.findById(sensorId);
        if (sensor == null) {
            throw new ResourceNotFoundException("Sensor", sensorId);
        }
        return new SensorReadingResource(sensorId, sensorDAO);
    }
}
