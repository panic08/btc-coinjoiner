package ru.marthastudios.coinjoiner.service.implement;

import lombok.RequiredArgsConstructor;
import org.bitcoinj.core.*;
import org.bitcoinj.params.MainNetParams;
import org.bitcoinj.script.ScriptBuilder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.marthastudios.coinjoiner.dto.CryptoDataDto;
import ru.marthastudios.coinjoiner.dto.blockchain.BlockchainBtcAddressOutputDataDto;
import ru.marthastudios.coinjoiner.enums.CryptoDataType;
import ru.marthastudios.coinjoiner.mapper.CryptoDataToCryptoDataDtoMapperImpl;
import ru.marthastudios.coinjoiner.model.CryptoData;
import ru.marthastudios.coinjoiner.payload.CreateCryptoDataRequest;
import ru.marthastudios.coinjoiner.payload.CreateCryptoBtcTransactionRequest;
import ru.marthastudios.coinjoiner.payload.GenerateCryptoPrivateKeyResponse;
import ru.marthastudios.coinjoiner.pojo.AddressAmount;
import ru.marthastudios.coinjoiner.pojo.CryptoTransaction;
import ru.marthastudios.coinjoiner.pojo.HashDuty;
import ru.marthastudios.coinjoiner.repository.CryptoDataRepository;
import ru.marthastudios.coinjoiner.service.CryptoService;

import java.math.BigInteger;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

@Service
@RequiredArgsConstructor
public class CryptoServiceImpl implements CryptoService {

    private final CryptoDataRepository cryptoDataRepository;
    private final CryptoDataToCryptoDataDtoMapperImpl cryptoDataToCryptoDataDtoMapper;

    private static final String BLOCKCHAIN_URL = "https://blockchain.info";
    private static final String WASSABI_URL = "https://wasabiwallet.io/api/v4/btc/Blockchain/broadcast";

    @Override
    @Transactional
    public Mono<CryptoDataDto> createCryptoData(long principalId, CreateCryptoDataRequest createCryptoDataRequest) {
        return Mono.fromSupplier(() ->  {
            CryptoData cryptoData = new CryptoData();

            cryptoData.setUserId(principalId);
            cryptoData.setPrivateKey(createCryptoDataRequest.getPrivateKey());
            cryptoData.setType(createCryptoDataRequest.getType());

            switch (createCryptoDataRequest.getType()){
                case BTC -> {
                    ECKey privateKey = ECKey.fromPrivate(Utils.HEX.decode(createCryptoDataRequest.getPrivateKey()));
                    SegwitAddress address = SegwitAddress.fromKey(MainNetParams.get(), privateKey);

                    cryptoData.setAddress(address.toBech32());
                }
            }

            return cryptoData;
        }).flatMap(cryptoDataRepository::save)
                .map(cryptoDataToCryptoDataDtoMapper::cryptoDataToCryptoDataDto);
    }

    @Override
    public Flux<CryptoDataDto> getAllCryptoDataByType(long principalId, CryptoDataType type) {
        return cryptoDataRepository.findAllByUserIdAndType(principalId, type)
                .map(cryptoDataToCryptoDataDtoMapper::cryptoDataToCryptoDataDto);

    }

    @Override
    public Mono<Void> deleteCryptoData(long principalId, long cryptoDataId) {
        return Mono.defer(() -> cryptoDataRepository.deleteById(cryptoDataId));
    }

    @Override
    public Mono<GenerateCryptoPrivateKeyResponse> generateCryptoPrivateKeyByType(CryptoDataType type) {
        return Mono.fromCallable(() -> {
            switch (type){
                case BTC -> {
                    ECKey ecKey = new ECKey();

                    return new GenerateCryptoPrivateKeyResponse(CryptoDataType.BTC, ecKey.getPrivateKeyAsHex());
                }
            }

            return null;

        });
    }

    @Override
    public Mono<Void> handleSendCryptoBtcTransaction(CreateCryptoBtcTransactionRequest createCryptoTransactionRequest) {
        List<CryptoTransaction> cryptoTransactions = new ArrayList<>();

        return Flux.fromArray(createCryptoTransactionRequest.getInputs())
                .flatMap(input -> {
                    return cryptoDataRepository.findById(input.getCryptoDataId())
                            .flatMapMany(cryptoData -> {

                                HashMap<String, List<HashDuty>> addressHashDutyList = new HashMap<>();

                                return Flux.fromArray(input.getKeys())
                                        .flatMap(key -> {

                                            return Flux.fromArray(createCryptoTransactionRequest.getOuts())
                                                    .flatMap(out -> {
                                                        AtomicLong amount = new AtomicLong();
                                                        AtomicBoolean endSignal = new AtomicBoolean(false);

                                                        if (key == out.getKey()) {
                                                            return getBtcAddressOutputDataByAddress(cryptoData.getAddress())
                                                                    .flatMapMany(blockchainBtcAddressOutputDataDto -> {


                                                                        return Flux.fromArray(blockchainBtcAddressOutputDataDto.getUnspentOutputs())
                                                                                .takeWhile(s -> !endSignal.get())
                                                                                .flatMap(output -> {

                                                                                    if (addressHashDutyList.get(cryptoData.getAddress()) == null) {
                                                                                        List<HashDuty> hashDutyList = new ArrayList<>();


                                                                                        amount.getAndAdd(output.getValue());

                                                                                        hashDutyList.add(new HashDuty(output.getTxHashBigEndian(),
                                                                                                (long) -(output.getValue() - (output.getValue() - out.getAmount()*1e8))));

                                                                                        addressHashDutyList.put(cryptoData.getAddress(), hashDutyList);

                                                                                        if (amount.get() >= out.getAmount() * 1e8 + 1000) {
                                                                                            CryptoTransaction cryptoTransaction = new CryptoTransaction();

                                                                                            cryptoTransaction.setHash(output.getTxHashBigEndian());
                                                                                            cryptoTransaction.setAmount(out.getAmount());
                                                                                            cryptoTransaction.setToAddress(out.getAddress());
                                                                                            cryptoTransaction.setOutputN(output.getTxOutputN());
                                                                                            cryptoTransaction.setPrivateKey(cryptoData.getPrivateKey());


                                                                                            cryptoTransactions.add(cryptoTransaction);

                                                                                            endSignal.set(true);

                                                                                            return Flux.empty();
                                                                                        }

                                                                                    } else {
                                                                                        return Flux.fromIterable(addressHashDutyList.get(cryptoData.getAddress()))
                                                                                                .flatMap(addressHashDuty -> {

                                                                                                    if (output.getValue() + addressHashDuty.getDuty() >= 0) {
                                                                                                        amount.addAndGet(output.getValue() + addressHashDuty.getDuty());

                                                                                                        addressHashDuty.setDuty((long) (-out.getAmount()*1e8) + addressHashDuty.getDuty());
                                                                                                    }

                                                                                                    if (amount.get() >= out.getAmount() * 1e8 + 1000) {
                                                                                                        CryptoTransaction cryptoTransaction = new CryptoTransaction();

                                                                                                        cryptoTransaction.setHash(output.getTxHashBigEndian());
                                                                                                        cryptoTransaction.setAmount(out.getAmount());
                                                                                                        cryptoTransaction.setToAddress(out.getAddress());
                                                                                                        cryptoTransaction.setOutputN(output.getTxOutputN());
                                                                                                        cryptoTransaction.setPrivateKey(cryptoData.getPrivateKey());

                                                                                                        cryptoTransactions.add(cryptoTransaction);
                                                                                                        endSignal.set(true);
                                                                                                        return Flux.empty();
                                                                                                    }

                                                                                                    return Flux.empty();
                                                                                                });
                                                                                    }

                                                                                    return Flux.empty();

                                                                                });
                                                                    });
                                                        }

                                                        return Flux.empty();
                                                    });
                                        });
                            }).cache();
                }).then().then(Mono.defer(() -> {
                    MainNetParams params = new MainNetParams();
                    Transaction transaction = new Transaction(params);

                    AtomicInteger iteration = new AtomicInteger(0);
                    HashMap<String, List<String>> privateKeyHashesMap = new HashMap<>();
                    List<AddressAmount> outAddressesAmountList = new ArrayList<>();

                    return Flux.fromIterable(cryptoTransactions)
                            .flatMap(cryptoTransaction -> {
                                ECKey ecKey = ECKey.fromPrivate(new BigInteger(cryptoTransaction.getPrivateKey(), 16));

                                Address recipientAddress = Address.fromString(params, cryptoTransaction.getToAddress());

                                Coin amountToSend = Coin.parseCoin(String.valueOf(cryptoTransaction.getAmount()));

                                privateKeyHashesMap.computeIfAbsent(cryptoTransaction.getPrivateKey(), k -> new ArrayList<>());

                                if (!privateKeyHashesMap.get(cryptoTransaction.getPrivateKey()).contains(cryptoTransaction.getHash())){

                                    Sha256Hash sha256Hash = Sha256Hash.wrap(cryptoTransaction.getHash());
                                    TransactionOutPoint transactionOutPoint = new TransactionOutPoint(params, cryptoTransaction.getOutputN(), sha256Hash);
                                    TransactionInput transactionInput = new TransactionInput(params, transaction, new byte[]{}, transactionOutPoint);


                                    transaction.addInput(transactionInput);
                                    transaction.getInput(iteration.get()).setScriptSig(ScriptBuilder.createInputScript(null, ecKey));

                                    List<String> hashes = privateKeyHashesMap.get(cryptoTransaction.getPrivateKey());

                                    hashes.add(cryptoTransaction.getHash());

                                    privateKeyHashesMap.put(cryptoTransaction.getPrivateKey(), hashes);

                                    iteration.incrementAndGet();
                                }


                                if (!outAddressesAmountList.contains(new AddressAmount(
                                        cryptoTransaction.getToAddress(),
                                        cryptoTransaction.getAmount()))){

                                    TransactionOutput transactionOutput = new TransactionOutput(params, transaction, amountToSend, recipientAddress);

                                    transaction.addOutput(transactionOutput);

                                    outAddressesAmountList.add(new AddressAmount(cryptoTransaction.getToAddress(), cryptoTransaction.getAmount()));
                                }


                                return Flux.empty();
                            }).then().then(Mono.defer(() -> {

                                return sendBtcTransaction(Utils.HEX.encode(transaction.bitcoinSerialize()));
                            })).cache().then();
                }));

    }

    private Mono<BlockchainBtcAddressOutputDataDto> getBtcAddressOutputDataByAddress(String address){
        return WebClient.builder().baseUrl(BLOCKCHAIN_URL + "/unspent?active=" + address)
                .build()
                .get()
                .retrieve()
                .bodyToMono(BlockchainBtcAddressOutputDataDto.class);
    }

    private Mono<Void> sendBtcTransaction(String serializeTransaction){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        return WebClient.builder().baseUrl(WASSABI_URL)
                .build()
                .post()
                .headers(httpHeaders -> httpHeaders.addAll(headers))
                .bodyValue(serializeTransaction)
                .retrieve()
                .bodyToMono(Void.class)
                .cache();
    }
}
