package finance.modelling.data.ingestfinancialfundamentalsfmp.client;

import finance.modelling.data.ingestfinancialfundamentalsfmp.client.dto.FmpBalanceSheetsDTO;
import finance.modelling.data.ingestfinancialfundamentalsfmp.client.dto.FmpCashFlowsDTO;
import finance.modelling.data.ingestfinancialfundamentalsfmp.client.dto.FmpIncomeStatementsDTO;
import finance.modelling.data.ingestfinancialfundamentalsfmp.client.dto.FmpTickerDTO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;

public interface FmpClient {
    Flux<FmpTickerDTO> getAllCompanyTickers(URI resourceUri);
    Mono<FmpIncomeStatementsDTO> getTickerQuarterlyIncomeStatements(URI resourceUri);
    Mono<FmpBalanceSheetsDTO> getTickerQuarterlyBalanceSheets(URI resourceUri);
    Mono<FmpCashFlowsDTO> getTickerQuarterlyCashFlows(URI resourceUri);
}
