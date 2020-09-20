package finance.modelling.data.ingestfinancialfundamentalsfmp.service.contract;

public interface CashFlowService {
    void ingestAllQuarterlyCashFlows();
    void ingestTickerQuarterlyCashFlows(String ticker);
}
