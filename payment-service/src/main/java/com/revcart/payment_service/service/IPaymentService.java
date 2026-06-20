package com.revcart.payment_service.service;

import com.revcart.common_events.events.InventoryReservedEvent;

public interface IPaymentService {
    public void processPayment(InventoryReservedEvent event,String messageType);
}
