package finance.modelling.data.ingestfinancialfundamentalsfmp;

import finance.modelling.data.ingestfinancialfundamentalsfmp.api.consumer.KafkaConsumerTickerImpl;
import finance.modelling.data.ingestfinancialfundamentalsfmp.service.impl.BalanceSheetServiceImpl;
import finance.modelling.data.ingestfinancialfundamentalsfmp.service.impl.CashFlowServiceImpl;
import finance.modelling.data.ingestfinancialfundamentalsfmp.service.impl.IncomeStatementServiceImpl;
import finance.modelling.fmcommons.data.logging.LogConsumer;
import finance.modelling.fmcommons.data.schema.fmp.dto.FmpTickerDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import reactor.kafka.receiver.ReceiverRecord;

import javax.annotation.PostConstruct;

import java.time.Duration;

import static finance.modelling.fmcommons.data.logging.LogConsumer.determineTraceIdFromHeaders;

@SpringBootApplication
public class IngestFinancialFundamentalsFmpApplication {

	@Autowired KafkaConsumerTickerImpl kafkaConsumerTicker;
	@Value("${kafka.bindings.publisher.fmp.fmpTickers}") String inputTickerTopic;
	@Autowired BalanceSheetServiceImpl balanceSheetService;
	@Autowired CashFlowServiceImpl cashFlowService;
	@Autowired IncomeStatementServiceImpl incomeStatementService;

	public static void main(String[] args) {
		SpringApplication.run(IngestFinancialFundamentalsFmpApplication.class, args);
	}

	// Todo: Add fmp client configuration to group together related @Values injected into services
	// Todo: Create 'AllFinancialStatementsService' that does the below more cleanly - calls other services
	// Todo: Send certain failures to DLQ or implement a stateful retry schedule

	@PostConstruct
	void run() {
		kafkaConsumerTicker
				.receiveMessages(inputTickerTopic)
				.delayElements(Duration.ofMillis(330))
				.doOnNext(this::consumeAllFmpFinancialStatements)
				.subscribe(
						message -> LogConsumer.logInfoDataItemConsumed(
								FmpTickerDTO.class, inputTickerTopic, determineTraceIdFromHeaders(message.headers())),
						error -> LogConsumer.logErrorFailedToConsumeDataItem(FmpTickerDTO.class, inputTickerTopic)
				);
	}

	private void consumeAllFmpFinancialStatements(ReceiverRecord<String, FmpTickerDTO> tickerMessage) {
		balanceSheetService.ingestTickerQuarterlyBalanceSheets(tickerMessage.value().getSymbol());
		cashFlowService.ingestTickerQuarterlyCashFlows(tickerMessage.value().getSymbol());
		incomeStatementService.ingestTickerQuarterlyIncomeStatements(tickerMessage.value().getSymbol());
	}

}
