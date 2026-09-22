package com.example.statemachine.entity;
import com.example.statemachine.domain.OrderState;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
public class OrderEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String description;
    @Enumerated(EnumType.STRING)
    private OrderState state;
    
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "order_id")
    private List<StateHistory> history = new ArrayList<>();

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public OrderState getState() { return state; }
    public void setState(OrderState state) { this.state = state; }
    public List<StateHistory> getHistory() { return history; }
    public void setHistory(List<StateHistory> history) { this.history = history; }
}
