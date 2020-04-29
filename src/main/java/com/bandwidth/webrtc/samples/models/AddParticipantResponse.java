package com.bandwidth.webrtc.samples.models;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class AddParticipantResponse {
    private String conferenceId;
    private String participantId;
    private String deviceToken;
    private String websocketUrl;
    private String phoneNumber;
}
