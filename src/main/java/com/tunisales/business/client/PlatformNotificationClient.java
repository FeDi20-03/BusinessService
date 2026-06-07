package com.tunisales.business.client;

import com.tunisales.business.tenant.TenantContext;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
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

    private final String internalToken;

    public PlatformNotificationClient(
        @Value("${tunisales.platform.url:http://localhost:8082}") String baseUrl,
        @Value("${tunisales.platform.internal-token:}") String internalToken
    ) {
        this.baseUrl = baseUrl;
        this.internalToken = internalToken;
        this.restTemplate = new RestTemplate();
    }

    /**
     * Publish a notification to the platform. The request body matches the
     * {@code NotificationPublishRequest} expected by {@code POST /api/notifications/publish}:
     * {@code recipientLogin}, {@code type} and {@code payloadJson}. Title / body are
     * intentionally omitted so the Platform service renders them from the Freemarker
     * template attached to {@code type}.
     *
     * @param type           the notification type (e.g. {@code ORDER_PENDING}).
     * @param recipientLogin  a direct login, or a role fan-out wrapped in stars
     *                        (e.g. {@code *ROLE_ADMIN_COMMERCIAL*}).
     * @param payloadJson     a JSON document feeding the template model.
     */
    public void publish(String type, String recipientLogin, String payloadJson) {
        String url = baseUrl + "/api/notifications/publish";
        Map<String, Object> body = new HashMap<>();
        body.put("type", type);
        body.put("recipientLogin", recipientLogin);
        body.put("payloadJson", payloadJson);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        // Authentifie l'appel service-à-service (PlatformService exige authenticated()
        // sur /api/** ; sans ce token l'appel est rejeté en 401).
        if (internalToken != null && !internalToken.isEmpty()) {
            headers.add("X-Internal-Token", internalToken);
        }
        // Propage le tenant courant pour que la PlatformService persiste la notif
        // dans le bon scope (sinon elle ne sera pas visible côté admin via GET /me).
        UUID tenantId = TenantContext.get();
        if (tenantId != null) {
            headers.add("X-Tenant-Id", tenantId.toString());
        }
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        try {
            restTemplate.postForEntity(url, request, Void.class);
            log.debug("Notification published: type={}, recipient={}", type, recipientLogin);
        } catch (RestClientException ex) {
            log.warn("Platform notification service unreachable for type={} recipient={}: {}", type, recipientLogin, ex.getMessage());
        }
    }
}
