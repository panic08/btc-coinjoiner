package ru.marthastudios.coinjoiner.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.marthastudios.coinjoiner.dto.CryptoDataDto;
import ru.marthastudios.coinjoiner.enums.CryptoDataType;
import ru.marthastudios.coinjoiner.payload.CreateCryptoBtcTransactionRequest;
import ru.marthastudios.coinjoiner.payload.CreateCryptoDataRequest;
import ru.marthastudios.coinjoiner.payload.GenerateCryptoPrivateKeyResponse;
import ru.marthastudios.coinjoiner.service.implement.CryptoServiceImpl;

@RestController
@RequestMapping("/api/crypto")
@RequiredArgsConstructor
public class CryptoController {

    private final CryptoServiceImpl cryptoService;

    @GetMapping("/cryptoData/getAllByType")
    public Flux<CryptoDataDto> getAllCryptoDataByType(UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken,
                                                @RequestParam("type") CryptoDataType type){
        return cryptoService.getAllCryptoDataByType(Long.parseLong(usernamePasswordAuthenticationToken.getName()), type);
    }

    @PostMapping("/cryptoData")
    public Mono<CryptoDataDto> createCryptoData(UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken,
                                                @RequestBody CreateCryptoDataRequest createCryptoDataRequest){
        return cryptoService.createCryptoData(Long.parseLong(usernamePasswordAuthenticationToken.getName()), createCryptoDataRequest);
    }

    @DeleteMapping("/cryptoData")
    public Mono<Void> deleteCryptoData(UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken,
                                       @RequestParam("id") long id){
        return cryptoService.deleteCryptoData(Long.parseLong(usernamePasswordAuthenticationToken.getName()), id);
    }

    @GetMapping("/cryptoData/generatePrivateKeyByType")
    public Mono<GenerateCryptoPrivateKeyResponse> generateCryptoPrivateKeyByType(UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken,
                                                                                 @RequestParam("type") CryptoDataType type){
        return cryptoService.generateCryptoPrivateKeyByType(type);
    }

    @PostMapping("/sendBtcTransaction")
    public Mono<Void> handleSendCryptoBtcTransaction(UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken,
                                            @RequestBody CreateCryptoBtcTransactionRequest createCryptoBtcTransactionRequest){

        return cryptoService.handleSendCryptoBtcTransaction(createCryptoBtcTransactionRequest);

    }
}
