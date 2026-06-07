package com.tunisales.business.service;

import com.tunisales.business.client.PlatformNotificationClient;
import com.tunisales.business.domain.Complaint;
import com.tunisales.business.domain.enumeration.ComplaintStatus;
import com.tunisales.business.repository.ComplaintRepository;
import com.tunisales.business.security.AuthoritiesConstants;
import com.tunisales.business.security.SecurityUtils;
import com.tunisales.business.service.dto.ComplaintDTO;
import com.tunisales.business.service.mapper.ComplaintMapper;
import com.tunisales.business.tenant.TenantUtils;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

/**
 * Sub-step 2.10 — service for {@link Complaint}.
 *
 * <p>On creation, a notification of type {@code NEW_COMPLAINT} is published to
 * {@code ROLE_ADMIN_COMMERCIAL} via {@link PlatformNotificationClient}. Failures
 * to notify never block the business transaction.</p>
 */
@Service
@Transactional
public class ComplaintService {

    private final Logger log = LoggerFactory.getLogger(ComplaintService.class);

    private final ComplaintRepository complaintRepository;
    private final ComplaintMapper complaintMapper;
    private final PlatformNotificationClient platformNotificationClient;

    public ComplaintService(
        ComplaintRepository complaintRepository,
        ComplaintMapper complaintMapper,
        PlatformNotificationClient platformNotificationClient
    ) {
        this.complaintRepository = complaintRepository;
        this.complaintMapper = complaintMapper;
        this.platformNotificationClient = platformNotificationClient;
    }

    /** Create a complaint in {@link ComplaintStatus#NEW} state and notify admins. */
    public ComplaintDTO create(ComplaintDTO dto) {
        log.debug("Creating Complaint: {}", dto);
        Complaint entity = complaintMapper.toEntity(dto);
        entity.setId(null);
        if (entity.getTenantId() == null) entity.setTenantId(TenantUtils.currentTenantId());
        if (entity.getAuthorLogin() == null) entity.setAuthorLogin(SecurityUtils.getCurrentUserLogin().orElse(null));
        entity.setStatus(ComplaintStatus.NEW);
        entity.setCreatedAt(ZonedDateTime.now());
        Complaint saved = complaintRepository.save(entity);

        String payload = String.format(
            "{\"complaintId\":%d,\"type\":\"%s\",\"author\":\"%s\"}",
            saved.getId(),
            saved.getType(),
            saved.getAuthorLogin()
        );
        platformNotificationClient.publish("NEW_COMPLAINT", "*" + AuthoritiesConstants.ADMIN_COMMERCIAL + "*", payload);

        return complaintMapper.toDto(saved);
    }

    public ComplaintDTO update(ComplaintDTO dto) {
        log.debug("Updating Complaint: {}", dto);
        Complaint entity = complaintMapper.toEntity(dto);
        return complaintMapper.toDto(complaintRepository.save(entity));
    }

    /** Resolve a complaint. Idempotence: 409 if already terminated. */
    public ComplaintDTO resolve(Long id) {
        log.debug("Resolving Complaint {}", id);
        Complaint entity = complaintRepository
            .findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Complaint not found: " + id));
        if (entity.getStatus() == ComplaintStatus.RESOLVED || entity.getStatus() == ComplaintStatus.REJECTED) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Complaint already terminated: " + entity.getStatus());
        }
        entity.setStatus(ComplaintStatus.RESOLVED);
        entity.setResolvedAt(ZonedDateTime.now());
        entity.setResolvedByLogin(SecurityUtils.getCurrentUserLogin().orElse(null));
        return complaintMapper.toDto(complaintRepository.save(entity));
    }

    @Transactional(readOnly = true)
    public Page<ComplaintDTO> findAll(Pageable pageable) {
        return complaintRepository.findAll(pageable).map(complaintMapper::toDto);
    }

    @Transactional(readOnly = true)
    public List<ComplaintDTO> findAll() {
        return complaintMapper.toDto(complaintRepository.findAll());
    }

    @Transactional(readOnly = true)
    public Optional<ComplaintDTO> findOne(Long id) {
        return complaintRepository.findById(id).map(complaintMapper::toDto);
    }

    public void delete(Long id) {
        complaintRepository.deleteById(id);
    }
}
