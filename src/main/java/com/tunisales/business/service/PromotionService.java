package com.tunisales.business.service;

import com.tunisales.business.domain.Promotion;
import com.tunisales.business.repository.PromotionRepository;
import com.tunisales.business.service.dto.PromotionDTO;
import com.tunisales.business.service.mapper.PromotionMapper;
import com.tunisales.business.tenant.TenantUtils;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Sub-step 2.9 — CRUD service for {@link Promotion}. */
@Service
@Transactional
public class PromotionService {

    private final Logger log = LoggerFactory.getLogger(PromotionService.class);

    private final PromotionRepository promotionRepository;
    private final PromotionMapper promotionMapper;

    public PromotionService(PromotionRepository promotionRepository, PromotionMapper promotionMapper) {
        this.promotionRepository = promotionRepository;
        this.promotionMapper = promotionMapper;
    }

    public PromotionDTO save(PromotionDTO dto) {
        log.debug("Saving Promotion: {}", dto);
        Promotion entity = promotionMapper.toEntity(dto);
        if (entity.getTenantId() == null) entity.setTenantId(TenantUtils.currentTenantId());
        if (entity.getIsActive() == null) entity.setIsActive(Boolean.TRUE);
        return promotionMapper.toDto(promotionRepository.save(entity));
    }

    public PromotionDTO update(PromotionDTO dto) {
        return save(dto);
    }

    @Transactional(readOnly = true)
    public Page<PromotionDTO> findAll(Pageable pageable) {
        return promotionRepository.findAll(pageable).map(promotionMapper::toDto);
    }

    @Transactional(readOnly = true)
    public List<PromotionDTO> findAll() {
        return promotionMapper.toDto(promotionRepository.findAll());
    }

    @Transactional(readOnly = true)
    public Optional<PromotionDTO> findOne(Long id) {
        return promotionRepository.findById(id).map(promotionMapper::toDto);
    }

    public void delete(Long id) {
        promotionRepository.deleteById(id);
    }
}
