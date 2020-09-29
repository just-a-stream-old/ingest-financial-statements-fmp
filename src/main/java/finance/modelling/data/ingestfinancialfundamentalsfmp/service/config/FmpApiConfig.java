package finance.modelling.data.ingestfinancialfundamentalsfmp.service.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
@Getter
public class FmpApiConfig {

    private final String baseUrl;
    private final String apiKey;
    private final Duration requestDelayMs;
    private final String balanceSheetResourceUrl;
    private final String cashFlowResourceUrl;
    private final String incomeStatementResourceUrl;

    public FmpApiConfig(
            @Value("${client.fmp.baseUrl}") String baseUrl,
            @Value("${client.fmp.security.key}") String apiKey,
            @Value("${client.fmp.request.delay.ms}") Long requestDelayMs,
            @Value("${client.fmp.resource.balanceSheet}") String balanceSheetResourceUrl,
            @Value("${client.fmp.resource.cashFlow}") String cashFlowResourceUrl,
            @Value("${client.fmp.resource.incomeStatement}") String incomeStatementResourceUrl) {
        this.baseUrl = baseUrl;
        this.apiKey = apiKey;
        this.requestDelayMs = Duration.ofMillis(requestDelayMs);
        this.balanceSheetResourceUrl = balanceSheetResourceUrl;
        this.cashFlowResourceUrl = cashFlowResourceUrl;
        this.incomeStatementResourceUrl = incomeStatementResourceUrl;
    }
}
