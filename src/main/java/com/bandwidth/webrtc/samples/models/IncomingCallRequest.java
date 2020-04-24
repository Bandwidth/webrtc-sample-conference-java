package com.bandwidth.webrtc.samples.models;

import lombok.Data;

@Data
public class IncomingCallRequest {
    private String from;
    private String to;
    private String callId;
}
