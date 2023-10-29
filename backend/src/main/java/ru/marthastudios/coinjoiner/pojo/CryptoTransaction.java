package ru.marthastudios.coinjoiner.pojo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CryptoTransaction {
    private String hash;
    private int outputN;
    private String toAddress;
    private String privateKey;
    private double amount;
}
