package com.bandwidth.webrtc.samples.controller;

import com.bandwidth.webrtc.models.CreateParticipantResponse;
import com.bandwidth.webrtc.samples.ConferenceManager;
import com.bandwidth.webrtc.samples.config.SampleAppConfig;
import com.bandwidth.webrtc.samples.exceptions.ConferenceAlreadyExistsException;
import com.bandwidth.webrtc.samples.models.AddParticipantRequest;
import com.bandwidth.webrtc.samples.models.AddParticipantResponse;
import com.bandwidth.webrtc.samples.models.Conference;
import com.bandwidth.webrtc.samples.models.CreateConferenceRequest;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ConferenceController {

    private SampleAppConfig config;
    private final ConferenceManager conferenceManager;

    public ConferenceController(SampleAppConfig config, ConferenceManager conferenceManager) {
        this.config = config;
        this.conferenceManager = conferenceManager;
    }


    @PostMapping("/conferences")
    public Conference createConference(@RequestBody(required = false) CreateConferenceRequest request) throws ConferenceAlreadyExistsException {
        String conferenceName = null;
        if (request != null) {
            conferenceName = request.getName();
        }
        return conferenceManager.createConference(conferenceName);
    }

    @PostMapping("/conferences/{conferenceSlug}/participants")
    public AddParticipantResponse addParticipant(@RequestBody(required = false) AddParticipantRequest request, @PathVariable String conferenceSlug) throws ConferenceAlreadyExistsException {
        String participantName = null;
        if (request != null) {
            participantName = request.getName();
        }
        CreateParticipantResponse createParticipantResponse = conferenceManager.createParticipant(conferenceSlug, participantName);
        return new AddParticipantResponse(createParticipantResponse.getConferenceId(),
                createParticipantResponse.getParticipantId(),
                createParticipantResponse.getDeviceToken(),
                config.getWebsocketUrl(),
                config.getVoicePhoneNumber());
    }


}
