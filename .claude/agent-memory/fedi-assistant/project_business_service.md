---
name: BusinessService project context
description: TunisalesBusinessService — JHipster 7.9.3 / Spring Boot 2.7 / Java 11 multi-tenant order management backend. Captures stack, dev environment quirks, and active work (Order workflow rollout 2.x).
type: project
---

Stack:
- JHipster 7.9.3 generator, Spring Boot 2.7.3, Java 11 (compiles cleanly under JDK 17 too).
- PostgreSQL via Liquibase changesets in `src/main/resources/config/liquibase/`. New incremental changesets must be added before the `jhipster-needle-liquibase-add-incremental-changelog` needle in `master.xml`.
- Multi-tenancy: `TenantContext` (ThreadLocal<UUID>) populated by `TenantInterceptor` from the `X-Tenant-Id` header (set by the upstream gateway). NB: `Order.tenantId` is `Long`, not UUID — there is an inconsistency between the JDL-generated entity and the tenant infrastructure.

Dev environment on this Windows machine:
- JAVA_HOME is not set; JDK 17 is installed at `C:\Program Files\Java\jdk-17`. Set `$env:JAVA_HOME` and prepend `$env:JAVA_HOME\bin` to PATH before running `.\mvnw.cmd`.
- Docker daemon is **not running**, so the `testdev` failsafe profile (Postgres testcontainers) cannot start a Spring context. All `*IT` integration tests fail with `Failed to determine a suitable driver class` — environmental, not a code issue. Use surefire (`-Dtest=Foo test`) or a real DB to validate IT changes.
- Pre-existing surefire failures (unrelated to ongoing work): `TokenProviderTest`, `TokenProviderSecurityMetersTests`, `JWTFilterTest` — JWT/metrics tests that fail on this environment. Do not block work on these.

Why: documenting these saves the 5-minute discovery cycle every time the environment is touched.
How to apply: when running tests, expect IT tests to fail without Docker; focus on unit-test green and let the user run IT in CI / a dev box with Docker. Don't try to "fix" the JWT/metrics failures — they are pre-existing.

Order workflow rollout (active initiative — May 2026):
- Sub-step 2.1 (NEGOTIATING) implemented on branch `claude/analyze-pdf-missing-tasks-oRF9Y` on 2026-05-08. Adds `OrderStatus.{DRAFT,SUBMITTED,VALIDATED,NEGOTIATING,REJECTED}` (legacy French values kept), `Order.negotiationReason`, `OrderDTO.{negotiationReason,paymentMethod}`, transitions `submit/validate/negotiate/reject` in `OrderService` (with 409 ConflictException on illegal state, idempotence guard, audit timestamps), four `POST /api/orders/{id}/{action}` endpoints in `OrderResource` (admin actions guarded by `@PreAuthorize hasAnyAuthority('ROLE_ADMIN_COMMERCIAL','ROLE_ADMIN_SYSTEME')`). Two new authorities added to `AuthoritiesConstants`: `ADMIN_COMMERCIAL`, `ADMIN_SYSTEME`. Liquibase changeset `20260508120000_added_col_order_negotiation.xml` adds `negotiation_reason VARCHAR(500)`.
- Sub-step 2.2 (PaymentEligibilityService) implemented on 2026-05-08. Adds `PaymentMethod {CASH,CHECK,CARD,TRANSFER}` and `ClientGrade {A,B,C}` enums, `Order.paymentMethod` (NOT NULL DEFAULT CASH) and `Client.grade` (nullable) columns, `PaymentEligibilityService.assess(Order) → EligibilityResult` with rules (non-CHECK = ok; CHECK requires creditUsed+totalAmount ≤ creditLimit AND non-null grade; Grade C capped at 50% of creditLimit), `OrderNotEligibleException` extending `BadRequestAlertException` (HTTP 400). Wired into `OrderService.submit()`. 14 unit tests in `PaymentEligibilityServiceTest` (all green). Liquibase: `20260508130000_added_col_order_payment_method.xml` + `20260508130500_added_col_client_grade.xml`. `OrderDTO.paymentMethod` retyped from String to PaymentMethod.
- Outstanding sub-steps: 2.3 DiscountRangeValidator, 2.4 Invoice generation on validate. TODO comments remain in `OrderService.submit()` (2.3) and `OrderService.validate()` (2.4).

Why: the workflow is the central piece of the PFE; downstream features (invoicing, discounts) hang off it.
How to apply: when the user references "2.x" or "step X.Y", look for matching TODO markers in `OrderService` and the workflow methods first. Preserve the legacy French enum values until the migration of existing data is planned.
