package com.bandwidth.webrtc.samples;

import com.bandwidth.webrtc.WebRtc;
import com.bandwidth.webrtc.models.CreateParticipantResponse;
import com.bandwidth.webrtc.models.StartConferenceResponse;
import com.bandwidth.webrtc.samples.exceptions.ConferenceAlreadyExistsException;
import com.bandwidth.webrtc.samples.models.Conference;
import com.bandwidth.webrtc.samples.models.Participant;
import com.github.slugify.Slugify;
import org.kohsuke.randname.RandomNameGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.google.common.base.Strings.isNullOrEmpty;

@Component
public class ConferenceManager {
    private static final Logger log = LoggerFactory.getLogger(ConferenceManager.class.getName());
    private final WebRtc webRtc;
    private final Map<String, Conference> conferenceMap = new ConcurrentHashMap<>();
    private final Map<String, String> slugsToIds = new ConcurrentHashMap<>();

    public ConferenceManager(WebRtc webRtc) {
        this.webRtc = webRtc;

        webRtc.setOnParticipantJoined((event) -> {
            String conferenceId = event.getConferenceId();
            if (conferenceMap.containsKey(conferenceId)) {
                String participantId = event.getParticipantId();
                log.info(participantId + " joined conference " + conferenceId);
                subscribeParticipantToAllStreams(conferenceMap.get(conferenceId), participantId);
            }
        });

        webRtc.setOnParticipantPublished((event) -> {
            String conferenceId = event.getConferenceId();
            if (conferenceMap.containsKey(conferenceId)) {
                Conference conference = conferenceMap.get(conferenceId);
                log.info("participant " + event.getParticipantId() + " published in conference " + event.getConferenceId() + " with stream id " + event.getStreamId());
                conference.getParticipants().get(event.getParticipantId()).getStreams().add(event.getStreamId());
                publishStreamToAllParticipants(conferenceMap.get(conferenceId), event.getParticipantId(), event.getStreamId());
            }
        });

        webRtc.setOnParticipantLeft((event) -> {
            String conferenceId = event.getConferenceId();
            String participantId = event.getParticipantId();
            if (conferenceMap.containsKey(conferenceId)) {
                Conference conference = conferenceMap.get(conferenceId);
                log.info("participant " + participantId + " left conference " + event.getConferenceId());
                webRtc.removeParticipant(conferenceId, participantId);
                conference.getParticipants().remove(participantId);
                if (conference.getParticipants().size() == 0) {
                    slugsToIds.remove(conference.getSlug());
                    webRtc.endConference(conferenceId);
                    conferenceMap.remove(conferenceId);
                    log.info("ended conference " + conferenceId);
                }
            }
        });

        webRtc.setOnParticipantUnpublished((event) -> {
            String conferenceId = event.getConferenceId();
            if (conferenceMap.containsKey(conferenceId)) {
                Conference conference = conferenceMap.get(conferenceId);
                log.info("participant " + event.getParticipantId() + " unpublished in conference " + event.getConferenceId() + " stream id " + event.getStreamId());
                conference.getParticipants().get(event.getParticipantId()).getStreams().remove(event.getStreamId());
            }
        });
    }

    public Conference getConference(String conferenceId) {
        return conferenceMap.get(conferenceId);
    }

    public Conference createConference(String name) throws ConferenceAlreadyExistsException {
        Slugify slg = new Slugify();
        if (isNullOrEmpty(name)) {
            RandomNameGenerator rng = new RandomNameGenerator();
            name = rng.next().replace("_", "-");
        }
        String slug = slg.slugify(name).toLowerCase();
        log.info("using slug " + slug);
        if (slugsToIds.containsKey(slug)) {
            throw new ConferenceAlreadyExistsException(slug);
        }
        StartConferenceResponse response = webRtc.startConference();
        log.info("created conference " + response.getConferenceId());
        Conference conference = new Conference(response.getConferenceId(), name, slug);
        conferenceMap.put(conference.getId(), conference);
        slugsToIds.put(slug, conference.getId());
        return conference;
    }

    public CreateParticipantResponse createParticipant(Conference conference, String name) throws ConferenceAlreadyExistsException {
        CreateParticipantResponse response = webRtc.createParticipant(conference.getId());
        Participant participant = new Participant(response.getParticipantId(), conference.getId(), "pending", name);
        conference.getParticipants().put(participant.getId(), participant);
        return response;
    }

    public CreateParticipantResponse createParticipant(String conferenceSlug, String name) throws ConferenceAlreadyExistsException {
        String conferenceId = slugsToIds.get(conferenceSlug);
        Conference conference;
        if (conferenceId != null) {
            conference = conferenceMap.get(conferenceId);
        } else {
            conference = createConference(conferenceSlug);
            conferenceId = conference.getId();
        }
        return createParticipant(conference, name);
    }

    private void subscribeParticipantToAllStreams(Conference conference, String subscriberId) {
        Participant participant = conference.getParticipants().get(subscriberId);
        participant.setStatus("connected");
        conference.getParticipants().values().forEach(publisher -> {
            if (!publisher.getId().equals(subscriberId)) {
                publisher.getStreams().forEach(streamId -> {
                    log.info("subscribing participant " + subscriberId + " to stream " + streamId);
                    webRtc.subscribe(conference.getId(), subscriberId, streamId);
                });
            }
        });
    }

    private void publishStreamToAllParticipants(Conference conference, String publisherId, String streamId) {
        conference.getParticipants().values().forEach(participant -> {
            if (!publisherId.equals(participant.getId()) && participant.getStatus().equals("connected")) {
                log.info("subscribing participant " + participant.getId() + " to stream " + publisherId + ":" + streamId);
                webRtc.subscribe(conference.getId(), participant.getId(), streamId);
            }
        });
    }
}
