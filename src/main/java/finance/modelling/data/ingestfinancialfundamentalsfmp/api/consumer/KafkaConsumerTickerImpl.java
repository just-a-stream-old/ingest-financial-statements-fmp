package finance.modelling.data.ingestfinancialfundamentalsfmp.api.consumer;

import finance.modelling.fmcommons.data.schema.fmp.dto.FmpTickerDTO;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.kafka.receiver.KafkaReceiver;
import reactor.kafka.receiver.ReceiverOptions;
import reactor.kafka.receiver.ReceiverRecord;

import java.util.Collections;

@Component
public class KafkaConsumerTickerImpl implements KafkaConsumer<FmpTickerDTO> {

    private final ReceiverOptions<String, FmpTickerDTO> receiverOptions;

    public KafkaConsumerTickerImpl(ReceiverOptions<String, FmpTickerDTO> receiverOptions) {
        this.receiverOptions = receiverOptions;
    }

    public Flux<ReceiverRecord<String, FmpTickerDTO>> receiveMessages(String topic) {
        return KafkaReceiver
                .create(receiverOptions.subscription(Collections.singleton(topic)))
                .receive();
    }
}
