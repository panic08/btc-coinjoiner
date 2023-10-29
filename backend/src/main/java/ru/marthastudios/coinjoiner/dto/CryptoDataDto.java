package ru.marthastudios.coinjoiner.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;
import lombok.Setter;
import ru.marthastudios.coinjoiner.enums.CryptoDataType;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class CryptoDataDto {
    private long id;
    private CryptoDataType type;
    private String address;
}
