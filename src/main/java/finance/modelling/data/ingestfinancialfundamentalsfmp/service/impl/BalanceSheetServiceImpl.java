package finance.modelling.data.ingestfinancialfundamentalsfmp.service.impl;

import finance.modelling.data.ingestfinancialfundamentalsfmp.api.consumer.KafkaConsumerTickerImpl;
import finance.modelling.data.ingestfinancialfundamentalsfmp.client.FmpClient;
import finance.modelling.data.ingestfinancialfundamentalsfmp.publisher.impl.KafkaPublisherBalanceSheetImpl;
import finance.modelling.data.ingestfinancialfundamentalsfmp.service.contract.BalanceSheetService;
import finance.modelling.fmcommons.data.helper.client.FModellingClientHelper;
import finance.modelling.fmcommons.data.logging.LogClient;
import finance.modelling.fmcommons.data.logging.LogConsumer;
import finance.modelling.fmcommons.data.schema.fmp.dto.FmpBalanceSheetsDTO;
import finance.modelling.fmcommons.data.schema.fmp.dto.FmpTickerDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.time.Duration;

import static finance.modelling.fmcommons.data.logging.LogClient.buildResourcePath;
import static finance.modelling.fmcommons.data.logging.LogConsumer.determineTraceIdFromHeaders;

@Service
public class BalanceSheetServiceImpl implements BalanceSheetService {

    private final FModellingClientHelper fmHelper;
    private final KafkaConsumerTickerImpl kafkaConsumer;
    private final String inputTickerTopic;
    private final FmpClient fmpClient;
    private final KafkaPublisherBalanceSheetImpl kafkaPublisher;
    private final String outputBalanceSheetTopic;
    private final String fmpApiKey;
    private final String fmpBaseUrl;
    private final String balanceSheetResourceUrl;
    private final String logResourcePath;
    private final Long requestDelayMs;

    public BalanceSheetServiceImpl(
            FModellingClientHelper fmHelper,
            KafkaConsumerTickerImpl kafkaConsumer,
            @Value("${kafka.bindings.publisher.fmp.fmpTickers}") String inputTickerTopic,
            FmpClient fmpClient,
            KafkaPublisherBalanceSheetImpl kafkaPublisher,
            @Value("${kafka.bindings.publisher.fmp.balanceSheet}") String outputBalanceSheetTopic,
            @Value("${client.fmp.security.key}") String fmpApiKey,
            @Value("${client.fmp.baseUrl}") String fmpBaseUrl,
            @Value("${client.fmp.resource.balanceSheet}") String balanceSheetResourceUrl,
            @Value("${client.fmp.request.delay.ms}") Long requestDelayMs) {
        this.fmHelper = fmHelper;
        this.kafkaConsumer = kafkaConsumer;
        this.inputTickerTopic = inputTickerTopic;
        this.fmpClient = fmpClient;
        this.kafkaPublisher = kafkaPublisher;
        this.outputBalanceSheetTopic = outputBalanceSheetTopic;
        this.fmpApiKey = fmpApiKey;
        this.fmpBaseUrl = fmpBaseUrl;
        this.balanceSheetResourceUrl = balanceSheetResourceUrl;
        this.logResourcePath = buildResourcePath(fmpBaseUrl, balanceSheetResourceUrl);
        this.requestDelayMs = requestDelayMs;
    }

    public void ingestAllQuarterlyBalanceSheets() {
        kafkaConsumer
                .receiveMessages(inputTickerTopic)
                .delayElements(Duration.ofMillis(requestDelayMs))
                .doOnNext(message -> ingestTickerQuarterlyBalanceSheets(message.value().getSymbol()))
                .subscribe(
                        message -> LogConsumer.logInfoDataItemConsumed(
                                FmpTickerDTO.class, inputTickerTopic, determineTraceIdFromHeaders(message.headers())),
                        error -> LogConsumer.logErrorFailedToConsumeDataItem(FmpTickerDTO.class, inputTickerTopic)
                );
    }

    public void ingestTickerQuarterlyBalanceSheets(String ticker) {
        fmpClient
                .getTickerQuarterlyBalanceSheets(buildQuarterlyBalanceSheetUri(ticker))
                .doOnNext(balanceSheet -> kafkaPublisher.publishMessage(outputBalanceSheetTopic, balanceSheet))
                .subscribe(
                        balanceSheet -> LogClient.logInfoDataItemReceived(
                                balanceSheet.getSymbol(), FmpBalanceSheetsDTO.class, logResourcePath),
                        error ->  fmHelper.respondToErrorType(ticker, FmpBalanceSheetsDTO.class, error, logResourcePath)
                );
    }

    private URI buildQuarterlyBalanceSheetUri(String ticker) {
        return UriComponentsBuilder.newInstance()
                .scheme("https")
                .host(fmpBaseUrl)
                .path(balanceSheetResourceUrl.concat(ticker))
                .queryParam("period", "quarter")
                .queryParam("apikey", fmpApiKey)
                .build()
                .toUri();
    }
}
