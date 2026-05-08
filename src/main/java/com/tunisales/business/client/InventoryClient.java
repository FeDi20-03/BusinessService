package com.tunisales.business.client;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

/**
 * Sub-step 2.5 — HTTP client for the Inventory service.
 *
 * <p>Used by the SalesReturn workflow to (a) scan a stock item by IMEI to
 * resolve its identity and (b) move the physical item between states
 * (mark-repaired, send-to-rework) on approval of a return.</p>
 *
 * <p>Errors are logged at WARN level and surfaced as {@link InventoryUnavailableException}
 * (HTTP 503) so the business transaction can be rolled back without committing
 * an inconsistent state.</p>
 */
@Component
public class InventoryClient {

    private final Logger log = LoggerFactory.getLogger(InventoryClient.class);

    private final RestTemplate restTemplate;

    private final String baseUrl;

    public InventoryClient(@Value("${tunisales.inventory.url:http://localhost:8081}") String baseUrl) {
        this.baseUrl = baseUrl;
        this.restTemplate = new RestTemplate();
    }

    /**
     * Resolve a stock item by IMEI. Returns the item descriptor as a generic map
     * (keys typically include {@code id}, {@code imei}, {@code productId}, {@code condition}).
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> scan(String imei) {
        String url = baseUrl + "/api/stock-items/scan";
        Map<String, String> body = Collections.singletonMap("imei", imei);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, String>> request = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);
            return response.getBody() == null ? new HashMap<>() : (Map<String, Object>) response.getBody();
        } catch (RestClientException ex) {
            log.warn("Inventory scan failed for imei={}: {}", imei, ex.getMessage());
            throw new InventoryUnavailableException("Inventory service unavailable: " + ex.getMessage(), ex);
        }
    }

    /** Mark the given stock item as "repaired and back to sellable stock". */
    public void markRepaired(Long stockItemId) {
        post(baseUrl + "/api/stock-items/" + stockItemId + "/mark-repaired", "markRepaired", stockItemId);
    }

    /** Send the given stock item to the rework pipeline (damaged item). */
    public void sendToRework(Long stockItemId) {
        post(baseUrl + "/api/stock-items/" + stockItemId + "/send-to-rework", "sendToRework", stockItemId);
    }

    private void post(String url, String op, Long stockItemId) {
        try {
            restTemplate.postForEntity(url, HttpEntity.EMPTY, Void.class);
            log.debug("Inventory {} ok for stockItem={}", op, stockItemId);
        } catch (RestClientException ex) {
            log.warn("Inventory {} failed for stockItem={}: {}", op, stockItemId, ex.getMessage());
            throw new InventoryUnavailableException("Inventory service unavailable: " + ex.getMessage(), ex);
        }
    }
}
