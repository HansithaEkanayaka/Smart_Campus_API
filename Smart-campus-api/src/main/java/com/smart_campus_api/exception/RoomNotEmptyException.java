/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.smart_campus_api.exception;

/**
 * Thrown when a deletion is attempted on a Room that still has Sensors assigned.
 * Maps to HTTP 409 Conflict.
 */
public class RoomNotEmptyException extends RuntimeException {
    private final String roomId;

    public RoomNotEmptyException(String roomId) {
        super("Room '" + roomId + "' cannot be deleted because it still has sensors assigned to it.");
        this.roomId = roomId;
    }

    public String getRoomId() { return roomId; }
}
