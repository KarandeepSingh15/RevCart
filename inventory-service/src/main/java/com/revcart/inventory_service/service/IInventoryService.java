package com.revcart.inventory_service.service;

import com.revcart.common_events.events.PaymentFailedEvent;
import com.revcart.common_events.events.PaymentSucceededEvent;
import com.revcart.common_events.events.ReserveInventoryCommand;
import com.revcart.inventory_service.dto.InventoryResponse;
import com.revcart.inventory_service.dto.ReserveOrReleaseRequest;
import com.revcart.inventory_service.dto.UpsertInventoryRequest;

public interface IInventoryService {

    InventoryResponse upsertInventory(Long productId, UpsertInventoryRequest request);

    InventoryResponse upsertRestockInventory(Long productId, UpsertInventoryRequest request);

    InventoryResponse reserveStock(Long productId, ReserveOrReleaseRequest request);

    InventoryResponse releaseStock(Long productId, ReserveOrReleaseRequest request);

    InventoryResponse getInventory(Long productId);

    void reserveInventory(ReserveInventoryCommand command,String messageType);

    void handleReservationFailed(ReserveInventoryCommand command, RuntimeException e, String messageType);

    void commitInventory(PaymentSucceededEvent event,String messageType);

    void releaseInventory(PaymentFailedEvent event,String messageType);
}

