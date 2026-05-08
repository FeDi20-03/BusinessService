package com.tunisales.business.service.mapper;

import com.tunisales.business.domain.Zone;
import com.tunisales.business.service.dto.ZoneDTO;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;

/** Sub-step 2.8 — manual mapper for {@link Zone}. */
@Component
public class ZoneMapper {

    public ZoneDTO toDto(Zone entity) {
        if (entity == null) return null;
        ZoneDTO dto = new ZoneDTO();
        dto.setId(entity.getId());
        dto.setTenantId(entity.getTenantId());
        dto.setName(entity.getName());
        dto.setCode(entity.getCode());
        dto.setDescription(entity.getDescription());
        dto.setIsActive(entity.getIsActive());
        return dto;
    }

    public List<ZoneDTO> toDto(List<Zone> entities) {
        List<ZoneDTO> out = new ArrayList<>();
        if (entities != null) {
            for (Zone z : entities) out.add(toDto(z));
        }
        return out;
    }

    public Zone toEntity(ZoneDTO dto) {
        if (dto == null) return null;
        Zone entity = new Zone();
        entity.setId(dto.getId());
        entity.setTenantId(dto.getTenantId());
        entity.setName(dto.getName());
        entity.setCode(dto.getCode());
        entity.setDescription(dto.getDescription());
        entity.setIsActive(dto.getIsActive());
        return entity;
    }
}
