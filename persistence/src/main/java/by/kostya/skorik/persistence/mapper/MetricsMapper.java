package by.kostya.skorik.persistence.mapper;


import by.kostya.skorik.domain.model.Metrics;
import by.kostya.skorik.persistence.entity.MetricsEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface MetricsMapper {

    Metrics entityToModel(MetricsEntity entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "router",ignore = true)
    MetricsEntity modelToEntity(Metrics model);
}
