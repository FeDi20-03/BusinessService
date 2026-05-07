package com.tunisales.business.tenant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.UUID;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link TenantContext} and {@link TenantInterceptor}.
 */
class TenantContextTest {

    private TenantInterceptor interceptor;

    @BeforeEach
    void setUp() {
        interceptor = new TenantInterceptor();
        TenantContext.clear();
    }

    @AfterEach
    void tearDown() {
        TenantContext.clear();
    }

    @Test
    void tenantContextIsNullByDefault() {
        assertThat(TenantContext.get()).isNull();
    }

    @Test
    void tenantContextSetAndClear() {
        UUID id = UUID.randomUUID();
        TenantContext.set(id);
        assertThat(TenantContext.get()).isEqualTo(id);
        TenantContext.clear();
        assertThat(TenantContext.get()).isNull();
    }

    @Test
    void interceptorPopulatesContextFromHeader() throws Exception {
        UUID tenantId = UUID.randomUUID();
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        when(request.getHeader(TenantInterceptor.TENANT_HEADER)).thenReturn(tenantId.toString());

        boolean proceed = interceptor.preHandle(request, response, new Object());

        assertThat(proceed).isTrue();
        assertThat(TenantContext.get()).isEqualTo(tenantId);
    }

    @Test
    void interceptorClearsContextAfterCompletion() throws Exception {
        UUID tenantId = UUID.randomUUID();
        TenantContext.set(tenantId);
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        interceptor.afterCompletion(request, response, new Object(), null);

        assertThat(TenantContext.get()).isNull();
    }

    @Test
    void interceptorIgnoresMissingHeader() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        when(request.getHeader(TenantInterceptor.TENANT_HEADER)).thenReturn(null);

        boolean proceed = interceptor.preHandle(request, response, new Object());

        assertThat(proceed).isTrue();
        assertThat(TenantContext.get()).isNull();
    }

    @Test
    void interceptorIgnoresInvalidUuidHeader() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        when(request.getHeader(TenantInterceptor.TENANT_HEADER)).thenReturn("not-a-uuid");

        boolean proceed = interceptor.preHandle(request, response, new Object());

        assertThat(proceed).isTrue();
        assertThat(TenantContext.get()).isNull();
    }
}
