package com.bandwidth.webrtc.samples.controller;

import com.bandwidth.webrtc.WebRtc;
import com.bandwidth.webrtc.models.CreateParticipantResponse;
import com.bandwidth.webrtc.samples.ConferenceManager;
import com.bandwidth.webrtc.samples.config.SampleAppConfig;
import com.bandwidth.webrtc.samples.exceptions.ConferenceAlreadyExistsException;
import com.bandwidth.webrtc.samples.models.Conference;
import com.bandwidth.webrtc.samples.models.GatherCompleteRequest;
import com.bandwidth.webrtc.samples.models.IncomingCallRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CallbackController {
    private static final Logger log = LoggerFactory.getLogger(CallbackController.class.getName());
    private final SampleAppConfig config;
    private final ConferenceManager conferenceManager;
    private final WebRtc webRtc;

    public CallbackController(SampleAppConfig config, ConferenceManager conferenceManager, WebRtc webRtc) {
        this.config = config;
        this.conferenceManager = conferenceManager;
        this.webRtc = webRtc;
    }

    @PostMapping(path = "/callback/incoming", produces = MediaType.APPLICATION_XML_VALUE)
    public String handleIncomingCall(@RequestBody IncomingCallRequest incomingCallRequest) {
        log.info("incoming call {}, from {} to {}", incomingCallRequest.getCallId(), incomingCallRequest.getFrom(), incomingCallRequest.getTo());
        String bxml = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n" +
                "  <Response>\n" +
                "      <Gather maxDigits=\"7\" gatherUrl=\"" + config.getVoiceCallbackUrl() + "/joinConference\">\n" +
                "        <SpeakSentence voice=\"julie\">Welcome to Bandwidth WebRTC Conferencing. Please enter your 7 digit conference ID.</SpeakSentence>\n" +
                "      </Gather>\n" +
                "  </Response>";
        log.debug("responding with bxml: {}", bxml);
        return bxml;
    }

    @PostMapping(path = "/callback/joinConference", produces = MediaType.APPLICATION_XML_VALUE)
    public String handleJoinConference(@RequestBody GatherCompleteRequest incomingCallRequest) throws ConferenceAlreadyExistsException {

        String conferenceId = incomingCallRequest.getDigits();

        log.info("{} is attempting to join conference {}", incomingCallRequest.getFrom(), conferenceId);

        Conference conference = conferenceManager.getConference(conferenceId);
        CreateParticipantResponse participantResponse = conferenceManager.createParticipant(conference, incomingCallRequest.getFrom());
        String bxml = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n" +
                "  <Response>\n" +
                "      <SpeakSentence voice=\"julie\">Thank you. Connecting you to your conference now.</SpeakSentence>\n" +
                "      " + webRtc.generateTransferBxml(conferenceId, participantResponse.getParticipantId()) + "\n" +
                "  </Response>";
        log.debug("responding with bxml: {}", bxml);
        return bxml;
    }
}