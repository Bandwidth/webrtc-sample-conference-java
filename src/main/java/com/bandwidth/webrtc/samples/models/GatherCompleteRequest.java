package com.bandwidth.webrtc.samples.models;

import lombok.Data;

@Data
public class GatherCompleteRequest {
    private String from;
    private String to;
    private String callId;
    private String digits;
}
