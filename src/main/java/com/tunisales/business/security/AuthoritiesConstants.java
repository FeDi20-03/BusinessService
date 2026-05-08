package com.tunisales.business.security;

/**
 * Constants for Spring Security authorities.
 */
public final class AuthoritiesConstants {

    public static final String ADMIN = "ROLE_ADMIN";

    public static final String USER = "ROLE_USER";

    public static final String ANONYMOUS = "ROLE_ANONYMOUS";

    /** Commercial admin: can submit/validate/negotiate/reject orders. */
    public static final String ADMIN_COMMERCIAL = "ROLE_ADMIN_COMMERCIAL";

    /** System admin: highest-privilege admin, can perform all admin actions. */
    public static final String ADMIN_SYSTEME = "ROLE_ADMIN_SYSTEME";

    /** Sub-step 2.13 — fleet manager (chef de parc): can register vehicle inspections. */
    public static final String CHEF_PARC = "ROLE_CHEF_PARC";

    /** Sub-step 2.9 — commercial: salesperson eligible for bonus & performance scoring. */
    public static final String COMMERCIAL = "ROLE_COMMERCIAL";

    private AuthoritiesConstants() {}
}
