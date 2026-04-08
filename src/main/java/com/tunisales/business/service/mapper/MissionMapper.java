package com.tunisales.business.service.mapper;

import com.tunisales.business.domain.Mission;
import com.tunisales.business.service.dto.MissionDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Mission} and its DTO {@link MissionDTO}.
 */
@Mapper(componentModel = "spring")
public interface MissionMapper extends EntityMapper<MissionDTO, Mission> {}
