package com.bandwidth.webrtc.samples.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.CONFLICT)
public class ConferenceAlreadyExistsException extends Exception {
    public ConferenceAlreadyExistsException(String conferenceId) {
        super("Conference with ID + '" + conferenceId + "' already exists");
    }
}
