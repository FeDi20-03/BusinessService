package com.tunisales.business.service.mapper;

import com.tunisales.business.domain.Client;
import com.tunisales.business.domain.Mission;
import com.tunisales.business.domain.Visit;
import com.tunisales.business.service.dto.ClientDTO;
import com.tunisales.business.service.dto.MissionDTO;
import com.tunisales.business.service.dto.VisitDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Visit} and its DTO {@link VisitDTO}.
 */
@Mapper(componentModel = "spring")
public interface VisitMapper extends EntityMapper<VisitDTO, Visit> {
    @Mapping(target = "client", source = "client", qualifiedByName = "clientName")
    @Mapping(target = "mission", source = "mission", qualifiedByName = "missionTitle")
    VisitDTO toDto(Visit s);

    @Named("clientName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    ClientDTO toDtoClientName(Client client);

    @Named("missionTitle")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "title", source = "title")
    MissionDTO toDtoMissionTitle(Mission mission);
}
