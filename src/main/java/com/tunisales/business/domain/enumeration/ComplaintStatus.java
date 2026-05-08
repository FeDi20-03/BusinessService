package com.tunisales.business.domain.enumeration;

/**
 * Sub-step 2.10 — lifecycle of a {@link com.tunisales.business.domain.Complaint}.
 *
 * <ul>
 *   <li>{@link #NEW} — just created, awaiting triage.</li>
 *   <li>{@link #IN_REVIEW} — admin is investigating.</li>
 *   <li>{@link #RESOLVED} — closed positively.</li>
 *   <li>{@link #REJECTED} — closed without action (e.g. duplicate, out of scope).</li>
 * </ul>
 */
public enum ComplaintStatus {
    NEW,
    IN_REVIEW,
    RESOLVED,
    REJECTED,
}
