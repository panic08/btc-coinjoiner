package ru.marthastudios.coinjoiner.repository;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import ru.marthastudios.coinjoiner.enums.CryptoDataType;
import ru.marthastudios.coinjoiner.model.CryptoData;

@Repository
public interface CryptoDataRepository extends ReactiveCrudRepository<CryptoData, Long> {
    @Query("SELECT cd.* FROM crypto_data_table cd WHERE user_id = :userId AND type = :type")
    Flux<CryptoData> findAllByUserIdAndType(@Param("userId") long userId, @Param("type") CryptoDataType cryptoDataType);
}
