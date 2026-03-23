package by.kostya.skorik.service.snmp.mapper;

import by.kostya.skorik.domain.dto.MetricsDto;
import by.kostya.skorik.domain.model.Metrics;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MetricsDtoMapper {

    MetricsDto modelToDto(Metrics metrics);

}
