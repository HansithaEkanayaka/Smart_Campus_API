/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.smart_campus_api;

import com.smart_campus_api.exception.GenericExceptionMapper;
import com.smart_campus_api.exception.LinkedResourceNotFoundExceptionMapper;
import com.smart_campus_api.exception.ResourceNotFoundExceptionMapper;
import com.smart_campus_api.exception.RoomNotEmptyExceptionMapper;
import com.smart_campus_api.exception.SensorUnavailableExceptionMapper;
import com.smart_campus_api.filter.ApiLoggingFilter;
import com.smart_campus_api.resource.DiscoveryResource;
import com.smart_campus_api.resource.RoomResource;
import com.smart_campus_api.resource.SensorResource;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

/**
 * JAX-RS Application bootstrap class.
 *
 * Lifecycle note: By default, JAX-RS resource classes are request-scoped —
 * a new instance is created for every HTTP request. This means instance
 * variables are NOT shared across requests and cannot be used for shared
 * in-memory state. To manage shared in-memory data structures (HashMap, etc.)
 * safely, we use static, synchronized maps held in dedicated DAO classes,
 * ensuring thread-safe access across concurrent requests.
 */
@ApplicationPath("/api/v1")
public class SmartCampusApplication extends Application {

    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> classes = new HashSet<>();

        // Resources
        classes.add(DiscoveryResource.class);
        classes.add(RoomResource.class);
        classes.add(SensorResource.class);

        // Exception Mappers
        classes.add(RoomNotEmptyExceptionMapper.class);
        classes.add(LinkedResourceNotFoundExceptionMapper.class);
        classes.add(SensorUnavailableExceptionMapper.class);
        classes.add(ResourceNotFoundExceptionMapper.class);
        classes.add(GenericExceptionMapper.class);

        // Filters
        classes.add(ApiLoggingFilter.class);

        return classes;
    }
}
