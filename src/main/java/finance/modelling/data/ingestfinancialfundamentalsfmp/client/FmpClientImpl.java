package finance.modelling.data.ingestfinancialfundamentalsfmp.client;

import finance.modelling.data.ingestfinancialfundamentalsfmp.client.mapper.FmpFundamentalsMapper;
import finance.modelling.fmcommons.data.helper.client.FModellingClientHelper;
import finance.modelling.fmcommons.data.schema.fmp.dto.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.net.URI;
import java.time.Duration;

@Component
@Slf4j
public class FmpClientImpl implements FmpClient {

    private final WebClient client;
    private final FModellingClientHelper fmHelper;

    public FmpClientImpl(WebClient client, FModellingClientHelper fmHelper) {
        this.client = client;
        this.fmHelper = fmHelper;
    }

    public Flux<FmpTickerDTO> getAllCompanyTickers(URI resourceUri) {
        return client
                .get()
                .uri(resourceUri)
                .retrieve()
                .bodyToFlux(FmpTickerDTO.class)
                .onErrorMap(fmHelper::returnTechnicalException)
                .retryWhen(getRetry());
    }

    public Mono<FmpIncomeStatementsDTO> getTickerQuarterlyIncomeStatements(URI resourceUri) {
        return client
                .get()
                .uri(resourceUri)
                .retrieve()
                .bodyToFlux(FmpIncomeStatementDTO.class)
                .collectList()
                .map(FmpFundamentalsMapper::mapIncomeStatementDTOListToIncomeStatementsDTO)
                .onErrorMap(fmHelper::returnTechnicalException)
                .retryWhen(getRetry());
    }

    public Mono<FmpBalanceSheetsDTO> getTickerQuarterlyBalanceSheets(URI resourceUri) {
        return client
                .get()
                .uri(resourceUri)
                .retrieve()
                .bodyToFlux(FmpBalanceSheetDTO.class)
                .collectList()
                .map(FmpFundamentalsMapper::mapBalanceSheetDTOListToBalanceSheetsDTO)
                .onErrorMap(fmHelper::returnTechnicalException)
                .retryWhen(getRetry());
    }

    public Mono<FmpCashFlowsDTO> getTickerQuarterlyCashFlows(URI resourceUri) {
        return client
                .get()
                .uri(resourceUri)
                .retrieve()
                .bodyToFlux(FmpCashFlowDTO.class)
                .collectList()
                .map(FmpFundamentalsMapper::mapCashFlowDTOListToCashFlowsDTO)
                .onErrorMap(fmHelper::returnTechnicalException)
                .retryWhen(getRetry());
    }

    protected Retry getRetry() {
        return Retry
                .backoff(10, Duration.ofMillis(200))
                .filter(fmHelper::isRetryableException);
    }
}
