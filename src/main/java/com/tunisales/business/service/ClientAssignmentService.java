package com.tunisales.business.service;

import com.tunisales.business.domain.ClientAssignment;
import com.tunisales.business.repository.ClientAssignmentRepository;
import com.tunisales.business.security.SecurityUtils;
import com.tunisales.business.service.dto.ClientAssignmentDTO;
import com.tunisales.business.service.mapper.ClientAssignmentMapper;
import com.tunisales.business.tenant.TenantUtils;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Sub-step 2.8 — service for managing {@link ClientAssignment}s. */
@Service
@Transactional
public class ClientAssignmentService {

    private final Logger log = LoggerFactory.getLogger(ClientAssignmentService.class);

    private final ClientAssignmentRepository assignmentRepository;
    private final ClientAssignmentMapper assignmentMapper;

    public ClientAssignmentService(ClientAssignmentRepository assignmentRepository, ClientAssignmentMapper assignmentMapper) {
        this.assignmentRepository = assignmentRepository;
        this.assignmentMapper = assignmentMapper;
    }

    /**
     * Replace the current assignment for the given client (or create one if none exists).
     * The previous record is deleted to keep a single canonical row per client.
     */
    public ClientAssignmentDTO assign(Long clientId, String commercialLogin, Long zoneId) {
        log.debug("Assigning client {} to commercial {} on zone {}", clientId, commercialLogin, zoneId);
        Optional<ClientAssignment> existing = assignmentRepository.findFirstByClientIdOrderByAssignedAtDesc(clientId);
        existing.ifPresent(a -> assignmentRepository.deleteById(a.getId()));

        ClientAssignment assignment = new ClientAssignment();
        assignment.setTenantId(TenantUtils.currentTenantId());
        assignment.setClientId(clientId);
        assignment.setCommercialLogin(commercialLogin);
        assignment.setZoneId(zoneId);
        assignment.setAssignedAt(ZonedDateTime.now());
        assignment.setAssignedByLogin(SecurityUtils.getCurrentUserLogin().orElse(null));
        return assignmentMapper.toDto(assignmentRepository.save(assignment));
    }

    @Transactional(readOnly = true)
    public List<Long> findClientIdsByCommercialLogin(String commercialLogin) {
        return assignmentRepository
            .findByCommercialLogin(commercialLogin)
            .stream()
            .map(ClientAssignment::getClientId)
            .collect(Collectors.toList());
    }
}
