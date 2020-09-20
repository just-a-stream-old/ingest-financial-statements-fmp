package finance.modelling.data.ingestfinancialfundamentalsfmp.service.contract;

public interface IncomeStatementService {
    void ingestAllQuarterlyIncomeStatements();
    void ingestTickerQuarterlyIncomeStatements(String ticker);
}
