package ru.marthastudios.coinjoiner.payload;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateCryptoBtcTransactionRequest {
    private Input[] inputs;
    private Out[] outs;
    @Getter
    @Setter
    public static class Input{
        private Integer[] keys;
        @JsonProperty("crypto_data_id")
        private long cryptoDataId;
    }
    @Getter
    @Setter
    public static class Out{
        private int key;
        private String address;
        private double amount;
    }
}
