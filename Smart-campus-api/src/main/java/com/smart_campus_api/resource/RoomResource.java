package com.smart_campus_api.resource;

import com.smart_campus_api.dao.RoomDAO;
import com.smart_campus_api.dao.SensorDAO;
import com.smart_campus_api.exception.ResourceNotFoundException;
import com.smart_campus_api.exception.RoomNotEmptyException;
import com.smart_campus_api.model.Room;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.List;

/**
 * JAX-RS Resource for Room management at /api/v1/rooms.
 *
 * Each method on this class corresponds to one REST operation.
 * The RoomDAO and SensorDAO use static ConcurrentHashMaps so that
 * all data persists across the request-scoped lifecycle of this class.
 */
@Path("/rooms")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class RoomResource {

    private final RoomDAO roomDAO = new RoomDAO();
    private final SensorDAO sensorDAO = new SensorDAO();

    /**
     * GET /api/v1/rooms
     * Returns the full list of all rooms.
     *
     * Design note: Returning full objects rather than IDs only is a trade-off.
     * Returning full objects costs more bandwidth but avoids requiring the client
     * to make N additional GET /{id} requests ("N+1 problem"). For large collections,
     * pagination or returning IDs only may be preferable to keep response payloads
     * small and reduce network overhead. For this campus scale, full objects are
     * appropriate.
     */
    @GET
    public Response getAllRooms() {
        List<Room> rooms = roomDAO.findAll();
        return Response.ok(rooms).build();
    }

    /**
     * POST /api/v1/rooms
     * Creates a new room. Returns 201 Created with a Location header.
     */
    @POST
    public Response createRoom(Room room) {
        if (room == null || room.getId() == null || room.getId().isBlank()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"status\":400,\"code\":\"ERR_INVALID_INPUT\",\"message\":\"Room id is required.\"}")
                    .build();
        }
        if (roomDAO.exists(room.getId())) {
            return Response.status(Response.Status.CONFLICT)
                    .entity("{\"status\":409,\"code\":\"ERR_ROOM_EXISTS\",\"message\":\"A room with that id already exists.\"}")
                    .build();
        }
        roomDAO.save(room);
        URI location = URI.create("/api/v1/rooms/" + room.getId());
        return Response.created(location).entity(room).build();
    }

    /**
     * GET /api/v1/rooms/{roomId}
     * Returns detailed metadata for a specific room.
     */
    @GET
    @Path("/{roomId}")
    public Response getRoomById(@PathParam("roomId") String roomId) {
        Room room = roomDAO.findById(roomId);
        if (room == null) {
            throw new ResourceNotFoundException("Room", roomId);
        }
        return Response.ok(room).build();
    }

    /**
     * DELETE /api/v1/rooms/{roomId}
     * Deletes a room. Business constraint: a room with active sensors cannot be deleted.
     *
     * Idempotency analysis: This DELETE is NOT fully idempotent in this implementation.
     * The first call returns 204 No Content (success). Subsequent identical calls return
     * 404 Not Found because the room no longer exists. While the server state is identical
     * after both calls (room is gone), the different HTTP status codes mean the operation
     * is not strictly idempotent in the HTTP sense — though the side effect (room deleted)
     * is the same. This is an acceptable and common REST pattern.
     */
    @DELETE
    @Path("/{roomId}")
    public Response deleteRoom(@PathParam("roomId") String roomId) {
        Room room = roomDAO.findById(roomId);
        if (room == null) {
            throw new ResourceNotFoundException("Room", roomId);
        }

        // Business Logic Constraint: block deletion if sensors are still assigned
        if (!room.getSensorIds().isEmpty()) {
            throw new RoomNotEmptyException(roomId);
        }

        roomDAO.delete(roomId);
        return Response.noContent().build();
    }
}
