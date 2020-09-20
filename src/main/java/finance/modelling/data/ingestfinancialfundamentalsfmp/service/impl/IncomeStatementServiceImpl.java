package finance.modelling.data.ingestfinancialfundamentalsfmp.service.impl;

import finance.modelling.data.ingestfinancialfundamentalsfmp.api.consumer.KafkaConsumerTickerImpl;
import finance.modelling.data.ingestfinancialfundamentalsfmp.client.FmpClient;
import finance.modelling.data.ingestfinancialfundamentalsfmp.publisher.impl.KafkaPublisherIncomeStatementImpl;
import finance.modelling.data.ingestfinancialfundamentalsfmp.service.contract.IncomeStatementService;
import finance.modelling.fmcommons.data.helper.client.FModellingClientHelper;
import finance.modelling.fmcommons.data.logging.LogClient;
import finance.modelling.fmcommons.data.logging.LogConsumer;
import finance.modelling.fmcommons.data.schema.fmp.dto.FmpIncomeStatementsDTO;
import finance.modelling.fmcommons.data.schema.fmp.dto.FmpTickerDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.time.Duration;

import static finance.modelling.fmcommons.data.logging.LogClient.buildResourcePath;
import static finance.modelling.fmcommons.data.logging.LogConsumer.determineTraceIdFromHeaders;

@Service
public class IncomeStatementServiceImpl implements IncomeStatementService {

    private final FModellingClientHelper fmHelper;
    private final KafkaConsumerTickerImpl kafkaConsumer;
    private final String inputTickerTopic;
    private final FmpClient fmpClient;
    private final KafkaPublisherIncomeStatementImpl kafkaPublisher;
    private final String outputIncomeStatementTopic;
    private final String fmpApiKey;
    private final String fmpBaseUrl;
    private final String incomeStatementResourceUrl;
    private final String logResourcePath;
    private final Long requestDelayMs;

    public IncomeStatementServiceImpl(
            FModellingClientHelper fmHelper,
            KafkaConsumerTickerImpl kafkaConsumer,
            @Value("${kafka.bindings.publisher.fmp.fmpTickers}") String inputTickerTopic,
            FmpClient fmpClient,
            KafkaPublisherIncomeStatementImpl kafkaPublisher,
            @Value("${kafka.bindings.publisher.fmp.incomeStatement}") String outputIncomeStatementTopic,
            @Value("${client.fmp.security.key}") String fmpApiKey,
            @Value("${client.fmp.baseUrl}") String fmpBaseUrl,
            @Value("${client.fmp.resource.incomeStatement}") String incomeStatementResourceUrl,
            @Value("${client.fmp.request.delay.ms}") Long requestDelayMs) {
        this.fmHelper = fmHelper;
        this.kafkaConsumer = kafkaConsumer;
        this.inputTickerTopic = inputTickerTopic;
        this.fmpClient = fmpClient;
        this.kafkaPublisher = kafkaPublisher;
        this.outputIncomeStatementTopic = outputIncomeStatementTopic;
        this.fmpApiKey = fmpApiKey;
        this.fmpBaseUrl = fmpBaseUrl;
        this.incomeStatementResourceUrl = incomeStatementResourceUrl;
        this.logResourcePath = buildResourcePath(fmpBaseUrl, incomeStatementResourceUrl);
        this.requestDelayMs = requestDelayMs;
    }


    public void ingestAllQuarterlyIncomeStatements() {
        kafkaConsumer
                .receiveMessages(inputTickerTopic)
                .delayElements(Duration.ofMillis(requestDelayMs))
                .doOnNext(message -> ingestTickerQuarterlyIncomeStatements(message.value().getSymbol()))
                .subscribe(
                        message -> LogConsumer.logInfoDataItemConsumed(
                                FmpTickerDTO.class, inputTickerTopic, determineTraceIdFromHeaders(message.headers())),
                        error -> LogConsumer.logErrorFailedToConsumeDataItem(FmpTickerDTO.class, inputTickerTopic)
                );
    }

    public void ingestTickerQuarterlyIncomeStatements(String symbol) {
        fmpClient
                .getTickerQuarterlyIncomeStatements(buildQuarterlyIncomeStatementUri(symbol))
                .doOnNext(incomeStatement -> kafkaPublisher.publishMessage(outputIncomeStatementTopic, incomeStatement))
                .subscribe(
                        incomeStatement -> LogClient.logInfoDataItemReceived(
                                incomeStatement.getSymbol(), FmpIncomeStatementsDTO.class, logResourcePath),
                        error ->  fmHelper.respondToErrorType(symbol, FmpIncomeStatementsDTO.class, error, logResourcePath)
                );
    }

    private URI buildQuarterlyIncomeStatementUri(String symbol) {
        return UriComponentsBuilder.newInstance()
                .scheme("https")
                .host(fmpBaseUrl)
                .path(incomeStatementResourceUrl.concat(symbol))
                .queryParam("period", "quarter")
                .queryParam("apikey", fmpApiKey)
                .build()
                .toUri();
    }
}
