package by.kostya.skorik.persistence.mapper;

import by.kostya.skorik.domain.model.Router;
import by.kostya.skorik.persistence.entity.RouterEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface RouterMapper {

    Router entityToModel(RouterEntity entity);

    @Mapping(target = "id", ignore = true)
    RouterEntity modelToEntity(Router model);

    @Mapping(target = "id", ignore = true)
    void updateRouter(RouterEntity newRouter, @MappingTarget RouterEntity oldRouter);
}
