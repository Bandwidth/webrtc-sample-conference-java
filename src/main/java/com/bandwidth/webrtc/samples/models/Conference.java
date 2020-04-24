package com.bandwidth.webrtc.samples.models;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@AllArgsConstructor
@Getter
public class Conference {
    private String id;
    private String name;
    private String slug;
    private transient final Map<String, Participant> participants = new ConcurrentHashMap<>();
}
