package com.explorer.realtime.sessionhandling.waitingroom.exception;

public class ExceedingCapacityException extends RuntimeException {

    public ExceedingCapacityException() {
        super("Capacity exceeded. Unable to add more participants.");
    }

    public ExceedingCapacityException(String message) {
        super(message);
    }

    public ExceedingCapacityException(String message, Throwable cause) {
        super(message, cause);
    }

}
