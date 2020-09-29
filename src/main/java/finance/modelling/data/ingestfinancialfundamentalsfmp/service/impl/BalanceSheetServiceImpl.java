package finance.modelling.data.ingestfinancialfundamentalsfmp.service.impl;

import finance.modelling.data.ingestfinancialfundamentalsfmp.api.consumer.KafkaConsumerTickerImpl;
import finance.modelling.data.ingestfinancialfundamentalsfmp.client.FmpClient;
import finance.modelling.data.ingestfinancialfundamentalsfmp.publisher.impl.KafkaPublisherBalanceSheetImpl;
import finance.modelling.data.ingestfinancialfundamentalsfmp.service.config.FmpApiConfig;
import finance.modelling.data.ingestfinancialfundamentalsfmp.service.config.TopicConfig;
import finance.modelling.data.ingestfinancialfundamentalsfmp.service.contract.BalanceSheetService;
import finance.modelling.fmcommons.data.helper.client.FModellingClientHelper;
import finance.modelling.fmcommons.data.logging.LogClient;
import finance.modelling.fmcommons.data.logging.LogConsumer;
import finance.modelling.fmcommons.data.schema.fmp.dto.FmpBalanceSheetsDTO;
import finance.modelling.fmcommons.data.schema.fmp.dto.FmpTickerDTO;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;
import java.net.URI;

import static finance.modelling.fmcommons.data.logging.LogClient.buildResourcePath;
import static finance.modelling.fmcommons.data.logging.LogConsumer.determineTraceIdFromHeaders;

@Service
public class BalanceSheetServiceImpl implements BalanceSheetService {

    private final FModellingClientHelper fmHelper;
    private final TopicConfig topics;
    private final KafkaConsumerTickerImpl kafkaConsumer;
    private final FmpClient fmpClient;
    private final FmpApiConfig fmpApi;
    private final KafkaPublisherBalanceSheetImpl kafkaPublisher;
    private final String logResourcePath;

    public BalanceSheetServiceImpl(
            FModellingClientHelper fmHelper,
            TopicConfig topics,
            KafkaConsumerTickerImpl kafkaConsumer,
            FmpClient fmpClient,
            FmpApiConfig fmpApi, KafkaPublisherBalanceSheetImpl kafkaPublisher) {
        this.fmHelper = fmHelper;
        this.kafkaConsumer = kafkaConsumer;
        this.fmpClient = fmpClient;
        this.fmpApi = fmpApi;
        this.kafkaPublisher = kafkaPublisher;
        this.topics = topics;
        this.logResourcePath = buildResourcePath(fmpApi.getBaseUrl(), fmpApi.getBalanceSheetResourceUrl());
    }

    public void ingestAllQuarterlyBalanceSheets() {
        kafkaConsumer
                .receiveMessages(topics.getTickerTopic())
                .delayElements(fmpApi.getRequestDelayMs())
                .doOnNext(message -> ingestTickerQuarterlyBalanceSheets(message.value().getSymbol()))
                .subscribe(
                        message -> LogConsumer.logInfoDataItemConsumed(FmpTickerDTO.class, topics.getTickerTopic(),
                                determineTraceIdFromHeaders(message.headers(), topics.getTraceIdHeaderName())),
                        error -> LogConsumer.logErrorFailedToConsumeDataItem(FmpTickerDTO.class, topics.getTickerTopic())
                );
    }

    public void ingestTickerQuarterlyBalanceSheets(String symbol) {
        fmpClient
                .getTickerQuarterlyBalanceSheets(buildQuarterlyBalanceSheetUri(symbol))
                .doOnNext(balanceSheet -> kafkaPublisher.publishMessage(topics.getBalanceSheetTopic(), balanceSheet))
                .subscribe(
                        balanceSheet -> LogClient.logInfoDataItemReceived(
                                balanceSheet.getSymbol(), FmpBalanceSheetsDTO.class, logResourcePath),
                        error ->  fmHelper.respondToErrorType(symbol, FmpBalanceSheetsDTO.class, error, logResourcePath)
                );
    }

    private URI buildQuarterlyBalanceSheetUri(String symbol) {
        return UriComponentsBuilder.newInstance()
                .scheme("https")
                .host(fmpApi.getBaseUrl())
                .path(fmpApi.getBalanceSheetResourceUrl().concat(symbol))
                .queryParam("period", "quarter")
                .queryParam("apikey", fmpApi.getApiKey())
                .build()
                .toUri();
    }
}