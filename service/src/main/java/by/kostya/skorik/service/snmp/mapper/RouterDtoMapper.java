package by.kostya.skorik.service.snmp.mapper;

import by.kostya.skorik.domain.dto.RouterDto;
import by.kostya.skorik.domain.model.Router;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RouterDtoMapper {

    Router dtoToModel(RouterDto routerDto);

    RouterDto modelToDto(Router router);
}