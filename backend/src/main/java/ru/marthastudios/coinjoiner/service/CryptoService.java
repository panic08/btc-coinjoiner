package ru.marthastudios.coinjoiner.service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.marthastudios.coinjoiner.dto.CryptoDataDto;
import ru.marthastudios.coinjoiner.enums.CryptoDataType;
import ru.marthastudios.coinjoiner.payload.CreateCryptoDataRequest;
import ru.marthastudios.coinjoiner.payload.CreateCryptoBtcTransactionRequest;
import ru.marthastudios.coinjoiner.payload.GenerateCryptoPrivateKeyResponse;

public interface CryptoService {
    Mono<CryptoDataDto> createCryptoData(long principalId, CreateCryptoDataRequest createCryptoDataRequest);
    Flux<CryptoDataDto> getAllCryptoDataByType(long principalId, CryptoDataType type);
    Mono<Void> deleteCryptoData(long principalId, long cryptoDataId);
    Mono<GenerateCryptoPrivateKeyResponse> generateCryptoPrivateKeyByType(CryptoDataType type);
    Mono<Void> handleSendCryptoBtcTransaction(CreateCryptoBtcTransactionRequest createCryptoBtcTransactionRequest);
}
