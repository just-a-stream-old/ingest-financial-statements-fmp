package finance.modelling.data.ingestfinancialfundamentalsfmp;

import finance.modelling.data.ingestfinancialfundamentalsfmp.service.contract.BalanceSheetService;
import finance.modelling.data.ingestfinancialfundamentalsfmp.service.contract.CashFlowService;
import finance.modelling.data.ingestfinancialfundamentalsfmp.service.contract.IncomeStatementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;

@SpringBootApplication
public class IngestFinancialFundamentalsFmpApplication {

	@Autowired BalanceSheetService balanceSheetService;
	@Autowired CashFlowService cashFlowService;
	@Autowired IncomeStatementService incomeStatementService;

	public static void main(String[] args) {
		SpringApplication.run(IngestFinancialFundamentalsFmpApplication.class, args);
	}

	@PostConstruct
	void run() {
		balanceSheetService.ingestAllQuarterlyBalanceSheets();
		cashFlowService.ingestAllQuarterlyCashFlows();
		incomeStatementService.ingestAllQuarterlyIncomeStatements();
	}

}
