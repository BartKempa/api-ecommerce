package com.example.apiecommerce.domain.order;

import com.example.apiecommerce.domain.address.Address;
import com.example.apiecommerce.domain.delivery.Delivery;
import com.example.apiecommerce.domain.orderItem.OrderItem;
import com.example.apiecommerce.domain.user.User;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private double totalPrice;
    private LocalDateTime orderDate;
    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;
    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    @ManyToOne
    @JoinColumn(name = "address_id", referencedColumnName = "id")
    private Address address;
    @OneToMany(mappedBy = "order", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private Set<OrderItem> orderItems = new HashSet<>();
    @ManyToOne
    @JoinColumn(name = "delivery_id")
    private Delivery delivery;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(Double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDateTime orderDate) {
        this.orderDate = orderDate;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public Set<OrderItem> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(Set<OrderItem> orderItems) {
        this.orderItems = orderItems;
    }


    public Delivery getDelivery() {
        return delivery;
    }

    public void setDelivery(Delivery delivery) {
        this.delivery = delivery;
    }

    public PaymentStatus getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(PaymentStatus paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public OrderStatus getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Order order)) return false;
        return Objects.equals(id, order.id) && Objects.equals(totalPrice, order.totalPrice) && Objects.equals(orderDate, order.orderDate) && paymentStatus == order.paymentStatus && orderStatus == order.orderStatus;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, totalPrice, orderDate, paymentStatus, orderStatus);
    }
}
