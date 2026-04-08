package com.tunisales.business.service.mapper;

import com.tunisales.business.domain.Client;
import com.tunisales.business.domain.ClientContact;
import com.tunisales.business.service.dto.ClientContactDTO;
import com.tunisales.business.service.dto.ClientDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link ClientContact} and its DTO {@link ClientContactDTO}.
 */
@Mapper(componentModel = "spring")
public interface ClientContactMapper extends EntityMapper<ClientContactDTO, ClientContact> {
    @Mapping(target = "client", source = "client", qualifiedByName = "clientName")
    ClientContactDTO toDto(ClientContact s);

    @Named("clientName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    ClientDTO toDtoClientName(Client client);
}
