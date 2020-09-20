package finance.modelling.data.ingestfinancialfundamentalsfmp.publisher.contract;

public interface KafkaPublisher<V> {
    void publishMessage(String topic, V payload);
}
