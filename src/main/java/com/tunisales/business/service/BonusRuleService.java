package com.tunisales.business.service;

import com.tunisales.business.domain.BonusRule;
import com.tunisales.business.repository.BonusRuleRepository;
import com.tunisales.business.service.dto.BonusRuleDTO;
import com.tunisales.business.service.mapper.BonusRuleMapper;
import com.tunisales.business.tenant.TenantUtils;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Sub-step 2.9 — CRUD service for {@link BonusRule}. */
@Service
@Transactional
public class BonusRuleService {

    private final Logger log = LoggerFactory.getLogger(BonusRuleService.class);

    private final BonusRuleRepository bonusRuleRepository;
    private final BonusRuleMapper bonusRuleMapper;

    public BonusRuleService(BonusRuleRepository bonusRuleRepository, BonusRuleMapper bonusRuleMapper) {
        this.bonusRuleRepository = bonusRuleRepository;
        this.bonusRuleMapper = bonusRuleMapper;
    }

    public BonusRuleDTO save(BonusRuleDTO dto) {
        log.debug("Saving BonusRule: {}", dto);
        BonusRule entity = bonusRuleMapper.toEntity(dto);
        if (entity.getTenantId() == null) entity.setTenantId(TenantUtils.currentTenantId());
        if (entity.getIsActive() == null) entity.setIsActive(Boolean.TRUE);
        return bonusRuleMapper.toDto(bonusRuleRepository.save(entity));
    }

    public BonusRuleDTO update(BonusRuleDTO dto) {
        return save(dto);
    }

    @Transactional(readOnly = true)
    public Page<BonusRuleDTO> findAll(Pageable pageable) {
        return bonusRuleRepository.findAll(pageable).map(bonusRuleMapper::toDto);
    }

    @Transactional(readOnly = true)
    public List<BonusRuleDTO> findAll() {
        return bonusRuleMapper.toDto(bonusRuleRepository.findAll());
    }

    @Transactional(readOnly = true)
    public Optional<BonusRuleDTO> findOne(Long id) {
        return bonusRuleRepository.findById(id).map(bonusRuleMapper::toDto);
    }

    public void delete(Long id) {
        bonusRuleRepository.deleteById(id);
    }
}
