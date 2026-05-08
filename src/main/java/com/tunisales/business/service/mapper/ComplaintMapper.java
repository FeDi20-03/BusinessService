package com.tunisales.business.service.mapper;

import com.tunisales.business.domain.Complaint;
import com.tunisales.business.service.dto.ComplaintDTO;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;

/** Sub-step 2.10 — manual mapper for {@link Complaint}. */
@Component
public class ComplaintMapper {

    public ComplaintDTO toDto(Complaint entity) {
        if (entity == null) return null;
        ComplaintDTO dto = new ComplaintDTO();
        dto.setId(entity.getId());
        dto.setTenantId(entity.getTenantId());
        dto.setAuthorLogin(entity.getAuthorLogin());
        dto.setType(entity.getType());
        dto.setDescription(entity.getDescription());
        dto.setStatus(entity.getStatus());
        dto.setStockItemId(entity.getStockItemId());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setResolvedAt(entity.getResolvedAt());
        dto.setResolvedByLogin(entity.getResolvedByLogin());
        return dto;
    }

    public List<ComplaintDTO> toDto(List<Complaint> entities) {
        List<ComplaintDTO> out = new ArrayList<>();
        if (entities != null) {
            for (Complaint c : entities) out.add(toDto(c));
        }
        return out;
    }

    public Complaint toEntity(ComplaintDTO dto) {
        if (dto == null) return null;
        Complaint entity = new Complaint();
        entity.setId(dto.getId());
        entity.setTenantId(dto.getTenantId());
        entity.setAuthorLogin(dto.getAuthorLogin());
        entity.setType(dto.getType());
        entity.setDescription(dto.getDescription());
        entity.setStatus(dto.getStatus());
        entity.setStockItemId(dto.getStockItemId());
        entity.setCreatedAt(dto.getCreatedAt());
        entity.setResolvedAt(dto.getResolvedAt());
        entity.setResolvedByLogin(dto.getResolvedByLogin());
        return entity;
    }
}
