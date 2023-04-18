package com.esms.orderservice.service;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriBuilder;

import com.esms.orderservice.dto.InventoryResponse;
import com.esms.orderservice.dto.OrderLineItemsDto;
import com.esms.orderservice.dto.OrderRequest;
import com.esms.orderservice.model.Order;
import com.esms.orderservice.model.OrderLineItems;
import com.esms.orderservice.repository.OrderRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final WebClient webClient;
    
    public void placeOrder(OrderRequest orderRequest) {
        Order order = new Order();

        order.setOrderNumber(UUID.randomUUID().toString());

        List<OrderLineItems> orderLineItems = orderRequest.getOrderLineItemsDtoList()
            .stream()
            .map(o -> mapToDto(o))
            .toList();

        order.setOrderLineItems(orderLineItems);

        List<String> skuCodes = order.getOrderLineItems().stream()  
            .map(OrderLineItems::getSkuCode)
            .toList();

        InventoryResponse[] inventoryResponseArray = webClient.get()
            .uri("http://localhost:8080/api/inventory", 
                uriBuilder -> uriBuilder.queryParam("skuCode", skuCodes)
                .build())
            .retrieve()
            .bodyToMono(InventoryResponse[].class)
            .block();

        boolean allProductsInStock = Arrays.stream(inventoryResponseArray).allMatch(InventoryResponse::isInStock);
        
        if (allProductsInStock) {
            orderRepository.save(order);
        } else {
            throw new IllegalArgumentException("Product is not in stock, please try again");
        }

        orderRepository.save(order);
    }

    private OrderLineItems mapToDto(OrderLineItemsDto dto) {
        OrderLineItems orderLineItens = new OrderLineItems();
        orderLineItens.setPrice(dto.getPrice());
        orderLineItens.setQuantity(dto.getQuantity());
        orderLineItens.setSkuCode(dto.getSkuCode());

        return orderLineItens;
    }
}
