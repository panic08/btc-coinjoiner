package ru.marthastudios.coinjoiner.dto.blockchain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BlockchainBtcAddressOutputDataDto {
    @JsonProperty("unspent_outputs")
    private UnspentOutput[] unspentOutputs;

    @Getter
    @Setter
    public static class UnspentOutput{
        @JsonProperty("tx_hash_big_endian")
        private String txHashBigEndian;
        @JsonProperty("tx_output_n")
        private int txOutputN;
        private long value;
    }
}
