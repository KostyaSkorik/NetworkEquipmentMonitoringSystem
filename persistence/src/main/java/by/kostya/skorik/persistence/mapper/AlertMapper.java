package by.kostya.skorik.persistence.mapper;

import by.kostya.skorik.domain.model.Alerts;
import by.kostya.skorik.persistence.entity.AlertEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AlertMapper {
    Alerts entityToDto(AlertEntity alertEntity);

    @Mapping(target = "id", ignore = true)
    AlertEntity dtoToEntity(Alerts alerts);
}