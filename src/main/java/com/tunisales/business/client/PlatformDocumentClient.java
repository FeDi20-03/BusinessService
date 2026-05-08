package com.tunisales.business.client;

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
 * Sub-step 2.4 — HTTP client for the Platform document service.
 *
 * <p>Used to (a) request the rendering of a business document (invoice, sales return,
 * credit note) and (b) fetch the rendered PDF bytes.</p>
 *
 * <p>Failures from the remote side are logged at WARN level and swallowed for
 * {@link #createDocument(String, String, Long)}; the caller continues normally.
 * For {@link #fetchRendered(Long)} the exception is propagated so the controller
 * can return a 503-ish status.</p>
 */
@Component
public class PlatformDocumentClient {

    private final Logger log = LoggerFactory.getLogger(PlatformDocumentClient.class);

    private final RestTemplate restTemplate;

    private final String baseUrl;

    public PlatformDocumentClient(@Value("${tunisales.platform.url:http://localhost:8082}") String baseUrl) {
        this.baseUrl = baseUrl;
        this.restTemplate = new RestTemplate();
    }

    /**
     * Ask the Platform service to create (and asynchronously render) a document.
     * Errors are logged but never propagated, so the calling business transaction
     * can still commit.
     */
    public void createDocument(String docType, String entityType, Long entityId) {
        String url = baseUrl + "/api/documents";
        Map<String, Object> body = new HashMap<>();
        body.put("docType", docType);
        body.put("entityType", entityType);
        body.put("entityId", entityId);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        try {
            restTemplate.postForEntity(url, request, Void.class);
            log.debug("Document creation requested: type={}, entity={}#{}", docType, entityType, entityId);
        } catch (RestClientException ex) {
            log.warn("Platform document service unreachable for {} {}#{}: {}", docType, entityType, entityId, ex.getMessage());
        }
    }

    /**
     * Fetch the rendered PDF bytes for a previously created document.
     * Throws on failure; let the controller translate to an HTTP error.
     */
    public byte[] fetchRendered(Long documentId) {
        String url = baseUrl + "/api/documents/" + documentId + "/rendered";
        ResponseEntity<byte[]> response = restTemplate.getForEntity(url, byte[].class);
        return response.getBody();
    }
}
