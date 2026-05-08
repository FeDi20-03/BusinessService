package com.tunisales.business.service.mapper;

import com.tunisales.business.domain.CreditNote;
import com.tunisales.business.service.dto.CreditNoteDTO;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;

/** Sub-step 2.6 — manual mapper for {@link CreditNote}. */
@Component
public class CreditNoteMapper {

    public CreditNoteDTO toDto(CreditNote entity) {
        if (entity == null) return null;
        CreditNoteDTO dto = new CreditNoteDTO();
        dto.setId(entity.getId());
        dto.setTenantId(entity.getTenantId());
        dto.setClientId(entity.getClientId());
        dto.setCreditNoteNumber(entity.getCreditNoteNumber());
        dto.setAmount(entity.getAmount());
        dto.setReason(entity.getReason());
        dto.setStatus(entity.getStatus());
        dto.setCreatedByLogin(entity.getCreatedByLogin());
        dto.setValidatedByLogin(entity.getValidatedByLogin());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setValidatedAt(entity.getValidatedAt());
        return dto;
    }

    public List<CreditNoteDTO> toDto(List<CreditNote> entities) {
        List<CreditNoteDTO> out = new ArrayList<>();
        if (entities != null) {
            for (CreditNote e : entities) out.add(toDto(e));
        }
        return out;
    }

    public CreditNote toEntity(CreditNoteDTO dto) {
        if (dto == null) return null;
        CreditNote entity = new CreditNote();
        entity.setId(dto.getId());
        entity.setTenantId(dto.getTenantId());
        entity.setClientId(dto.getClientId());
        entity.setCreditNoteNumber(dto.getCreditNoteNumber());
        entity.setAmount(dto.getAmount());
        entity.setReason(dto.getReason());
        entity.setStatus(dto.getStatus());
        entity.setCreatedByLogin(dto.getCreatedByLogin());
        entity.setValidatedByLogin(dto.getValidatedByLogin());
        entity.setCreatedAt(dto.getCreatedAt());
        entity.setValidatedAt(dto.getValidatedAt());
        return entity;
    }
}
