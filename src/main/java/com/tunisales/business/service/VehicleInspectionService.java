package com.tunisales.business.service;

import com.tunisales.business.client.PlatformNotificationClient;
import com.tunisales.business.domain.VehicleInspection;
import com.tunisales.business.repository.VehicleInspectionRepository;
import com.tunisales.business.security.AuthoritiesConstants;
import com.tunisales.business.security.SecurityUtils;
import com.tunisales.business.service.dto.VehicleInspectionDTO;
import com.tunisales.business.service.mapper.VehicleInspectionMapper;
import com.tunisales.business.tenant.TenantUtils;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Sub-step 2.13 — service for {@link VehicleInspection}.
 *
 * <p>When an inspection is recorded with {@code needsRepair=true}, a notification of
 * type {@code INSPECTION_REQUIRED} is published to {@code ROLE_ADMIN_COMMERCIAL}.</p>
 */
@Service
@Transactional
public class VehicleInspectionService {

    private final Logger log = LoggerFactory.getLogger(VehicleInspectionService.class);

    private final VehicleInspectionRepository vehicleInspectionRepository;
    private final VehicleInspectionMapper vehicleInspectionMapper;
    private final PlatformNotificationClient platformNotificationClient;

    public VehicleInspectionService(
        VehicleInspectionRepository vehicleInspectionRepository,
        VehicleInspectionMapper vehicleInspectionMapper,
        PlatformNotificationClient platformNotificationClient
    ) {
        this.vehicleInspectionRepository = vehicleInspectionRepository;
        this.vehicleInspectionMapper = vehicleInspectionMapper;
        this.platformNotificationClient = platformNotificationClient;
    }

    public VehicleInspectionDTO create(VehicleInspectionDTO dto) {
        log.debug("Creating VehicleInspection: {}", dto);
        VehicleInspection entity = vehicleInspectionMapper.toEntity(dto);
        entity.setId(null);
        if (entity.getTenantId() == null) entity.setTenantId(TenantUtils.currentTenantId());
        if (entity.getChefParcLogin() == null) {
            entity.setChefParcLogin(SecurityUtils.getCurrentUserLogin().orElse(null));
        }
        if (entity.getNeedsRepair() == null) entity.setNeedsRepair(Boolean.FALSE);
        VehicleInspection saved = vehicleInspectionRepository.save(entity);

        if (Boolean.TRUE.equals(saved.getNeedsRepair())) {
            String payload = String.format(
                "{\"inspectionId\":%d,\"commercial\":\"%s\",\"chefParc\":\"%s\",\"state\":\"%s\"}",
                saved.getId(),
                saved.getCommercialLogin(),
                saved.getChefParcLogin(),
                saved.getVehicleState()
            );
            platformNotificationClient.publish("INSPECTION_REQUIRED", AuthoritiesConstants.ADMIN_COMMERCIAL, payload);
        }

        return vehicleInspectionMapper.toDto(saved);
    }

    public VehicleInspectionDTO update(VehicleInspectionDTO dto) {
        log.debug("Updating VehicleInspection: {}", dto);
        VehicleInspection entity = vehicleInspectionMapper.toEntity(dto);
        return vehicleInspectionMapper.toDto(vehicleInspectionRepository.save(entity));
    }

    @Transactional(readOnly = true)
    public Page<VehicleInspectionDTO> findAll(Pageable pageable) {
        return vehicleInspectionRepository.findAll(pageable).map(vehicleInspectionMapper::toDto);
    }

    @Transactional(readOnly = true)
    public List<VehicleInspectionDTO> findAll() {
        return vehicleInspectionMapper.toDto(vehicleInspectionRepository.findAll());
    }

    @Transactional(readOnly = true)
    public Optional<VehicleInspectionDTO> findOne(Long id) {
        return vehicleInspectionRepository.findById(id).map(vehicleInspectionMapper::toDto);
    }

    public void delete(Long id) {
        vehicleInspectionRepository.deleteById(id);
    }
}
