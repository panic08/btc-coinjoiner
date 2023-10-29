package ru.marthastudios.coinjoiner.pojo;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class AddressAmount {
    private String address;
    private double amount;
}
