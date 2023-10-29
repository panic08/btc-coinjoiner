package ru.marthastudios.coinjoiner.payload;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.marthastudios.coinjoiner.enums.CryptoDataType;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GenerateCryptoPrivateKeyResponse {
    private CryptoDataType type;
    @JsonProperty("private_key")
    private String privateKey;
}
