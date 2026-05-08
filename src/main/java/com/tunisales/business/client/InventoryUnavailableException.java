package com.tunisales.business.client;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Sub-step 2.5 — thrown when {@link InventoryClient} cannot reach the
 * Inventory service (connection refused, timeout, 5xx response).
 *
 * <p>Mapped to HTTP 503 Service Unavailable.</p>
 */
@ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
public class InventoryUnavailableException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public InventoryUnavailableException(String message) {
        super(message);
    }

    public InventoryUnavailableException(String message, Throwable cause) {
        super(message, cause);
    }
}
