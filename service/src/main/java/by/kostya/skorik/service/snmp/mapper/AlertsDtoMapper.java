package by.kostya.skorik.service.snmp.mapper;

import by.kostya.skorik.domain.dto.AlertsDto;
import by.kostya.skorik.domain.model.Alerts;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AlertsDtoMapper {

    AlertsDto modelToDto(Alerts alerts);
}