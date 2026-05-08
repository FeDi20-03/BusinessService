package com.tunisales.business.client;

import java.math.BigDecimal;
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
 * Sub-step 2.12 — pushes the periodic performance score of a commercial to the Platform service.
 *
 * <p>Failures are logged WARN and not propagated; the scheduled job continues
 * to the next user.</p>
 */
@Component
public class PlatformPerformanceScoreClient {

    private final Logger log = LoggerFactory.getLogger(PlatformPerformanceScoreClient.class);

    private final RestTemplate restTemplate;

    private final String baseUrl;

    public PlatformPerformanceScoreClient(@Value("${tunisales.platform.url:http://localhost:8082}") String baseUrl) {
        this.baseUrl = baseUrl;
        this.restTemplate = new RestTemplate();
    }

    public void postScore(String login, BigDecimal score, String period) {
        String url = baseUrl + "/api/performance-scores";
        Map<String, Object> body = new HashMap<>();
        body.put("login", login);
        body.put("score", score);
        body.put("period", period);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        try {
            restTemplate.postForEntity(url, request, Void.class);
            log.debug("Commercial performance score posted: login={}, period={}, score={}", login, period, score);
        } catch (RestClientException ex) {
            log.warn("Platform performance-score service unreachable for login={} period={}: {}", login, period, ex.getMessage());
        }
    }
}
