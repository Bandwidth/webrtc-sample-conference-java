package com.bandwidth.webrtc.samples;

import com.bandwidth.webrtc.WebRtc;
import com.bandwidth.webrtc.authorization.WebRtcCredentials;
import com.bandwidth.webrtc.exceptions.HttpException;
import com.bandwidth.webrtc.samples.config.SampleAppConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@SpringBootApplication
@RestController
public class ConferenceApplication {

    private Logger log = LoggerFactory.getLogger(this.getClass().getName());

    public static void main(String[] args) {
        SpringApplication.run(ConferenceApplication.class, args);
    }

    @Bean
    public WebRtc initializeBandwidthWebRtcSdk(SampleAppConfig config) throws IOException, HttpException {
        WebRtc webRtc = new WebRtc();
        WebRtcCredentials credentials = WebRtcCredentials
                .builder()
                .accountId(config.getAccountId())
                .username(config.getUsername())
                .password(config.getPassword())
                .build();
        webRtc.connect(credentials);
        return webRtc;
    }

}
