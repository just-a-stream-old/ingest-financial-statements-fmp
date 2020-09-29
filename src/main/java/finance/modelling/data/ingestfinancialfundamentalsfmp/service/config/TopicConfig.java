package finance.modelling.data.ingestfinancialfundamentalsfmp.service.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class TopicConfig {

    private final String traceIdHeaderName;
    private final String tickerTopic;
    private final String balanceSheetTopic;
    private final String cashFlowTopic;
    private final String incomeStatementTopic;

    public TopicConfig(
            @Value("${spring.kafka.header.traceId}") String traceIdHeaderName,
            @Value("${kafka.bindings.publisher.fmp.fmpTickers}") String tickerTopic,
            @Value("${kafka.bindings.publisher.fmp.balanceSheet}") String balanceSheetTopic,
            @Value("${kafka.bindings.publisher.fmp.cashFlow}") String cashFlowTopic,
            @Value("${kafka.bindings.publisher.fmp.incomeStatement}") String incomeStatementTopic) {
        this.traceIdHeaderName = traceIdHeaderName;
        this.tickerTopic = tickerTopic;
        this.balanceSheetTopic = balanceSheetTopic;
        this.cashFlowTopic = cashFlowTopic;
        this.incomeStatementTopic = incomeStatementTopic;
    }
}
