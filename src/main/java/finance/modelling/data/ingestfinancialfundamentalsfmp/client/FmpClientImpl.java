package finance.modelling.data.ingestfinancialfundamentalsfmp.client;

import finance.modelling.data.ingestfinancialfundamentalsfmp.client.dto.*;
import finance.modelling.data.ingestfinancialfundamentalsfmp.client.mapper.FmpFundamentalsMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;

@Component
@Slf4j
public class FmpClientImpl implements FmpClient {

    private final WebClient client;

    public FmpClientImpl(WebClient client) {
        this.client = client;
    }

    public Flux<FmpTickerDTO> getAllCompanyTickers(URI resourceUri) {
        return client
                .get()
                .uri(resourceUri)
                .retrieve()
                .bodyToFlux(FmpTickerDTO.class);
    }

    public Mono<FmpIncomeStatementsDTO> getTickerQuarterlyIncomeStatements(URI resourceUri) {
        return client
                .get()
                .uri(resourceUri)
                .retrieve()
                .bodyToFlux(FmpIncomeStatementDTO.class)
                .collectList()
                .map(FmpFundamentalsMapper::mapIncomeStatementDTOListToIncomeStatementsDTO);
    }

    public Mono<FmpBalanceSheetsDTO> getTickerQuarterlyBalanceSheets(URI resourceUri) {
        return client
                .get()
                .uri(resourceUri)
                .retrieve()
                .bodyToFlux(FmpBalanceSheetDTO.class)
                .collectList()
                .map(FmpFundamentalsMapper::mapBalanceSheetDTOListToBalanceSheetsDTO);
    }

    public Mono<FmpCashFlowsDTO> getTickerQuarterlyCashFlows(URI resourceUri) {
        return client
                .get()
                .uri(resourceUri)
                .retrieve()
                .bodyToFlux(FmpCashFlowDTO.class)
                .collectList()
                .map(FmpFundamentalsMapper::mapCashFlowDTOListToCashFlowsDTO);
    }
}
