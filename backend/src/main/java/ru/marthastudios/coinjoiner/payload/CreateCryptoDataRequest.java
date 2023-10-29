package ru.marthastudios.coinjoiner.payload;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import ru.marthastudios.coinjoiner.enums.CryptoDataType;

@Getter
@Setter
public class CreateCryptoDataRequest {
    @JsonProperty("private_key")
    private String privateKey;
    private CryptoDataType type;
}
