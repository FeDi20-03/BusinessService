package com.tunisales.business.service.mapper;

import com.tunisales.business.domain.VehicleInspection;
import com.tunisales.business.service.dto.VehicleInspectionDTO;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;

/** Sub-step 2.13 — manual mapper for {@link VehicleInspection}. */
@Component
public class VehicleInspectionMapper {

    public VehicleInspectionDTO toDto(VehicleInspection entity) {
        if (entity == null) return null;
        VehicleInspectionDTO dto = new VehicleInspectionDTO();
        dto.setId(entity.getId());
        dto.setTenantId(entity.getTenantId());
        dto.setCommercialLogin(entity.getCommercialLogin());
        dto.setChefParcLogin(entity.getChefParcLogin());
        dto.setInspectionDate(entity.getInspectionDate());
        dto.setVehicleState(entity.getVehicleState());
        dto.setNote(entity.getNote());
        dto.setNeedsRepair(entity.getNeedsRepair());
        dto.setComments(entity.getComments());
        return dto;
    }

    public List<VehicleInspectionDTO> toDto(List<VehicleInspection> entities) {
        List<VehicleInspectionDTO> out = new ArrayList<>();
        if (entities != null) {
            for (VehicleInspection v : entities) out.add(toDto(v));
        }
        return out;
    }

    public VehicleInspection toEntity(VehicleInspectionDTO dto) {
        if (dto == null) return null;
        VehicleInspection entity = new VehicleInspection();
        entity.setId(dto.getId());
        entity.setTenantId(dto.getTenantId());
        entity.setCommercialLogin(dto.getCommercialLogin());
        entity.setChefParcLogin(dto.getChefParcLogin());
        entity.setInspectionDate(dto.getInspectionDate());
        entity.setVehicleState(dto.getVehicleState());
        entity.setNote(dto.getNote());
        entity.setNeedsRepair(dto.getNeedsRepair());
        entity.setComments(dto.getComments());
        return entity;
    }
}
