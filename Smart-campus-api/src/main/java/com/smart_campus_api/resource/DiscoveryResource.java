/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.smart_campus_api.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;


@Path("/")
@Produces(MediaType.APPLICATION_JSON)
public class DiscoveryResource {

    @GET
    public Response discover() {
        Map<String, Object> metadata = new HashMap<>();

        metadata.put("api", "Smart Campus Sensor & Room Management API");
        metadata.put("version", "1.0.0");
        metadata.put("description", "RESTful API for managing campus rooms and IoT sensors.");
        Map<String, String> contact = new HashMap<>();
        contact.put("name", "E.A.R.H.Ekanayaka");
        contact.put("email", "w2120662@westminster.ac.uk");
        contact.put("module", "5COSC022W Client-Server Architectures");
        metadata.put("contact", contact);

        Map<String, String> resources = new HashMap<>();
        resources.put("rooms", "/api/v1/rooms");
        resources.put("sensors", "/api/v1/sensors");

        metadata.put("resources", resources);

        Map<String, String> links = new HashMap<>();
        links.put("self", "/api/v1");
        links.put("rooms", "/api/v1/rooms");
        links.put("sensors", "/api/v1/sensors");
        metadata.put("_links", links);

        return Response.ok(metadata).build();
    }
}
