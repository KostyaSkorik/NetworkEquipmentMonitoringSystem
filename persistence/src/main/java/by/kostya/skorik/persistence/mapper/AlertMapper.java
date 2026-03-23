package by.kostya.skorik.persistence.mapper;

import by.kostya.skorik.domain.dto.AlertDto;
import by.kostya.skorik.persistence.entity.AlertEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AlertMapper {
    AlertDto entityToDto(AlertEntity alertEntity);

    @Mapping(target = "id", ignore = true)
    AlertEntity dtoToEntity(AlertDto alertDto);
}
