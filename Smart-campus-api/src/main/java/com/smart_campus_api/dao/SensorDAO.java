/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.smart_campus_api.dao;

import com.smart_campus_api.model.Sensor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Data Access Object for Sensor entities.
 * Uses a static ConcurrentHashMap for thread-safe in-memory persistence.
 */
public class SensorDAO {

    private static final Map<String, Sensor> sensors = new ConcurrentHashMap<>();

    public void save(Sensor sensor) {
        sensors.put(sensor.getId(), sensor);
    }

    public Sensor findById(String id) {
        return sensors.get(id);
    }

    public List<Sensor> findAll() {
        return new ArrayList<>(sensors.values());
    }

    public List<Sensor> findByType(String type) {
        return sensors.values().stream()
                .filter(s -> s.getType() != null && s.getType().equalsIgnoreCase(type))
                .collect(Collectors.toList());
    }

    public List<Sensor> findByRoomId(String roomId) {
        return sensors.values().stream()
                .filter(s -> roomId.equals(s.getRoomId()))
                .collect(Collectors.toList());
    }

    public boolean exists(String id) {
        return sensors.containsKey(id);
    }

    public void delete(String id) {
        sensors.remove(id);
    }

    public void update(Sensor sensor) {
        sensors.put(sensor.getId(), sensor);
    }
}
