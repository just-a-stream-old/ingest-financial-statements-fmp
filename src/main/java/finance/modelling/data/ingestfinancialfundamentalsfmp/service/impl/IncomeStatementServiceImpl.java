package finance.modelling.data.ingestfinancialfundamentalsfmp.service.impl;

import finance.modelling.data.ingestfinancialfundamentalsfmp.api.consumer.KafkaConsumerTickerImpl;
import finance.modelling.data.ingestfinancialfundamentalsfmp.client.FmpClient;
import finance.modelling.data.ingestfinancialfundamentalsfmp.publisher.impl.KafkaPublisherIncomeStatementImpl;
import finance.modelling.data.ingestfinancialfundamentalsfmp.service.config.FmpApiConfig;
import finance.modelling.data.ingestfinancialfundamentalsfmp.service.config.TopicConfig;
import finance.modelling.data.ingestfinancialfundamentalsfmp.service.contract.IncomeStatementService;
import finance.modelling.fmcommons.data.helper.client.FModellingClientHelper;
import finance.modelling.fmcommons.data.logging.LogClient;
import finance.modelling.fmcommons.data.logging.LogConsumer;
import finance.modelling.fmcommons.data.schema.fmp.dto.FmpIncomeStatementsDTO;
import finance.modelling.fmcommons.data.schema.fmp.dto.FmpTickerDTO;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;
import java.net.URI;

import static finance.modelling.fmcommons.data.logging.LogClient.buildResourcePath;
import static finance.modelling.fmcommons.data.logging.LogConsumer.determineTraceIdFromHeaders;

@Service
public class IncomeStatementServiceImpl implements IncomeStatementService {

    private final FModellingClientHelper fmHelper;
    private final TopicConfig topics;
    private final KafkaConsumerTickerImpl kafkaConsumer;
    private final FmpClient fmpClient;
    private final FmpApiConfig fmpApi;
    private final KafkaPublisherIncomeStatementImpl kafkaPublisher;
    private final String logResourcePath;

    public IncomeStatementServiceImpl(
            FModellingClientHelper fmHelper,
            TopicConfig topics,
            KafkaConsumerTickerImpl kafkaConsumer,
            FmpClient fmpClient,
            FmpApiConfig fmpApi,
            KafkaPublisherIncomeStatementImpl kafkaPublisher) {
        this.fmHelper = fmHelper;
        this.topics = topics;
        this.kafkaConsumer = kafkaConsumer;
        this.fmpClient = fmpClient;
        this.fmpApi = fmpApi;
        this.kafkaPublisher = kafkaPublisher;
        this.logResourcePath = buildResourcePath(fmpApi.getBaseUrl(), fmpApi.getIncomeStatementResourceUrl());
    }


    public void ingestAllQuarterlyIncomeStatements() {
        kafkaConsumer
                .receiveMessages(topics.getTickerTopic())
                .delayElements(fmpApi.getRequestDelayMs())
                .doOnNext(message -> ingestTickerQuarterlyIncomeStatements(message.value().getSymbol()))
                .subscribe(
                        message -> LogConsumer.logInfoDataItemConsumed(FmpTickerDTO.class, topics.getTickerTopic(),
                                determineTraceIdFromHeaders(message.headers(), topics.getTraceIdHeaderName())),
                        error -> LogConsumer.logErrorFailedToConsumeDataItem(FmpTickerDTO.class, topics.getTickerTopic())
                );
    }

    public void ingestTickerQuarterlyIncomeStatements(String symbol) {
        fmpClient
                .getTickerQuarterlyIncomeStatements(buildQuarterlyIncomeStatementUri(symbol))
                .doOnNext(incomeStatement -> kafkaPublisher.publishMessage(topics.getIncomeStatementTopic(), incomeStatement))
                .subscribe(
                        incomeStatement -> LogClient.logInfoDataItemReceived(
                                incomeStatement.getSymbol(), FmpIncomeStatementsDTO.class, logResourcePath),
                        error ->  fmHelper.respondToErrorType(symbol, FmpIncomeStatementsDTO.class, error, logResourcePath)
                );
    }

    private URI buildQuarterlyIncomeStatementUri(String symbol) {
        return UriComponentsBuilder.newInstance()
                .scheme("https")
                .host(fmpApi.getBaseUrl())
                .path(fmpApi.getIncomeStatementResourceUrl().concat(symbol))
                .queryParam("period", "quarter")
                .queryParam("apikey", fmpApi.getApiKey())
                .build()
                .toUri();
    }
}