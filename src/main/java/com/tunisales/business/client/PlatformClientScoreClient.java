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
 * Sub-step 2.11 — pushes the periodic loyalty score of a client to the Platform service.
 *
 * <p>Failures are logged WARN and not propagated; the scheduled job continues
 * to the next client.</p>
 */
@Component
public class PlatformClientScoreClient {

    private final Logger log = LoggerFactory.getLogger(PlatformClientScoreClient.class);

    private final RestTemplate restTemplate;

    private final String baseUrl;

    public PlatformClientScoreClient(@Value("${tunisales.platform.url:http://localhost:8082}") String baseUrl) {
        this.baseUrl = baseUrl;
        this.restTemplate = new RestTemplate();
    }

    public void postScore(Long clientId, BigDecimal score) {
        String url = baseUrl + "/api/client-scores";
        Map<String, Object> body = new HashMap<>();
        body.put("clientId", clientId);
        body.put("score", score);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        try {
            restTemplate.postForEntity(url, request, Void.class);
            log.debug("Client loyalty score posted: clientId={}, score={}", clientId, score);
        } catch (RestClientException ex) {
            log.warn("Platform client-score service unreachable for clientId={}: {}", clientId, ex.getMessage());
        }
    }
}
