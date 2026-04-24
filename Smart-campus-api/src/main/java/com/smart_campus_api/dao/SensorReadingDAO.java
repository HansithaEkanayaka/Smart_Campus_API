/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.smart_campus_api.dao;

import com.smart_campus_api.model.SensorReading;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Data Access Object for SensorReading entities.
 * Stores readings per sensor in a ConcurrentHashMap of Lists.
 * List operations are synchronized to prevent race conditions.
 */
public class SensorReadingDAO {

    // Map<sensorId, List<SensorReading>>
    private static final Map<String, List<SensorReading>> readings = new ConcurrentHashMap<>();

    public void save(String sensorId, SensorReading reading) {
        readings.computeIfAbsent(sensorId, k -> Collections.synchronizedList(new ArrayList<>()))
                .add(reading);
    }

    public List<SensorReading> findBySensorId(String sensorId) {
        List<SensorReading> list = readings.get(sensorId);
        if (list == null) return new ArrayList<>();
        synchronized (list) {
            return new ArrayList<>(list);
        }
    }

    public SensorReading findById(String sensorId, String readingId) {
        List<SensorReading> list = readings.get(sensorId);
        if (list == null) return null;
        synchronized (list) {
            return list.stream().filter(r -> r.getId().equals(readingId)).findFirst().orElse(null);
        }
    }
}
