package com.tunisales.business.service;

import com.tunisales.business.domain.Client;
import com.tunisales.business.domain.CreditNote;
import com.tunisales.business.domain.enumeration.CreditNoteStatus;
import com.tunisales.business.repository.ClientRepository;
import com.tunisales.business.repository.CreditNoteRepository;
import com.tunisales.business.security.SecurityUtils;
import com.tunisales.business.service.dto.CreditNoteDTO;
import com.tunisales.business.service.mapper.CreditNoteMapper;
import com.tunisales.business.tenant.TenantUtils;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

/**
 * Sub-step 2.6 — workflow service for credit notes (avoirs commerciaux).
 *
 * <p>Three transitions: {@link #create(CreditNoteDTO)} (DRAFT), {@link #validate(Long)}
 * (DRAFT → ISSUED), {@link #apply(Long, Long)} (ISSUED → APPLIED, with creditUsed
 * decrement on the client).</p>
 */
@Service
@Transactional
public class CreditNoteService {

    private final Logger log = LoggerFactory.getLogger(CreditNoteService.class);

    private final CreditNoteRepository creditNoteRepository;
    private final CreditNoteMapper creditNoteMapper;
    private final SequenceService sequenceService;
    private final ClientRepository clientRepository;

    public CreditNoteService(
        CreditNoteRepository creditNoteRepository,
        CreditNoteMapper creditNoteMapper,
        SequenceService sequenceService,
        ClientRepository clientRepository
    ) {
        this.creditNoteRepository = creditNoteRepository;
        this.creditNoteMapper = creditNoteMapper;
        this.sequenceService = sequenceService;
        this.clientRepository = clientRepository;
    }

    /** Create a credit note in DRAFT state. */
    public CreditNoteDTO create(CreditNoteDTO dto) {
        log.debug("Creating CreditNote for client {}", dto.getClientId());
        CreditNote entity = creditNoteMapper.toEntity(dto);
        entity.setId(null);
        entity.setTenantId(dto.getTenantId() == null ? TenantUtils.currentTenantId() : dto.getTenantId());
        entity.setStatus(CreditNoteStatus.DRAFT);
        entity.setCreditNoteNumber(sequenceService.nextCreditNoteNumber());
        entity.setCreatedByLogin(SecurityUtils.getCurrentUserLogin().orElse(null));
        entity.setCreatedAt(ZonedDateTime.now());
        return creditNoteMapper.toDto(creditNoteRepository.save(entity));
    }

    /** Move a credit note from DRAFT to ISSUED. Idempotence: 409 if not in DRAFT. */
    public CreditNoteDTO validate(Long id) {
        log.debug("Validating CreditNote {}", id);
        CreditNote entity = loadOrThrow(id);
        requireStatus(entity, CreditNoteStatus.DRAFT, "validate");

        entity.setStatus(CreditNoteStatus.ISSUED);
        entity.setValidatedByLogin(SecurityUtils.getCurrentUserLogin().orElse(null));
        entity.setValidatedAt(ZonedDateTime.now());
        return creditNoteMapper.toDto(creditNoteRepository.save(entity));
    }

    /**
     * Apply an ISSUED credit note against an order. The client's {@code creditUsed}
     * is decremented by the credit-note amount (clamped at zero).
     */
    public CreditNoteDTO apply(Long id, Long orderId) {
        log.debug("Applying CreditNote {} to order {}", id, orderId);
        CreditNote entity = loadOrThrow(id);
        requireStatus(entity, CreditNoteStatus.ISSUED, "apply");

        Client client = clientRepository
            .findById(entity.getClientId())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Client not found: " + entity.getClientId()));

        BigDecimal currentUsed = client.getCreditUsed() == null ? BigDecimal.ZERO : client.getCreditUsed();
        BigDecimal newUsed = currentUsed.subtract(entity.getAmount());
        if (newUsed.compareTo(BigDecimal.ZERO) < 0) {
            newUsed = BigDecimal.ZERO;
        }
        client.setCreditUsed(newUsed);
        clientRepository.save(client);

        entity.setStatus(CreditNoteStatus.APPLIED);
        log.info(
            "CreditNote {} applied: client {} creditUsed {} -> {}",
            entity.getCreditNoteNumber(),
            client.getId(),
            currentUsed,
            newUsed
        );
        return creditNoteMapper.toDto(creditNoteRepository.save(entity));
    }

    @Transactional(readOnly = true)
    public Optional<CreditNoteDTO> findOne(Long id) {
        return creditNoteRepository.findById(id).map(creditNoteMapper::toDto);
    }

    private CreditNote loadOrThrow(Long id) {
        return creditNoteRepository
            .findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "CreditNote not found: " + id));
    }

    private void requireStatus(CreditNote entity, CreditNoteStatus expected, String action) {
        if (entity.getStatus() != expected) {
            throw new ResponseStatusException(
                HttpStatus.CONFLICT,
                "Cannot " + action + " CreditNote " + entity.getId() + ": expected " + expected + " but was " + entity.getStatus()
            );
        }
    }
}
