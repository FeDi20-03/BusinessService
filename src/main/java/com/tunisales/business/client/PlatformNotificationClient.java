package com.tunisales.business.client;

import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

/**
 * Sub-step 2.10 — HTTP client used to publish notifications via the Platform service.
 *
 * <p>Failures are logged WARN and never propagated, so a notification outage
 * cannot break a business transaction (a complaint creation or vehicle inspection
 * still succeeds even if the alert never reaches the admin).</p>
 */
@Component
public class PlatformNotificationClient {

    private final Logger log = LoggerFactory.getLogger(PlatformNotificationClient.class);

    private final RestTemplate restTemplate;

    private final String baseUrl;

    public PlatformNotificationClient(@Value("${tunisales.platform.url:http://localhost:8082}") String baseUrl) {
        this.baseUrl = baseUrl;
        this.restTemplate = new RestTemplate();
    }

    /**
     * Publish a notification to the platform. {@code payload} is sent as a string
     * field; callers may JSON-encode it themselves if structure is needed.
     */
    public void publish(String type, String targetRole, String payload) {
        String url = baseUrl + "/api/notifications/publish";
        Map<String, Object> body = new HashMap<>();
        body.put("type", type);
        body.put("targetRole", targetRole);
        body.put("payload", payload);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        try {
            restTemplate.postForEntity(url, request, Void.class);
            log.debug("Notification published: type={}, role={}", type, targetRole);
        } catch (RestClientException ex) {
            log.warn("Platform notification service unreachable for type={} role={}: {}", type, targetRole, ex.getMessage());
        }
    }
}
