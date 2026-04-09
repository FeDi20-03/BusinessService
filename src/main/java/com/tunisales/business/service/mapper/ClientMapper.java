package com.tunisales.business.service.mapper;

import com.tunisales.business.domain.Client;
import com.tunisales.business.service.dto.ClientDTO;
import java.util.List;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Client} and its DTO {@link ClientDTO}.
 */
@Mapper(componentModel = "spring", uses = { ClientContactMapper.class })
public interface ClientMapper extends EntityMapper<ClientDTO, Client> {
    @Mapping(target = "contacts", source = "contacts")
    ClientDTO toDto(Client client);

    List<ClientDTO> toDto(List<Client> clients);
}
