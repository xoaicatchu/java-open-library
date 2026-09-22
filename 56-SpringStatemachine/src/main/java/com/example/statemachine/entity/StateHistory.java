package com.example.statemachine.entity;
import com.example.statemachine.domain.OrderState;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "state_history")
public class StateHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Enumerated(EnumType.STRING)
    private OrderState state;
    private LocalDateTime timestamp;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public OrderState getState() { return state; }
    public void setState(OrderState state) { this.state = state; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}
