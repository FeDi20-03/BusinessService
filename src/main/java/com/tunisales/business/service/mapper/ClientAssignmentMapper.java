package com.tunisales.business.service.mapper;

import com.tunisales.business.domain.ClientAssignment;
import com.tunisales.business.service.dto.ClientAssignmentDTO;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;

/** Sub-step 2.8 — manual mapper for {@link ClientAssignment}. */
@Component
public class ClientAssignmentMapper {

    public ClientAssignmentDTO toDto(ClientAssignment entity) {
        if (entity == null) return null;
        ClientAssignmentDTO dto = new ClientAssignmentDTO();
        dto.setId(entity.getId());
        dto.setTenantId(entity.getTenantId());
        dto.setClientId(entity.getClientId());
        dto.setCommercialLogin(entity.getCommercialLogin());
        dto.setZoneId(entity.getZoneId());
        dto.setAssignedAt(entity.getAssignedAt());
        dto.setAssignedByLogin(entity.getAssignedByLogin());
        return dto;
    }

    public List<ClientAssignmentDTO> toDto(List<ClientAssignment> entities) {
        List<ClientAssignmentDTO> out = new ArrayList<>();
        if (entities != null) {
            for (ClientAssignment a : entities) out.add(toDto(a));
        }
        return out;
    }

    public ClientAssignment toEntity(ClientAssignmentDTO dto) {
        if (dto == null) return null;
        ClientAssignment entity = new ClientAssignment();
        entity.setId(dto.getId());
        entity.setTenantId(dto.getTenantId());
        entity.setClientId(dto.getClientId());
        entity.setCommercialLogin(dto.getCommercialLogin());
        entity.setZoneId(dto.getZoneId());
        entity.setAssignedAt(dto.getAssignedAt());
        entity.setAssignedByLogin(dto.getAssignedByLogin());
        return entity;
    }
}
