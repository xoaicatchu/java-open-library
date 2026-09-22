package com.example.statemachine.service;

import com.example.statemachine.domain.OrderEvent;
import com.example.statemachine.domain.OrderState;
import com.example.statemachine.entity.OrderEntity;
import com.example.statemachine.entity.StateHistory;
import com.example.statemachine.repository.OrderRepository;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.state.State;
import org.springframework.statemachine.support.DefaultStateMachineContext;
import org.springframework.statemachine.support.StateMachineInterceptorAdapter;
import org.springframework.statemachine.transition.Transition;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class OrderService {
    public static final String ORDER_ID_HEADER = "order_id";

    private final OrderRepository orderRepository;
    private final StateMachineFactory<OrderState, OrderEvent> stateMachineFactory;

    public OrderService(OrderRepository orderRepository, StateMachineFactory<OrderState, OrderEvent> stateMachineFactory) {
        this.orderRepository = orderRepository;
        this.stateMachineFactory = stateMachineFactory;
    }

    @Transactional
    public OrderEntity createOrder(String description) {
        OrderEntity order = new OrderEntity();
        order.setDescription(description);
        order.setState(OrderState.CREATED);
        
        StateHistory h = new StateHistory();
        h.setState(OrderState.CREATED);
        h.setTimestamp(LocalDateTime.now());
        order.getHistory().add(h);

        return orderRepository.save(order);
    }

    @Transactional
    public OrderEntity sendEvent(Long orderId, OrderEvent event) {
        OrderEntity order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        StateMachine<OrderState, OrderEvent> sm = build(order);
        Message<OrderEvent> msg = MessageBuilder.withPayload(event)
                .setHeader(ORDER_ID_HEADER, order.getId())
                .build();
                
        sm.sendEvent(Mono.just(msg)).blockLast();

        return orderRepository.findById(orderId).orElseThrow();
    }

    @Transactional(readOnly = true)
    public OrderEntity getOrder(Long orderId) {
        return orderRepository.findById(orderId).orElseThrow();
    }

    private StateMachine<OrderState, OrderEvent> build(OrderEntity order) {
        StateMachine<OrderState, OrderEvent> sm = stateMachineFactory.getStateMachine(Long.toString(order.getId()));
        sm.stopReactively().block();
        sm.getStateMachineAccessor()
                .doWithAllRegions(sma -> {
                    sma.addStateMachineInterceptor(new OrderInterceptor());
                    sma.resetStateMachineReactively(new DefaultStateMachineContext<>(order.getState(), null, null, null)).block();
                });
        sm.startReactively().block();
        return sm;
    }

    private class OrderInterceptor extends StateMachineInterceptorAdapter<OrderState, OrderEvent> {
        @Override
        public void preStateChange(State<OrderState, OrderEvent> state, Message<OrderEvent> message, 
                                   Transition<OrderState, OrderEvent> transition, StateMachine<OrderState, OrderEvent> stateMachine, 
                                   StateMachine<OrderState, OrderEvent> rootStateMachine) {
            
            Optional.ofNullable(message)
                    .flatMap(msg -> Optional.ofNullable((Long) msg.getHeaders().getOrDefault(ORDER_ID_HEADER, -1L)))
                    .flatMap(orderRepository::findById)
                    .ifPresent(order -> {
                        order.setState(state.getId());
                        StateHistory h = new StateHistory();
                        h.setState(state.getId());
                        h.setTimestamp(LocalDateTime.now());
                        order.getHistory().add(h);
                        orderRepository.save(order);
                    });
        }
    }
}
