package com.tunisales.business.service.mapper;

import com.tunisales.business.domain.Promotion;
import com.tunisales.business.service.dto.PromotionDTO;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;

/** Sub-step 2.9 — manual mapper for {@link Promotion}. */
@Component
public class PromotionMapper {

    public PromotionDTO toDto(Promotion entity) {
        if (entity == null) return null;
        PromotionDTO dto = new PromotionDTO();
        dto.setId(entity.getId());
        dto.setTenantId(entity.getTenantId());
        dto.setProductId(entity.getProductId());
        dto.setDiscountPct(entity.getDiscountPct());
        dto.setStartDate(entity.getStartDate());
        dto.setEndDate(entity.getEndDate());
        dto.setConditionsJson(entity.getConditionsJson());
        dto.setIsActive(entity.getIsActive());
        return dto;
    }

    public List<PromotionDTO> toDto(List<Promotion> entities) {
        List<PromotionDTO> out = new ArrayList<>();
        if (entities != null) {
            for (Promotion p : entities) out.add(toDto(p));
        }
        return out;
    }

    public Promotion toEntity(PromotionDTO dto) {
        if (dto == null) return null;
        Promotion entity = new Promotion();
        entity.setId(dto.getId());
        entity.setTenantId(dto.getTenantId());
        entity.setProductId(dto.getProductId());
        entity.setDiscountPct(dto.getDiscountPct());
        entity.setStartDate(dto.getStartDate());
        entity.setEndDate(dto.getEndDate());
        entity.setConditionsJson(dto.getConditionsJson());
        entity.setIsActive(dto.getIsActive());
        return entity;
    }
}
