package ru.marthastudios.coinjoiner.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import ru.marthastudios.coinjoiner.dto.CryptoDataDto;
import ru.marthastudios.coinjoiner.model.CryptoData;

@Mapper(componentModel = "spring")
public interface CryptoDataToCryptoDataDtoMapper {
    @Mappings({
            @Mapping(source = "id", target = "id"),
            @Mapping(source = "type", target = "type"),
            @Mapping(source = "address", target = "address"),
    })
    CryptoDataDto cryptoDataToCryptoDataDto(CryptoData cryptoData);
}