package finance.modelling.data.ingestfinancialfundamentalsfmp.service.contract;

public interface BalanceSheetService {
    void ingestAllQuarterlyBalanceSheets();
    void ingestTickerQuarterlyBalanceSheets(String ticker);
}
