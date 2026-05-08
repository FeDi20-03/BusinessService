package com.tunisales.business.service.mapper;

import com.tunisales.business.domain.BonusRule;
import com.tunisales.business.service.dto.BonusRuleDTO;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;

/** Sub-step 2.9 — manual mapper for {@link BonusRule}. */
@Component
public class BonusRuleMapper {

    public BonusRuleDTO toDto(BonusRule entity) {
        if (entity == null) return null;
        BonusRuleDTO dto = new BonusRuleDTO();
        dto.setId(entity.getId());
        dto.setTenantId(entity.getTenantId());
        dto.setProductId(entity.getProductId());
        dto.setZoneId(entity.getZoneId());
        dto.setAmount(entity.getAmount());
        dto.setPeriodStart(entity.getPeriodStart());
        dto.setPeriodEnd(entity.getPeriodEnd());
        dto.setIsActive(entity.getIsActive());
        return dto;
    }

    public List<BonusRuleDTO> toDto(List<BonusRule> entities) {
        List<BonusRuleDTO> out = new ArrayList<>();
        if (entities != null) {
            for (BonusRule b : entities) out.add(toDto(b));
        }
        return out;
    }

    public BonusRule toEntity(BonusRuleDTO dto) {
        if (dto == null) return null;
        BonusRule entity = new BonusRule();
        entity.setId(dto.getId());
        entity.setTenantId(dto.getTenantId());
        entity.setProductId(dto.getProductId());
        entity.setZoneId(dto.getZoneId());
        entity.setAmount(dto.getAmount());
        entity.setPeriodStart(dto.getPeriodStart());
        entity.setPeriodEnd(dto.getPeriodEnd());
        entity.setIsActive(dto.getIsActive());
        return entity;
    }
}
