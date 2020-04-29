package com.bandwidth.webrtc.samples.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Getter
public class Participant {
    private String id;
    private String conferenceId;
    @Setter
    private String status;
    private String name;
    private final List<String> streams = new ArrayList<>();
}
