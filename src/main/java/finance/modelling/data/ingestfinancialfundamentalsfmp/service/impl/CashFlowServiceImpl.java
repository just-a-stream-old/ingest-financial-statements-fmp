package finance.modelling.data.ingestfinancialfundamentalsfmp.service.impl;

import finance.modelling.data.ingestfinancialfundamentalsfmp.api.consumer.KafkaConsumerTickerImpl;
import finance.modelling.data.ingestfinancialfundamentalsfmp.client.FmpClient;
import finance.modelling.data.ingestfinancialfundamentalsfmp.publisher.impl.KafkaPublisherCashFlowImpl;
import finance.modelling.data.ingestfinancialfundamentalsfmp.service.config.FmpApiConfig;
import finance.modelling.data.ingestfinancialfundamentalsfmp.service.config.TopicConfig;
import finance.modelling.data.ingestfinancialfundamentalsfmp.service.contract.CashFlowService;
import finance.modelling.fmcommons.data.helper.client.FModellingClientHelper;
import finance.modelling.fmcommons.data.logging.LogClient;
import finance.modelling.fmcommons.data.logging.LogConsumer;
import finance.modelling.fmcommons.data.schema.fmp.dto.FmpCashFlowsDTO;
import finance.modelling.fmcommons.data.schema.fmp.dto.FmpTickerDTO;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;
import java.net.URI;

import static finance.modelling.fmcommons.data.logging.LogClient.buildResourcePath;
import static finance.modelling.fmcommons.data.logging.LogConsumer.determineTraceIdFromHeaders;

@Service
public class CashFlowServiceImpl implements CashFlowService {

    private final FModellingClientHelper fmHelper;
    private final TopicConfig topics;
    private final KafkaConsumerTickerImpl kafkaConsumer;
    private final FmpClient fmpClient;
    private final FmpApiConfig fmpApi;
    private final KafkaPublisherCashFlowImpl kafkaPublisher;
    private final String logResourcePath;

    public CashFlowServiceImpl(
            FModellingClientHelper fmHelper,
            TopicConfig topics,
            KafkaConsumerTickerImpl kafkaConsumer,
            FmpClient fmpClient,
            FmpApiConfig fmpApi,
            KafkaPublisherCashFlowImpl kafkaPublisher) {
        this.fmHelper = fmHelper;
        this.topics = topics;
        this.kafkaConsumer = kafkaConsumer;
        this.fmpClient = fmpClient;
        this.fmpApi = fmpApi;
        this.kafkaPublisher = kafkaPublisher;
        this.logResourcePath = buildResourcePath(fmpApi.getBaseUrl(), fmpApi.getCashFlowResourceUrl());
    }


    public void ingestAllQuarterlyCashFlows() {
        kafkaConsumer
                .receiveMessages(topics.getTickerTopic())
                .delayElements(fmpApi.getRequestDelayMs())
                .doOnNext(message -> ingestTickerQuarterlyCashFlows(message.value().getSymbol()))
                .subscribe(
                        message -> LogConsumer.logInfoDataItemConsumed(FmpTickerDTO.class, topics.getTickerTopic(),
                                determineTraceIdFromHeaders(message.headers(), topics.getTraceIdHeaderName())),
                        error -> LogConsumer.logErrorFailedToConsumeDataItem(FmpTickerDTO.class, topics.getTickerTopic())
                );
    }

    public void ingestTickerQuarterlyCashFlows(String symbol) {
        fmpClient
                .getTickerQuarterlyCashFlows(buildQuarterlyCashFlowUri(symbol))
                .doOnNext(cashFlow -> kafkaPublisher.publishMessage(topics.getCashFlowTopic(), cashFlow))
                .subscribe(
                        cashFlow -> LogClient.logInfoDataItemReceived(
                                cashFlow.getSymbol(), FmpCashFlowsDTO.class, logResourcePath),
                        error ->  fmHelper.respondToErrorType(symbol, FmpCashFlowsDTO.class, error, logResourcePath)
                );
    }

    private URI buildQuarterlyCashFlowUri(String symbol) {
        return UriComponentsBuilder.newInstance()
                .scheme("https")
                .host(fmpApi.getBaseUrl())
                .path(fmpApi.getCashFlowResourceUrl().concat(symbol))
                .queryParam("period", "quarter")
                .queryParam("apikey", fmpApi.getApiKey())
                .build()
                .toUri();
    }
}