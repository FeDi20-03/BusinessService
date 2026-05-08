package com.tunisales.business.service.mapper;

import com.tunisales.business.domain.BonusEntry;
import com.tunisales.business.service.dto.BonusEntryDTO;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;

/** Sub-step 2.9 — manual mapper for {@link BonusEntry}. */
@Component
public class BonusEntryMapper {

    public BonusEntryDTO toDto(BonusEntry entity) {
        if (entity == null) return null;
        BonusEntryDTO dto = new BonusEntryDTO();
        dto.setId(entity.getId());
        dto.setTenantId(entity.getTenantId());
        dto.setVendeurLogin(entity.getVendeurLogin());
        dto.setOrderLineId(entity.getOrderLineId());
        dto.setBonusRuleId(entity.getBonusRuleId());
        dto.setAmount(entity.getAmount());
        dto.setComputedAt(entity.getComputedAt());
        return dto;
    }

    public List<BonusEntryDTO> toDto(List<BonusEntry> entities) {
        List<BonusEntryDTO> out = new ArrayList<>();
        if (entities != null) {
            for (BonusEntry b : entities) out.add(toDto(b));
        }
        return out;
    }

    public BonusEntry toEntity(BonusEntryDTO dto) {
        if (dto == null) return null;
        BonusEntry entity = new BonusEntry();
        entity.setId(dto.getId());
        entity.setTenantId(dto.getTenantId());
        entity.setVendeurLogin(dto.getVendeurLogin());
        entity.setOrderLineId(dto.getOrderLineId());
        entity.setBonusRuleId(dto.getBonusRuleId());
        entity.setAmount(dto.getAmount());
        entity.setComputedAt(dto.getComputedAt());
        return entity;
    }
}
