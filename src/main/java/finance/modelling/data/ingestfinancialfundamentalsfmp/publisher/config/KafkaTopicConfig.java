package finance.modelling.data.ingestfinancialfundamentalsfmp.publisher.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    private final Integer numPartitions;
    private final Integer numReplicas;
    private final String inputFmpTickerTopic;
    private final String outputFmpBalanceSheetTopic;
    private final String outputFmpCashFlowTopic;
    private final String outputFmpIncomeStatementTopic;

    public KafkaTopicConfig(
            @Value("${kafka.bindings.publisher.partitions}") Integer numPartitions,
            @Value("${kafka.bindings.publisher.replicas}") Integer numReplicas,
            @Value("${kafka.bindings.publisher.fmp.fmpTickers}") String inputFmpTickerTopic,
            @Value("${kafka.bindings.publisher.fmp.balanceSheet}") String outputFmpBalanceSheetTopic,
            @Value("${kafka.bindings.publisher.fmp.cashFlow}") String outputFmpCashFlowTopic,
            @Value("${kafka.bindings.publisher.fmp.incomeStatement}") String outputFmpIncomeStatementTopic) {
        this.numPartitions = numPartitions;
        this.numReplicas = numReplicas;
        this.inputFmpTickerTopic = inputFmpTickerTopic;
        this.outputFmpBalanceSheetTopic = outputFmpBalanceSheetTopic;
        this.outputFmpCashFlowTopic = outputFmpCashFlowTopic;
        this.outputFmpIncomeStatementTopic = outputFmpIncomeStatementTopic;
    }

    @Bean
    public NewTopic outputFmpBalanceSheetTopic() {
        return TopicBuilder
                .name(outputFmpBalanceSheetTopic)
                .partitions(numPartitions)
                .replicas(numReplicas)
                .compact()
                .build();
    }

    @Bean
    public NewTopic outputFmpCashFlowTopic() {
        return TopicBuilder
                .name(outputFmpCashFlowTopic)
                .partitions(numPartitions)
                .replicas(numReplicas)
                .compact()
                .build();
    }

    @Bean
    public NewTopic outputFmpIncomeStatementTopic() {
        return TopicBuilder
                .name(outputFmpIncomeStatementTopic)
                .partitions(numPartitions)
                .replicas(numReplicas)
                .compact()
                .build();
    }
}
