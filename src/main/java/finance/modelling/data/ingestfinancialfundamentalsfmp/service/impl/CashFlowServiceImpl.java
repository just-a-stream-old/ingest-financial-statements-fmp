package finance.modelling.data.ingestfinancialfundamentalsfmp.service.impl;

import finance.modelling.data.ingestfinancialfundamentalsfmp.api.consumer.KafkaConsumerTickerImpl;
import finance.modelling.data.ingestfinancialfundamentalsfmp.client.FmpClient;
import finance.modelling.data.ingestfinancialfundamentalsfmp.publisher.impl.KafkaPublisherCashFlowImpl;
import finance.modelling.data.ingestfinancialfundamentalsfmp.service.contract.CashFlowService;
import finance.modelling.fmcommons.data.helper.client.FModellingClientHelper;
import finance.modelling.fmcommons.data.logging.LogClient;
import finance.modelling.fmcommons.data.logging.LogConsumer;
import finance.modelling.fmcommons.data.schema.fmp.dto.FmpCashFlowsDTO;
import finance.modelling.fmcommons.data.schema.fmp.dto.FmpTickerDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.time.Duration;

import static finance.modelling.fmcommons.data.logging.LogClient.buildResourcePath;
import static finance.modelling.fmcommons.data.logging.LogConsumer.determineTraceIdFromHeaders;

@Service
public class CashFlowServiceImpl implements CashFlowService {

    private final FModellingClientHelper fmHelper;
    private final KafkaConsumerTickerImpl kafkaConsumer;
    private final String inputTickerTopic;
    private final FmpClient fmpClient;
    private final KafkaPublisherCashFlowImpl kafkaPublisher;
    private final String outputCashFlowTopic;
    private final String fmpApiKey;
    private final String fmpBaseUrl;
    private final String cashFlowResourceUrl;
    private final String logResourcePath;
    private final Long requestDelayMs;

    public CashFlowServiceImpl(
            FModellingClientHelper fmHelper,
            KafkaConsumerTickerImpl kafkaConsumer,
            @Value("${kafka.bindings.publisher.fmp.fmpTickers}") String inputTickerTopic,
            FmpClient fmpClient,
            KafkaPublisherCashFlowImpl kafkaPublisher,
            @Value("${kafka.bindings.publisher.fmp.cashFlow}") String outputCashFlowTopic,
            @Value("${client.fmp.security.key}") String fmpApiKey,
            @Value("${client.fmp.baseUrl}") String fmpBaseUrl,
            @Value("${client.fmp.resource.cashFlow}") String cashFlowResourceUrl,
            @Value("${client.fmp.request.delay.ms}") Long requestDelayMs) {
        this.fmHelper = fmHelper;
        this.kafkaConsumer = kafkaConsumer;
        this.inputTickerTopic = inputTickerTopic;
        this.fmpClient = fmpClient;
        this.kafkaPublisher = kafkaPublisher;
        this.outputCashFlowTopic = outputCashFlowTopic;
        this.fmpApiKey = fmpApiKey;
        this.fmpBaseUrl = fmpBaseUrl;
        this.cashFlowResourceUrl = cashFlowResourceUrl;
        this.logResourcePath = buildResourcePath(fmpBaseUrl, cashFlowResourceUrl);
        this.requestDelayMs = requestDelayMs;
    }


    public void ingestAllQuarterlyCashFlows() {
        kafkaConsumer
                .receiveMessages(inputTickerTopic)
                .delayElements(Duration.ofMillis(requestDelayMs))
                .doOnNext(message -> ingestTickerQuarterlyCashFlows(message.value().getSymbol()))
                .subscribe(
                        message -> LogConsumer.logInfoDataItemConsumed(
                                FmpTickerDTO.class, inputTickerTopic, determineTraceIdFromHeaders(message.headers())),
                        error -> LogConsumer.logErrorFailedToConsumeDataItem(FmpTickerDTO.class, inputTickerTopic)
                );
    }

    public void ingestTickerQuarterlyCashFlows(String ticker) {
        fmpClient
                .getTickerQuarterlyCashFlows(buildQuarterlyCashFlowUri(ticker))
                .doOnNext(cashFlow -> kafkaPublisher.publishMessage(outputCashFlowTopic, cashFlow))
                .subscribe(
                        cashFlow -> LogClient.logInfoDataItemReceived(
                                cashFlow.getSymbol(), FmpCashFlowsDTO.class, logResourcePath),
                        error ->  fmHelper.respondToErrorType(ticker, FmpCashFlowsDTO.class, error, logResourcePath)
                );
    }

    private URI buildQuarterlyCashFlowUri(String ticker) {
        return UriComponentsBuilder.newInstance()
                .scheme("https")
                .host(fmpBaseUrl)
                .path(cashFlowResourceUrl.concat(ticker))
                .queryParam("period", "quarter")
                .queryParam("apikey", fmpApiKey)
                .build()
                .toUri();
    }
}
