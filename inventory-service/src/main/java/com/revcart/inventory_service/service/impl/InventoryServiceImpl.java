package com.revcart.inventory_service.service.impl;

import com.revcart.common_events.commands.ReserveInventoryCommand;
import com.revcart.common_events.constants.KafkaTopics;
import com.revcart.common_events.events.InventoryReservationFailedEvent;
import com.revcart.common_events.events.InventoryReservedEvent;
import com.revcart.common_events.payload.OrderItemPayload;
import com.revcart.common_events.service.MessageDeduplicationService;
import com.revcart.common_outbox.service.OutboxService;
import com.revcart.inventory_service.dto.InventoryResponse;
import com.revcart.inventory_service.dto.ReserveOrReleaseRequest;
import com.revcart.inventory_service.dto.UpsertInventoryRequest;
import com.revcart.inventory_service.entity.Inventory;
import com.revcart.inventory_service.exception.InsufficientStockAvailabilityException;
import com.revcart.inventory_service.exception.InventoryRecordNotFoundException;
import com.revcart.inventory_service.repository.InventoryRepository;
import com.revcart.inventory_service.service.IInventoryService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class InventoryServiceImpl
        implements IInventoryService {

    private final InventoryRepository repository;
    private final OutboxService outboxService;
    private final MessageDeduplicationService messageDeduplicationService;

    @Override
    public InventoryResponse upsertInventory(Long productId, UpsertInventoryRequest request) {
        return getInventoryResponse(productId, request, false);
    }

    private InventoryResponse getInventoryResponse(Long productId, UpsertInventoryRequest request, boolean restock) {
        Inventory inventory = repository.findByProductId(productId)
                .orElse(Inventory.builder()
                        .productId(productId)
                        .availableQuantity(0)
                        .reservedQuantity(0)
                        .build()
                );
        inventory.setAvailableQuantity(restock ? inventory.getAvailableQuantity() + request.quantity() : request.quantity());
        Inventory saved = repository.save(inventory);
        return mapInventoryToInventoryResponse(saved);
    }

    @Override
    public InventoryResponse upsertRestockInventory(Long productId, UpsertInventoryRequest request) {
        return getInventoryResponse(productId, request, true);
    }

    @Override
    public InventoryResponse reserveStock(Long productId, ReserveOrReleaseRequest request) {
        Optional<Inventory> inventory = repository.findByProductId(productId);
        if (inventory.isPresent()) {
            Inventory inventoryValue = inventory.get();
            if (request.quantity() > inventoryValue.getAvailableQuantity()) {
                throw new InsufficientStockAvailabilityException("Unable to reserve stock as requested quantity is more than available quantity");
            }
            inventoryValue.setAvailableQuantity(inventoryValue.getAvailableQuantity() - request.quantity());
            inventoryValue.setReservedQuantity(inventoryValue.getReservedQuantity() + request.quantity());
            Inventory saved = repository.save(inventoryValue);
            return mapInventoryToInventoryResponse(saved);
        } else {
            throw new InventoryRecordNotFoundException("No inventory record found for product with productId: " + productId);
        }
    }

    @Override
    public InventoryResponse releaseStock(Long productId, ReserveOrReleaseRequest request) {
        Optional<Inventory> inventory = repository.findByProductId(productId);
        if (inventory.isPresent()) {
            Inventory inventoryValue = inventory.get();
            if (request.quantity() > inventoryValue.getReservedQuantity()) {
                throw new InsufficientStockAvailabilityException("Unable to release stock as requested quantity is more than reserved quantity");
            }
            inventoryValue.setAvailableQuantity(inventoryValue.getAvailableQuantity() + request.quantity());
            inventoryValue.setReservedQuantity(inventoryValue.getReservedQuantity() - request.quantity());
            Inventory saved = repository.save(inventoryValue);
            return mapInventoryToInventoryResponse(saved);
        } else {
            throw new InventoryRecordNotFoundException("No inventory record found for product with productId: " + productId);
        }
    }

    private InventoryResponse mapInventoryToInventoryResponse(Inventory saved) {
        return new InventoryResponse(saved.getProductId(), saved.getAvailableQuantity(), saved.getReservedQuantity());
    }

    @Override
    public InventoryResponse getInventory(Long productId) {
        Optional<Inventory> inventory = repository.findByProductId(productId);
        return inventory.map(this::mapInventoryToInventoryResponse).orElseGet(() -> new InventoryResponse(productId, 0, 0));
    }

    @Override
    @Transactional
    public void reserveInventory(ReserveInventoryCommand command,String messageType) {
        for (OrderItemPayload item : command.items()) {
            reserveStock(item.productId(), new ReserveOrReleaseRequest(item.quantity()));
        }
        InventoryReservedEvent event = new InventoryReservedEvent(command.sagaId(), command.orderId(), command.customerId(), command.totalAmount(), command.items(), Instant.now());
        outboxService.saveEvent("INVENTORY", command.orderId().toString(), InventoryReservedEvent.class.getSimpleName(), KafkaTopics.PAYMENT_COMMAND_TOPIC, event);
        messageDeduplicationService.markProcessed(command.sagaId(), messageType);
    }

    @Override
    @Transactional
    public void handleReservationFailed(ReserveInventoryCommand command, RuntimeException e,String messageType) {
        InventoryReservationFailedEvent event = new InventoryReservationFailedEvent(command.sagaId(), command.orderId(), e.getMessage(), Instant.now());
        outboxService.saveEvent("INVENTORY", command.orderId().toString(), InventoryReservationFailedEvent.class.getSimpleName(), KafkaTopics.INVENTORY_RESPONSE_TOPIC, event);
        messageDeduplicationService.markProcessed(command.sagaId(),messageType);
    }
}
