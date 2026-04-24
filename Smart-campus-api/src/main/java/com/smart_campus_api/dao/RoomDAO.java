/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.smart_campus_api.dao;

import com.smart_campus_api.model.Room;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Data Access Object for Room entities.
 *
 * Uses a static ConcurrentHashMap as the in-memory data store so that data
 * persists across the request-scoped lifecycle of JAX-RS resource instances.
 * ConcurrentHashMap provides thread-safe reads and writes without requiring
 * explicit synchronization on every operation, preventing race conditions
 * when multiple HTTP requests arrive concurrently.
 */
public class RoomDAO {

    // Static store: survives across all request-scoped resource instances
    private static final Map<String, Room> rooms = new ConcurrentHashMap<>();

    public void save(Room room) {
        rooms.put(room.getId(), room);
    }

    public Room findById(String id) {
        return rooms.get(id);
    }

    public List<Room> findAll() {
        return new ArrayList<>(rooms.values());
    }

    public boolean exists(String id) {
        return rooms.containsKey(id);
    }

    public void delete(String id) {
        rooms.remove(id);
    }

    public void update(Room room) {
        rooms.put(room.getId(), room);
    }
}
