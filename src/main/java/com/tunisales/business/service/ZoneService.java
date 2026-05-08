package com.tunisales.business.service;

import com.tunisales.business.domain.Zone;
import com.tunisales.business.repository.ZoneRepository;
import com.tunisales.business.service.dto.ZoneDTO;
import com.tunisales.business.service.mapper.ZoneMapper;
import com.tunisales.business.tenant.TenantUtils;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Sub-step 2.8 — CRUD service for {@link Zone}. */
@Service
@Transactional
public class ZoneService {

    private final Logger log = LoggerFactory.getLogger(ZoneService.class);

    private final ZoneRepository zoneRepository;
    private final ZoneMapper zoneMapper;

    public ZoneService(ZoneRepository zoneRepository, ZoneMapper zoneMapper) {
        this.zoneRepository = zoneRepository;
        this.zoneMapper = zoneMapper;
    }

    public ZoneDTO save(ZoneDTO dto) {
        log.debug("Saving Zone: {}", dto);
        Zone z = zoneMapper.toEntity(dto);
        if (z.getTenantId() == null) z.setTenantId(TenantUtils.currentTenantId());
        if (z.getIsActive() == null) z.setIsActive(Boolean.TRUE);
        return zoneMapper.toDto(zoneRepository.save(z));
    }

    public ZoneDTO update(ZoneDTO dto) {
        return save(dto);
    }

    @Transactional(readOnly = true)
    public Page<ZoneDTO> findAll(Pageable pageable) {
        return zoneRepository.findAll(pageable).map(zoneMapper::toDto);
    }

    @Transactional(readOnly = true)
    public List<ZoneDTO> findAll() {
        return zoneMapper.toDto(zoneRepository.findAll());
    }

    @Transactional(readOnly = true)
    public Optional<ZoneDTO> findOne(Long id) {
        return zoneRepository.findById(id).map(zoneMapper::toDto);
    }

    public void delete(Long id) {
        zoneRepository.deleteById(id);
    }
}
