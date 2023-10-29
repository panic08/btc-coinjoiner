package ru.marthastudios.coinjoiner.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import ru.marthastudios.coinjoiner.enums.CryptoDataType;

@Data
@Table("crypto_data_table")
public class CryptoData {
    @Id
    @Column("id")
    private Long id;
    @Column("user_id")
    private Long userId;
    @Column("type")
    private CryptoDataType type;
    @Column("private_key")
    private String privateKey;
    @Column("address")
    private String address;
}
