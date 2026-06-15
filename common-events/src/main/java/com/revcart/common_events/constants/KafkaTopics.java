package com.revcart.common_events.constants;

public final class KafkaTopics {

    private KafkaTopics() {
    }

    public static final String INVENTORY_COMMAND_TOPIC = "inventory-command-topic";
    public static final String INVENTORY_RESPONSE_TOPIC = "inventory-response-topic";
    public static final String PAYMENT_COMMAND_TOPIC = "payment-command-topic";
    public static final String PAYMENT_RESPONSE_TOPIC = "payment-response-topic";
}
