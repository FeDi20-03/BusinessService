package com.tunisales.business.tenant;

import java.util.UUID;

/**
 * Helper utilities for resolving the tenant identifier in a form
 * compatible with persisted entities.
 *
 * <p>The legacy entity model carries {@code tenantId} as a {@code Long}
 * column, while the gateway-driven {@link TenantContext} stores a
 * {@link UUID}. This helper bridges the two so that new entities can
 * call {@code TenantUtils.currentTenantId()} without each service
 * having to deal with the conversion.</p>
 *
 * <p>Strategy: take the most-significant bits of the UUID and clamp
 * them to a positive {@code long}. When no tenant is set in the
 * thread-local context (e.g. background scheduled jobs, tests), fall
 * back to {@code 1L} which matches the dev-profile default tenant.</p>
 */
public final class TenantUtils {

    /** Default tenant id used when no {@code X-Tenant-Id} header is present. */
    public static final long DEFAULT_TENANT_ID = 1L;

    private TenantUtils() {}

    /**
     * Returns the current tenant identifier as a {@code Long}.
     * Never returns {@code null}; falls back to {@link #DEFAULT_TENANT_ID}.
     */
    public static Long currentTenantId() {
        UUID uuid = TenantContext.get();
        if (uuid == null) {
            return DEFAULT_TENANT_ID;
        }
        long bits = uuid.getMostSignificantBits();
        // Force positive value to fit @Min(0) or DEFAULT positive constraints.
        return bits == Long.MIN_VALUE ? DEFAULT_TENANT_ID : Math.abs(bits);
    }
}
