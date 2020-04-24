package com.bandwidth.webrtc.samples.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties
@Getter
@Setter
public class SampleAppConfig {
    private String accountId;
    private String username;
    private String password;
    private String websocketUrl;
    private String voiceCallbackUrl;
    private String voicePhoneNumber;
}
